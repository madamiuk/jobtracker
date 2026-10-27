package pl.jobtracker.dto;

public record CreateCompanyRequest(
        String name,
        String website,
        String industry,
        String city,
        String country,
        String description,
        String notes
) {
}
