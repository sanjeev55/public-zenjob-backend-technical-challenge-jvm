package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.BookTalentRequestDto;
import com.zenjob.challenge.dto.ShiftsResponseDto;
import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.dto.ShiftDto;
import com.zenjob.challenge.exception.ShiftNotFoundException;
import com.zenjob.challenge.exception.ShiftsForTalentNotFoundException;
import com.zenjob.challenge.service.ShiftService;
import com.zenjob.challenge.util.UUIDValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Api(tags = "Shift Management")
@Slf4j
@RestController
@RequestMapping(path = "/v1/shift")
@RequiredArgsConstructor
public class ShiftController {
    private final ShiftService shiftService;

    @ApiOperation(value = "Fetch Shift by JobId", notes = "Fetches all the shifts with specified JobId")
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

    @ApiOperation(value = "Book a talent for a shift", notes = "Books a talent for the specified shift ID.")
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


    @ApiOperation(value = "Cancel shifts for a talent", notes = "Cancels all shifts booked for the specified talent ID.")
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

    @ApiOperation(value = "Cancel a shift", notes = "Cancels the shift with the specified ID.")
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
