package com.zenjob.challenge.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@NoArgsConstructor
@Data
public class BookTalentRequestDto {
    @NotNull
    private UUID talent;
}
