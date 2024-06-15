package com.zenjob.challenge.service;

import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.exception.*;
import com.zenjob.challenge.repository.ShiftRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ShiftServiceTest {
    @Mock
    private ShiftRepository shiftRepository;

    @InjectMocks
    private ShiftService shiftService;

    public ShiftServiceTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void book_success() {

        UUID shiftId = UUID.randomUUID();
        UUID talentId = UUID.randomUUID();
        Shift shift = Shift.builder()
                .id(shiftId)
                .status(Shift.Status.CREATED)
                .build();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));


        shiftService.book(shiftId, talentId);


        assertThat(shift.getTalentId()).isEqualTo(talentId);
        assertThat(shift.getStatus()).isEqualTo(Shift.Status.BOOKED);
        verify(shiftRepository, times(1)).save(shift);
    }

    @Test
    public void book_shiftNotFoundError() {
        UUID shiftId = UUID.randomUUID();
        UUID talentId = UUID.randomUUID();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());


        assertThrows(ShiftNotFoundException.class, () -> {
            shiftService.book(shiftId, talentId);
        });

        verify(shiftRepository, times(0)).save(any(Shift.class));
    }

    @Test
    public void book_cannotBookShiftError() {
        UUID shiftId = UUID.randomUUID();
        UUID talentId = UUID.randomUUID();
        Shift shift = Shift.builder()
                .id(shiftId)
                .status(Shift.Status.BOOKED)
                .build();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        assertThrows(CannotBookShiftException.class, () -> {
            shiftService.book(shiftId, talentId);
        });

        verify(shiftRepository, times(0)).save(any(Shift.class));
    }

    @Test
    public void cancel_success() {
        UUID shiftId = UUID.randomUUID();
        Job job = Job.builder().id(UUID.randomUUID()).status(Job.Status.CREATED).build();
        Shift shift = Shift.builder()
                .id(shiftId)
                .job(job)
                .status(Shift.Status.CREATED)
                .build();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(shiftRepository.countAllByJobIdAndStatusNot(job.getId(), Shift.Status.CANCELED)).thenReturn(2L);

        shiftService.cancel(shiftId);

        assertThat(shift.getStatus()).isEqualTo(Shift.Status.CANCELED);
        verify(shiftRepository, times(1)).save(shift);
    }

    @Test
    public void cancel_shiftNotFoundError() {
        UUID shiftId = UUID.randomUUID();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

        assertThrows(ShiftNotFoundException.class, () -> {
            shiftService.cancel(shiftId);
        });

        verify(shiftRepository, times(0)).save(any(Shift.class));
    }

    @Test
    public void cancel_lastAvailableShift() {
        UUID shiftId = UUID.randomUUID();
        Job job = Job.builder().id(UUID.randomUUID()).status(Job.Status.CREATED).build();
        Shift shift = Shift.builder()
                .id(shiftId)
                .job(job)
                .status(Shift.Status.CREATED)
                .build();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(shiftRepository.countAllByJobIdAndStatusNot(job.getId(), Shift.Status.CANCELED)).thenReturn(1L);

        assertThrows(LastShiftException.class, () -> {
            shiftService.cancel(shiftId);
        });

        verify(shiftRepository, times(0)).save(any(Shift.class));
    }

    @Test
    public void cancelForTalent_success() {
        UUID talentId = UUID.randomUUID();
        Job job = Job.builder().id(UUID.randomUUID()).build();
        List<Shift> shifts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Shift shift = Shift.builder()
                    .id(UUID.randomUUID())
                    .job(job)
                    .talentId(talentId)
                    .status(Shift.Status.CREATED)
                    .build();
            shifts.add(shift);
        }

        when(shiftRepository.findAllByTalentId(talentId)).thenReturn(shifts);

        shiftService.cancelShiftForTalent(talentId);

        shifts.forEach(shift -> {
            assertThat(shift.getStatus()).isEqualTo(Shift.Status.CANCELED);
        });
        verify(shiftRepository, times(6)).save(any(Shift.class));
    }

    @Test
    public void cancelForTalent_shiftsNotFoundError() {
        UUID talentId = UUID.randomUUID();

        when(shiftRepository.findAllByTalentId(talentId)).thenReturn(new ArrayList<>());

        assertThrows(ShiftsForTalentNotFoundException.class, () -> {
            shiftService.cancelShiftForTalent(talentId);
        });

        verify(shiftRepository, times(0)).save(any(Shift.class));
    }

    @Test
    public void cancelForTalent_noActiveShiftsForTalent() {
        UUID talentId = UUID.randomUUID();
        Job job = Job.builder().id(UUID.randomUUID()).build();
        List<Shift> shifts = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Shift shift = Shift.builder()
                    .id(UUID.randomUUID())
                    .job(job)
                    .talentId(talentId)
                    .status(Shift.Status.CANCELED)
                    .build();
            shifts.add(shift);
        }

        when(shiftRepository.findAllByTalentId(talentId)).thenReturn(shifts);

        assertThrows(NoAvailableShiftException.class, () -> {
            shiftService.cancelShiftForTalent(talentId);
        });

        verify(shiftRepository, times(0)).save(any(Shift.class));
    }

}
