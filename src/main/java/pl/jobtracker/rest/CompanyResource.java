package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import pl.jobtracker.entity.Company;

import java.util.List;

@Path("/companies")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class CompanyResource {

    @PersistenceContext(unitName = "JobTrackerPU")
    private EntityManager entityManager;

    @GET
    public List<Company> listCompanies() {
        return entityManager
                .createQuery(
                        "SELECT c FROM Company c ORDER BY c.name",
                        Company.class
                )
                .getResultList();
    }
}
