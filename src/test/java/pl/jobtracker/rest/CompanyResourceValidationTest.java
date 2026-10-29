package pl.jobtracker.rest;

import org.junit.jupiter.api.Test;
import pl.jobtracker.dto.CreateCompanyRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompanyResourceValidationTest {

    @Test
    void nullRequestShouldBeRejected() {
        assertFalse(
                CompanyResource.isValidCreateRequest(null)
        );
    }

    @Test
    void nullCompanyNameShouldBeRejected() {
        var request = new CreateCompanyRequest(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertFalse(
                CompanyResource.isValidCreateRequest(request)
        );
    }

    @Test
    void blankCompanyNameShouldBeRejected() {
        var request = new CreateCompanyRequest(
                "   ",
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertFalse(
                CompanyResource.isValidCreateRequest(request)
        );
    }

    @Test
    void validCompanyNameShouldBeAccepted() {
        var request = new CreateCompanyRequest(
                "Example Company",
                "https://example.com",
                "IT",
                "Warsaw",
                "Poland",
                "Example employer",
                null
        );

        assertTrue(
                CompanyResource.isValidCreateRequest(request)
        );
    }
}
