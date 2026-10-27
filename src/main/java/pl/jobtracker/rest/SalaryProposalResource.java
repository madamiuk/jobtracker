package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pl.jobtracker.dto.SalaryProposalResponse;
import pl.jobtracker.entity.SalaryProposal;

import java.util.List;

@Path("/applications/{applicationId}/salary-proposals")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class SalaryProposalResource {

    @PersistenceContext(unitName = "JobTrackerPU")
    private EntityManager entityManager;

    @GET
    public List<SalaryProposalResponse> salaryProposals(
            @PathParam("applicationId") Long applicationId) {

        return entityManager.createQuery(
                        """
                        SELECT s
                        FROM SalaryProposal s
                        WHERE s.application.id = :applicationId
                        ORDER BY s.proposedAt, s.id
                        """,
                        SalaryProposal.class)
                .setParameter("applicationId", applicationId)
                .getResultList()
                .stream()
                .map(s -> new SalaryProposalResponse(
                        s.getId(),
                        s.getProposalType().name(),
                        s.getAmountMin(),
                        s.getAmountMax(),
                        s.getCurrency(),
                        s.getPeriod(),
                        s.getProposedAt(),
                        s.getNotes()
                ))
                .toList();
    }
}
