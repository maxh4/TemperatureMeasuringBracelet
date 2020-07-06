package com.futongware.temperaturemeasuringbracelet.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.futongware.temperaturemeasuringbracelet.util.RfidUtil;
import com.uhf.api.cls.Reader;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Arrays;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagInfo {
    @JsonProperty()
    private Byte antennaId;
    @JsonProperty
    private Integer frequency;
    private Integer timestamp;
    @JsonProperty
    private String embededDataStr;
    @JsonIgnore
    private Short embededDataLen;
    @JsonIgnore
    private byte[] embededData = null;
    @JsonIgnore
    private byte[] res = new byte[2];
    @JsonIgnore
    private byte[] pc = new byte[2];
    @JsonIgnore
    private byte[] crc = new byte[2];
    @JsonProperty
    private String epcIdStr;
    @JsonIgnore
    private Short epcLen;
    @JsonIgnore
    private byte[] epcId = null;
    @JsonProperty
    private Integer phase;
    @JsonProperty
    private TagProtocol protocol;
    @JsonProperty
    private Integer readCnt;
    @JsonProperty
    private Integer rssi;

    public TagInfo() {}
    @SneakyThrows
    public TagInfo(Reader.TAGINFO taginfo) {
        this.antennaId = taginfo.AntennaID;
        this.frequency = taginfo.Frequency;
        this.timestamp = taginfo.TimeStamp;
        this.embededDataLen = taginfo.EmbededDatalen;
        this.embededData = taginfo.EmbededData;
        this.res = taginfo.Res;
        this.epcLen = taginfo.Epclen;
        this.pc = taginfo.PC;
        this.crc = taginfo.CRC;
        this.epcId = taginfo.EpcId;
        this.phase = taginfo.Phase;
        this.protocol = TagProtocol.fromProtocol(taginfo.protocol);
        this.readCnt = taginfo.ReadCnt;
        this.rssi = taginfo.RSSI;
    }

    @JsonGetter("embededDataStr")
    public String getEmbededDataStr() {
        return RfidUtil.convertToString(embededData, embededDataLen);
    }
    @JsonGetter("epcIdStr")
    public String getEpcIdStr() {
        return RfidUtil.convertToString(epcId, epcLen);
    }
}
