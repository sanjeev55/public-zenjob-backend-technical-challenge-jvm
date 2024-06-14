package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.BookTalentRequestDto;
import com.zenjob.challenge.dto.ShiftsResponseDto;
import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.dto.ShiftDto;
import com.zenjob.challenge.exception.ShiftNotFoundException;
import com.zenjob.challenge.exception.ShiftsForTalentNotFoundException;
import com.zenjob.challenge.service.JobService;
import com.zenjob.challenge.service.ShiftService;
import com.zenjob.challenge.util.UUIDValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/v1/shift")
@RequiredArgsConstructor
public class ShiftController {
    private final ShiftService shiftService;

    /**
     * Retrieves shifts for a specific job.
     *
     * @param jobIdString The ID of the job in string form.
     * @return A response containing the shifts.
     */
    @GetMapping(path = "/{jobId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<ShiftsResponseDto> fetchByJobId(@PathVariable("jobId") String jobIdString) {
        UUID jobId = UUIDValidator.validateUUID(jobIdString);

        log.info("Request to retrieve shifts for job with ID: {}", jobId);

        List<ShiftDto> shifts = shiftService.fetchByJobId(jobId);

        return ResponseDto.<ShiftsResponseDto>builder()
                .data(ShiftsResponseDto.builder()
                        .shifts(shifts)
                        .build())
                .build();
    }

    @PutMapping(path = "/book/{shiftId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void book(@PathVariable("shiftId") String shiftIdString, @RequestBody @Valid BookTalentRequestDto bookTalentRequestDto) {
        UUID shiftId = UUIDValidator.validateUUID(shiftIdString);
        log.info("Request to book talent with ID: {} for shift with ID: {}", bookTalentRequestDto.getTalent(), shiftId);
        try {
            shiftService.book(shiftId, bookTalentRequestDto.getTalent());
        }catch(ShiftNotFoundException e){
            log.info("Error booking shift: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping(path = "/talent/{talentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void cancelForTalent(@PathVariable("talentId") String talentIdString) {
        UUID talentId = UUIDValidator.validateUUID(talentIdString);
        log.info("Request to cancel shift for talent with ID: {}", talentId);
        try {
            shiftService.cancelShiftForTalent(talentId);
            log.info("Successfully cancelled shift for talent with ID: {}", talentId);
        }catch(ShiftNotFoundException | ShiftsForTalentNotFoundException e){
            log.info("Error cancelling shifts for talent: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping(path = "cancel/{shiftId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void cancel(@PathVariable("shiftId") String shiftIdString) {
        UUID shiftId = UUIDValidator.validateUUID(shiftIdString);
        log.info("Request to cancel shift with ID: {}", shiftId);
        try {
            shiftService.cancel(shiftId);
        }catch(ShiftNotFoundException e){
            log.info("Error cancelling shift: {}", e.getMessage());
            throw e;
        }
    }


}
