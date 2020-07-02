package com.futongware.temperaturemeasuringbracelet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futongware.temperaturemeasuringbracelet.annotation.MultiRequestBody;
import com.futongware.temperaturemeasuringbracelet.entity.RfidResult;
import com.futongware.temperaturemeasuringbracelet.service.RfidService;
import com.uhf.api.cls.Reader.*;
import io.swagger.annotations.Api;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rfid")
@Api(value = "RFID")
public class RfidController {

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, RfidService> connectedReaderMap = new HashMap<>();

    //region BaseOperation
    @RequestMapping(value = "/{address}/connect", method = RequestMethod.GET)
    @ResponseBody
    public RfidResult connectRfid(@PathVariable(value = "address") String ipAddr, Integer antCnt) {
        if (!connectedReaderMap.containsKey(ipAddr) || connectedReaderMap.get(ipAddr) == null)
            connectedReaderMap.put(ipAddr, new RfidService());

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.connectRfid(ipAddr, antCnt);
        return result;
    }

    /**
     * Disconnect RFID Reader
     * @return
     */
    @RequestMapping(value = "/{address}/disconnect", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult disconnectRfid(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        rfidService.disConnectRfid();
        return new RfidResult();
    }

    @RequestMapping(value = "/{address}/isConnected", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult isConnected(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        Boolean connected = rfidService.isConnected();
        if (connected)
            return new RfidResult();
        else
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);
    }

    @RequestMapping(value = "/{address}/checkAnts", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult checkConnectedAnts(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.checkConnectionAnts();
        return result;
    }

    @RequestMapping(value = "/{address}/startInventory", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult startInventory(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.startInventoy();
        return result;
    }

    @RequestMapping(value = "/{address}/stopInventory", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult stopInventory(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.stopInventoy();
        return result;
    }

    @RequestMapping(value = "/{address}/clearInventory", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult clearInventory(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.clearInventoy();
        return result;
    }
    //endregion

    //region Reeader Parameter Settings
    @RequestMapping(value = "/{address}/readerParam", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getReaderParams(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getParams();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/antPowerConf", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getAntPowerConf(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getAntPowerConf();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/antPowerConf", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setAntPowerConf(@PathVariable(value = "address") String ipAddr, @MultiRequestBody String[] readPowers, @MultiRequestBody String[] writePowers) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setAntPowerConf(readPowers, writePowers);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/ipInfo", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getIpInfo(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getIPInfo();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/ipInfo", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setIpInfo(@PathVariable(value = "address") String ipAddr, @MultiRequestBody @NotNull Map<String, String> ipInfo) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setIpInfo(ipInfo);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/isCheckAnt", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getIsCheckAnt(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getIsChkAnt();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/isCheckAnt", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setIsCheckAnt(@PathVariable(value = "address") String ipAddr, @MultiRequestBody int isCheckAnt) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setIsChkAnt(isCheckAnt);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2MaxEpcLen", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getGen2MaxEpcLen(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getGen2MaxEpcLen();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2MaxEpcLen", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setGen2MaxEpcLen(@PathVariable(value = "address") String ipAddr, @MultiRequestBody @Min(0) int gen2MaxEpcLen) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setGen2MaxEpcLen(gen2MaxEpcLen);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2Session", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getGen2Session(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getGen2Session();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2Session", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setGen2Session(@PathVariable(value = "address") String ipAddr, @MultiRequestBody @Min(0) int gen2Session) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setGen2Session(gen2Session);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2Qval", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getGen2Qval(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getGen2Qval();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2Qval", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setGen2Qval(@PathVariable(value = "address") String ipAddr, @MultiRequestBody @Min(0) int gen2Qval) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setGen2Qval(gen2Qval);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2TagEncoding", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getGen2TagEncoding(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getGen2TagEncoding();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2TagEncoding", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setGen2TagEncoding(@PathVariable(value = "address") String ipAddr, @MultiRequestBody @Min(0) int gen2TagEncoding) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setGen2TagEncoding(gen2TagEncoding);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2WriteMode", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getGen2WriteMode(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getGen2WriteMode();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2WriteMode", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setGen2WriteMode(@PathVariable(value = "address") String ipAddr, @MultiRequestBody @Min(0) int gen2WriteMode) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setGen2WriteMode(gen2WriteMode);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2Target", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getGen2Target(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getGen2Target();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2Target", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setGen2Target(@PathVariable(value = "address") String ipAddr, @MultiRequestBody @Min(0) int gen2Target) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setGen2Target(gen2Target);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2BLF", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getGen2BLF(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getGen2BLF();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2BLF", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setGen2BLF(@PathVariable(value = "address") String ipAddr, @MultiRequestBody @Min(0) int gen2BLF) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setGen2BLF(gen2BLF);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2Tari", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getGen2Tari(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getGen2Tari();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/gen2Tari", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setGen2Tari(@PathVariable(value = "address") String ipAddr, @MultiRequestBody @Min(0) int gen2Tari) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setGen2Tari(gen2Tari);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/uniqueByAnt", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getUniqueByAnt(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getUniqueByAnt();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/uniqueByAnt", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setUniqueByAnt(@PathVariable(value = "address") String ipAddr, @MultiRequestBody @Min(0) int uniqueByAnt) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setUniqueByAnt(uniqueByAnt);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/uniqueByEmdData", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getUniqueByEmdData(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getUniqueByEmdData();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/uniqueByEmdData", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setUniqueByEmdData(@PathVariable(value = "address") String ipAddr, @MultiRequestBody @Min(0) int uniqueByEmdData) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setUniqueByEmdData(uniqueByEmdData);
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/recordHighestRSSI", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult getRecordHighestRSSI(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getRecordHighestRSSI();
        return result;
    }

    @RequestMapping(value = "/{address}/readerParam/recordHighestRSSI", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult setRecordHighestRSSI(@PathVariable(value = "address") String ipAddr, @MultiRequestBody @Min(0) int recordHighestRSSI) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setRecordHighestRSSI(recordHighestRSSI);
        return result;
    }

    //#endregion

    //region TagOperation
    @RequestMapping(value = "/{address}/tagop/filter", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult tagOpGetTagFilter(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.getTagFilter();
        return result;
    }

    @RequestMapping(value = "/{address}/tagop/filter", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult tagOpSetTagFilter(@PathVariable(value = "address") String ipAddr, @MultiRequestBody String data, @MultiRequestBody boolean isNotInvert, @MultiRequestBody int bankStart, int bankId) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.setTagFilter(data, isNotInvert, bankStart, bankId);
        return result;
    }

    @RequestMapping(value = "/{address}/tagop/read", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult tagOpRead(@PathVariable(value = "address") String ipAddr, @MultiRequestBody int antID, @MultiRequestBody int bankID, @MultiRequestBody int bankStart, @MultiRequestBody int blockNum, @MultiRequestBody String pwd, @MultiRequestBody short timeout) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.tagOpRead(antID, bankID, bankStart, blockNum, pwd, timeout);
        return result;
    }

    @RequestMapping(value = "/{address}/tagop/write", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult tagOpWrite(@PathVariable(value = "address") String ipAddr, @MultiRequestBody int antID, @MultiRequestBody int bankID, @MultiRequestBody int bankStart, @MultiRequestBody String pwd, @MultiRequestBody String data, @MultiRequestBody short timeout) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.tagOpWrite(antID, bankID, bankStart, pwd, data, timeout);
        return result;
    }

    @RequestMapping(value = "/{address}/tagop/writeEPC", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult tagOpWriteEPC(@PathVariable(value = "address") String ipAddr, @MultiRequestBody int antID, @MultiRequestBody String pwd, @MultiRequestBody String data, @MultiRequestBody short timeout) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.tagOpWriteEPC(antID, pwd, data, timeout);
        return result;
    }

    @RequestMapping(value = "/{address}/tagop/startRead", method = RequestMethod.POST)
    @ResponseBody
    @SneakyThrows
    public RfidResult tagOpStartRead(@PathVariable(value = "address") String ipAddr, @MultiRequestBody int antID, @MultiRequestBody int bankID, @MultiRequestBody int bankStart, @MultiRequestBody int blockNum, @MultiRequestBody String pwd, @MultiRequestBody short timeout, @MultiRequestBody boolean isUnique) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.tagOpStartRead(antID, bankID, bankStart, blockNum, pwd, timeout, isUnique);
        return result;
    }

    @RequestMapping(value = "/{address}/tagop/stopRead", method = RequestMethod.GET)
    @ResponseBody
    @SneakyThrows
    public RfidResult tagOpStopRead(@PathVariable(value = "address") String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.tagOpStopRead();
        return result;
    }
    //endregion
}
