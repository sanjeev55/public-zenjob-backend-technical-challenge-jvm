package com.zenjob.challenge.exception;

import java.util.UUID;

public class JobAlreadyCanceledException extends RuntimeException {
    public JobAlreadyCanceledException(UUID jobId) {
        super("job with ID " + jobId + " was already canceled");
    }
}
