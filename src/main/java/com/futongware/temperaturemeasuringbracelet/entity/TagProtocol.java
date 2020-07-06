package com.futongware.temperaturemeasuringbracelet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.uhf.api.cls.Reader;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

public enum TagProtocol {
    GEN2(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_GEN2, "gen2"),
    IPX256(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_IPX256, "ipx256"),
    IPX64(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_IPX64, "ipx64"),
    ISO180006b(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_ISO180006B, "iso180006b"),
    ISO180006b_UCODE(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_ISO180006B_UCODE, "iso180006b_ucode"),
    NONE(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_NONE, "none");

    @Getter @Setter @JsonIgnore
    private Reader.SL_TagProtocol protocol;
    @Getter @Setter @JsonValue
    private String name;

    private TagProtocol(Reader.SL_TagProtocol protocol, String name) {
        this.protocol = protocol;
        this.name = name;
    }

    public static TagProtocol fromName(String name) throws NoSuchFieldException, IllegalAccessException {
        Class<TagProtocol> enumClazz = TagProtocol.class;
        TagProtocol[] tagProtocols = enumClazz.getEnumConstants();
        for(TagProtocol tagProtocol : tagProtocols) {
            Field nameField = tagProtocol.getClass().getDeclaredField("name");
            nameField.setAccessible(true); // if it is private for example.
            if (nameField.get(tagProtocol).equals(name)) {
                return tagProtocol;
            }
        }
        return null;
    }
    public static TagProtocol fromProtocol(Reader.SL_TagProtocol protocol) throws NoSuchFieldException, IllegalAccessException {
        Class<TagProtocol> enumClazz = TagProtocol.class;
        TagProtocol[] tagProtocols = enumClazz.getEnumConstants();
        for(TagProtocol tagProtocol : tagProtocols) {
            Field protocolField = tagProtocol.getClass().getDeclaredField("protocol");
            protocolField.setAccessible(true); // if it is private for example.
            if (protocolField.get(tagProtocol).equals(protocol)) {
                return tagProtocol;
            }
        }
        return null;
    }
    public static Reader.SL_TagProtocol getProtocolByName(String name) throws NoSuchFieldException, IllegalAccessException {
        TagProtocol tagProtocol = fromName(name);
        return tagProtocol == null ? null : tagProtocol.getProtocol();
    }
    public static String getNameByProtocol(Reader.SL_TagProtocol protocol) throws NoSuchFieldException, IllegalAccessException {
        TagProtocol tagProtocol = fromProtocol(protocol);
        return tagProtocol == null ? null : tagProtocol.getName();
    }
}


