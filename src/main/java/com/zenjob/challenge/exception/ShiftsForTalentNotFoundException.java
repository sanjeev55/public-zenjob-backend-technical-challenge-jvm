package com.zenjob.challenge.exception;

import java.util.UUID;

public class ShiftsForTalentNotFoundException extends RuntimeException {
    public ShiftsForTalentNotFoundException(UUID talentId) {
        super("Shift for talent with Id: " +talentId+ " not found");
    }
}
