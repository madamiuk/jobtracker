package pl.jobtracker.entity;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainModelTest {

    @Test
    void applicationStatusShouldContainExpectedWorkflowStates() {
        var statuses = Arrays.asList(
                JobApplication.ApplicationStatus.values()
        );

        assertTrue(statuses.contains(JobApplication.ApplicationStatus.PLANNED));
        assertTrue(statuses.contains(JobApplication.ApplicationStatus.APPLIED));
        assertTrue(statuses.contains(JobApplication.ApplicationStatus.HR_INTERVIEW));
        assertTrue(statuses.contains(JobApplication.ApplicationStatus.TECH_INTERVIEW));
        assertTrue(statuses.contains(JobApplication.ApplicationStatus.OFFER));
        assertTrue(statuses.contains(JobApplication.ApplicationStatus.REJECTED));
        assertTrue(statuses.contains(JobApplication.ApplicationStatus.HIRED));
    }

    @Test
    void jobOfferSourceTypeShouldContainSupportedSources() {
        var sources = Arrays.asList(
                JobOffer.SourceType.values()
        );

        assertTrue(sources.contains(JobOffer.SourceType.LINKEDIN));
        assertTrue(sources.contains(JobOffer.SourceType.PRACUJ));
        assertTrue(sources.contains(JobOffer.SourceType.JUSTJOINIT));
        assertTrue(sources.contains(JobOffer.SourceType.NOFLUFFJOBS));
        assertTrue(sources.contains(JobOffer.SourceType.COMPANY_WEBSITE));
        assertTrue(sources.contains(JobOffer.SourceType.RECRUITER));
        assertTrue(sources.contains(JobOffer.SourceType.OTHER));
    }

    @Test
    void workModeShouldContainExpectedModes() {
        assertEquals(
                3,
                JobOffer.WorkMode.values().length
        );

        assertTrue(
                Arrays.asList(JobOffer.WorkMode.values())
                        .contains(JobOffer.WorkMode.REMOTE)
        );

        assertTrue(
                Arrays.asList(JobOffer.WorkMode.values())
                        .contains(JobOffer.WorkMode.HYBRID)
        );

        assertTrue(
                Arrays.asList(JobOffer.WorkMode.values())
                        .contains(JobOffer.WorkMode.ONSITE)
        );
    }

    @Test
    void employmentTypeShouldContainExpectedTypes() {
        var types = Arrays.asList(
                JobOffer.EmploymentType.values()
        );

        assertTrue(types.contains(JobOffer.EmploymentType.EMPLOYMENT_CONTRACT));
        assertTrue(types.contains(JobOffer.EmploymentType.B2B));
        assertTrue(types.contains(JobOffer.EmploymentType.MANDATE));
        assertTrue(types.contains(JobOffer.EmploymentType.OTHER));
    }
}
