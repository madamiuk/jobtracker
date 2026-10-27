package pl.jobtracker.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CreateJobApplicationRequest(
        Long jobOfferId,
        OffsetDateTime appliedAt,
        String currentStatus,
        Short priority,
        Short fitScore,
        Short interestScore,
        BigDecimal expectedSalaryMin,
        BigDecimal expectedSalaryMax,
        String expectedSalaryCurrency
) {
}
