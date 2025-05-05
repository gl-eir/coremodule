package com.gl.ceir.flowManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExceptionListKey {
    private String imei;

    private String imsi;

    private String msisdn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExceptionListKey that = (ExceptionListKey) o;
        return Objects.equals(imei, that.imei) && Objects.equals(imsi, that.imsi) && Objects.equals(msisdn, that.msisdn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imei, imsi, msisdn);
    }
}
