package com.zenjob.challenge.service;


import com.zenjob.challenge.dto.JobRequestDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.exception.JobNotFoundException;
import com.zenjob.challenge.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class JobServiceTest {
    @Mock
    private JobRepository jobRepository;

    @Mock
    private ShiftService shiftService;

    @InjectMocks
    private JobService jobService;

    public JobServiceTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void create_success() {

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        JobRequestDto jobRequestDto = new JobRequestDto(UUID.randomUUID(), startDate, endDate);

        Job jobWithoutShifts = Job.builder()
                .id(UUID.randomUUID())
                .companyId(jobRequestDto.getCompanyId())
                .startTime(startDate.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                .endTime(endDate.atTime(16, 0, 0).toInstant(ZoneOffset.UTC))
                .status(Job.Status.CREATED)
                .build();

        List<Shift> shifts = LongStream.range(0, ChronoUnit.DAYS.between(startDate, endDate) + 1)
                .mapToObj(idx -> Shift.builder()
                        .job(jobWithoutShifts)
                        .startTime(startDate.plusDays(idx).atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                        .endTime(startDate.plusDays(idx).atTime(16, 0, 0).toInstant(ZoneOffset.UTC))
                        .status(Shift.Status.CREATED)
                        .build())
                .collect(Collectors.toList());

        jobWithoutShifts.setShifts(shifts);

        when(jobRepository.save(any(Job.class))).thenReturn(jobWithoutShifts);


        Job createdJob = jobService.create(jobRequestDto);

        assertThat(createdJob).isNotNull();
        assertThat(createdJob.getId()).isEqualTo(jobWithoutShifts.getId());
        assertThat(createdJob.getShifts()).hasSize(3);
        verify(jobRepository, times(1)).save(any(Job.class));
    }


    @Test
    public void create_startDateInPastError() {

        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        JobRequestDto jobRequestDto = new JobRequestDto(UUID.randomUUID(), startDate, endDate);


        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            jobService.create(jobRequestDto);
        });

        assertThat(thrown.getMessage()).isEqualTo("Start date of a job cannot be in the past!");
        verify(jobRepository, times(0)).save(any(Job.class));
    }

    @Test
    public void create_endDateBeforeStartDateError() {

        LocalDate startDate = LocalDate.now().plusDays(3);
        LocalDate endDate = LocalDate.now().plusDays(1);
        JobRequestDto jobRequestDto = new JobRequestDto(UUID.randomUUID(), startDate, endDate);


        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            jobService.create(jobRequestDto);
        });

        assertThat(thrown.getMessage()).isEqualTo("End date of a job cannot be before start date!");
        verify(jobRepository, times(0)).save(any(Job.class));
    }

    @Test
    public void cancel_success() {
        UUID jobId = UUID.randomUUID();
        List<Shift> shifts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Shift shift = new Shift();
            shift.setId(UUID.randomUUID());
            shift.setStatus(Shift.Status.CREATED);
            shifts.add(shift);
        }

        Job job = Job.builder()
                .id(jobId)
                .companyId(UUID.randomUUID())
                .status(Job.Status.CREATED)
                .shifts(shifts)
                .build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        jobService.cancel(jobId);

        verify(jobRepository, times(1)).save(job);
        verify(shiftService, times(3)).cancel(any(Shift.class));
        assertThat(job.getStatus()).isEqualTo(Job.Status.CANCELED);
    }

    @Test
    public void cancel_jobNotFoundError() {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(JobNotFoundException.class, () -> {
            jobService.cancel(jobId);
        });

        verify(jobRepository, times(0)).save(any(Job.class));
        verify(shiftService, times(0)).cancel(any(Shift.class));
    }

    @Test
    public void cancel_alreadyCanceled() {
        UUID jobId = UUID.randomUUID();
        List<Shift> shifts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Shift shift = new Shift();
            shift.setId(UUID.randomUUID());
            shift.setStatus(Shift.Status.CREATED);
            shifts.add(shift);
        }

        Job job = Job.builder()
                .id(jobId)
                .companyId(UUID.randomUUID())
                .status(Job.Status.CANCELED)
                .shifts(shifts)
                .build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        jobService.cancel(jobId);

        verify(jobRepository, times(0)).save(job);
        verify(shiftService, times(0)).cancel(any(Shift.class));
    }

    @Test
    public void fetch_success() {
        UUID jobId = UUID.randomUUID();
        Job job = Job.builder()
                .id(jobId)
                .companyId(UUID.randomUUID())
                .status(Job.Status.CREATED)
                .build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        Job fetchedJob = jobService.fetch(jobId);

        assertThat(fetchedJob).isNotNull();
        assertThat(fetchedJob.getId()).isEqualTo(jobId);
        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    public void fetch_jobNotFoundError() {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(JobNotFoundException.class, () -> {
            jobService.fetch(jobId);
        });

        verify(jobRepository, times(1)).findById(jobId);
    }





}
