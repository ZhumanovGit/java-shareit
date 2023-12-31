package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@Builder
public class BookingApproveDto {
    @NotNull
    private Long bookingId;
    @NotNull
    private Boolean isApproved;
}
