# JobTracker

[![JobTracker CI](https://github.com/madamiuk/jobtracker/actions/workflows/jobtracker-ci.yml/badge.svg)](https://github.com/madamiuk/jobtracker/actions/workflows/jobtracker-ci.yml)

JobTracker is a practical job application tracking system built with **Java 21**, **Jakarta EE 11**, **WildFly** and **PostgreSQL 18**.

The project is developed as both:

- a useful recruitment management application,
- a realistic Java / Linux / DevOps laboratory.

It integrates application development with:

- Maven
- GitHub Actions
- Ansible
- Terraform
- Oracle Cloud Infrastructure
- Nagios
- automated CI/CD
- runtime verification
- end-to-end database testing

---

## Current status

JobTracker is a working full-stack application deployed on Oracle Cloud Infrastructure.

Current capabilities include:

- Java 21 backend
- Jakarta EE 11
- Jakarta REST API
- Jakarta Persistence / Hibernate
- PostgreSQL persistence
- WildFly JTA datasource
- interactive web interface
- company management
- job offer management
- job application management
- application status history
- recruiters
- interviews
- contact history
- notes
- documents
- salary proposals
- active and archived job offers
- Maven build
- automated application tests
- GitHub Actions CI
- WAR artifact generation
- automated cross-repository CD
- Ansible deployment
- deployment preflight validation
- artifact checksum verification
- automatic rollback
- REST runtime verification
- full end-to-end database verification
- Nagios monitoring

The complete CI/CD path has been successfully validated from source commit through OCI deployment and PostgreSQL verification.

---

## Architecture

The laboratory is split across three independent GitHub repositories:

```text
madamiuk/jobtracker
        |
        | Java application
        | Maven build
        | automated tests
        | GitHub Actions CI
        | WAR artifact
        v

madamiuk/ansible-lab
        |
        | configuration management
        | WildFly
        | PostgreSQL
        | Nagios
        | application deployment
        | runtime verification
        | E2E tests
        v

madamiuk/oci-terraform-lab
        |
        | Terraform
        | OCI networking
        | OCI Compute
        | OCI Monitoring
        v

Oracle Cloud Infrastructure
```

Responsibilities are intentionally separated:

```text
Terraform  -> infrastructure
Ansible    -> configuration and deployment
JobTracker -> application code and business logic
```

---

## OCI environment

Current application topology:

```text
Oracle Cloud Infrastructure
│
├── oci-ansible01
│   ├── Oracle Linux 9
│   ├── Java 21
│   ├── WildFly
│   └── JobTracker
│
├── oci-ansible02
│   ├── Oracle Linux 9
│   └── Nagios
│
└── oci-ansible03
    ├── Oracle Linux 9
    ├── PostgreSQL 18
    └── jobtracker database
```

Private addressing:

```text
oci-ansible01    10.10.1.37
oci-ansible02    10.10.1.176
oci-ansible03    10.10.1.26
```

Application-to-database communication uses the private OCI network.

PostgreSQL runs on the dedicated database host `oci-ansible03`. It does not run locally on the WildFly application server.

---

## Application architecture

```text
Web browser
     |
     v
HTML / CSS / JavaScript
     |
     v
Jakarta REST API
     |
     v
JPA / Hibernate
     |
     v
WildFly JTA datasource
     |
     v
PostgreSQL 18
```

The frontend is packaged directly inside:

```text
jobtracker.war
```

and is served by WildFly together with the REST API.

Frontend source files are stored under:

```text
src/main/webapp/
```

including:

```text
src/main/webapp/
├── index.html
├── app.js
└── styles.css
```

---

## Recruitment model

The primary domain relationship is:

```text
Company
   |
   v
Job Offer
   |
   v
Job Application
```

A job application may contain:

```text
Job Application
    |
    +--> Status history
    +--> Recruiters
    +--> Interviews
    +--> Contact events
    +--> Notes
    +--> Documents
    +--> Salary proposals
```

The current application status and its history are deliberately separated.

Example:

```text
APPLIED
   |
   v
HR_SCREENING
   |
   v
HR_INTERVIEW
   |
   v
TECH_INTERVIEW
```

Current state is stored in:

```text
job_application.current_status
```

Historical transitions are stored in:

```text
application_status_history
```

This preserves recruitment history while still allowing efficient access to the current state.

---

## Job offer lifecycle

Job offers are archived instead of physically deleted.

Current lifecycle:

```text
ACTIVE
   |
   v
ARCHIVED
```

Archived offers:

- remain stored in PostgreSQL,
- remain associated with existing applications,
- remain available for historical analysis,
- are excluded from the normal new-application workflow.

This avoids broken historical relationships.

---

## Web interface

The application frontend currently supports:

- creating companies,
- creating job offers,
- creating job applications,
- viewing active job offers,
- archiving job offers,
- viewing archived job offers,
- viewing job applications,
- opening application details,
- viewing status history,
- viewing recruiters,
- viewing interviews,
- viewing contact events,
- viewing notes,
- viewing documents,
- viewing salary proposals,
- refreshing data from the REST API.

The frontend communicates directly with the Jakarta REST endpoints.

---

## REST API

The Jakarta REST application is exposed under:

```text
/api
```

### Health

```text
GET /api/health
```

### Companies

```text
GET  /api/companies
POST /api/companies
```

### Job offers

```text
GET  /api/offers
GET  /api/offers/archived
POST /api/offers
POST /api/offers/{offerId}/archive
```

### Job applications

```text
GET  /api/applications
POST /api/applications
POST /api/applications/{applicationId}/status
```

### Application details

```text
GET /api/applications/{applicationId}/history
GET /api/applications/{applicationId}/contacts
GET /api/applications/{applicationId}/interviews
GET /api/applications/{applicationId}/recruiters
GET /api/applications/{applicationId}/documents
GET /api/applications/{applicationId}/salary-proposals
GET /api/applications/{applicationId}/notes
```

---

## Database

The PostgreSQL schema is maintained explicitly in:

```text
database/schema.sql
```

Important tables include:

```text
company
job_offer
job_application
technology
job_offer_technology
recruiter
application_recruiter
application_status_history
interview
contact_event
document
salary_proposal
note
```

The application uses:

```text
Persistence unit: JobTrackerPU
Datasource:       java:/jdbc/JobTrackerDS
```

Hibernate automatic schema generation is intentionally disabled.

Database structure is managed explicitly instead of allowing the application to modify production schemas automatically.

---

## WildFly runtime

JobTracker runs on:

```text
Host:        oci-ansible01
Platform:    Oracle Linux 9
Application: WildFly
WAR:         jobtracker.war
```

Datasource:

```text
Datasource: JobTrackerDS
JNDI:       java:/jdbc/JobTrackerDS
```

Application context:

```text
/jobtracker/
```

REST context:

```text
/jobtracker/api/
```

WildFly port `8080` is not intentionally exposed publicly.

Development access can use an SSH tunnel:

```bash
ssh -L 8080:10.10.1.37:8080 opc@<PUBLIC_IP>
```

Then open:

```text
http://localhost:8080/jobtracker/
```

---

## Build

Build locally with:

```bash
mvn clean verify
```

The generated artifact is:

```text
target/jobtracker.war
```

The WAR contains both:

```text
Java backend
+
Web frontend
```

---

## Automated tests

The current test suite contains:

```text
47 automated tests
```

Coverage includes:

- domain model validation,
- persistence configuration,
- REST contracts,
- request validation,
- endpoint behavior,
- job offer lifecycle,
- application status history,
- JPA persistence interactions.

Tests validate scenarios such as:

```text
HTTP 400 for invalid requests
HTTP 404 for missing entities
HTTP 201 for successful creation
EntityManager persistence
EntityManager flush
job offer ACTIVE state
job offer archive transition
application status history creation
```

---

# CI/CD

JobTracker uses a fully automated cross-repository CI/CD pipeline.

## Continuous Integration

Workflow:

```text
.github/workflows/jobtracker-ci.yml
```

The CI workflow runs automatically for pushes and pull requests targeting:

```text
main
```

Pipeline:

```text
checkout
   |
   v
Java 21
   |
   v
Maven
   |
   v
mvn clean verify
   |
   v
47 automated tests
   |
   v
WAR validation
   |
   v
jobtracker.war
   |
   v
GitHub Actions artifact
```

The artifact is published as:

```text
jobtracker-war
```

The workflow also validates that the WAR contains critical application components including:

```text
WEB-INF/classes/pl/jobtracker/rest/HealthResource.class
WEB-INF/classes/META-INF/persistence.xml
```

---

## CD trigger

Workflow:

```text
.github/workflows/jobtracker-cd-trigger.yml
```

After a successful `JobTracker CI` run, the trigger workflow sends a:

```text
repository_dispatch
```

event to:

```text
madamiuk/ansible-lab
```

with event type:

```text
jobtracker-ci-success
```

The payload contains the exact JobTracker CI run ID.

This guarantees that deployment consumes the WAR artifact created by the CI run that triggered the deployment.

---

## Complete CI/CD flow

```text
git push to main
       |
       v
JobTracker CI
       |
       v
mvn clean verify
       |
       v
47 automated tests
       |
       v
WAR validation
       |
       v
jobtracker-war artifact
       |
       v
Trigger JobTracker CD
       |
       v
repository_dispatch
       |
       v
madamiuk/ansible-lab
       |
       v
JobTracker CD
       |
       v
self-hosted macOS ARM64 runner
       |
       v
download exact CI artifact
       |
       v
Ansible deployment
       |
       v
oci-ansible01 / WildFly
       |
       v
health + REST smoke tests
       |
       v
jobtracker_e2e.yml
       |
       v
oci-ansible03 / PostgreSQL
       |
       v
GREEN
```

The complete automated path has been successfully validated end-to-end.

---

## Deployment ownership

The JobTracker repository does not perform infrastructure deployment directly.

Responsibilities are separated:

```text
jobtracker
    |
    +--> build
    +--> tests
    +--> WAR artifact
    +--> CD trigger

ansible-lab
    |
    +--> artifact download
    +--> deployment
    +--> WildFly verification
    +--> REST smoke tests
    +--> E2E verification
```

This keeps application code independent from infrastructure implementation details.

---

## Deployment verification

Before accepting a deployment, the Ansible pipeline verifies:

```text
WAR exists
WAR is non-empty
WAR is a valid archive
required application classes exist
persistence.xml exists
WildFly datasource exists
database connection works
```

Artifact integrity is checked using SHA-256:

```text
local WAR
    |
    v
SHA-256
    |
    v
transfer
    |
    v
remote WAR
    |
    v
SHA-256
    |
    v
checksum comparison
```

After deployment:

```text
WildFly deployment marker
REST health endpoint
REST response validation
application/json validation
JSON contract validation
```

Automatic rollback is available when WildFly reports deployment failure.

---

## Runtime smoke tests

Deployment validation includes:

```text
GET /api/health
GET /api/companies
GET /api/offers
GET /api/applications
```

Checks include:

```text
HTTP 200
application/json
valid JSON
expected response structure
```

A deployment is therefore not considered successful merely because WildFly accepted the WAR.

The application itself must also respond correctly.

---

## End-to-end verification

The Ansible laboratory contains:

```text
jobtracker_e2e.yml
```

It creates a real recruitment data chain:

```text
Company
   |
   v
Job Offer
   |
   v
Job Application
```

The flow performs:

```text
POST Company
      |
      v
POST Job Offer
      |
      v
POST Job Application
      |
      v
REST verification
      |
      v
direct PostgreSQL verification
      |
      v
cleanup
```

This verifies the complete path:

```text
REST
  |
  v
Jakarta EE
  |
  v
JPA / Hibernate
  |
  v
WildFly JTA datasource
  |
  v
PostgreSQL
```

Temporary E2E data is removed after verification.

---

## Monitoring

Nagios runs on:

```text
oci-ansible02
```

Monitoring includes:

```text
PING
SSH
system load
memory
filesystem
process count
WildFly HTTP
PostgreSQL process
PostgreSQL connectivity
PostgreSQL connections
PostgreSQL database size
JobTracker
```

Monitoring configuration is maintained in the Ansible repository.

OCI Monitoring provides an additional infrastructure monitoring layer.

---

## Security model

Application and infrastructure secrets are not stored in this repository.

Infrastructure secrets are managed using:

```text
Ansible Vault
GitHub Actions Secrets
Terraform environment variables
```

Examples include:

```text
PostgreSQL credentials
application database passwords
CI/CD tokens
OCI credentials
```

Private keys, Terraform state files and credentials are never intended to be committed to Git.

---

## Repository structure

```text
.
├── .github/
│   └── workflows/
│       ├── jobtracker-ci.yml
│       └── jobtracker-cd-trigger.yml
│
├── database/
│   └── schema.sql
│
├── docs/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── pl/jobtracker/
│   │   │       ├── config/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       └── rest/
│   │   │
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml
│   │   │
│   │   └── webapp/
│   │       ├── index.html
│   │       ├── app.js
│   │       └── styles.css
│   │
│   └── test/
│       └── java/
│
├── pom.xml
└── README.md
```

---

## Development principles

### Preserve history

Important recruitment events remain available after the current state changes.

Examples include:

```text
application status changes
interviews
contacts
notes
documents
salary proposals
```

### Archive instead of delete

Job offers are archived rather than physically removed.

### Separate current state from history

Current application status and historical transitions have separate representations.

### Separate infrastructure from application logic

JobTracker does not manage:

```text
OCI infrastructure
WildFly installation
PostgreSQL installation
Nagios configuration
```

Those responsibilities belong to Terraform and Ansible.

---

## Current development direction

The next application development stage focuses on improving the recruitment workspace.

Planned areas include:

- adding notes from the web interface,
- adding interviews,
- recruiter management,
- contact event management,
- document registration,
- salary proposal management,
- application lifecycle modelling,
- active versus completed recruitment processes,
- reporting,
- recruitment statistics,
- dashboard views,
- filtering and search.

Application lifecycle rules are intentionally being designed before additional automatic archival behavior is introduced.

---

## Project goal

The long-term goal is a complete personal recruitment management system with a reproducible delivery platform:

```text
Developer
   |
   v
GitHub
   |
   v
JobTracker CI
   |
   v
automated tests
   |
   v
WAR artifact
   |
   v
cross-repository CD trigger
   |
   v
Ansible deployment
   |
   v
WildFly
   |
   v
PostgreSQL
   |
   v
runtime verification
   |
   v
E2E verification
   |
   v
Nagios / OCI Monitoring
```

The project is intentionally developed as both a practical application and a realistic Java / Linux / DevOps laboratory.
