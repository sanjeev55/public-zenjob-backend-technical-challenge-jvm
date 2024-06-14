package com.zenjob.challenge.service;

import com.zenjob.challenge.dto.JobRequestDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.JsonPathAssertions;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {
    @Mock
    private JobRepository jobRepository;

    @Mock
    private ShiftService shiftService;

    @InjectMocks
    private JobService jobService;

    @Test
    public void createJob_success() {
       JobRequestDto jobRequestDto = new JobRequestDto(UUID.randomUUID(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
       Job job = Job.builder()
                .id(UUID.randomUUID())
                .companyId(jobRequestDto.getCompanyId())
                .startTime(jobRequestDto.getStart().atTime(8, 0).toInstant(ZoneOffset.UTC))
                .endTime(jobRequestDto.getEnd().atTime(16, 0).toInstant(ZoneOffset.UTC))
                .status(Job.Status.CREATED)
                .build();

        when(jobRepository.save(any(Job.class))).thenReturn(job);

        final Job createdJob = jobService.create(jobRequestDto);

        assertEquals(createdJob.getId(), job.getId());
        assertEquals(createdJob.getStatus(), Job.Status.CREATED);
        assertEquals(createdJob.getShifts().size(), 3);// Check the number of created shifts

        verify(jobRepository, times(1)).save(any(Job.class));
    }
}
