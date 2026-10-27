package pl.jobtracker.dto;

public record RecruiterResponse(
        Long id,
        String firstName,
        String lastName,
        String position,
        String email,
        String phone,
        String linkedinUrl,
        String companyName,
        String notes
) {
}
