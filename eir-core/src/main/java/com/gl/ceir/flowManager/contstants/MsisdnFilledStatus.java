package com.gl.ceir.flowManager.contstants;

public enum MsisdnFilledStatus {
    UNKNOWN("UNKNOWN", 0),
    FILLED("Already present in request", 1),
    MISSING("Not received in request", 2),
    FILLED_FROM_HLR("filled from HLR Dump", 3),
    NOT_FOUND_IN_HLR("checked in HLR Dump,but not found", 4);

    private String description;
    private Integer code;

    private MsisdnFilledStatus(String description, Integer code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getCode() {
        return this.code;
    }
}
