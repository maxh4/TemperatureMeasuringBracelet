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

@RestController
@RequestMapping("/rfid")
public class RfidController {
    @Resource
    private RfidService rfidService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/connect")
    @ResponseBody
    @SneakyThrows
    public String connectRfid(String ipAddr, Integer antCnt, Boolean connected, HttpSession session) {
        if (connected) {
            return objectMapper.writeValueAsString(new RfidResult().setData("connected"));
        }
        RfidResult result = rfidService.connectRfid(ipAddr, antCnt);
        if (result.getErr() == READER_ERR.MT_OK_ERR) {
            session.setAttribute("connectionStat", true);
            session.setAttribute("error", null);
        } else
            session.setAttribute("error", result.getErr().toString());

        return objectMapper.writeValueAsString(result);
    }

    /**
     * Disconnect RFID Reader
     * @param session
     * @return
     */
    @RequestMapping("/disconnect")
    public String disconnectRfid(HttpSession session) {
        if (session.getAttribute("connectionStat") != null)
            rfidService.disConnectRfid();
        session.removeAttribute("connectionStat");
        return "redirect:/assets/rfid/rfidconf";
    }

    @RequestMapping("/startInventory")
    @ResponseBody
    @SneakyThrows
    public String startInventory() {
        RfidResult result = rfidService.startInventoy();
        return objectMapper.writeValueAsString(result);
    }

    @RequestMapping("/stopInventory")
    @ResponseBody
    @SneakyThrows
    public String stopInventory() {
        RfidResult result = rfidService.stopInventoy();
        return objectMapper.writeValueAsString(result);
    }

    @RequestMapping("/clearInventory")
    @ResponseBody
    @SneakyThrows
    public String clearInventory() {
        RfidResult result = rfidService.clearInventoy();
        return objectMapper.writeValueAsString(result);
    }

    @RequestMapping("/tagop/read")
    @ResponseBody
    @SneakyThrows
    public String tagOpRead(int antID, int bankID, int bankStart, int blockNum, String pwd, short timeout) {
        RfidResult result = rfidService.tagOpRead(antID, bankID, bankStart, blockNum, pwd, timeout);
        return objectMapper.writeValueAsString(result);
    }

    @RequestMapping("/tagop/write")
    @ResponseBody
    @SneakyThrows
    public String tagOpWrite(int antID, int bankID, int bankStart, String pwd, String data, short timeout) {
        RfidResult result = rfidService.tagOpWrite(antID, bankID, bankStart, pwd, data, timeout);
        return objectMapper.writeValueAsString(result);
    }

    @RequestMapping("/tagop/writeEPC")
    @ResponseBody
    @SneakyThrows
    public String tagOpWriteEPC(int antID, String pwd, String data, short timeout) {
        RfidResult result = rfidService.tagOpWriteEPC(antID, pwd, data, timeout);
        return objectMapper.writeValueAsString(result);
    }

    @RequestMapping("/tagop/startRead")
    @ResponseBody
    @SneakyThrows
    public String tagOpStartRead(int antID, int bankID, int bankStart, int blockNum, String pwd, short timeout, boolean isUnique) {
        RfidResult result = rfidService.tagOpStartRead(antID, bankID, bankStart, blockNum, pwd, timeout, isUnique);
        return objectMapper.writeValueAsString(result);
    }

    @RequestMapping("/tagop/stopRead")
    @ResponseBody
    @SneakyThrows
    public String tagOpStopRead() {
        RfidResult result = rfidService.tagOpStopRead();
        return objectMapper.writeValueAsString(result);
    }
}
