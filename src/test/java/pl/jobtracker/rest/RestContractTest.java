package pl.jobtracker.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.Test;
import pl.jobtracker.config.RestApplication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestContractTest {

    @Test
    void applicationShouldUseApiBasePath() {
        ApplicationPath annotation =
                RestApplication.class.getAnnotation(ApplicationPath.class);

        assertNotNull(annotation);
        assertEquals("/api", annotation.value());
    }

    @Test
    void healthResourceShouldUseHealthPath() {
        Path annotation =
                HealthResource.class.getAnnotation(Path.class);

        assertNotNull(annotation);
        assertEquals("/health", annotation.value());
    }

    @Test
    void companyResourceShouldUseCompaniesPath() {
        Path annotation =
                CompanyResource.class.getAnnotation(Path.class);

        assertNotNull(annotation);
        assertEquals("/companies", annotation.value());
    }

    @Test
    void jobOfferResourceShouldUseOffersPath() {
        Path annotation =
                JobOfferResource.class.getAnnotation(Path.class);

        assertNotNull(annotation);
        assertEquals("/offers", annotation.value());
    }

    @Test
    void jobApplicationResourceShouldUseApplicationsPath() {
        Path annotation =
                JobApplicationResource.class.getAnnotation(Path.class);

        assertNotNull(annotation);
        assertEquals("/applications", annotation.value());
    }
}
