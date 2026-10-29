package pl.jobtracker.rest;

import org.junit.jupiter.api.Test;
import pl.jobtracker.dto.CreateJobApplicationRequest;
import pl.jobtracker.dto.UpdateApplicationStatusRequest;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobApplicationResourceValidationTest {

    @Test
    void nullRequestShouldBeRejected() {
        assertFalse(
                JobApplicationResource.isValidCreateRequest(null)
        );
    }

    @Test
    void missingJobOfferIdShouldBeRejected() {
        var request = new CreateJobApplicationRequest(
                null,
                OffsetDateTime.now(),
                "APPLIED",
                null,
                null,
                null,
                null,
                null,
                "PLN"
        );

        assertFalse(
                JobApplicationResource.isValidCreateRequest(request)
        );
    }

    @Test
    void nullStatusShouldBeRejected() {
        var request = new CreateJobApplicationRequest(
                1L,
                OffsetDateTime.now(),
                null,
                null,
                null,
                null,
                null,
                null,
                "PLN"
        );

        assertFalse(
                JobApplicationResource.isValidCreateRequest(request)
        );
    }

    @Test
    void blankStatusShouldBeRejected() {
        var request = new CreateJobApplicationRequest(
                1L,
                OffsetDateTime.now(),
                "   ",
                null,
                null,
                null,
                null,
                null,
                "PLN"
        );

        assertFalse(
                JobApplicationResource.isValidCreateRequest(request)
        );
    }

    @Test
    void validRequestShouldBeAccepted() {
        var request = new CreateJobApplicationRequest(
                1L,
                OffsetDateTime.now(),
                "APPLIED",
                null,
                null,
                null,
                null,
                null,
                "PLN"
        );

        assertTrue(
                JobApplicationResource.isValidCreateRequest(request)
        );
    }

    @Test
    void invalidStatusShouldBeRejected() {
        var request = new CreateJobApplicationRequest(
                1L,
                OffsetDateTime.now(),
                "INVALID_STATUS",
                null,
                null,
                null,
                null,
                null,
                "PLN"
        );

        assertFalse(
                JobApplicationResource.isValidCreateRequest(request)
        );
    }

    @Test
    void nullStatusUpdateShouldBeRejected() {
        assertFalse(
                JobApplicationResource.isValidStatusUpdateRequest(null)
        );
    }

    @Test
    void blankStatusUpdateShouldBeRejected() {
        var request =
                new UpdateApplicationStatusRequest(
                        "   ",
                        OffsetDateTime.now(),
                        "test"
                );

        assertFalse(
                JobApplicationResource.isValidStatusUpdateRequest(request)
        );
    }

    @Test
    void invalidStatusUpdateShouldBeRejected() {
        var request =
                new UpdateApplicationStatusRequest(
                        "INVALID_STATUS",
                        OffsetDateTime.now(),
                        "test"
                );

        assertFalse(
                JobApplicationResource.isValidStatusUpdateRequest(request)
        );
    }

    @Test
    void validStatusUpdateShouldBeAccepted() {
        var request =
                new UpdateApplicationStatusRequest(
                        "TECH_INTERVIEW",
                        OffsetDateTime.now(),
                        "Technical interview scheduled"
                );

        assertTrue(
                JobApplicationResource.isValidStatusUpdateRequest(request)
        );
    }
}
