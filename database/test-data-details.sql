BEGIN;

-- Technologie
INSERT INTO technology (name, category)
VALUES
    ('Linux', 'Operating System'),
    ('Ansible', 'Automation'),
    ('WildFly', 'Application Server'),
    ('PostgreSQL', 'Database'),
    ('Nagios', 'Monitoring')
ON CONFLICT (name) DO NOTHING;

-- Powiązanie technologii z ostatnią ofertą testową
INSERT INTO job_offer_technology (
    job_offer_id,
    technology_id,
    requirement_level
)
SELECT
    jo.id,
    t.id,
    x.requirement_level::requirement_level
FROM job_offer jo
JOIN (
    VALUES
        ('Linux', 'REQUIRED'),
        ('Ansible', 'REQUIRED'),
        ('WildFly', 'PREFERRED'),
        ('PostgreSQL', 'PREFERRED'),
        ('Nagios', 'OPTIONAL')
) AS x(name, requirement_level)
    ON TRUE
JOIN technology t
    ON t.name = x.name
WHERE jo.position_title = 'Linux System Engineer'
ORDER BY jo.id DESC
ON CONFLICT (job_offer_id, technology_id) DO NOTHING;

-- Kontakt z rekruterem
INSERT INTO contact_event (
    application_id,
    recruiter_id,
    contact_type,
    contact_at,
    direction,
    subject,
    description,
    follow_up_required,
    follow_up_at
)
SELECT
    ja.id,
    r.id,
    'EMAIL',
    CURRENT_TIMESTAMP,
    'INBOUND',
    'Zaproszenie na rozmowę HR',
    'Rekruter przesłał zaproszenie na rozmowę przez Microsoft Teams.',
    true,
    CURRENT_TIMESTAMP + INTERVAL '1 day'
FROM job_application ja
JOIN job_offer jo
    ON jo.id = ja.job_offer_id
JOIN application_recruiter ar
    ON ar.application_id = ja.id
JOIN recruiter r
    ON r.id = ar.recruiter_id
WHERE jo.position_title = 'Linux System Engineer'
ORDER BY ja.id DESC
LIMIT 1;

-- Dokument wysłany rekruterowi
INSERT INTO document (
    application_id,
    document_type,
    file_name,
    file_path,
    version,
    sent_at,
    sha256,
    notes
)
SELECT
    ja.id,
    'CV',
    'Miroslaw_Adamiuk_CV.pdf',
    'documents/Miroslaw_Adamiuk_CV.pdf',
    '1.0',
    CURRENT_TIMESTAMP,
    repeat('0', 64),
    'Testowy wpis reprezentujący CV wysłane w procesie rekrutacyjnym.'
FROM job_application ja
JOIN job_offer jo
    ON jo.id = ja.job_offer_id
WHERE jo.position_title = 'Linux System Engineer'
ORDER BY ja.id DESC
LIMIT 1
ON CONFLICT (application_id, file_name, version) DO NOTHING;

-- Oczekiwania finansowe kandydata
INSERT INTO salary_proposal (
    application_id,
    proposal_type,
    amount_min,
    amount_max,
    currency,
    period,
    proposed_at,
    notes
)
SELECT
    ja.id,
    'MY_EXPECTATION',
    17000,
    19000,
    'PLN',
    'MONTH',
    CURRENT_TIMESTAMP,
    'Oczekiwania finansowe podane w procesie rekrutacyjnym.'
FROM job_application ja
JOIN job_offer jo
    ON jo.id = ja.job_offer_id
WHERE jo.position_title = 'Linux System Engineer'
ORDER BY ja.id DESC
LIMIT 1;

-- Notatka
INSERT INTO note (
    application_id,
    content
)
SELECT
    ja.id,
    'Przygotować przykłady automatyzacji Ansible, monitoringu Nagios i administracji WildFly.'
FROM job_application ja
JOIN job_offer jo
    ON jo.id = ja.job_offer_id
WHERE jo.position_title = 'Linux System Engineer'
ORDER BY ja.id DESC
LIMIT 1;

COMMIT;
