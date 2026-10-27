package pl.jobtracker.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "document")
public class Document {

    public enum DocumentType {
        CV,
        COVER_LETTER,
        PORTFOLIO,
        CERTIFICATE,
        TASK,
        OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_path", length = 1000)
    private String filePath;

    @Column(length = 50)
    private String version;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Column(length = 64)
    private String sha256;

    @Column(columnDefinition = "text")
    private String notes;

    public Long getId() { return id; }
    public DocumentType getDocumentType() { return documentType; }
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public String getVersion() { return version; }
    public OffsetDateTime getSentAt() { return sentAt; }
    public String getSha256() { return sha256; }
    public String getNotes() { return notes; }
}
