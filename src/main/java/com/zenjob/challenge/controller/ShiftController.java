package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.BookTalentRequestDto;
import com.zenjob.challenge.dto.ShiftsResponseDto;
import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.dto.ShiftDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.exception.ShiftAlreadyCanceledException;
import com.zenjob.challenge.exception.ShiftNotFoundException;
import com.zenjob.challenge.exception.ShiftsForTalentNotFoundException;
import com.zenjob.challenge.service.JobService;
import com.zenjob.challenge.service.ShiftService;
import com.zenjob.challenge.util.UUIDValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/shift")
@RequiredArgsConstructor
public class ShiftController {
    private final ShiftService shiftService;
    private final JobService jobService;

    private static final Logger logger = LoggerFactory.getLogger(ShiftController.class);

    /**
     * Retrieves shifts for a specific job.
     *
     * @param jobIdString The ID of the job in string form.
     * @return A response containing the shifts.
     */
    @GetMapping(path = "/{jobId}")
    @ResponseBody
    public ResponseDto<ShiftsResponseDto> getShifts(@PathVariable("jobId") String jobIdString) {
        UUID jobId = UUIDValidator.validateUUID(jobIdString);
        logger.info("Request to retrieve shifts for job with ID: {}", jobId);
        System.out.println("This is get shifts!");

        //Ensuring that the job exists
        jobService.getJob(jobId);

        List<ShiftDto> shiftResponses = shiftService.getShiftsByJobId(jobId).stream()
                .map(shift -> ShiftDto.builder()
                        .id(shift.getId())
                        .talentId(shift.getTalentId())
                        .jobId(shift.getJob().getId())
                        .start(shift.getStartTime())
                        .end(shift.getEndTime())
                        .status(shift.getShiftStatus())
                        .build())
                .collect(Collectors.toList());

        return ResponseDto.<ShiftsResponseDto>builder()
                .data(ShiftsResponseDto.builder()
                        .shifts(shiftResponses)
                        .build())
                .build();
    }

    @PatchMapping(path = "/book/{shiftId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void bookTalent(@PathVariable("shiftId") String shiftIdString, @RequestBody @Valid BookTalentRequestDto dto) {
        UUID shiftId = UUIDValidator.validateUUID(shiftIdString);
        logger.info("Request to book talent with ID: {} for shift with ID: {}", dto.getTalent(), shiftId);
        try {
            shiftService.bookTalent(shiftId, dto.getTalent());
        }catch(ShiftAlreadyCanceledException | ShiftNotFoundException e){
            logger.info("Error booking shift: {}", e.getMessage());
            throw e;
        }
    }

    @PatchMapping(path = "/talent/{talentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelShiftsForTalent(@PathVariable("talentId") String talentIdString) {
        UUID talentId = UUIDValidator.validateUUID(talentIdString);
        System.out.printf("TAlenid: %s\n", talentId);
        logger.info("Request to cancel shift for talent with ID: {}", talentId);
        try {
            shiftService.cancelShiftForTalent(talentId);
            logger.info("Successfully cancelled shift for talent with ID: {}", talentId);
        }catch(ShiftAlreadyCanceledException | ShiftNotFoundException | ShiftsForTalentNotFoundException e){
            logger.info("Error cancelling shifts for talent: {}", e.getMessage());
            throw e;
        }
    }

    @PatchMapping(path = "/{shiftId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void cancelShift(@PathVariable("shiftId") String shiftIdString) {
        UUID shiftId = UUIDValidator.validateUUID(shiftIdString);
        logger.info("Request to cancel shift with ID: {}", shiftId);
        try {
            shiftService.cancelShift(shiftId);
        }catch(ShiftAlreadyCanceledException | ShiftNotFoundException e){
            logger.info("Error cancelling shift: {}", e.getMessage());
            throw e;
        }
    }


}
