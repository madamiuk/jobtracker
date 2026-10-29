package pl.jobtracker.rest;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import pl.jobtracker.dto.CreateJobApplicationRequest;
import pl.jobtracker.entity.Company;
import pl.jobtracker.entity.JobApplication;
import pl.jobtracker.entity.JobOffer;
import pl.jobtracker.dto.UpdateApplicationStatusRequest;
import pl.jobtracker.entity.ApplicationStatusHistory;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JobApplicationResourceBehaviorTest {

    private static JobApplicationResource createResource(
            EntityManager entityManager
    ) throws Exception {

        JobApplicationResource resource =
                new JobApplicationResource();

        Field field = JobApplicationResource.class
                .getDeclaredField("entityManager");

        field.setAccessible(true);
        field.set(resource, entityManager);

        return resource;
    }

    private static CreateJobApplicationRequest validRequest() {
        return new CreateJobApplicationRequest(
                1L,
                OffsetDateTime.now(),
                "APPLIED",
                (short) 3,
                (short) 4,
                (short) 5,
                null,
                null,
                "PLN"
        );
    }

    private static JobOffer createJobOffer() {
        Company company = new Company();
        company.setName("Example Company");

        JobOffer jobOffer = new JobOffer();
        jobOffer.setCompany(company);
        jobOffer.setPositionTitle(
                "Linux Administrator"
        );
        jobOffer.setSourceType(
                JobOffer.SourceType.LINKEDIN
        );

        return jobOffer;
    }

    @Test
    void invalidRequestShouldReturnBadRequest()
            throws Exception {

        EntityManager entityManager =
                mock(EntityManager.class);

        JobApplicationResource resource =
                createResource(entityManager);

        CreateJobApplicationRequest request =
                new CreateJobApplicationRequest(
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

        Response response =
                resource.createApplication(request);

        assertEquals(
                Response.Status.BAD_REQUEST
                        .getStatusCode(),
                response.getStatus()
        );

        verify(
                entityManager,
                never()
        ).persist(any());
    }

    @Test
    void missingJobOfferShouldReturnNotFound()
            throws Exception {

        EntityManager entityManager =
                mock(EntityManager.class);

        when(
                entityManager.find(
                        JobOffer.class,
                        1L
                )
        ).thenReturn(null);

        JobApplicationResource resource =
                createResource(entityManager);

        Response response =
                resource.createApplication(
                        validRequest()
                );

        assertEquals(
                Response.Status.NOT_FOUND
                        .getStatusCode(),
                response.getStatus()
        );

        verify(
                entityManager,
                never()
        ).persist(any());
    }

    @Test
    void validRequestShouldReturnCreated()
            throws Exception {

        EntityManager entityManager =
                mock(EntityManager.class);

        JobOffer jobOffer =
                createJobOffer();

        when(
                entityManager.find(
                        JobOffer.class,
                        1L
                )
        ).thenReturn(jobOffer);

        JobApplicationResource resource =
                createResource(entityManager);

        Response response =
                resource.createApplication(
                        validRequest()
                );

        assertEquals(
                Response.Status.CREATED
                        .getStatusCode(),
                response.getStatus()
        );

        verify(entityManager)
                .persist(any(JobApplication.class));

        verify(entityManager)
                .flush();
    }

    @Test
    void statusUpdateShouldCreateHistoryEntry()
            throws Exception {

        EntityManager entityManager =
                mock(EntityManager.class);

        JobApplication application =
                new JobApplication();

        application.setCurrentStatus(
                JobApplication.ApplicationStatus.APPLIED
        );

        when(
                entityManager.find(
                        JobApplication.class,
                        10L
                )
        ).thenReturn(application);

        JobApplicationResource resource =
                createResource(entityManager);

        Response response =
                resource.updateStatus(
                        10L,
                        new UpdateApplicationStatusRequest(
                                "TECH_INTERVIEW",
                                OffsetDateTime.now(),
                                "Technical interview scheduled"
                        )
                );

        assertEquals(
                Response.Status.CREATED.getStatusCode(),
                response.getStatus()
        );

        assertEquals(
                JobApplication.ApplicationStatus.TECH_INTERVIEW,
                application.getCurrentStatus()
        );

        verify(entityManager)
                .persist(
                        any(ApplicationStatusHistory.class)
                );

        verify(entityManager)
                .flush();
    }
}
