package com.gl.ceir.flowManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlackListKey {
    private String imei;

    private String imsi;

    private String msisdn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlackListKey that = (BlackListKey) o;
        return Objects.equals(imei, that.imei) && Objects.equals(imsi, that.imsi) && Objects.equals(msisdn, that.msisdn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imei, imsi, msisdn);
    }
}
