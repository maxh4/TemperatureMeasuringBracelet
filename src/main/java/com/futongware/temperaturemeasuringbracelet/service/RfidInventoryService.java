package com.futongware.temperaturemeasuringbracelet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.futongware.temperaturemeasuringbracelet.entity.RfidResult;
import com.futongware.temperaturemeasuringbracelet.entity.TagInfo;
import com.futongware.temperaturemeasuringbracelet.task.CustomizedRunner;
import com.uhf.api.cls.BackReadOption;
import com.uhf.api.cls.ReadExceptionListener;
import com.uhf.api.cls.ReadListener;
import com.uhf.api.cls.Reader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SDK可以通过两种方式实现异步盘存
 *  1. 普通异步盘存模式: 通过内部新创建线程循环调用同步盘存Reader.TagInventory_Raw方法来实现
 *  2. 高速异步盘存模式: 真正的异步盘存，读写器处于连续盘存状态。这种方式读写器的盘存性能是最佳的，对于有较高盘存性能要求的应用应使用此种异步盘存模式
 */
public class RfidInventoryService {
    private Reader reader;
    private int[] selectedAnts;
    private Short timeout;
    private Map<String, TagInfo> tagInfoMap = new LinkedHashMap<>();

    private RfidInventoryService() {}
    public RfidInventoryService(Reader reader, Integer[] selectedAnts, Short timeout) {
        this.reader = reader;
        this.selectedAnts = Arrays.stream(selectedAnts).mapToInt(Integer::valueOf).toArray();;
        this.timeout = timeout;
    }

    /**
     * 读写器盘存到标签时的回调处理函数
     * @return
     */
    private ReadListener getReadListener() {
        return new ReadListener() {
            @Override
            public void tagRead(Reader reader, Reader.TAGINFO[] taginfos) {
                int tagCnt = taginfos.length;
                List<TagInfo> collect = Arrays.stream(taginfos).map(taginfo -> new TagInfo(taginfo)).collect(Collectors.toList());
                collect.forEach(tagInfo -> {
                    String epcId = tagInfo.getEpcIdStr();
                    if (tagInfoMap.containsKey(epcId)) {
                        TagInfo tagInfoOld = tagInfoMap.get(epcId);
                        tagInfo.setReadCnt(tagInfoOld.getReadCnt() + tagInfo.getReadCnt());
                    }
                    tagInfoMap.put(epcId, tagInfo);

                    try {
                        System.out.println("[Reader-" + reader.GetReaderAddress() + "]" + new ObjectMapper().writeValueAsString(tagInfoMap.get(epcId)));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
            }
        };
    }
    /**
     * 读写器发生错误时的回调处理函数
     * @return
     */
    private ReadExceptionListener getReadExceptionListener() {
        return new ReadExceptionListener() {
            @Override
            public void tagReadException(Reader reader, Reader.READER_ERR reader_err) {
                String readerAddress = reader.GetReaderAddress();
                String errorStr = reader_err.toString();
            }
        };
    }

    public RfidResult startAsyncInventory(Boolean isAdvanceMode) {
        // 设置盘存到标签时的回调处理函数
        reader.addReadListener(getReadListener());
        // 设置读写器发生错误时的回调处理函数
        reader.addReadExceptionListener(getReadExceptionListener());

        int antCnt = selectedAnts.length;
        BackReadOption backReadOp = new BackReadOption();
        // 盘存周期,单位为ms，可根据实际使用的天线个数按照每个天线需要200ms的方式计算得出,
        // 如果启用高速模式则此选项没有任何意义，可以设置为任意值，或者干脆不设置
        backReadOp.ReadDuration = (short)(200 * antCnt);
        // 盘存周期间的设备不工作时间,单位为ms,一般可设置为0，增加设备不工作时间有利于
        // 节电和减少设备发热（针对某些使用电池供电或空间结构不利于散热的情况会有帮助）
        backReadOp.ReadInterval = 10;
        backReadOp.IsGPITrigger = false;
        backReadOp.IsFastRead = false;
        if (isAdvanceMode) {
            backReadOp.IsFastRead = true;
            // 标签信息是否携带识别天线的编号
            backReadOp.TMFlags.IsAntennaID = true;
            // 标签信息是否携带标签识别次数
            backReadOp.TMFlags.IsReadCnt = true;
            // 标签信息是否携带识别标签时的信号强度
            backReadOp.TMFlags.IsRSSI = true;
            // 标签信息是否携带识别标签时的工作频点
            backReadOp.TMFlags.IsFrequency = true;
            // 保留字段，可始终设置为false
            backReadOp.TMFlags.IsRFU = false;
            // 标签信息是否携带时间戳
            backReadOp.TMFlags.IsTimestamp = false;
            // 标签信息是否携带识别标签时同时读取的其它bank数据信息,如果要获取在
            // 盘存时同时读取其它bank的信息还必须设置MTR_PARAM_TAG_EMBEDEDDATA参数,
            // （目前只有slr11xx和slr12xx系列读写器才支持）
            backReadOp.TMFlags.IsEmdData = false;
        }
        Reader.READER_ERR err = reader.StartReading(selectedAnts, antCnt, backReadOp);
        return new RfidResult().setErr(err);
    }
    public RfidResult stopAsyncInventory() {
        Reader.READER_ERR err = reader.StopReading();
        return new RfidResult().setErr(err);
    }
    public Map<String, TagInfo> getTagInfoMap() {
        return tagInfoMap;
    }
    public RfidResult clearAsyncInventory() {
        tagInfoMap.clear();
        return new RfidResult();
    }

    private Thread thread = null;
    private CustomizedRunner<String, String> runner = null;
    private final static Integer TAG_MAX_CNT = 200;
    //region Thread Operation
    private Function getSyncRawInventoryTask() {
        return new Function<Object, RfidResult>() {
            @Override
            public RfidResult apply(Object o) {
                int[] tagcnt = new int[1];
                Reader.TAGINFO[] taginfo = new Reader.TAGINFO[TAG_MAX_CNT];
                Reader.READER_ERR err = reader.TagInventory_Raw(selectedAnts, selectedAnts.length, timeout, tagcnt);
                return new RfidResult().setErr(err);
            }
        };
    }
    private Function getASyncInventoryTask(Boolean isAdvanceMode) {
        return new Function<Object, RfidResult>() {
            @Override
            public RfidResult apply(Object o) {
                return startAsyncInventory(isAdvanceMode);
            }
        };
    }
    @Deprecated
    public RfidResult startTagInventory() {
        runner = new CustomizedRunner<String, String>();
        runner.setTaskArgs("test arg");
        runner.setTask(args -> {
            System.out.println(args);
            return "test result";
        });
        runner.setRecall(args -> {
            System.out.println(args);
        });
        thread = new Thread(runner);
        System.err.println("Starting thread: " + thread);
        thread.start();
        return new RfidResult();
    }
    @Deprecated
    public RfidResult stopTagInventory() {
        System.err.println("Stopping thread: " + thread);
        if (thread == null)
            return new RfidResult().setErr(Reader.READER_ERR.MT_CMD_FAILED_ERR);
        runner.terminate();
        try {
            thread.join();
            System.err.println("Thread successfully stopped.");
            return new RfidResult();
        }
        catch (InterruptedException e) {
            System.err.println("Exception In Main"+ e);
            e.printStackTrace();
            return new RfidResult().setErr(Reader.READER_ERR.MT_CMD_FAILED_ERR);
        }
    }
    @Deprecated
    public RfidResult pauseTagInventory() {
        System.err.println("Pausing thread: " + thread);
        if (thread != null)
            return new RfidResult().setErr(Reader.READER_ERR.MT_CMD_FAILED_ERR);
        runner.pause();
        System.err.println("Thread successfully Paused.");
        return new RfidResult();
    }
    @Deprecated
    public RfidResult resumeTagInventory() {
        System.err.println("Resume thread: " + thread);
        if (thread != null)
            return new RfidResult().setErr(Reader.READER_ERR.MT_CMD_FAILED_ERR);
        runner.resume();
        System.err.println("Thread successfully resumed.");
        return new RfidResult();
    }
    //endregion
}
