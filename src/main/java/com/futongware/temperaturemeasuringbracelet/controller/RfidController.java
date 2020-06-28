package com.futongware.temperaturemeasuringbracelet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.futongware.temperaturemeasuringbracelet.entity.RfidResult;
import com.futongware.temperaturemeasuringbracelet.service.RfidService;
import com.uhf.api.cls.Reader.*;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rfid")
public class RfidController {

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, RfidService> connectedReaderMap = new HashMap<>();

    //region BaseOperation
    @RequestMapping("/connect")
    @ResponseBody
    @SneakyThrows
    public RfidResult connectRfid(String ipAddr, Integer antCnt) {
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
    @RequestMapping("/disconnect")
    @ResponseBody
    @SneakyThrows
    public RfidResult disconnectRfid(String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        rfidService.disConnectRfid();
        return new RfidResult();
    }

    @RequestMapping("/startInventory")
    @ResponseBody
    @SneakyThrows
    public RfidResult startInventory(String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.startInventoy();
        return result;
    }

    @RequestMapping("/stopInventory")
    @ResponseBody
    @SneakyThrows
    public RfidResult stopInventory(String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.stopInventoy();
        return result;
    }

    @RequestMapping("/clearInventory")
    @ResponseBody
    @SneakyThrows
    public RfidResult clearInventory(String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.clearInventoy();
        return result;
    }
    //endregion

    //region Reeader Parameter Settings

    //#endregion

    //region TagOperation
    @RequestMapping("/tagop/read")
    @ResponseBody
    @SneakyThrows
    public RfidResult tagOpRead(String ipAddr, int antID, int bankID, int bankStart, int blockNum, String pwd, short timeout) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.tagOpRead(antID, bankID, bankStart, blockNum, pwd, timeout);
        return result;
    }

    @RequestMapping("/tagop/write")
    @ResponseBody
    @SneakyThrows
    public RfidResult tagOpWrite(String ipAddr, int antID, int bankID, int bankStart, String pwd, String data, short timeout) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.tagOpWrite(antID, bankID, bankStart, pwd, data, timeout);
        return result;
    }

    @RequestMapping("/tagop/writeEPC")
    @ResponseBody
    @SneakyThrows
    public RfidResult tagOpWriteEPC(String ipAddr, int antID, String pwd, String data, short timeout) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.tagOpWriteEPC(antID, pwd, data, timeout);
        return result;
    }

    @RequestMapping("/tagop/startRead")
    @ResponseBody
    @SneakyThrows
    public RfidResult tagOpStartRead(String ipAddr, int antID, int bankID, int bankStart, int blockNum, String pwd, short timeout, boolean isUnique) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.tagOpStartRead(antID, bankID, bankStart, blockNum, pwd, timeout, isUnique);
        return result;
    }

    @RequestMapping("/tagop/stopRead")
    @ResponseBody
    @SneakyThrows
    public RfidResult tagOpStopRead(String ipAddr) {
        if (!connectedReaderMap.containsKey(ipAddr))
            return new RfidResult().setErr(READER_ERR.MT_CMD_FAILED_ERR);

        RfidService rfidService = connectedReaderMap.get(ipAddr);
        RfidResult result = rfidService.tagOpStopRead();
        return result;
    }
    //endregion
}
