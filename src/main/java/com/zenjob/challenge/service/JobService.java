package com.zenjob.challenge.service;

import com.zenjob.challenge.dto.JobRequestDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.exception.JobNotFoundException;
import com.zenjob.challenge.repository.JobRepository;
import com.zenjob.challenge.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@RequiredArgsConstructor
@Service
public class JobService {
    private final JobRepository jobRepository;
    private final ShiftService shiftService;

    @Transactional
    public Job create(final JobRequestDto jobRequestDto) {

        LocalDate startDate = jobRequestDto.getStart();
        LocalDate endDate = jobRequestDto.getEnd();

        validateStartEndDate(startDate, endDate);

        //Creates job entity
        Job job = Job.builder()
                .companyId(UUID.randomUUID())
                .startTime(startDate.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                .endTime(endDate.atTime(16, 0, 0).toInstant(ZoneOffset.UTC))
                .status(Job.Status.CREATED)
                .build();

        //Creates shifts for each day from start to end dates
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        System.out.println("Between days: "+totalDays);
        job.setShifts(LongStream.range(0, totalDays)
                .mapToObj(idx -> startDate.plus(idx, ChronoUnit.DAYS))
                .map(date -> Shift.builder()
                        .job(job)
                        .startTime(date.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                        .endTime(date.atTime(16, 0, 0).toInstant(ZoneOffset.UTC))
                        .status(Shift.Status.CREATED)
                        .build())
                .collect(Collectors.toList()));

        return jobRepository.save(job);
    }

    /**
     * Validates that the start date is not in the past and the end date is not before the start date.
     *
     * @param start The start date to validate.
     * @param end   The end date to validate.
     */
    private void validateStartEndDate(LocalDate start, LocalDate end) {
        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date of a job cannot be in the past!");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date of a job cannot be before start date!");
        }
    }

    /**
     * Cancels a job and its associated shifts if they are not already canceled.
     *
     * @param jobId The ID of the job to cancel.
     */
    @Transactional
    public void cancel(final UUID jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(()->new JobNotFoundException(jobId));

        if(!job.getStatus().equals(Job.Status.CANCELED)){
            job.setStatus(Job.Status.CANCELED);
            jobRepository.save(job);

            //Cancels all the shifts associated with the job
            job.getShifts().forEach(shiftService::cancel);
        }

    }

    public Job fetch(final UUID jobId){
        return jobRepository.findById(jobId).orElseThrow(()->new JobNotFoundException(jobId));
    }


}
