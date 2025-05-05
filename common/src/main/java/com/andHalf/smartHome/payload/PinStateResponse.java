package com.andHalf.smartHome.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PinStateResponse {

    private PinNo pinNo;
    private String roomName;
    private String deviceType;
    private PinState status;
    private String onTime;
    private Long approxConsumption;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PinStateResponse that = (PinStateResponse) o;
        return pinNo == that.pinNo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pinNo);
    }
}
