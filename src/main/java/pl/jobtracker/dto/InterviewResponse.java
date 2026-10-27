package pl.jobtracker.dto;

import java.time.OffsetDateTime;

public record InterviewResponse(
        Long id,
        String interviewType,
        OffsetDateTime scheduledAt,
        Integer durationMinutes,
        String locationType,
        String locationDetails,
        String result,
        String summary,
        String questions,
        String myNotes
) {
}
