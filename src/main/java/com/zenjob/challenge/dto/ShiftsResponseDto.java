package com.zenjob.challenge.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ShiftsResponseDto {
   private List<ShiftDto> shifts;
}
