package com.futongware.temperaturemeasuringbracelet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.futongware.temperaturemeasuringbracelet.entity.RfidResult;
import org.springframework.stereotype.Component;

import com.uhf.api.cls.Reader;
import com.uhf.api.cls.Reader.*;

import java.util.*;

@Component
public class RfidService {

    private ObjectMapper objectMapper = new ObjectMapper();

    public final static Integer TAG_RESERVED_BANK = 0;
    public final static Integer TAG_EPC_BANK = 1;
    public final static Integer TAG_TID_BANK = 2;
    public final static Integer TAG_USER_BANK = 3;

    private Reader reader = null;
    private Boolean isM6E = null;
    private Boolean isEth = null;
    private Integer[] ants;
    private Map<SL_TagProtocol, String> tagProtocolDic= new HashMap<SL_TagProtocol, String>() {{
        put(SL_TagProtocol.SL_TAG_PROTOCOL_GEN2, "gen2");
        put(SL_TagProtocol.SL_TAG_PROTOCOL_IPX256, "ipx256");
        put(SL_TagProtocol.SL_TAG_PROTOCOL_IPX64, "ipx64");
        put(SL_TagProtocol.SL_TAG_PROTOCOL_ISO180006B, "iso180006b");
        put(SL_TagProtocol.SL_TAG_PROTOCOL_ISO180006B_UCODE, "iso180006b_ucode");
        put(SL_TagProtocol.SL_TAG_PROTOCOL_NONE, "none");
    }};

    //region Base Connection
    public RfidResult connectRfid(String ipAddr, Integer antCnt) {
        if (reader != null) {
            reader.CloseReader();
            reader = null;
        }
        reader = new Reader();
        isM6E = true;
        READER_ERR err = null;
        // 判断是网口连接或串口连接
        if (ipAddr.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
            // 网口连接
            err = reader.InitReader_Notype(ipAddr, antCnt);
            isEth = true;
        } else {
            // 串口连接
            err = reader.InitReader(ipAddr, Reader_Type.MODULE_FOUR_ANTS);
            isEth = false;
        }
        ants = new Integer[antCnt];
        for (int i = 0; i < antCnt; i++) {
            ants[i] = i + 1;
        }
        setAntPowerConf(new String[]{"3000", "3000", "3000", "3000"}, new String[]{"3000", "3000", "3000", "3000"});
        return new RfidResult().setErr(err);
    }

    public void disConnectRfid() {
        if (reader != null) {
            reader.CloseReader();
            reader = null;
        }
    }

    public Boolean isConnected() {
        return reader != null;
    }

    public RfidResult checkConnectionAnts() throws JsonProcessingException {
        ConnAnts_ST connAnts = reader.new ConnAnts_ST();
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_READER_CONN_ANTS, connAnts);
        if (err != READER_ERR.MT_OK_ERR)
            return new RfidResult().setErr(err);
        return new RfidResult().setData(objectMapper.writeValueAsString(connAnts));
    }
    //endregion

    //region Reader Params Config
    public RfidResult getParams() throws JsonProcessingException {
        Map<String, Object> params = new HashMap<String, Object>();
        RfidResult result = new RfidResult();

        //region ip配置
        if (isEth) {
            result = getIPInfo();
            if (result.getErr() != READER_ERR.MT_OK_ERR)
                return result;
            params.put("IPInfo", result.getData());
        }
        //endregion
        //region 天线功率配置
        result = getAntPowerConf();
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        params.put("antPowerConf", objectMapper.writeValueAsString(result.getData()));
        //endregion
        //region Gen2Session
        result = getGen2Session();
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        params.put("gen2Session", result.getData());
        //endregion
        //region 是否检查天线
        result = getIsChkAnt();
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        params.put("isChkAnt", result.getData());
        //endregion
        //region Gen2Qval
        result = getGen2Qval();
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        params.put("gen2Qval", result.getData());
        //endregion
        //region Gen2Writemode
        result = getGen2WriteMode();
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        params.put("gen2Writemode", result.getData());
        //endregion
        //region Gen2MaxEPCLen
        result = getGen2MaxEpcLen();
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        params.put("gen2MaxEPCLen", result.getData());
        //endregion
        //region Gen2Target
        result = getGen2Target();
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        params.put("gen2Target", result.getData());
        //endregion
        //region Gen2TagEncoding
        result = getGen2TagEncoding();
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        params.put("gen2TagEncoding", result.getData());
        //endregion
        //region 天线唯一性
        result = getUniqueByAnt();
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        params.put("uniqueByAnt", result.getData());
        //endregion
        //region 附加数据唯一性
        result = getUniqueByEmdData();
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        params.put("uniqueByEMDData", result.getData());
        //endregion
        //region 记录最大RSSI
        result = getRecordHighestRSSI();
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        params.put("recordHighestRSSI", result.getData());
        //endregion
        return new RfidResult().setData(params);
    }

    public RfidResult getAntPowerConf() {
        // return error if no connections with reader
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        AntPowerConf antPowerConf = reader.new AntPowerConf();
        READER_ERR err = reader.ParamGet( Mtr_Param.MTR_PARAM_RF_ANTPOWER, antPowerConf);
        if (err == READER_ERR.MT_OK_ERR)
            return new RfidResult().setData(antPowerConf);
        return new RfidResult().setErr(err);
    }

    public RfidResult setAntPowerConf(String[] readPowers, String[] writePowers) {
        // return error if no connections with reader
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int antCnt = readPowers.length;
        AntPowerConf antPowerConf = reader.new AntPowerConf();
        for (int i = 0; i < antCnt; i++) {
            AntPower power = reader.new AntPower();
            power.antid = i + 1;
            if (!readPowers[i].isEmpty())
                power.readPower = Short.parseShort(readPowers[i]);
            else
                power.readPower = 3000; // 3000dBM as default power
            if (!writePowers[i].isEmpty())
                power.writePower = Short.parseShort(writePowers[i]);
            else
                power.writePower = 3000;
            antPowerConf.Powers[i] = power;
        }
        antPowerConf.antcnt = antCnt;
        READER_ERR err = reader.ParamSet( Mtr_Param.MTR_PARAM_RF_ANTPOWER, antPowerConf);
        return new RfidResult().setErr(err);
    }

    public RfidResult getIPInfo() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        Reader_Ip readerIP = reader.new Reader_Ip();
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_READER_IP, readerIP);
        if (err == READER_ERR.MT_OK_ERR) {
            Map<String, String> ipInfo = new HashMap<String, String>() {{
                put("gateway", new String(readerIP.gateway));
                put("ip", new String(readerIP.ip));
                put("mask", new String(readerIP.mask));
            }};
            return new RfidResult().setData(ipInfo);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setIpInfo(Map<String, String> ipInfo) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        Reader_Ip readerIP = reader.new Reader_Ip();
        readerIP.ip = ipInfo.get("ip").getBytes();
        readerIP.mask = ipInfo.get("mask").getBytes();
        readerIP.gateway = ipInfo.get("gateway").getBytes();
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_READER_IP, readerIP);
        return new RfidResult().setErr(err);
    }

    public RfidResult getIsChkAnt() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] isChkAnt = new int[1];
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT, isChkAnt);
        if (err == READER_ERR.MT_OK_ERR) {
            return new RfidResult().setData(isChkAnt[0]);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setIsChkAnt(int isChkAnt) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] isChkAnt_ = new int[] {isChkAnt};
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT, isChkAnt_);
        return new RfidResult().setErr(err);
    }

    public RfidResult getGen2MaxEpcLen() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2MaxEPCLen = new int[1];
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_MAXEPCLEN, gen2MaxEPCLen);
        if (err == READER_ERR.MT_OK_ERR) {
            return new RfidResult().setData(gen2MaxEPCLen[0]);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setGen2MaxEpcLen(int gen2MaxEpcLen) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        if (isM6E)
            return new RfidResult().setErr(READER_ERR.MT_OP_NOT_SUPPORTED);
        int[] gen2MaxEPCLen_ = new int[] {gen2MaxEpcLen};
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_MAXEPCLEN, gen2MaxEpcLen);
        return new RfidResult().setErr(err);
    }

    public RfidResult getGen2Session() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2Session = new int[1];
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, gen2Session);
        if (err == READER_ERR.MT_OK_ERR) {
            return new RfidResult().setData(gen2Session[0]);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setGen2Session(int gen2Session) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2Session_ = new int[] {gen2Session};
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, gen2Session_);
        return new RfidResult().setErr(err);
    }

    public RfidResult getGen2Qval() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2Qval = new int[1];
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_Q, gen2Qval);
        if (err == READER_ERR.MT_OK_ERR) {
            return new RfidResult().setData(gen2Qval[0]);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setGen2Qval(int gen2Qval) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2Qval_ = new int[] {gen2Qval};
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_Q, gen2Qval_);
        return new RfidResult().setErr(err);
    }

    public RfidResult getGen2TagEncoding() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2TagEncoding = new int[1];
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, gen2TagEncoding);
        if (err == READER_ERR.MT_OK_ERR) {
            return new RfidResult().setData(gen2TagEncoding[0]);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setGen2TagEncoding(int gen2TagEncoding) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        if (isM6E)
            return new RfidResult().setErr(READER_ERR.MT_OP_NOT_SUPPORTED);
        int[] gen2TagEncoding_ = new int[] {gen2TagEncoding};
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, gen2TagEncoding_);
        return new RfidResult().setErr(err);
    }

    public RfidResult getGen2WriteMode() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2Writemode = new int[1];
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_WRITEMODE, gen2Writemode);
        if (err == READER_ERR.MT_OK_ERR) {
            return new RfidResult().setData(gen2Writemode[0]);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setGen2WriteMode(int gen2Writemode) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2Writemode_ = new int[] {gen2Writemode};
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_WRITEMODE, gen2Writemode_);
        return new RfidResult().setErr(err);
    }

    public RfidResult getGen2Target() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2Target = new int[1];
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, gen2Target);
        if (err == READER_ERR.MT_OK_ERR) {
            return new RfidResult().setData(gen2Target[0]);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setGen2Target(int gen2Target) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2Target_ = new int[] {gen2Target};
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, gen2Target_);
        return new RfidResult().setErr(err);
    }

    public RfidResult getGen2BLF() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2BLF = new int[1];
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_BLF, gen2BLF);
        if (err == READER_ERR.MT_OK_ERR) {
            return new RfidResult().setData(gen2BLF[0]);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setGen2BLF(int gen2BLF) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2BLF_ = new int[] {gen2BLF};
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_BLF, gen2BLF_);
        return new RfidResult().setErr(err);
    }

    public RfidResult getGen2Tari() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2Tari = new int[1];
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARI, gen2Tari);
        if (err == READER_ERR.MT_OK_ERR) {
            return new RfidResult().setData(gen2Tari[0]);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setGen2Tari(int gen2Tari) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] gen2Tari_ = new int[] {gen2Tari};
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARI, gen2Tari_);
        return new RfidResult().setErr(err);
    }

    public RfidResult getUniqueByAnt() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] uniqueByAnt_ = new int[1];
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYANT, uniqueByAnt_);
        if (err == READER_ERR.MT_OK_ERR) {
            return new RfidResult().setData(uniqueByAnt_[0]);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setUniqueByAnt(int uniqueByAnt) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] uniqueByAnt_ = new int[] {uniqueByAnt};
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYANT, uniqueByAnt_);
        return new RfidResult().setErr(err);
    }

    public RfidResult getUniqueByEmdData() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] uniqueByEmdData_ = new int[1];
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA, uniqueByEmdData_);
        if (err == READER_ERR.MT_OK_ERR) {
            return new RfidResult().setData(uniqueByEmdData_[0]);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setUniqueByEmdData(int uniqueByEmdData) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] uniqueByEmdData_ = new int[] {uniqueByEmdData};
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA, uniqueByEmdData_);
        return new RfidResult().setErr(err);
    }

    public RfidResult getRecordHighestRSSI() {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] recordHighestRSSI_ = new int[1];
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI, recordHighestRSSI_);
        if (err == READER_ERR.MT_OK_ERR) {
            return new RfidResult().setData(recordHighestRSSI_[0]);
        }
        return new RfidResult().setErr(err);
    }

    public RfidResult setRecordHighestRSSI(int recordHighestRSSI) {
        if (reader == null)
            return new RfidResult().setErr(READER_ERR.MT_IO_ERR);
        int[] recordHighestRSSI_ = new int[] {recordHighestRSSI};
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI, recordHighestRSSI_);
        return new RfidResult().setErr(err);
    }
    //endregion

    //region Inventory
    private Map<String, Map> tagMap = new LinkedHashMap<String, Map>();

    private Thread tagThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true) {
//				if (Thread.currentThread().interrupted()) {
//					System.out.println("线程停止");
//					return;
//				}
                int[] tagcnt = new int[4];
                TAGINFO[] taginfo = new TAGINFO[200];
                READER_ERR err = reader.TagInventory(new int[] { 1,2,3,4 }, 4, (short) 1000, taginfo, tagcnt);
                if (err == READER_ERR.MT_OK_ERR) {
                    for (int i = 0; i < tagcnt[0]; i++) {
                        Map<String, String> currentTag = null;
                        String epcID = Reader.bytes_Hexstr(taginfo[i].EpcId);
                        // 读到的次数
                        int readCnt = taginfo[i].ReadCnt;
                        // 接收到的标签的信号强度
                        int RSSI = taginfo[i].RSSI;
                        // 从哪个频点读到
                        int frequency = taginfo[i].Frequency;
                        // 嵌入数据的字节数
                        short dataLen = taginfo[i].EmbededDatalen;
                        // 嵌入数据
                        byte[] data = taginfo[i].EmbededData;
                        // 标签被哪个天线读到
                        byte antennaID = taginfo[i].AntennaID;
                        // 标签读到的相位
                        int phase = taginfo[i].Phase;
                        // 读到的时间戳
                        String readTime = new Date(taginfo[i].TimeStamp + System.currentTimeMillis()).toLocaleString();
                        // 所使用的标签协议
                        String protocol = tagProtocolDic.get(taginfo[i].protocol);
//						String protocol = "Unknown";
//						if (taginfo[i].protocol == SL_TagProtocol.SL_TAG_PROTOCOL_GEN2) {
//							protocol = "Gen2";
//						}
                        if (tagMap.containsKey(epcID)) {
                            Map<String, String> oldTag = tagMap.get(epcID);
                            oldTag.put("readCnt", Integer.toString(Integer.parseInt(oldTag.get("readCnt"))+readCnt));
                            oldTag.put("readTime", readTime);
                            oldTag.put("data", new String(data));
                            oldTag.put("RSSI", Integer.toString(RSSI));
                            oldTag.put("frequency", Integer.toString(frequency));
                            oldTag.put("phase", Integer.toString(phase));
                            oldTag.put("protocol", protocol);
                            currentTag = oldTag;
                        } else {
                            Map<String, String> newTag = new HashMap<String, String>();
                            newTag.put("seq", Integer.toString(tagMap.size()+1));
                            newTag.put("epcID", epcID);
                            newTag.put("readCnt", Integer.toString(readCnt));
                            newTag.put("readTime", readTime);
                            newTag.put("data", new String(data));
                            newTag.put("antennaID", Integer.toString(antennaID));
                            newTag.put("RSSI", Integer.toString(RSSI));
                            newTag.put("frequency", Integer.toString(frequency));
                            newTag.put("phase", Integer.toString(phase));
                            newTag.put("protocol", protocol);
                            currentTag = newTag;
                        }
                        System.out.println(currentTag);
                        tagMap.put(epcID, currentTag);
                    }
                }
            }
        }
    });

    public RfidResult startInventoy() {
        if (tagThread.isAlive()) {
            tagThread.resume();
        } else {
            tagThread.start();
        }
        return new RfidResult().setData(tagMap);
    }

    public RfidResult stopInventoy() {
        tagThread.suspend();
        return new RfidResult();
    }

    public RfidResult clearInventoy() {
        tagMap.clear();
        return new RfidResult();
    }
    //endregion

    //region Tag Operation
    public RfidResult getTagFilter() throws JsonProcessingException {
        TagFilter_ST tagFilter = reader.new TagFilter_ST();
        READER_ERR err = reader.ParamGet(Mtr_Param.MTR_PARAM_TAG_FILTER, tagFilter);
        if (err != READER_ERR.MT_OK_ERR) {
            return new RfidResult().setErr(err);
        }
        if (tagFilter.flen == 0) {
            return new RfidResult();
        }
        int flen = tagFilter.flen;
        int bankID = tagFilter.bank;
        int bankStart = tagFilter.startaddr;
        boolean isNotInvert = tagFilter.isInvert==1 ? true : false;
        byte[] datab = tagFilter.fdata;
        char[] data = new char[flen * 2];
        reader.Hex2Str(datab, flen, data);
        Map<String, String> tagFilterMap = new HashMap<String, String>();
        tagFilterMap.put("bankID", Integer.toString(bankID));
        tagFilterMap.put("bankStart", Integer.toString(bankStart));
        tagFilterMap.put("isNotInvert", Boolean.toString(isNotInvert));
        tagFilterMap.put("data", new String(data));
        return new RfidResult().setData(objectMapper.writeValueAsString(tagFilterMap));
    }

    /**
     * 设置过滤参数 
     * @param data 过滤数据
     * @param isNotInvert 是否匹配
     * @param bankStart bank起始地址
     * @param bankId 过滤bank
     * @return
     */
    public RfidResult setTagFilter(String data, boolean isNotInvert, int bankStart, int bankId) {
        // 建立过滤器
        TagFilter_ST tagFilter = reader.new TagFilter_ST();
        // 过滤bank
        tagFilter.bank = bankId;
        // 过滤数据
        byte[] datab = new byte[data.length()/2];
        reader.Str2Hex(data, data.length(), datab);
        tagFilter.fdata = datab;
        tagFilter.flen = datab.length;
        // 是否启用过滤
        tagFilter.isInvert = isNotInvert ? 1 : 0;
        // bank中的起始地址
        tagFilter.startaddr = bankStart;
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER, tagFilter);
        return new RfidResult().setErr(err);
    }

    /**
     * 清除过滤参数
     * @return
     */
    public RfidResult clearTagFilter() {
        READER_ERR err = reader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER, null);
        return new RfidResult().setErr(err);
    }

    /**
     * 读取标签不同Bank中的信息
     * @param antID 要执行操作的天线ID
     * @param bankID 读取的BankID,取值范围为0~4;0~3表示操作Gen2标签,4表示操作180006b标签
     * @param bankStart 在Bank中的起始地址,以块编号
     * @param blockNum 要读取的块数
     * @param pwd 如果需要访问密码则为访问密码(4字节),如果不需要访问密码则为null
     * @param timeout 操作的超时时间
     * @return
     */
    public RfidResult tagOpRead(int antID, int bankID, int bankStart, int blockNum, String pwd, short timeout) {
        TAGINFO[] tags=new TAGINFO[200];
        byte[] data = new byte[blockNum *2];
        byte[] pwdb = null;
        if (pwd != null && !pwd.equals("")) {
            pwdb = new byte[4];
            reader.Str2Hex(pwd, pwd.length(), pwdb);
        }
        READER_ERR err = reader.GetTagData(antID, (char)bankID, bankStart, blockNum, data, pwdb, timeout);
        if (err == READER_ERR.MT_OK_ERR) {
            char[] str = new char[data.length * 2];
            reader.Hex2Str(data, data.length, str);
            return new RfidResult().setData(new String(str).toUpperCase());
        }
        return new RfidResult().setErr(err);
    }

    /**
     * 向标签的不同bank中写入数据
     * @param antID 要执行操作的天线ID
     * @param bankID 读取的BankID,取值范围为0~4;0~3表示操作Gen2标签,4表示操作180006b标签
     * @param bankStart 在Bank中的起始地址,以块编号
     * @param pwd 如果需要访问密码则为访问密码(4字节),如果不需要访问密码则为null
     * @param data 写入的数据
     * @param timeout 操作的超时时间
     * @return
     */
    public RfidResult tagOpWrite(int antID, int bankID, int bankStart, String pwd, String data, short timeout) {
        data = data.trim();
        byte[] datab = new byte[data.length()/2];
        reader.Str2Hex(data, data.length(), datab);
        byte[] pwdb = null;
        if (pwd != null && !pwd.equals("")) {
            pwdb = new byte[4];
            reader.Str2Hex(pwd, pwd.length(), pwdb);
        }
        READER_ERR err = reader.WriteTagData(antID, (char)bankID, bankStart, datab, datab.length, pwdb, timeout);
        return new RfidResult().setErr(err);
    }

    /**
     * 写标签的EPC ID
     * 不支持过滤条件,也不能写EPC区被锁定的标签,此函数一般用于初始化标签.
     * 与werite的区别:writeEPC可以在写epc码的同时改变EPC区的PC字段,PC字段保存着EPC码长度的信息.
     * @param antID 要执行操作的天线ID
     * @param pwd 如果需要访问密码则为访问密码(4字节),如果不需要访问密码则为null
     * @param data 写入的数据
     * @param timeout 操作的超时时间
     * @return
     */
    public RfidResult tagOpWriteEPC(int antID, String pwd, String data, short timeout) {
        data = data.trim();
        byte[] datab = new byte[data.length()];
        reader.Str2Hex(data, data.length(), datab);
        byte[] pwdb = null;
        if (pwd != null && !pwd.equals("")) {
            pwdb = new byte[4];
            reader.Str2Hex(pwd, pwd.length(), pwdb);
        }
        READER_ERR err = reader.WriteTagEpcEx(antID, datab, datab.length, pwdb, timeout);
        return new RfidResult().setErr(err);
    }

    private Thread tagReadThread = null;
    private int tagReadSuccessCnt = 0;
    private String currentTagData = "";
    class TagReadRunnable implements Runnable {
        private int antID;
        private int bankID;
        private int bankStart;
        private int blockNum;
        private byte[] data;
        private byte[] pwdb;
        private short timeout;
        private boolean isUnique;
        public TagReadRunnable(int antID, int bankID, int bankStart, int blockNum, byte[] data, byte[] pwdb, short timeout,
                               boolean isUnique) {
            this.antID = antID;
            this.bankID = bankID;
            this.bankStart = bankStart;
            this.blockNum = blockNum;
            this.data = data;
            this.pwdb = pwdb;
            this.timeout = timeout;
            this.isUnique = isUnique;
        }
        public void run() {
            while (true) {
                READER_ERR err = reader.GetTagData(antID, (char)bankID, bankStart, blockNum, data, pwdb, timeout);
                if (err == READER_ERR.MT_OK_ERR) {
                    tagReadSuccessCnt++;
                    char[] str = new char[data.length * 2];
                    reader.Hex2Str(data, data.length, str);
                    currentTagData = new String(str).toUpperCase();
                    System.out.println("成功次数:" + tagReadSuccessCnt);
                }
            }
        }
    }
    /**
     * 连续读取标签不同Bank中的信息
     * @param antID 要执行操作的天线ID
     * @param bankID 读取的BankID,取值范围为0~4;0~3表示操作Gen2标签,4表示操作180006b标签
     * @param bankStart 在Bank中的起始地址,以块编号
     * @param blockNum 要读取的块数
     * @param pwd 如果需要访问密码则为访问密码(4字节),如果不需要访问密码则为null
     * @param timeout 操作的超时时间
     * @param isUnique 是否唯一
     * @return
     */
    public RfidResult tagOpStartRead(int antID, int bankID, int bankStart, int blockNum, String pwd, short timeout,
                                     boolean isUnique) {
        TAGINFO[] tags=new TAGINFO[200];
        byte[] data = new byte[blockNum *2];
        byte[] pwdb = null;
        if (!pwd.isEmpty()) {
            pwdb = new byte[4];
            reader.Str2Hex(pwd, pwd.length(), pwdb);
        }
        int tagOpReadSuccessCnt = 0;
        if (tagReadThread != null) {

        } else {
            tagReadThread = new Thread(new TagReadRunnable(antID, bankID, bankStart, blockNum, data, pwdb, timeout, isUnique));
            tagReadThread.start();
        }
        return new RfidResult().setData(currentTagData);
    }

    public RfidResult tagOpStopRead() {
        if (tagReadThread == null) {
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);
        }
        tagReadThread.stop();;
        tagReadThread = null;
        int totalCnt = tagReadSuccessCnt;
        // 清空数据
        tagReadSuccessCnt = 0;
        currentTagData = "";
        return new RfidResult().setData(totalCnt);
    }

    /**
     * 多Bank连续写入,可一次性对同一标签不同区域写入多条数据<br>
     * 注意: 写入的数据量越大,写入持续的时间需要越久!
     * @param antIds Integer[] 要操作的天线ID,只支持第一个选中的天线
     * @param dataMap Map<String, Map<String, String>> 包含要写入的数据的Map
     * @return 返回本次写入的结果
     */
    public RfidResult startMultiBankWrite(Integer[] antIds, Map<String, Map<String, String>> dataMap) {
        // 开启读写器连续读,读取标签的唯一标识TID
        RfidResult result = new RfidResult();
        String tid = null;
        short timeout = 3000;
        String pwd = null;
        while ((tid = String.valueOf(result.getData())) == "" || (tid = String.valueOf(result.getData())) == null || (tid = String.valueOf(result.getData())) == "null") {
            result = this.tagOpRead(antIds[0], TAG_TID_BANK, 2, 4, pwd, timeout);
        }
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        // 设置读写器过滤条件,将该TID作为过滤器筛选条件
        result = this.setTagFilter(tid, true, 2, TAG_TID_BANK);
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        // 循环写入数据,直到dataMap内的数据全部写完
        Collection<Map<String, String>> dataCollection = dataMap.values();
        for (Map<String, String> inputMap : dataCollection) {
            Integer bankID = Integer.parseInt(inputMap.get("bankID"));
            Integer bankStart = Integer.parseInt(inputMap.get("bankStart"));
            Integer blockNum = Integer.parseInt(inputMap.get("blockNum"));
            String inputData = inputMap.get("inputData");
            result.setErr(READER_ERR.MT_CMD_NO_TAG_ERR);
            while (result.getErr() == READER_ERR.MT_CMD_NO_TAG_ERR || result.getErr() == READER_ERR.MT_CMD_FAILED_ERR) {
                result = this.tagOpWrite(antIds[0], bankID, bankStart, pwd, inputData, timeout);
            }
            if (result.getErr() != READER_ERR.MT_OK_ERR)
                return result;
        }
        result = this.clearTagFilter();
        if (result.getErr() != READER_ERR.MT_OK_ERR)
            return result;
        return new RfidResult();
    }
    //endregion
}
