package com.zenjob.challenge.exception;

import com.zenjob.challenge.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.ElementCollection;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> jobNotFoundException(JobNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JobAlreadyCanceledException.class)
    public ResponseEntity<ErrorResponseDto> jobAlreadyCanceledException(JobAlreadyCanceledException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShiftNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> shiftNotFoundException(ShiftNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ShiftAlreadyBookedException.class)
    public ResponseEntity<ErrorResponseDto> shiftAlreadyBookedException(ShiftAlreadyBookedException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShiftAlreadyCanceledException.class)
    public ResponseEntity<ErrorResponseDto> shiftAlreadyCanceledException(ShiftAlreadyCanceledException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShiftsForTalentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> shiftsForTalentNotFoundException(ShiftsForTalentNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> badRequestException(BadRequestException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LastShiftExeption.class)
    public ResponseEntity<ErrorResponseDto> lastShiftException(LastShiftExeption e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoAvailableShiftException.class)
    public ResponseEntity<ErrorResponseDto> noAvailableShiftException(NoAvailableShiftException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }
}
