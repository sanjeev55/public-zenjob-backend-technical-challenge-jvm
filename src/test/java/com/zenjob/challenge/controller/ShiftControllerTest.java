package com.zenjob.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenjob.challenge.dto.BookTalentRequestDto;
import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.dto.ShiftDto;
import com.zenjob.challenge.dto.ShiftsResponseDto;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.exception.JobNotFoundException;
import com.zenjob.challenge.exception.ShiftNotFoundException;
import com.zenjob.challenge.exception.ShiftsForTalentNotFoundException;
import com.zenjob.challenge.service.ShiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShiftController.class)
public class ShiftControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShiftService shiftService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void fetchByJobId_success() throws Exception {
        UUID jobId = UUID.randomUUID();

        List<ShiftDto> shifts = Arrays.asList(
                ShiftDto.builder()
                        .id(UUID.randomUUID())
                        .talentId(UUID.randomUUID())
                        .jobId(jobId)
                        .start(LocalDate.now().plusDays(1).atTime(8, 0).toInstant(ZoneOffset.UTC))
                        .end(LocalDate.now().plusDays(1).atTime(16, 0).toInstant(ZoneOffset.UTC))
                        .status(Shift.Status.CREATED)
                        .build(),
                ShiftDto.builder()
                        .id(UUID.randomUUID())
                        .talentId(UUID.randomUUID())
                        .jobId(jobId)
                        .start(LocalDate.now().plusDays(2).atTime(8, 0).toInstant(ZoneOffset.UTC))
                        .end(LocalDate.now().plusDays(2).atTime(16, 0).toInstant(ZoneOffset.UTC))
                        .status(Shift.Status.CREATED)
                        .build()
        );

        when(shiftService.fetchByJobId(jobId)).thenReturn(shifts);

        mockMvc.perform(get("/v1/shift/{jobId}", jobId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.shifts[0].jobId").value(shifts.get(0).getJobId().toString()))
                .andExpect(jsonPath("$.data.shifts[0].status").value(shifts.get(0).getStatus().toString()));
    }

    @Test
    public void fetchByJobId_notFound() throws Exception {
        UUID jobId = UUID.randomUUID();
        doThrow(new JobNotFoundException(jobId)).when(shiftService).fetchByJobId(jobId);

        mockMvc.perform(get("/v1/job/{jobId}", jobId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void book_success() throws Exception {
        UUID shiftId = UUID.randomUUID();
        BookTalentRequestDto bookTalentRequestDto = new BookTalentRequestDto(UUID.randomUUID());
        mockMvc.perform(put("/v1/shift/book/{shiftId}", shiftId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookTalentRequestDto)))
                .andExpect(status().isAccepted());
    }

    @Test
    public void book_notFound() throws Exception {
        UUID shiftId = UUID.randomUUID();
        BookTalentRequestDto bookTalentRequestDto = new BookTalentRequestDto(UUID.randomUUID());

        doThrow(new ShiftNotFoundException(shiftId)).when(shiftService).book(shiftId, bookTalentRequestDto.getTalent());

        mockMvc.perform(put("/v1/shift/book/{shiftId}", shiftId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookTalentRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void cancelForTalent_success() throws Exception {
        UUID talentId = UUID.randomUUID();
        mockMvc.perform(put("/v1/shift/talent/{talentId}", talentId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    public void cancelForTalent_shiftForTalentNotFound() throws Exception {
        UUID talentId = UUID.randomUUID();
        doThrow(new ShiftsForTalentNotFoundException(talentId)).when(shiftService).cancelShiftForTalent(talentId);

        mockMvc.perform(put("/v1/shift/talent/{talentId}", talentId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void cancelForTalent_shiftsNotFound() throws Exception {
        UUID talentId = UUID.randomUUID();
        doThrow(new ShiftNotFoundException(talentId)).when(shiftService).cancelShiftForTalent(talentId);

        mockMvc.perform(put("/v1/shift/talent/{talentId}", talentId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void cancel_success() throws Exception {
        UUID shiftId = UUID.randomUUID();
        mockMvc.perform(put("/v1/shift/cancel/{shiftId}", shiftId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    public void cancel_notFound() throws Exception {
        UUID shiftId = UUID.randomUUID();
        doThrow(new ShiftNotFoundException(shiftId)).when(shiftService).cancel(shiftId);

        mockMvc.perform(put("/v1/shift/cancel/{shiftId}", shiftId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


}
