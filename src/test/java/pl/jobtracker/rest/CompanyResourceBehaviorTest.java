package pl.jobtracker.rest;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import pl.jobtracker.dto.CreateCompanyRequest;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CompanyResourceBehaviorTest {

    private static CompanyResource createResource(
            EntityManager entityManager
    ) throws Exception {

        CompanyResource resource = new CompanyResource();

        Field field = CompanyResource.class
                .getDeclaredField("entityManager");

        field.setAccessible(true);
        field.set(resource, entityManager);

        return resource;
    }

    @Test
    void invalidRequestShouldReturnBadRequest() throws Exception {
        EntityManager entityManager = mock(EntityManager.class);

        CompanyResource resource =
                createResource(entityManager);

        CreateCompanyRequest request =
                new CreateCompanyRequest(
                        "   ",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        Response response =
                resource.createCompany(request);

        assertEquals(
                Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus()
        );

        verify(
                entityManager,
                never()
        ).persist(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void validRequestShouldReturnCreated() throws Exception {
        EntityManager entityManager = mock(EntityManager.class);

        CompanyResource resource =
                createResource(entityManager);

        CreateCompanyRequest request =
                new CreateCompanyRequest(
                        "Example Company",
                        "https://example.com",
                        "IT",
                        "Warsaw",
                        "Poland",
                        "Example employer",
                        null
                );

        Response response =
                resource.createCompany(request);

        assertEquals(
                Response.Status.CREATED.getStatusCode(),
                response.getStatus()
        );

        verify(entityManager)
                .persist(org.mockito.ArgumentMatchers.any());

        verify(entityManager)
                .flush();
    }
}
