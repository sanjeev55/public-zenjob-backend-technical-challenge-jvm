package com.zenjob.challenge.exception;

import java.util.UUID;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(UUID jobId) {
        super("Job with Id " + jobId + " not found");
    }
}
