package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import pl.jobtracker.dto.InterviewResponse;
import pl.jobtracker.entity.Interview;

import java.util.List;

@Path("/applications/{applicationId}/interviews")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class InterviewResource {

    @PersistenceContext(unitName = "JobTrackerPU")
    private EntityManager entityManager;

    @GET
    public List<InterviewResponse> interviews(
            @PathParam("applicationId") Long applicationId) {

        return entityManager
                .createQuery(
                        """
                        SELECT i
                        FROM Interview i
                        WHERE i.application.id = :applicationId
                        ORDER BY i.scheduledAt, i.id
                        """,
                        Interview.class
                )
                .setParameter("applicationId", applicationId)
                .getResultList()
                .stream()
                .map(i -> new InterviewResponse(
                        i.getId(),
                        i.getInterviewType().name(),
                        i.getScheduledAt(),
                        i.getDurationMinutes(),
                        i.getLocationType() != null ? i.getLocationType().name() : null,
                        i.getLocationDetails(),
                        i.getResult(),
                        i.getSummary(),
                        i.getQuestions(),
                        i.getMyNotes()
                ))
                .toList();
    }
}
