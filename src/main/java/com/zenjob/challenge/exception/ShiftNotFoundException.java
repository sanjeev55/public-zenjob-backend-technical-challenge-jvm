package com.zenjob.challenge.exception;

import java.util.UUID;

public class ShiftNotFoundException extends RuntimeException {
    public ShiftNotFoundException(UUID shiftId) {
        super("Shift with Id: " + shiftId+ " not found");
    }
}
