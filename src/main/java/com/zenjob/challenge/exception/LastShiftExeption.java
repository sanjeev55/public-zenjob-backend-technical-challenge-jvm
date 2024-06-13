package com.zenjob.challenge.exception;

import java.util.UUID;

public class LastShiftExeption extends RuntimeException {
    public LastShiftExeption(UUID shiftId, UUID jobId) {
        super("Cannot cancel the last available shift (ID: " + shiftId + ") for job with ID: " + jobId);
    }
}
