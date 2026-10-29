package pl.jobtracker.rest;

import org.junit.jupiter.api.Test;
import pl.jobtracker.dto.CreateJobApplicationRequest;

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
}
