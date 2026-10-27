BEGIN;

WITH new_company AS (
    INSERT INTO company (
        name,
        website,
        industry,
        city,
        country,
        description,
        notes
    )
    VALUES (
        'Example Tech Sp. z o.o.',
        'https://example.com',
        'IT / Infrastructure',
        'Warszawa',
        'Polska',
        'Firma testowa używana do walidacji modelu JobTracker.',
        'Dane testowe.'
    )
    RETURNING id
),
new_offer AS (
    INSERT INTO job_offer (
        company_id,
        position_title,
        offer_url,
        description,
        original_text,
        source_type,
        source_url,
        source_details,
        work_mode,
        employment_type,
        location,
        salary_min,
        salary_max,
        salary_currency,
        salary_period,
        status
    )
    SELECT
        id,
        'Linux System Engineer',
        'https://example.com/jobs/linux-system-engineer',
        'Administracja Linux, automatyzacja i monitoring.',
        'Przykładowa treść ogłoszenia rekrutacyjnego.',
        'COMPANY_WEBSITE',
        'https://example.com/careers',
        'Oferta znaleziona na stronie pracodawcy.',
        'HYBRID',
        'EMPLOYMENT_CONTRACT',
        'Warszawa',
        15000,
        19000,
        'PLN',
        'MONTH',
        'ACTIVE'
    FROM new_company
    RETURNING id, company_id
),
new_application AS (
    INSERT INTO job_application (
        job_offer_id,
        applied_at,
        current_status,
        priority,
        fit_score,
        interest_score,
        expected_salary_min,
        expected_salary_max,
        expected_salary_currency
    )
    SELECT
        id,
        CURRENT_TIMESTAMP,
        'HR_INTERVIEW',
        4,
        5,
        4,
        17000,
        19000,
        'PLN'
    FROM new_offer
    RETURNING id, job_offer_id
),
new_recruiter AS (
    INSERT INTO recruiter (
        company_id,
        first_name,
        last_name,
        position,
        email,
        notes
    )
    SELECT
        company_id,
        'Anna',
        'Kowalska',
        'IT Recruiter',
        'anna.kowalska@example.com',
        'Kontakt testowy.'
    FROM new_offer
    RETURNING id
)
INSERT INTO application_recruiter (
    application_id,
    recruiter_id
)
SELECT
    a.id,
    r.id
FROM new_application a
CROSS JOIN new_recruiter r;

INSERT INTO application_status_history (
    application_id,
    status,
    comment
)
SELECT
    id,
    'APPLIED',
    'Aplikacja wysłana.'
FROM job_application
ORDER BY id DESC
LIMIT 1;

INSERT INTO application_status_history (
    application_id,
    status,
    comment
)
SELECT
    id,
    'HR_INTERVIEW',
    'Zaproszenie na rozmowę HR.'
FROM job_application
ORDER BY id DESC
LIMIT 1;

INSERT INTO interview (
    application_id,
    interview_type,
    scheduled_at,
    duration_minutes,
    location_type,
    location_details,
    result,
    summary,
    questions,
    my_notes
)
SELECT
    id,
    'HR',
    CURRENT_TIMESTAMP + INTERVAL '2 days',
    45,
    'TEAMS',
    'Microsoft Teams',
    'PLANNED',
    'Pierwsza rozmowa z rekruterem.',
    'Doświadczenie z Linux, Ansible i monitoringiem.',
    'Przygotować przykłady automatyzacji.'
FROM job_application
ORDER BY id DESC
LIMIT 1;

COMMIT;
