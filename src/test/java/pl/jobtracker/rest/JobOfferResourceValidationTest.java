package pl.jobtracker.rest;

import org.junit.jupiter.api.Test;
import pl.jobtracker.dto.CreateJobOfferRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobOfferResourceValidationTest {

    @Test
    void nullRequestShouldBeRejected() {
        assertFalse(
                JobOfferResource.isValidCreateRequest(null)
        );
    }

    @Test
    void missingCompanyIdShouldBeRejected() {
        var request = new CreateJobOfferRequest(
                null,
                "Linux Administrator",
                null,
                null,
                null,
                "LINKEDIN",
                null,
                null,
                "HYBRID",
                "EMPLOYMENT_CONTRACT",
                "Warsaw",
                null,
                null,
                "PLN",
                "MONTH",
                null
        );

        assertFalse(
                JobOfferResource.isValidCreateRequest(request)
        );
    }

    @Test
    void blankPositionTitleShouldBeRejected() {
        var request = new CreateJobOfferRequest(
                1L,
                "   ",
                null,
                null,
                null,
                "LINKEDIN",
                null,
                null,
                "HYBRID",
                "EMPLOYMENT_CONTRACT",
                "Warsaw",
                null,
                null,
                "PLN",
                "MONTH",
                null
        );

        assertFalse(
                JobOfferResource.isValidCreateRequest(request)
        );
    }

    @Test
    void blankSourceTypeShouldBeRejected() {
        var request = new CreateJobOfferRequest(
                1L,
                "Linux Administrator",
                null,
                null,
                null,
                "   ",
                null,
                null,
                "HYBRID",
                "EMPLOYMENT_CONTRACT",
                "Warsaw",
                null,
                null,
                "PLN",
                "MONTH",
                null
        );

        assertFalse(
                JobOfferResource.isValidCreateRequest(request)
        );
    }

    @Test
    void validRequestShouldBeAccepted() {
        var request = new CreateJobOfferRequest(
                1L,
                "Linux Administrator",
                null,
                null,
                null,
                "LINKEDIN",
                null,
                null,
                "HYBRID",
                "EMPLOYMENT_CONTRACT",
                "Warsaw",
                null,
                null,
                "PLN",
                "MONTH",
                null
        );

        assertTrue(
                JobOfferResource.isValidCreateRequest(request)
        );
    }

    @Test
    void invalidSourceTypeShouldBeRejected() {
        var request = new CreateJobOfferRequest(
                1L,
                "Linux Administrator",
                null,
                null,
                null,
                "INVALID_SOURCE",
                null,
                null,
                "HYBRID",
                "EMPLOYMENT_CONTRACT",
                "Warsaw",
                null,
                null,
                "PLN",
                "MONTH",
                null
        );

        assertFalse(
                JobOfferResource.isValidCreateRequest(request)
        );
    }

    @Test
    void invalidWorkModeShouldBeRejected() {
        var request = new CreateJobOfferRequest(
                1L,
                "Linux Administrator",
                null,
                null,
                null,
                "LINKEDIN",
                null,
                null,
                "INVALID_MODE",
                "EMPLOYMENT_CONTRACT",
                "Warsaw",
                null,
                null,
                "PLN",
                "MONTH",
                null
        );

        assertFalse(
                JobOfferResource.isValidCreateRequest(request)
        );
    }

    @Test
    void invalidEmploymentTypeShouldBeRejected() {
        var request = new CreateJobOfferRequest(
                1L,
                "Linux Administrator",
                null,
                null,
                null,
                "LINKEDIN",
                null,
                null,
                "HYBRID",
                "INVALID_TYPE",
                "Warsaw",
                null,
                null,
                "PLN",
                "MONTH",
                null
        );

        assertFalse(
               JobOfferResource.isValidCreateRequest(request)
        );
    }
}
