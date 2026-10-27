package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import pl.jobtracker.dto.JobApplicationResponse;
import pl.jobtracker.entity.JobApplication;

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
}
