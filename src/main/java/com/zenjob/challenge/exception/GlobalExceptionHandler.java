package com.zenjob.challenge.exception;

import com.zenjob.challenge.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(status.value(), message);
        return new ResponseEntity<>(errorResponseDto, status);
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleJobNotFoundException(JobNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ShiftNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleShiftNotFoundException(ShiftNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(CannotBookShiftException.class)
    public ResponseEntity<ErrorResponseDto> handleShiftAlreadyBookedException(CannotBookShiftException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }


    @ExceptionHandler(ShiftsForTalentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleShiftsForTalentNotFoundException(ShiftsForTalentNotFoundException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(BadRequestException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(LastShiftException.class)
    public ResponseEntity<ErrorResponseDto> handleLastShiftException(LastShiftException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoAvailableShiftException.class)
    public ResponseEntity<ErrorResponseDto> handleNoAvailableShiftException(NoAvailableShiftException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

}
