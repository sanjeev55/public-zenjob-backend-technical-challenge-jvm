package com.zenjob.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenjob.challenge.dto.JobRequestDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.exception.JobNotFoundException;
import com.zenjob.challenge.service.JobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobController.class)
public class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void create_success() throws Exception {
        JobRequestDto jobRequestDto = new JobRequestDto(UUID.randomUUID(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        Job job = Job.builder()
                .id(UUID.randomUUID())
                .companyId(jobRequestDto.getCompanyId())
                .startTime(jobRequestDto.getStart().atTime(8, 0).toInstant(ZoneOffset.UTC))
                .endTime(jobRequestDto.getEnd().atTime(16, 0).toInstant(ZoneOffset.UTC))
                .status(Job.Status.CREATED)
                .build();

        when(jobService.create(any(JobRequestDto.class))).thenReturn(job);

        mockMvc.perform(post("/v1/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.jobId").value(job.getId().toString()))
                .andExpect(jsonPath("$.data.status").value(job.getStatus().toString()));
    }

    @Test
    public void create_sameStartEndDate() throws Exception {
        JobRequestDto jobRequestDto = new JobRequestDto(UUID.randomUUID(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(1));

        Job job = Job.builder()
                .id(UUID.randomUUID())
                .companyId(jobRequestDto.getCompanyId())
                .startTime(jobRequestDto.getStart().atTime(8, 0).toInstant(ZoneOffset.UTC))
                .endTime(jobRequestDto.getEnd().atTime(16, 0).toInstant(ZoneOffset.UTC))
                .status(Job.Status.CREATED)
                .build();

        when(jobService.create(any(JobRequestDto.class))).thenReturn(job);

        mockMvc.perform(post("/v1/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.jobId").value(job.getId().toString()))
                .andExpect(jsonPath("$.data.status").value(job.getStatus().toString()));
    }

    @Test
    public void cancel_success() throws Exception {
        UUID jobId = UUID.randomUUID();
        mockMvc.perform(put("/v1/job/cancel/{jobId}", jobId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    public void cancel_jobNotFound() throws Exception {
        UUID jobId = UUID.randomUUID();

        doThrow(new JobNotFoundException(jobId)).when(jobService).cancel(jobId);

        mockMvc.perform(put("/v1/job/cancel/{jobId}", jobId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancel_invalidIdRequested() throws Exception {
        mockMvc.perform(get("/v1/job/invalid"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void fetch_success() throws Exception {
        UUID jobId = UUID.randomUUID();
        Job job = Job.builder()
                .id(jobId)
                .companyId(UUID.randomUUID())
                .startTime(LocalDate.now().plusDays(1).atTime(8, 0).toInstant(ZoneOffset.UTC))
                .endTime(LocalDate.now().plusDays(2).atTime(16, 0).toInstant(ZoneOffset.UTC))
                .status(Job.Status.CREATED)
                .build();

        when(jobService.fetch(jobId)).thenReturn(job);

        mockMvc.perform(get("/v1/job/{jobId}", jobId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jobId").value(job.getId().toString()))
                .andExpect(jsonPath("$.data.status").value(job.getStatus().toString()));
    }

    @Test
    public void fetch_jobNotFound() throws Exception{
        UUID jobId = UUID.randomUUID();
        doThrow(new JobNotFoundException(jobId)).when(jobService).fetch(jobId);

        mockMvc.perform(get("/v1/job/{jobId}", jobId.toString()))
                .andExpect(status().isNotFound());
    }
}
