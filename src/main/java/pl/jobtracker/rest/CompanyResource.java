package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import pl.jobtracker.entity.Company;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.Response;
import pl.jobtracker.dto.CreateCompanyRequest;

import java.util.List;

@Path("/companies")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class CompanyResource {

    static boolean isValidCreateRequest(CreateCompanyRequest request) {
    return request != null
            && request.name() != null
            && !request.name().isBlank();
    }

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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createCompany(CreateCompanyRequest request) {

        if (!isValidCreateRequest(request)) {

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Company name is required.")
                    .build();
        }

        Company company = new Company();

        company.setName(request.name());
        company.setWebsite(request.website());
        company.setIndustry(request.industry());
        company.setCity(request.city());
        company.setCountry(request.country());
        company.setDescription(request.description());
        company.setNotes(request.notes());

        entityManager.persist(company);
        entityManager.flush();

        return Response
                .status(Response.Status.CREATED)
                .entity(company)
                .build();
    }
}
