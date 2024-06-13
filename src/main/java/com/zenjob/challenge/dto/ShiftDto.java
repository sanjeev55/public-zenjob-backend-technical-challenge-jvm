package com.zenjob.challenge.dto;

import com.zenjob.challenge.enums.ShiftStatusEnum;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class ShiftDto {
    @NotNull
    private UUID id;
    private UUID talentId;
    @NotNull
    private UUID jobId;
    @NotNull
    private Instant start;
    @NotNull
    private Instant end;
    @NotNull
    private ShiftStatusEnum status;
}
