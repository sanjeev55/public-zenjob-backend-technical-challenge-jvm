package com.zenjob.challenge.exception;

import java.util.UUID;

public class CannotBookShiftException extends RuntimeException {
    public CannotBookShiftException(UUID shiftId) {
        super("Shift with ID: "+ shiftId+ " cannot be booked");
    }
}
