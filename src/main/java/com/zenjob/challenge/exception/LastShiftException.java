package com.zenjob.challenge.exception;

import com.zenjob.challenge.entity.Shift;

import java.util.UUID;

public class LastShiftException extends RuntimeException {
    public LastShiftException(Shift shift) {
        super("Cannot cancel the last available shift (ID: " + shift.getId() + ") for job with ID: " + shift.getJob().getId());
    }
}
