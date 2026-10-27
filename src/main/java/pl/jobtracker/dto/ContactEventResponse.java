package pl.jobtracker.dto;

import java.time.OffsetDateTime;

public record ContactEventResponse(
        Long id,
        String contactType,
        OffsetDateTime contactAt,
        String direction,
        String subject,
        String description,
        Boolean followUpRequired,
        OffsetDateTime followUpAt,
        Boolean followUpDone,
        String recruiterName
) {
}
