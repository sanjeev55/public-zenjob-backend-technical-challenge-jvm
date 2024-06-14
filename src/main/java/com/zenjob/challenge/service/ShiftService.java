package com.zenjob.challenge.service;

import com.zenjob.challenge.dto.ShiftDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.exception.*;
import com.zenjob.challenge.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;

    /**
     * Books a talent for a shift if the shift is in the CREATED state.
     *
     * @param shiftId The ID of the shift.
     * @param talentId The ID of the talent.
     */
    @Transactional
    public void book(UUID shiftId, UUID talentId) {
        Shift shift = shiftRepository.findById(shiftId).orElseThrow(()->new ShiftNotFoundException(shiftId));

        if(!shift.getStatus().equals(Shift.Status.CREATED)){
            throw new CannotBookShiftException(shiftId);
        }

        shift.setTalentId(talentId);
        shift.setStatus(Shift.Status.BOOKED);
        shiftRepository.save(shift);
    }

    @Transactional
    public void cancel(UUID shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ShiftNotFoundException(shiftId));

        checkIfLastAvailableShift(shift);  //checks if it's the only last shift of a job
        cancel(shift);
    }

    @Transactional
    public void cancel(Shift shift) {
        if (!shift.getStatus().equals(Shift.Status.CANCELED)) {
            shift.setStatus(Shift.Status.CANCELED);
            shiftRepository.save(shift);
        }
    }

    /**
     * Cancels all shifts for a given talent and creates replacement shifts.
     *
     * @param talentId The ID of the talent.
     */
    @Transactional
    public void cancelShiftForTalent(UUID talentId) {
        List<Shift> shifts = shiftRepository.findAllByTalentId(talentId);
        if(shifts.isEmpty()) {
            throw new ShiftsForTalentNotFoundException(talentId);
        }

        List<Shift> activeShifts = shifts.stream()
                .filter(shift -> !shift.getStatus().equals(Shift.Status.CANCELED))
                .collect(Collectors.toList());

        if (activeShifts.isEmpty()) {
            throw new NoAvailableShiftException(talentId);
        }
        activeShifts.forEach(shift -> {
                cancel(shift);
                createReplacementShift(shift);
                });
    }

    private void createReplacementShift(Shift canceledShift) {
        Shift replacementShift = Shift.builder()
                .job(canceledShift.getJob())
                .startTime(canceledShift.getStartTime())
                .endTime(canceledShift.getEndTime())
                .status(Shift.Status.CREATED)
                .build();

        shiftRepository.save(replacementShift);
    }

    /**
     * Check if a shift is the last available shift of a job.
     * Also checks if Job was canceled or not.
     *
     * @param currentShift The shift to be canceled.
     */
    private void checkIfLastAvailableShift(Shift currentShift) {
        Job job = currentShift.getJob();
        long numberOfAvailableShift =  shiftRepository.countAllByJobIdAndStatusNot(job.getId(), Shift.Status.CANCELED);
        if(!job.getStatus().equals(Job.Status.CANCELED) && numberOfAvailableShift == 1) {
            throw new LastShiftException(currentShift);
        }
    }

    public List<ShiftDto> fetchByJobId(final UUID jobId) {
        List<Shift> shifts = shiftRepository.findAllByJobId(jobId);
        if(shifts.isEmpty()) {
            throw new JobNotFoundException(jobId);
        }
        return shifts.stream()
                .map(shift -> ShiftDto.builder()
                        .id(shift.getId())
                        .talentId(shift.getTalentId())
                        .jobId(shift.getJob().getId())
                        .start(shift.getStartTime())
                        .end(shift.getEndTime())
                        .status(shift.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
