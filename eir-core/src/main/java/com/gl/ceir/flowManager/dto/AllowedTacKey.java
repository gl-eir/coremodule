package com.gl.ceir.flowManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllowedTacKey {

    private String tac;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllowedTacKey that = (AllowedTacKey) o;
        return Objects.equals(tac, that.tac);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tac);
    }
}
