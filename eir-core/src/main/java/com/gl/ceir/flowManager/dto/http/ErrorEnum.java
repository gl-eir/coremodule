package com.gl.ceir.flowManager.dto.http;

public enum ErrorEnum {
    OK(0), WARN(1), CRITICAL(2), ERROR(3);

    private int value;

    private ErrorEnum(int value) {
        this.value = value;
    }

    private Integer intValue() {
        return value;
    }
}
