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
@Table(name = "interview")
public class Interview {

    public enum InterviewType {
        HR,
        TECHNICAL,
        MANAGER,
        TASK_REVIEW,
        FINAL,
        OTHER
    }

    public enum InterviewLocationType {
        PHONE,
        TEAMS,
        ZOOM,
        GOOGLE_MEET,
        ONSITE,
        OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_type", nullable = false)
    private InterviewType interviewType;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type")
    private InterviewLocationType locationType;

    @Column(name = "location_details", columnDefinition = "text")
    private String locationDetails;

    @Column(length = 100)
    private String result;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(columnDefinition = "text")
    private String questions;

    @Column(name = "my_notes", columnDefinition = "text")
    private String myNotes;

    public Long getId() {
        return id;
    }

    public JobApplication getApplication() {
        return application;
    }

    public InterviewType getInterviewType() {
        return interviewType;
    }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public InterviewLocationType getLocationType() {
        return locationType;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public String getResult() {
        return result;
    }

    public String getSummary() {
        return summary;
    }

    public String getQuestions() {
        return questions;
    }

    public String getMyNotes() {
        return myNotes;
    }
}
