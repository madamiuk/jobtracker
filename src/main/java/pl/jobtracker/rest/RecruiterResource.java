package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import pl.jobtracker.dto.RecruiterResponse;
import pl.jobtracker.entity.Recruiter;

import java.util.List;

@Path("/applications/{applicationId}/recruiters")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class RecruiterResource {

    @PersistenceContext(unitName = "JobTrackerPU")
    private EntityManager entityManager;

    @GET
    public List<RecruiterResponse> recruiters(
            @PathParam("applicationId") Long applicationId) {

        List<Recruiter> recruiters = entityManager
                .createQuery(
                        """
                        SELECT r
                        FROM Recruiter r
                        JOIN FETCH r.company
                        WHERE r.id IN (
                            SELECT ar.recruiterId
                            FROM ApplicationRecruiter ar
                            WHERE ar.applicationId = :applicationId
                        )
                        ORDER BY r.lastName, r.firstName
                        """,
                        Recruiter.class
                )
                .setParameter("applicationId", applicationId)
                .getResultList();

        return recruiters.stream()
                .map(r -> new RecruiterResponse(
                        r.getId(),
                        r.getFirstName(),
                        r.getLastName(),
                        r.getPosition(),
                        r.getEmail(),
                        r.getPhone(),
                        r.getLinkedinUrl(),
                        r.getCompany().getName(),
                        r.getNotes()
                ))
                .toList();
    }
}
