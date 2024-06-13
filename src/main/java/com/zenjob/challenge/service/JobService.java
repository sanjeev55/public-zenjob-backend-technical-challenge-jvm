package com.zenjob.challenge.service;

import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.enums.JobStatusEnum;
import com.zenjob.challenge.enums.ShiftStatusEnum;
import com.zenjob.challenge.exception.JobNotFoundException;
import com.zenjob.challenge.repository.JobRepository;
import com.zenjob.challenge.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@RequiredArgsConstructor
@Service
public class JobService {
    @Autowired
    private final JobRepository   jobRepository;

    @Autowired
    private final ShiftRepository shiftRepository;
    @Autowired
    private ShiftService shiftService;

    @Transactional
    public Job createJob(LocalDate start, LocalDate end) {

        validateStartEndDate(start, end);

        //Creates job entity
        Job job = Job.builder()
                .companyId(UUID.randomUUID())
                .startTime(start.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                .endTime(end.atTime(16, 0, 0).toInstant(ZoneOffset.UTC))
                .jobStatus(JobStatusEnum.CREATED)
                .build();

        //Creates shifts for each day between start and end dates
        job.setShifts(LongStream.range(0, ChronoUnit.DAYS.between(start, end))
                .mapToObj(idx -> start.plus(idx, ChronoUnit.DAYS))
                .map(date -> Shift.builder()
                        .job(job)
                        .startTime(date.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                        .endTime(date.atTime(16, 0, 0).toInstant(ZoneOffset.UTC))
                        .shiftStatus(ShiftStatusEnum.CREATED)
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
    public void cancelJob(UUID jobId){

        Job job = jobRepository.findById(jobId)
                .orElseThrow(()->new JobNotFoundException(jobId));

        if(!job.getJobStatus().equals(JobStatusEnum.CANCELED)){
            job.setJobStatus(JobStatusEnum.CANCELED);
            jobRepository.save(job);

            //Cancels all the shifts associated with the job
            shiftRepository.findAllByJobId(jobId).stream()
                    .filter(shift -> !shift.getShiftStatus().equals(ShiftStatusEnum.CANCELED))
                    .forEach(shift -> shiftService.cancelShift(shift.getId()));
        }else{
            throw new JobNotFoundException(jobId);
        }

    }

    public Job getJob(UUID jobId){
        return jobRepository.findById(jobId).orElseThrow(()->new JobNotFoundException(jobId));
    }


}
