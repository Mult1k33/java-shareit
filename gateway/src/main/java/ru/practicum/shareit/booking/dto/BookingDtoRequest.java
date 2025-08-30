package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDtoRequest {

    @NotNull
    @Positive
    private Long itemId;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "UTC")
    private Instant start;

    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "UTC")
    private Instant end;
}