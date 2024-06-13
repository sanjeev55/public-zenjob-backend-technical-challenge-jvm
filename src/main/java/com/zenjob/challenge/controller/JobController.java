package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.JobRequestDto;
import com.zenjob.challenge.dto.JobResponseDto;
import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.exception.BadRequestException;
import com.zenjob.challenge.exception.JobAlreadyCanceledException;
import com.zenjob.challenge.exception.JobNotFoundException;
import com.zenjob.challenge.service.JobService;
import com.zenjob.challenge.util.UUIDValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping(path = "/job")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    @PostMapping
    @ResponseBody
    public ResponseDto<JobResponseDto> requestJob(@RequestBody @Valid JobRequestDto dto) {
        Job job = jobService.createJob(dto.getStart(), dto.getEnd());
        logger.info("Job created with ID: {}", job.getId());
        return ResponseDto.<JobResponseDto>builder()
                .data(JobResponseDto.builder()
                        .jobId(job.getId())
                        .build())
                .build();
    }

    @PatchMapping(path = "/{jobId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void cancelJob(@PathVariable("jobId") String jobIdString) {
        UUID jobId = UUIDValidator.validateUUID(jobIdString);
        try {
            jobService.cancelJob(jobId);
            logger.info("Job with ID: {} canceled successfully", jobId);
        } catch (JobNotFoundException | JobAlreadyCanceledException e) {
            logger.error("Error while cancelling job with ID: {}: {}",jobId, e.getMessage());
        }
    }

    @GetMapping(path = "/{jobId}")
    public ResponseDto<JobResponseDto> getJob(@PathVariable("jobId") String jobIdString) {
        UUID jobId = UUIDValidator.validateUUID(jobIdString);
        Job job = jobService.getJob(jobId);
        return ResponseDto.<JobResponseDto>builder()
                .data(JobResponseDto.builder()
                        .jobId(job.getId())
                        .build())
                .build();
    }
}
