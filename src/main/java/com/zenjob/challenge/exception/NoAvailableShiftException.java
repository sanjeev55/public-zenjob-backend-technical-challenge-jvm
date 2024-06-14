package com.zenjob.challenge.exception;

import java.util.UUID;

public class NoAvailableShiftException extends RuntimeException {
    public NoAvailableShiftException(UUID talentId) {
        super("All the shifts for talent " + talentId + "is already canceled");
    }
}
