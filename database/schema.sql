CREATE TYPE source_type AS ENUM (
    'LINKEDIN',
    'PRACUJ',
    'JUSTJOINIT',
    'NOFLUFFJOBS',
    'COMPANY_WEBSITE',
    'RECRUITER',
    'REFERRAL',
    'JOB_BOARD',
    'OTHER'
);

CREATE TYPE work_mode AS ENUM (
    'REMOTE',
    'HYBRID',
    'ONSITE'
);

CREATE TYPE employment_type AS ENUM (
    'EMPLOYMENT_CONTRACT',
    'B2B',
    'MANDATE',
    'OTHER'
);

CREATE TYPE application_status AS ENUM (
    'PLANNED',
    'APPLIED',
    'HR_SCREENING',
    'HR_INTERVIEW',
    'TECH_INTERVIEW',
    'TASK',
    'MANAGER_INTERVIEW',
    'FINAL_INTERVIEW',
    'WAITING',
    'OFFER',
    'REJECTED',
    'WITHDRAWN',
    'HIRED'
);

CREATE TABLE company (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    website VARCHAR(500),
    industry VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(100),
    description TEXT,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE job_offer (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES company(id),

    position_title VARCHAR(255) NOT NULL,
    offer_url VARCHAR(1000),
    description TEXT,
    original_text TEXT,

    source_type source_type NOT NULL,
    source_url VARCHAR(1000),
    source_details TEXT,

    work_mode work_mode,
    employment_type employment_type,

    location VARCHAR(255),

    salary_min NUMERIC(12,2),
    salary_max NUMERIC(12,2),
    salary_currency CHAR(3),
    salary_period VARCHAR(50),

    found_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ,

    status VARCHAR(50),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CHECK (
        salary_min IS NULL
        OR salary_max IS NULL
        OR salary_min <= salary_max
    )
);

CREATE TABLE job_application (
    id BIGSERIAL PRIMARY KEY,
    job_offer_id BIGINT NOT NULL REFERENCES job_offer(id),

    applied_at TIMESTAMPTZ,
    current_status application_status NOT NULL DEFAULT 'PLANNED',

    priority SMALLINT,
    fit_score SMALLINT,
    interest_score SMALLINT,

    expected_salary_min NUMERIC(12,2),
    expected_salary_max NUMERIC(12,2),
    expected_salary_currency CHAR(3),

    final_result VARCHAR(100),
    closed_at TIMESTAMPTZ,
    closing_reason TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CHECK (priority IS NULL OR priority BETWEEN 1 AND 5),
    CHECK (fit_score IS NULL OR fit_score BETWEEN 1 AND 5),
    CHECK (interest_score IS NULL OR interest_score BETWEEN 1 AND 5),
    CHECK (
        expected_salary_min IS NULL
        OR expected_salary_max IS NULL
        OR expected_salary_min <= expected_salary_max
    )
);

CREATE TYPE requirement_level AS ENUM (
    'REQUIRED',
    'PREFERRED',
    'OPTIONAL'
);

CREATE TYPE interview_type AS ENUM (
    'HR',
    'TECHNICAL',
    'MANAGER',
    'TASK_REVIEW',
    'FINAL',
    'OTHER'
);

CREATE TYPE interview_location_type AS ENUM (
    'PHONE',
    'TEAMS',
    'ZOOM',
    'GOOGLE_MEET',
    'ONSITE',
    'OTHER'
);

CREATE TABLE technology (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    category VARCHAR(100)
);

CREATE TABLE job_offer_technology (
    job_offer_id BIGINT NOT NULL REFERENCES job_offer(id) ON DELETE CASCADE,
    technology_id BIGINT NOT NULL REFERENCES technology(id),
    requirement_level requirement_level NOT NULL,

    PRIMARY KEY (job_offer_id, technology_id)
);

CREATE TABLE recruiter (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES company(id),

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    position VARCHAR(255),

    email VARCHAR(255),
    phone VARCHAR(50),
    linkedin_url VARCHAR(500),

    notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE application_status_history (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES job_application(id) ON DELETE CASCADE,

    status application_status NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comment TEXT
);

CREATE TABLE interview (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES job_application(id) ON DELETE CASCADE,

    interview_type interview_type NOT NULL,
    scheduled_at TIMESTAMPTZ NOT NULL,
    duration_minutes INTEGER,

    location_type interview_location_type,
    location_details TEXT,

    result VARCHAR(100),
    summary TEXT,
    questions TEXT,
    my_notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CHECK (duration_minutes IS NULL OR duration_minutes > 0)
);

CREATE TABLE application_recruiter (
    application_id BIGINT NOT NULL REFERENCES job_application(id) ON DELETE CASCADE,
    recruiter_id BIGINT NOT NULL REFERENCES recruiter(id),

    PRIMARY KEY (application_id, recruiter_id)
);

CREATE INDEX idx_job_offer_company
    ON job_offer(company_id);

CREATE INDEX idx_job_application_offer
    ON job_application(job_offer_id);

CREATE INDEX idx_status_history_application
    ON application_status_history(application_id);

CREATE INDEX idx_interview_application
    ON interview(application_id);

CREATE INDEX idx_recruiter_company
    ON recruiter(company_id);

CREATE TYPE contact_type AS ENUM (
    'EMAIL',
    'PHONE',
    'LINKEDIN',
    'MESSAGE',
    'FOLLOW_UP',
    'DOCUMENT_REQUEST',
    'FEEDBACK',
    'OTHER'
);

CREATE TYPE contact_direction AS ENUM (
    'INBOUND',
    'OUTBOUND'
);

CREATE TYPE document_type AS ENUM (
    'CV',
    'COVER_LETTER',
    'PORTFOLIO',
    'CERTIFICATE',
    'TASK',
    'OTHER'
);

CREATE TYPE salary_proposal_type AS ENUM (
    'JOB_OFFER',
    'MY_EXPECTATION',
    'COMPANY_INITIAL',
    'COMPANY_FINAL',
    'ACCEPTED'
);

CREATE TABLE contact_event (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES job_application(id) ON DELETE CASCADE,
    recruiter_id BIGINT REFERENCES recruiter(id),

    contact_type contact_type NOT NULL,
    contact_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    direction contact_direction NOT NULL,

    subject VARCHAR(255),
    description TEXT,

    follow_up_required BOOLEAN NOT NULL DEFAULT FALSE,
    follow_up_at TIMESTAMPTZ,
    follow_up_done BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE document (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES job_application(id) ON DELETE CASCADE,

    document_type document_type NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(1000),

    version VARCHAR(50),
    sent_at TIMESTAMPTZ,

    sha256 CHAR(64),
    notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (application_id, file_name, version)
);

CREATE TABLE salary_proposal (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES job_application(id) ON DELETE CASCADE,

    proposal_type salary_proposal_type NOT NULL,

    amount_min NUMERIC(12,2),
    amount_max NUMERIC(12,2),
    currency CHAR(3),
    period VARCHAR(50),

    proposed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,

    CHECK (
        amount_min IS NULL
        OR amount_max IS NULL
        OR amount_min <= amount_max
    )
);

CREATE TABLE note (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES job_application(id) ON DELETE CASCADE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    content TEXT NOT NULL
);

CREATE INDEX idx_contact_event_application
    ON contact_event(application_id);

CREATE INDEX idx_contact_event_recruiter
    ON contact_event(recruiter_id);

CREATE INDEX idx_document_application
    ON document(application_id);

CREATE INDEX idx_salary_proposal_application
    ON salary_proposal(application_id);

CREATE INDEX idx_note_application
    ON note(application_id);
