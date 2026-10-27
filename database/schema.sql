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

