package com.zenjob.challenge.exception;

import java.util.UUID;

public class ShiftAlreadyCanceledException extends RuntimeException {
    public ShiftAlreadyCanceledException(UUID shiftID) {
        super("Shift with ID: "+ shiftID+ " was already canceled");
    }
}
