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
@Table(name = "contact_event")
public class ContactEvent {

    public enum ContactType {
        EMAIL,
        PHONE,
        LINKEDIN,
        MESSAGE,
        FOLLOW_UP,
        DOCUMENT_REQUEST,
        FEEDBACK,
        OTHER
    }

    public enum ContactDirection {
        INBOUND,
        OUTBOUND
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id")
    private Recruiter recruiter;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type", nullable = false)
    private ContactType contactType;

    @Column(name = "contact_at", nullable = false)
    private OffsetDateTime contactAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactDirection direction;

    @Column(length = 255)
    private String subject;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "follow_up_required", nullable = false)
    private boolean followUpRequired;

    @Column(name = "follow_up_at")
    private OffsetDateTime followUpAt;

    @Column(name = "follow_up_done", nullable = false)
    private boolean followUpDone;

    public Long getId() {
        return id;
    }

    public JobApplication getApplication() {
        return application;
    }

    public Recruiter getRecruiter() {
        return recruiter;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public OffsetDateTime getContactAt() {
        return contactAt;
    }

    public ContactDirection getDirection() {
        return direction;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFollowUpRequired() {
        return followUpRequired;
    }

    public OffsetDateTime getFollowUpAt() {
        return followUpAt;
    }

    public boolean isFollowUpDone() {
        return followUpDone;
    }
}
