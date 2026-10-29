package pl.jobtracker.dto;

import java.time.OffsetDateTime;

public record UpdateApplicationStatusRequest(
        String status,
        OffsetDateTime changedAt,
        String comment
) {
}
