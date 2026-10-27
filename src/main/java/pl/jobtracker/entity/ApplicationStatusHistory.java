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

import java.time.OffsetDateTime;

@Entity
@Table(name = "application_status_history")
public class ApplicationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobApplication.ApplicationStatus status;

    @Column(name = "changed_at", nullable = false)
    private OffsetDateTime changedAt;

    @Column(columnDefinition = "text")
    private String comment;

    public Long getId() {
        return id;
    }

    public JobApplication getApplication() {
        return application;
    }

    public JobApplication.ApplicationStatus getStatus() {
        return status;
    }

    public OffsetDateTime getChangedAt() {
        return changedAt;
    }

    public String getComment() {
        return comment;
    }
}
