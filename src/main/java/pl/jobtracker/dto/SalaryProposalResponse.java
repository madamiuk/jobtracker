package pl.jobtracker.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SalaryProposalResponse(
        Long id,
        String proposalType,
        BigDecimal amountMin,
        BigDecimal amountMax,
        String currency,
        String period,
        OffsetDateTime proposedAt,
        String notes
) {}
