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

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
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

    public void setApplication(JobApplication application) {
        this.application = application;
    }

    public void setStatus(JobApplication.ApplicationStatus status) {
        this.status = status;
    }

    public void setChangedAt(OffsetDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
