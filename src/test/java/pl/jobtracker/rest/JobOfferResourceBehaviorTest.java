package pl.jobtracker.rest;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import pl.jobtracker.dto.CreateJobOfferRequest;
import pl.jobtracker.entity.Company;
import pl.jobtracker.entity.JobOffer;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JobOfferResourceBehaviorTest {

    private static JobOfferResource createResource(
            EntityManager entityManager
    ) throws Exception {

        JobOfferResource resource = new JobOfferResource();

        Field field = JobOfferResource.class
                .getDeclaredField("entityManager");

        field.setAccessible(true);
        field.set(resource, entityManager);

        return resource;
    }

    private static CreateJobOfferRequest validRequest() {
        return new CreateJobOfferRequest(
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
    }

    @Test
    void invalidRequestShouldReturnBadRequest() throws Exception {
        EntityManager entityManager = mock(EntityManager.class);

        JobOfferResource resource =
                createResource(entityManager);

        CreateJobOfferRequest request =
                new CreateJobOfferRequest(
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

        Response response =
                resource.createOffer(request);

        assertEquals(
                Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus()
        );

        verify(
                entityManager,
                never()
        ).persist(any());
    }

    @Test
    void missingCompanyShouldReturnNotFound() throws Exception {
        EntityManager entityManager = mock(EntityManager.class);

        when(
                entityManager.find(
                        Company.class,
                        1L
                )
        ).thenReturn(null);

        JobOfferResource resource =
                createResource(entityManager);

        Response response =
                resource.createOffer(validRequest());

        assertEquals(
                Response.Status.NOT_FOUND.getStatusCode(),
                response.getStatus()
        );

        verify(
                entityManager,
                never()
        ).persist(any());
    }

    @Test
    void validRequestShouldReturnCreated() throws Exception {
        EntityManager entityManager = mock(EntityManager.class);

        Company company = new Company();
        company.setName("Example Company");

        when(
                entityManager.find(
                        Company.class,
                        1L
                )
        ).thenReturn(company);

        JobOfferResource resource =
                createResource(entityManager);

        Response response =
                resource.createOffer(validRequest());

        assertEquals(
                Response.Status.CREATED.getStatusCode(),
                response.getStatus()
        );

        verify(entityManager)
                .persist(any(JobOffer.class));

        verify(entityManager)
                .flush();
    }

    @Test
    void createdOfferShouldBeActive()
            throws Exception {

        EntityManager entityManager =
                mock(EntityManager.class);

        Company company =
                new Company();

        company.setName("Example Company");

        when(
                entityManager.find(
                        Company.class,
                        1L
                )
        ).thenReturn(company);

        JobOfferResource resource =
                createResource(entityManager);

        resource.createOffer(
                validRequest()
        );

        ArgumentCaptor<JobOffer> captor =
                ArgumentCaptor.forClass(
                        JobOffer.class
                );

        verify(entityManager)
                .persist(captor.capture());

        assertEquals(
                JobOffer.OfferStatus.ACTIVE,
                captor.getValue().getStatus()
        );
    }


    @Test
    void archiveOfferShouldSetArchivedStatus()
            throws Exception {

        EntityManager entityManager =
                mock(EntityManager.class);

        JobOffer offer =
                new JobOffer();

        Company company =
                new Company();

        company.setName("Example Company");

        offer.setCompany(company);
        offer.setPositionTitle(
                "Linux Administrator"
        );
        offer.setStatus(
                JobOffer.OfferStatus.ACTIVE
        );

        when(
                entityManager.find(
                        JobOffer.class,
                        10L
                )
        ).thenReturn(offer);

        JobOfferResource resource =
                createResource(entityManager);

        Response response =
                resource.archiveOffer(10L);

        assertEquals(
                Response.Status.OK.getStatusCode(),
                response.getStatus()
        );

        assertEquals(
                JobOffer.OfferStatus.ARCHIVED,
                offer.getStatus()
        );

        verify(entityManager)
                .flush();
    }


    @Test
    void archiveMissingOfferShouldReturnNotFound()
            throws Exception {

        EntityManager entityManager =
                mock(EntityManager.class);

        when(
                entityManager.find(
                        JobOffer.class,
                        999L
               )
        ).thenReturn(null);

        JobOfferResource resource =
                createResource(entityManager);

        Response response =
                resource.archiveOffer(999L);

        assertEquals(
                Response.Status.NOT_FOUND.getStatusCode(),
                response.getStatus()
        );

        verify(
                entityManager,
                never()
        ).flush();
    }
}
