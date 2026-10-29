package pl.jobtracker.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pl.jobtracker.dto.CreateJobOfferRequest;
import pl.jobtracker.dto.JobOfferResponse;
import pl.jobtracker.entity.Company;
import pl.jobtracker.entity.JobOffer;

import java.util.List;

@Path("/offers")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class JobOfferResource {

    @PersistenceContext(unitName = "JobTrackerPU")
    private EntityManager entityManager;

    static boolean isValidSourceType(String value) {
        try {
            JobOffer.SourceType.valueOf(value);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    static boolean isValidWorkMode(String value) {
        if (value == null) {
            return true;
        }

        try {
            JobOffer.WorkMode.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    static boolean isValidEmploymentType(String value) {
        if (value == null) {
            return true;
        }

        try {
            JobOffer.EmploymentType.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    static boolean isValidCreateRequest(CreateJobOfferRequest request) {
        return request != null
                && request.companyId() != null
                && request.positionTitle() != null
                && !request.positionTitle().isBlank()
                && request.sourceType() != null
                && !request.sourceType().isBlank()
                && isValidSourceType(request.sourceType())
                && isValidWorkMode(request.workMode())
                && isValidEmploymentType(request.employmentType());
    }

    private JobOfferResponse toResponse(JobOffer offer) {
        return new JobOfferResponse(
                offer.getId(),
                offer.getCompany().getName(),
                offer.getPositionTitle(),
                offer.getLocation(),
                offer.getSalaryMin(),
                offer.getSalaryMax(),
                offer.getSalaryCurrency(),
                offer.getSalaryPeriod()
        );
    }

    @GET
    public List<JobOfferResponse> listOffers() {
        return entityManager
                .createQuery(
                        """
                        SELECT jo
                        FROM JobOffer jo
                        JOIN FETCH jo.company
                        WHERE jo.status IS NULL
                           OR jo.status = :activeStatus
                        ORDER BY jo.id
                        """,
                        JobOffer.class
                )
                .setParameter(
                        "activeStatus",
                        JobOffer.OfferStatus.ACTIVE
                )
                .getResultList()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GET
    @Path("/archived")
    public List<JobOfferResponse> listArchivedOffers() {
        return entityManager
                .createQuery(
                        """
                        SELECT jo
                        FROM JobOffer jo
                        JOIN FETCH jo.company
                        WHERE jo.status = :archivedStatus
                        ORDER BY jo.id
                        """,
                        JobOffer.class
                )
                .setParameter(
                        "archivedStatus",
                        JobOffer.OfferStatus.ARCHIVED
                )
                .getResultList()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createOffer(CreateJobOfferRequest request) {

        if (!isValidCreateRequest(request)) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Invalid job offer request.")
                    .build();
        }

        Company company = entityManager.find(
                Company.class,
                request.companyId()
        );

        if (company == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Company not found.")
                    .build();
        }

        JobOffer offer = new JobOffer();

        offer.setCompany(company);
        offer.setPositionTitle(request.positionTitle());
        offer.setLocation(request.location());
        offer.setSalaryMin(request.salaryMin());
        offer.setSalaryMax(request.salaryMax());
        offer.setSalaryCurrency(request.salaryCurrency());
        offer.setSalaryPeriod(request.salaryPeriod());

        offer.setSourceType(
                JobOffer.SourceType.valueOf(request.sourceType())
        );

        if (request.workMode() != null) {
            offer.setWorkMode(
                    JobOffer.WorkMode.valueOf(request.workMode())
            );
        }

        if (request.employmentType() != null) {
            offer.setEmploymentType(
                    JobOffer.EmploymentType.valueOf(
                            request.employmentType()
                    )
            );
        }

        offer.setStatus(
                JobOffer.OfferStatus.ACTIVE
        );

        entityManager.persist(offer);
        entityManager.flush();

        return Response
                .status(Response.Status.CREATED)
                .entity(toResponse(offer))
                .build();
    }

    @POST
    @Path("/{offerId}/archive")
    @Transactional
    public Response archiveOffer(
            @PathParam("offerId") Long offerId
    ) {

        JobOffer offer = entityManager.find(
                JobOffer.class,
                offerId
        );

        if (offer == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Job offer not found.")
                    .build();
        }

        offer.setStatus(
                JobOffer.OfferStatus.ARCHIVED
        );

        entityManager.flush();

        return Response
                .ok(toResponse(offer))
                .build();
    }
}
