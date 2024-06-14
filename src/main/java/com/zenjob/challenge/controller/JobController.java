package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.JobRequestDto;
import com.zenjob.challenge.dto.JobResponseDto;
import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.exception.JobNotFoundException;
import com.zenjob.challenge.service.JobService;
import com.zenjob.challenge.util.UUIDValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/v1/job")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<JobResponseDto> create(@RequestBody @Valid JobRequestDto jobRequestDto) {
        final Job job = jobService.create(jobRequestDto);
        log.info("Job created with ID: {}", job.getId());
        return ResponseDto.<JobResponseDto>builder()
                .data(JobResponseDto.builder()
                        .jobId(job.getId())
                        .status(job.getStatus())
                        .build())
                .build();
    }

    @PutMapping(path = "cancel/{jobId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void cancel(@PathVariable("jobId") String jobIdString) {
        final UUID jobId = UUIDValidator.validateUUID(jobIdString);
        try {
            jobService.cancel(jobId);
            log.info("Job with ID: {} canceled successfully", jobId);
        } catch (JobNotFoundException e) {
            log.error("Error while cancelling job with ID: {}: {}",jobId, e.getMessage());
            throw e;
        }
    }

    @GetMapping(path = "/{jobId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<JobResponseDto> fetch(@PathVariable("jobId") String jobIdString) {
        final UUID jobId = UUIDValidator.validateUUID(jobIdString);
        final Job job = jobService.fetch(jobId);
        return ResponseDto.<JobResponseDto>builder()
                .data(JobResponseDto.builder()
                        .jobId(job.getId())
                        .status(job.getStatus())
                        .build())
                .build();
    }
}
