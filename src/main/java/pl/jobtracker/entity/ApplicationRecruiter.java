package pl.jobtracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "application_recruiter")
@IdClass(ApplicationRecruiter.ApplicationRecruiterId.class)
public class ApplicationRecruiter {

    @Id
    @Column(name = "application_id")
    private Long applicationId;

    @Id
    @Column(name = "recruiter_id")
    private Long recruiterId;

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getRecruiterId() {
        return recruiterId;
    }

    public static class ApplicationRecruiterId implements Serializable {

        private Long applicationId;
        private Long recruiterId;

        public ApplicationRecruiterId() {
        }

        public ApplicationRecruiterId(Long applicationId, Long recruiterId) {
            this.applicationId = applicationId;
            this.recruiterId = recruiterId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ApplicationRecruiterId that)) return false;
            return Objects.equals(applicationId, that.applicationId)
                    && Objects.equals(recruiterId, that.recruiterId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(applicationId, recruiterId);
        }
    }
}
