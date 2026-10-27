package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import pl.jobtracker.dto.JobOfferResponse;
import pl.jobtracker.entity.JobOffer;

import java.util.List;

@Path("/offers")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class JobOfferResource {

    @PersistenceContext(unitName = "JobTrackerPU")
    private EntityManager entityManager;

    @GET
    public List<JobOfferResponse> listOffers() {
        List<JobOffer> offers = entityManager
                .createQuery(
                        """
                        SELECT jo
                        FROM JobOffer jo
                        JOIN FETCH jo.company
                        ORDER BY jo.id
                        """,
                        JobOffer.class
                )
                .getResultList();

        return offers.stream()
                .map(offer -> new JobOfferResponse(
                        offer.getId(),
                        offer.getCompany().getName(),
                        offer.getPositionTitle(),
                        offer.getLocation(),
                        offer.getSalaryMin(),
                        offer.getSalaryMax(),
                        offer.getSalaryCurrency(),
                        offer.getSalaryPeriod()
                ))
                .toList();
    }
}
