package com.zenjob.challenge.exception;

import java.util.UUID;

public class ShiftAlreadyBookedException extends RuntimeException {
    public ShiftAlreadyBookedException(UUID shiftId) {
        super("Shift with ID: "+ shiftId+ " is already booked");
    }
}
