package com.kaamwala.dto.request;

import com.kaamwala.entity.Booking;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating booking status through the state machine.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusUpdate {

    @NotNull(message = "Status is required")
    private Booking.BookingStatus status;
}
