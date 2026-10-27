package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pl.jobtracker.dto.CreateJobApplicationRequest;
import pl.jobtracker.dto.JobApplicationResponse;
import pl.jobtracker.entity.JobApplication;
import pl.jobtracker.entity.JobOffer;

import java.util.List;

@Path("/applications")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class JobApplicationResource {

    @PersistenceContext(unitName = "JobTrackerPU")
    private EntityManager entityManager;

    @GET
    public List<JobApplicationResponse> listApplications() {
        List<JobApplication> applications = entityManager
                .createQuery(
                        """
                        SELECT ja
                        FROM JobApplication ja
                        JOIN FETCH ja.jobOffer
                        JOIN FETCH ja.jobOffer.company
                        ORDER BY ja.id
                        """,
                        JobApplication.class
                )
                .getResultList();

        return applications.stream()
                .map(application -> new JobApplicationResponse(
                        application.getId(),
                        application.getJobOffer().getCompany().getName(),
                        application.getJobOffer().getPositionTitle(),
                        application.getCurrentStatus().name(),
                        application.getAppliedAt(),
                        application.getPriority(),
                        application.getFitScore(),
                        application.getInterestScore(),
                        application.getExpectedSalaryMin(),
                        application.getExpectedSalaryMax(),
                        application.getExpectedSalaryCurrency()
                ))
                .toList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createApplication(CreateJobApplicationRequest request) {

        if (request == null ||
                request.jobOfferId() == null ||
                request.currentStatus() == null ||
                request.currentStatus().isBlank()) {

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("jobOfferId and currentStatus are required.")
                    .build();
        }

        JobOffer jobOffer = entityManager.find(
                JobOffer.class,
                request.jobOfferId()
        );

        if (jobOffer == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Job offer not found.")
                    .build();
        }

        JobApplication.ApplicationStatus applicationStatus;

        try {
            applicationStatus = JobApplication.ApplicationStatus.valueOf(
                    request.currentStatus()
            );
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Invalid application status.")
                    .build();
        }

        JobApplication application = new JobApplication();

        application.setJobOffer(jobOffer);
        application.setAppliedAt(request.appliedAt());
        application.setCurrentStatus(applicationStatus);
        application.setPriority(request.priority());
        application.setFitScore(request.fitScore());
        application.setInterestScore(request.interestScore());
        application.setExpectedSalaryMin(request.expectedSalaryMin());
        application.setExpectedSalaryMax(request.expectedSalaryMax());
        application.setExpectedSalaryCurrency(
                request.expectedSalaryCurrency()
        );

        entityManager.persist(application);
        entityManager.flush();

        JobApplicationResponse response = new JobApplicationResponse(
                application.getId(),
                jobOffer.getCompany().getName(),
                jobOffer.getPositionTitle(),
                application.getCurrentStatus().name(),
                application.getAppliedAt(),
                application.getPriority(),
                application.getFitScore(),
                application.getInterestScore(),
                application.getExpectedSalaryMin(),
                application.getExpectedSalaryMax(),
                application.getExpectedSalaryCurrency()
        );

        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }
}
