package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import pl.jobtracker.dto.ApplicationStatusHistoryResponse;
import pl.jobtracker.entity.ApplicationStatusHistory;

import java.util.List;

@Path("/applications/{applicationId}/history")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationStatusHistoryResource {

    @PersistenceContext(unitName = "JobTrackerPU")
    private EntityManager entityManager;

    @GET
    public List<ApplicationStatusHistoryResponse> history(
            @PathParam("applicationId") Long applicationId) {

        return entityManager
                .createQuery(
                        """
                        SELECT h
                        FROM ApplicationStatusHistory h
                        WHERE h.application.id = :applicationId
                        ORDER BY h.changedAt, h.id
                        """,
                        ApplicationStatusHistory.class
                )
                .setParameter("applicationId", applicationId)
                .getResultList()
                .stream()
                .map(h -> new ApplicationStatusHistoryResponse(
                        h.getId(),
                        h.getStatus().name(),
                        h.getChangedAt(),
                        h.getComment()
                ))
                .toList();
    }
}
