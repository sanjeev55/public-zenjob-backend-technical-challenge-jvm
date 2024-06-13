package com.zenjob.challenge.service;

import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.enums.JobStatusEnum;
import com.zenjob.challenge.enums.ShiftStatusEnum;
import com.zenjob.challenge.exception.*;
import com.zenjob.challenge.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLOutput;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;

    public List<Shift> getShiftsByJobId(UUID id) {
        return shiftRepository.findAllByJobId(id);
    }

    /**
     * Books a talent for a shift if the shift is in the CREATED state.
     *
     * @param shiftId The ID of the shift.
     * @param talentId The ID of the talent.
     */
    @Transactional
    public void bookTalent(UUID shiftId, UUID talentId) {
        Shift shift = shiftRepository.findById(shiftId).orElseThrow(()->new ShiftNotFoundException(shiftId));

        if(shift.getShiftStatus().equals(ShiftStatusEnum.CREATED)) {
            shift.setTalentId(talentId);
            shift.setShiftStatus(ShiftStatusEnum.BOOKED);
            shiftRepository.save(shift);
        }else{
            throw new ShiftAlreadyBookedException(shiftId);
        }
    }

    /**
     * Cancels a shift if it is not already canceled.
     *
     * @param shiftId The ID of the shift.
     */
    @Transactional
    public void cancelShift(UUID shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ShiftNotFoundException(shiftId));

        checkIfLastAvailableShift(shiftId, shift.getJob());  //checks if it's the only last shift of a job

        if (!shift.getShiftStatus().equals(ShiftStatusEnum.CANCELED)) {
            shift.setShiftStatus(ShiftStatusEnum.CANCELED);
            shiftRepository.save(shift);
        } else {
            throw new ShiftAlreadyCanceledException(shiftId);
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
        shifts.stream()
                .filter(shift -> !shift.getShiftStatus().equals(ShiftStatusEnum.CANCELED))
                .forEach(shift -> {
                cancelShift(shift.getId());
                createReplacementShift(shift);
                });
    }

    private void createReplacementShift(Shift canceledShift) {
        Shift replacementShift = Shift.builder()
                .job(canceledShift.getJob())
                .startTime(canceledShift.getStartTime())
                .endTime(canceledShift.getEndTime())
                .shiftStatus(ShiftStatusEnum.CREATED)
                .build();

        shiftRepository.save(replacementShift);

    }

    /**
     * Check if a shift is the last available shift of a job.
     * Also checks if Job was canceled or not.
     *
     * @param shiftId The ID of the shift to be canceled.
     * @param job job in which the shift belongs to.
     */
    private void checkIfLastAvailableShift(UUID shiftId, Job job) {
        long numberOfAvailableShift =  shiftRepository.findAllByJobId(job.getId()).stream().
                filter(shift -> !shift.getShiftStatus().equals(ShiftStatusEnum.CANCELED)).count();
        if(!job.getJobStatus().equals(JobStatusEnum.CANCELED) && numberOfAvailableShift == 1) {
            throw new LastShiftExeption(shiftId, job.getId());
        }
    }
}
