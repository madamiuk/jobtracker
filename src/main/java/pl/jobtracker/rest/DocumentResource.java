package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pl.jobtracker.dto.DocumentResponse;
import pl.jobtracker.entity.Document;

import java.util.List;

@Path("/applications/{applicationId}/documents")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class DocumentResource {

    @PersistenceContext(unitName = "JobTrackerPU")
    private EntityManager entityManager;

    @GET
    public List<DocumentResponse> documents(@PathParam("applicationId") Long applicationId) {
        return entityManager.createQuery(
                        """
                        SELECT d
                        FROM Document d
                        WHERE d.application.id = :applicationId
                        ORDER BY d.sentAt, d.id
                        """,
                        Document.class)
                .setParameter("applicationId", applicationId)
                .getResultList()
                .stream()
                .map(d -> new DocumentResponse(
                        d.getId(),
                        d.getDocumentType().name(),
                        d.getFileName(),
                        d.getFilePath(),
                        d.getVersion(),
                        d.getSentAt(),
                        d.getSha256(),
                        d.getNotes()
                ))
                .toList();
    }
}
