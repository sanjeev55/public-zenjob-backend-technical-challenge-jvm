package com.zenjob.challenge.dto;

import com.zenjob.challenge.entity.Job;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class JobResponseDto {
    UUID jobId;
    Job.Status status;
}
