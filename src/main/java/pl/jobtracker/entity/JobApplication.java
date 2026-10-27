package pl.jobtracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "job_application")
public class JobApplication {

    public enum ApplicationStatus {
        PLANNED,
        APPLIED,
        HR_SCREENING,
        HR_INTERVIEW,
        TECH_INTERVIEW,
        TASK,
        MANAGER_INTERVIEW,
        FINAL_INTERVIEW,
        WAITING,
        OFFER,
        REJECTED,
        WITHDRAWN,
        HIRED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_offer_id", nullable = false)
    private JobOffer jobOffer;

    @Column(name = "applied_at")
    private OffsetDateTime appliedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    private ApplicationStatus currentStatus;

    private Short priority;

    @Column(name = "fit_score")
    private Short fitScore;

    @Column(name = "interest_score")
    private Short interestScore;

    @Column(name = "expected_salary_min", precision = 12, scale = 2)
    private BigDecimal expectedSalaryMin;

    @Column(name = "expected_salary_max", precision = 12, scale = 2)
    private BigDecimal expectedSalaryMax;

    @Column(name = "expected_salary_currency", length = 3)
    private String expectedSalaryCurrency;

    public Long getId() {
        return id;
    }

    public JobOffer getJobOffer() {
        return jobOffer;
    }

    public OffsetDateTime getAppliedAt() {
        return appliedAt;
    }

    public ApplicationStatus getCurrentStatus() {
        return currentStatus;
    }

    public Short getPriority() {
        return priority;
    }

    public Short getFitScore() {
        return fitScore;
    }

    public Short getInterestScore() {
        return interestScore;
    }

    public BigDecimal getExpectedSalaryMin() {
        return expectedSalaryMin;
    }

    public BigDecimal getExpectedSalaryMax() {
        return expectedSalaryMax;
    }

    public String getExpectedSalaryCurrency() {
        return expectedSalaryCurrency;
    }
}
