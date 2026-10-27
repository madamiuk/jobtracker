package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pl.jobtracker.dto.NoteResponse;
import pl.jobtracker.entity.Note;

import java.util.List;

@Path("/applications/{applicationId}/notes")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class NoteResource {

    @PersistenceContext(unitName = "JobTrackerPU")
    private EntityManager entityManager;

    @GET
    public List<NoteResponse> notes(@PathParam("applicationId") Long applicationId) {
        return entityManager.createQuery(
                        """
                        SELECT n
                        FROM Note n
                        WHERE n.application.id = :applicationId
                        ORDER BY n.createdAt, n.id
                        """,
                        Note.class)
                .setParameter("applicationId", applicationId)
                .getResultList()
                .stream()
                .map(n -> new NoteResponse(
                        n.getId(),
                        n.getCreatedAt(),
                        n.getContent()
                ))
                .toList();
    }
}
