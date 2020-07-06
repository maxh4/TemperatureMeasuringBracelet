package com.futongware.temperaturemeasuringbracelet;

public class Tagtemperture_DATA {
    int taglen;
    byte[] tagpc;
    byte[] tagepc;
    byte[] tagcrc;
    byte[] temperdata;
    int pvtReadCount;
    int pvtLqi;
    int pvtFrequency;
    int pvtPhase;
    int pvtAntenna;
    byte[] BankData;
    int pvtTsmp;
    int pvtPro;

    public int ReadCount() {
        return this.pvtReadCount;
    }

    public int Lqi() {
        return this.pvtLqi;
    }

    public int Frequency() {
        return this.pvtFrequency;
    }

    public int Phase() {
        return this.pvtPhase;
    }

    public int Antenna() {
        return this.pvtAntenna;
    }

    public int Timestamp() {
        return this.pvtTsmp;
    }

    public int Protocol() {
        return this.pvtPro;
    }

    public byte[] Data() {
        return this.temperdata;
    }

    public byte[] TagEpc() {
        return this.tagepc;
    }

    public Tagtemperture_DATA() {
    }

    public Tagtemperture_DATA(byte[] revddata, int wordCount) {
        this.temperdata = new byte[wordCount * 2];
        int ix = 0;
        int i = ix + 1;
        byte option = revddata[ix];
        int bdalen;
        if ((option & 16) != 0) {
            short metaflagx = (short)(revddata[i++] << 8);
            metaflagx = (short)(metaflagx | revddata[i++]);
            if ((metaflagx & 1) != 0) {
                this.pvtReadCount = revddata[i++];
            }

            if ((metaflagx & 2) != 0) {
                this.pvtLqi = revddata[i++];
            }

            if ((metaflagx & 4) != 0) {
                this.pvtAntenna = revddata[i++] & 15;
                if (this.pvtAntenna == 0) {
                    this.pvtAntenna = 16;
                }
            }

            if ((metaflagx & 8) != 0) {
                this.pvtFrequency = (revddata[i++] & 255) << 16;
                this.pvtFrequency |= (revddata[i++] & 255) << 8;
                this.pvtFrequency |= revddata[i++] & 255;
            }

            if ((metaflagx & 16) != 0) {
                this.pvtTsmp = (revddata[i++] & 255) << 24;
                this.pvtTsmp |= (revddata[i++] & 255) << 16;
                this.pvtTsmp |= (revddata[i++] & 255) << 8;
                this.pvtTsmp |= revddata[i++] & 255;
            }

            if ((metaflagx & 32) != 0) {
                this.pvtPhase = revddata[i + 1];
                i += 2;
            }

            if ((metaflagx & 64) != 0) {
                this.pvtPro = revddata[i++];
            }

            if ((metaflagx & 128) != 0) {
                bdalen = (revddata[i] << 8 | revddata[i + 1]) / 8;
                i += 2;
                if (bdalen != 0) {
                    this.BankData = new byte[bdalen];
                    System.arraycopy(revddata, i, this.BankData, 0, bdalen);
                }

                i += bdalen;
            }
        }

        bdalen = i;
//        System.out.println("bdalen: " + bdalen);
//        System.out.println("i: " + i);
        for(i = i; i < this.temperdata.length + bdalen; ++i) {
            this.temperdata[i - bdalen] = revddata[i];
        }
//        System.out.println("bdalen: " + bdalen);
//        System.out.println("i: " + i);
        this.taglen = revddata[i++];
        this.tagpc = new byte[2];
        this.tagpc[0] = revddata[i++];
        this.tagpc[1] = revddata[i++];
//        System.out.println("taglen: " + this.taglen);
        this.tagepc = new byte[this.taglen - 4];

        for(int j = i; j < this.tagepc.length + i; ++j) {
            this.tagepc[j - i] = revddata[j];
        }

        this.tagcrc = new byte[2];
        this.tagcrc[0] = revddata[i++];
        this.tagcrc[1] = revddata[i++];
    }

    public byte[] Temperdata() {
        return this.temperdata;
    }
}