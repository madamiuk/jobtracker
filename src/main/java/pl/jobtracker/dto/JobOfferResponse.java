package pl.jobtracker.dto;

import java.math.BigDecimal;

public record JobOfferResponse(
        Long id,
        String companyName,
        String positionTitle,
        String location,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        String salaryCurrency,
        String salaryPeriod
) {
}
