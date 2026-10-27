package pl.jobtracker.dto;

import java.math.BigDecimal;

public record CreateJobOfferRequest(
        Long companyId,
        String positionTitle,
        String offerUrl,
        String description,
        String originalText,
        String sourceType,
        String sourceUrl,
        String sourceDetails,
        String workMode,
        String employmentType,
        String location,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        String salaryCurrency,
        String salaryPeriod,
        String status
) {
}
