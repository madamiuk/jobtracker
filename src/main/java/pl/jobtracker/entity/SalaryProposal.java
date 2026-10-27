package pl.jobtracker.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "salary_proposal")
public class SalaryProposal {

    public enum ProposalType {
        JOB_OFFER,
        MY_EXPECTATION,
        COMPANY_INITIAL,
        COMPANY_FINAL,
        ACCEPTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "proposal_type", nullable = false)
    private ProposalType proposalType;

    @Column(name = "amount_min", precision = 12, scale = 2)
    private BigDecimal amountMin;

    @Column(name = "amount_max", precision = 12, scale = 2)
    private BigDecimal amountMax;

    @Column(length = 3)
    private String currency;

    @Column(length = 50)
    private String period;

    @Column(name = "proposed_at", nullable = false)
    private OffsetDateTime proposedAt;

    @Column(columnDefinition = "text")
    private String notes;

    public Long getId() { return id; }
    public ProposalType getProposalType() { return proposalType; }
    public BigDecimal getAmountMin() { return amountMin; }
    public BigDecimal getAmountMax() { return amountMax; }
    public String getCurrency() { return currency; }
    public String getPeriod() { return period; }
    public OffsetDateTime getProposedAt() { return proposedAt; }
    public String getNotes() { return notes; }
}
