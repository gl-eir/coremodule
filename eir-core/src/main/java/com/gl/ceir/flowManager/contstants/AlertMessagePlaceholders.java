package com.gl.ceir.flowManager.contstants;

public enum AlertMessagePlaceholders {
    TID("<TID>"), IMEI("<IMEI>"), QUEUE_SIZE("<QUEUE_SIZE>"), REASON_CODE("<REASON_CODE>"), URL("<URL>"), EXCEPTION("<EXCEPTION>");

    String placeholder;

    AlertMessagePlaceholders(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }
}
