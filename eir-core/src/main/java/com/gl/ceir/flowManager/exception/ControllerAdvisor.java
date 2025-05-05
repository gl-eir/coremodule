package com.gl.ceir.flowManager.exception;

import com.gl.ceir.flowManager.dto.EquipmentStatusResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<EquipmentStatusResponseDto> handleDataNotFoundException() {

        return new ResponseEntity<>(EquipmentStatusResponseDto.builder().build(), HttpStatus.NOT_FOUND);
    }
}
