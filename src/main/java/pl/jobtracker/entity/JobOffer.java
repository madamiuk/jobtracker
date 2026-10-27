package pl.jobtracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "job_offer")
public class JobOffer {

    public enum SourceType {
        LINKEDIN,
        PRACUJ,
        JUSTJOINIT,
        NOFLUFFJOBS,
        COMPANY_WEBSITE,
        RECRUITER,
        REFERRAL,
        JOB_BOARD,
        OTHER
    }

    public enum WorkMode {
        REMOTE,
        HYBRID,
        ONSITE
    }

    public enum EmploymentType {
        EMPLOYMENT_CONTRACT,
        B2B,
        MANDATE,
        OTHER
    }

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "work_mode")
    private WorkMode workMode;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "position_title", nullable = false, length = 255)
    private String positionTitle;

    @Column(length = 255)
    private String location;

    @Column(name = "salary_min", precision = 12, scale = 2)
    private BigDecimal salaryMin;

    @Column(name = "salary_max", precision = 12, scale = 2)
    private BigDecimal salaryMax;

    @Column(name = "salary_currency", length = 3)
    private String salaryCurrency;

    @Column(name = "salary_period", length = 50)
    private String salaryPeriod;

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public String getLocation() {
        return location;
    }

    public BigDecimal getSalaryMin() {
        return salaryMin;
    }

    public BigDecimal getSalaryMax() {
        return salaryMax;
    }

    public String getSalaryCurrency() {
        return salaryCurrency;
    }

    public String getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSalaryMin(BigDecimal salaryMin) {
        this.salaryMin = salaryMin;
    }

    public void setSalaryMax(BigDecimal salaryMax) {
        this.salaryMax = salaryMax;
    }

    public void setSalaryCurrency(String salaryCurrency) {
        this.salaryCurrency = salaryCurrency;
    }

    public void setSalaryPeriod(String salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public void setWorkMode(WorkMode workMode) {
        this.workMode = workMode;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public WorkMode getWorkMode() {
        return workMode;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

}
