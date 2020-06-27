package com.futongware.temperaturemeasuringbracelet.entity;

import com.uhf.api.cls.Reader.*;

public class RfidResult {
    private READER_ERR err = READER_ERR.MT_OK_ERR;
    private Object data = null;
    public READER_ERR getErr() {
        return err;
    }
    public RfidResult setErr(READER_ERR err) {
        this.err = err;
        return this;
    }
    public Object getData() {
        return data;
    }
    public RfidResult setData(Object data) {
        this.data = data;
        return this;
    }
}
