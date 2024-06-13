package com.zenjob.challenge.util;

import com.zenjob.challenge.exception.BadRequestException;

import java.util.UUID;

public class UUIDValidator {
    public static UUID validateUUID(String uuidString){
        try {
            return UUID.fromString(uuidString);
        }catch (IllegalArgumentException  e){
            throw new BadRequestException(e.getMessage());
        }
    }
}
