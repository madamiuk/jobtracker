package pl.jobtracker.dto;

import java.time.OffsetDateTime;

public record NoteResponse(
        Long id,
        OffsetDateTime createdAt,
        String content
) {}
