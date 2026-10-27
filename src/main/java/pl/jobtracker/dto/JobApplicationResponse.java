package pl.jobtracker.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record JobApplicationResponse(
        Long id,
        String companyName,
        String positionTitle,
        String currentStatus,
        OffsetDateTime appliedAt,
        Short priority,
        Short fitScore,
        Short interestScore,
        BigDecimal expectedSalaryMin,
        BigDecimal expectedSalaryMax,
        String expectedSalaryCurrency
) {
}
