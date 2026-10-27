package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import pl.jobtracker.dto.ContactEventResponse;
import pl.jobtracker.entity.ContactEvent;

import java.util.List;

@Path("/applications/{applicationId}/contacts")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class ContactEventResource {

    @PersistenceContext(unitName = "JobTrackerPU")
    private EntityManager entityManager;

    @GET
    public List<ContactEventResponse> contacts(
            @PathParam("applicationId") Long applicationId) {

        return entityManager
                .createQuery(
                        """
                        SELECT ce
                        FROM ContactEvent ce
                        LEFT JOIN FETCH ce.recruiter
                        WHERE ce.application.id = :applicationId
                        ORDER BY ce.contactAt, ce.id
                        """,
                        ContactEvent.class
                )
                .setParameter("applicationId", applicationId)
                .getResultList()
                .stream()
                .map(ce -> new ContactEventResponse(
                        ce.getId(),
                        ce.getContactType().name(),
                        ce.getContactAt(),
                        ce.getDirection().name(),
                        ce.getSubject(),
                        ce.getDescription(),
                        ce.isFollowUpRequired(),
                        ce.getFollowUpAt(),
                        ce.isFollowUpDone(),
                        ce.getRecruiter() != null
                                ? ce.getRecruiter().getFirstName() + " " + ce.getRecruiter().getLastName()
                                : null
                ))
                .toList();
    }
}
