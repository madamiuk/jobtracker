# JobTracker

[![JobTracker CI](https://github.com/madamiuk/jobtracker/actions/workflows/jobtracker-ci.yml/badge.svg)](https://github.com/madamiuk/jobtracker/actions/workflows/jobtracker-ci.yml)

JobTracker is a practical job application tracking system built with Java, Jakarta EE, WildFly and PostgreSQL.

The project is developed as the application layer of a larger Infrastructure as Code and DevOps laboratory combining:

- Java 21
- Jakarta EE 11
- WildFly
- PostgreSQL 18
- Maven
- Ansible
- Terraform
- Oracle Cloud Infrastructure
- Nagios
- GitHub Actions

The goal is to build a real application for managing recruitment processes while simultaneously exercising realistic application delivery, infrastructure automation, CI/CD and monitoring workflows.

---

## Current status

JobTracker is now a working full-stack application deployed on Oracle Cloud Infrastructure.

The current application provides:

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
- recruitment process tracking
- application status history
- recruitment details view
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
- Ansible deployment
- deployment preflight validation
- artifact checksum verification
- automatic rollback
- REST runtime verification
- end-to-end database testing
- Nagios monitoring infrastructure

The project is being developed incrementally toward a complete personal recruitment management system.

---

## Architecture

The environment is split between three independent GitHub repositories:

```text
oci-terraform-lab
        |
        | Terraform
        v
Oracle Cloud Infrastructure
        |
        +--> oci-ansible01
        |       Oracle Linux 9
        |       Java 21
        |       WildFly
        |       JobTracker
        |
        +--> oci-ansible02
        |       Oracle Linux 9
        |       Nagios
        |
        +--> oci-ansible03
                Oracle Linux 9
                PostgreSQL 18
                jobtracker database


ansible-lab
        |
        +--> OS configuration
        +--> WildFly
        +--> PostgreSQL
        +--> Nagios
        +--> application deployment
        +--> runtime verification
        +--> end-to-end tests


jobtracker
        |
        +--> Java application
        +--> REST API
        +--> persistence model
        +--> web interface
        +--> application tests
        +--> Maven build
        +--> GitHub Actions
        +--> WAR artifact
```

Responsibilities are intentionally separated:

```text
Terraform  -> infrastructure
Ansible    -> configuration and deployment
JobTracker -> application code and business logic
```

---

## Application architecture

The JobTracker application follows a simple three-layer model:

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

This avoids the need for a separate frontend runtime during the current stage of development.

---

## Main recruitment model

The central relationship is:

```text
Company
   |
   v
Job Offer
   |
   v
Job Application
```

Each job application may then contain additional recruitment information:

```text
Job Application
    |
    +--> Status history
    |
    +--> Recruiters
    |
    +--> Interviews
    |
    +--> Contact events
    |
    +--> Notes
    |
    +--> Documents
    |
    +--> Salary proposals
    |
    +--> Follow-up information
```

The design intentionally separates the current state from historical events.

For example:

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

The current application status is stored in:

```text
job_application.current_status
```

while previous changes remain available through:

```text
application_status_history
```

This allows the recruitment process to evolve without losing historical information.

---

## Job offer lifecycle

Job offers are not physically deleted.

The current lifecycle is:

```text
ACTIVE
   |
   v
ARCHIVED
```

Active offers are available for new job applications.

Archived offers:

- remain stored in PostgreSQL
- remain available for historical reference
- no longer appear in the main new-application selector
- remain associated with existing applications

This preserves historical data and prevents broken relationships between job applications and their original job offers.

Older database records where the offer status is `NULL` are currently treated as active for backward compatibility.

---

## Web interface

The application includes a lightweight interactive frontend implemented with:

```text
HTML
CSS
JavaScript
```

The frontend is stored under:

```text
src/main/webapp/
```

Current files include:

```text
src/main/webapp/
├── index.html
├── app.js
└── styles.css
```

The interface currently supports:

- creating companies
- creating job offers
- creating job applications
- viewing active job offers
- archiving job offers
- viewing archived job offers
- viewing job applications
- opening application details
- viewing application status history
- viewing recruiters
- viewing interviews
- viewing contact events
- viewing notes
- viewing documents
- viewing salary proposals
- refreshing data directly from the REST API

The application UI communicates directly with the JobTracker REST endpoints.

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

---

### Companies

```text
GET  /api/companies
POST /api/companies
```

---

### Job offers

Active offers:

```text
GET /api/offers
```

Archived offers:

```text
GET /api/offers/archived
```

Create offer:

```text
POST /api/offers
```

Archive offer:

```text
POST /api/offers/{offerId}/archive
```

---

### Job applications

List applications:

```text
GET /api/applications
```

Create application:

```text
POST /api/applications
```

Change application status:

```text
POST /api/applications/{applicationId}/status
```

A status change updates the current application state and creates a separate historical status entry.

---

### Application status history

```text
GET /api/applications/{applicationId}/history
```

---

### Contacts

```text
GET /api/applications/{applicationId}/contacts
```

---

### Interviews

```text
GET /api/applications/{applicationId}/interviews
```

---

### Recruiters

```text
GET /api/applications/{applicationId}/recruiters
```

---

### Documents

```text
GET /api/applications/{applicationId}/documents
```

---

### Salary proposals

```text
GET /api/applications/{applicationId}/salary-proposals
```

---

### Notes

```text
GET /api/applications/{applicationId}/notes
```

---

## Database

The PostgreSQL schema is maintained in:

```text
database/schema.sql
```

The database contains, among others:

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

The application uses the JPA persistence unit:

```text
JobTrackerPU
```

and the WildFly JTA datasource:

```text
java:/jdbc/JobTrackerDS
```

Hibernate schema generation is intentionally disabled.

The database schema is managed explicitly instead of allowing the application to modify production database structures automatically.

---

## Runtime database

The PostgreSQL database runs on a dedicated OCI host:

```text
Host:     oci-ansible03
Database: jobtracker
User:     jobtracker
Version:  PostgreSQL 18
```

PostgreSQL does not run on the WildFly application server.

WildFly connects to PostgreSQL through the OCI private network.

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

The application is available internally through:

```text
/jobtracker/
```

and the REST API through:

```text
/jobtracker/api/
```

During development the web interface can be accessed safely through an SSH tunnel without exposing WildFly port `8080` publicly.

Example:

```bash
ssh -L 8080:10.10.1.37:8080 opc@<PUBLIC_IP>
```

Then:

```text
http://localhost:8080/jobtracker/
```

---

## Build

Build the application locally with:

```bash
mvn clean verify
```

The resulting WAR is:

```text
target/jobtracker.war
```

The build includes compilation, automated tests and WAR packaging.

The WAR contains both:

```text
Java backend
+
Web frontend
```

---

## Automated tests

JobTracker currently contains:

```text
47 automated tests
```

The test suite covers several levels.

### Domain model tests

Validation of important domain enums and expected model values.

### Persistence contract tests

Validation of:

```text
JobTrackerPU
JTA persistence
java:/jdbc/JobTrackerDS
disabled automatic schema generation
```

### REST contract tests

Validation of REST application paths and important resource endpoints.

### Request validation tests

Validation of incorrect and valid input for:

```text
companies
job offers
job applications
application status changes
```

### REST behavior tests

Mockito-based tests validate endpoint behavior without requiring WildFly.

Examples include:

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

## CI

GitHub Actions runs the JobTracker CI pipeline on pushes and pull requests to:

```text
main
```

The current CI flow is:

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
47 application tests
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

The generated artifact is published as:

```text
jobtracker-war
```

The artifact is retained temporarily and can be consumed by the Ansible deployment workflow.

---

## Artifact-based deployment

The JobTracker repository does not deploy itself directly to OCI.

The deployment is managed from the separate:

```text
ansible-lab
```

repository.

The artifact-based flow is:

```text
Git push
   |
   v
JobTracker CI
   |
   v
mvn clean verify
   |
   v
jobtracker.war
   |
   v
GitHub Actions artifact
   |
   v
Ansible deployment wrapper
   |
   v
WildFly
   |
   v
Runtime verification
```

This keeps application build responsibilities separate from infrastructure deployment responsibilities.

---

## Deployment verification

The Ansible deployment process performs several safety checks.

Before deployment:

```text
WAR exists
WAR is non-empty
WAR is a valid archive
required application classes exist
persistence.xml exists
WildFly datasource exists
database connection works
```

During deployment:

```text
local SHA-256
      |
      v
WAR transfer
      |
      v
remote SHA-256
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

The deployment also supports automatic rollback if WildFly reports a failed deployment.

---

## Runtime smoke tests

The deployment playbook validates:

```text
GET /api/health
GET /api/companies
GET /api/offers
GET /api/applications
```

Runtime checks validate:

```text
HTTP 200
application/json
valid JSON arrays
expected response fields
```

This ensures that the deployment is not considered successful merely because WildFly accepted the WAR.

The application API must also respond with the expected contract.

---

## End-to-end tests

The Ansible laboratory includes a full JobTracker end-to-end test:

```text
Company
   |
   v
Job Offer
   |
   v
Job Application
```

The test performs real REST requests against WildFly:

```text
POST /companies
POST /offers
POST /applications
```

The resulting records are then:

```text
verified through REST
        +
verified directly in PostgreSQL
```

Finally the temporary test data is removed.

The E2E workflow therefore verifies:

```text
Browser/API model
      |
      v
REST
      |
      v
JPA / Hibernate
      |
      v
WildFly datasource
      |
      v
PostgreSQL
```

without permanently polluting the application database.

---

## Infrastructure repositories

JobTracker is part of a three-repository laboratory.

### Application

```text
madamiuk/jobtracker
```

Responsibilities:

- Java source code
- REST API
- persistence model
- frontend
- automated tests
- Maven build
- application CI
- WAR artifact

### Configuration and deployment

```text
madamiuk/ansible-lab
```

Responsibilities:

- operating system configuration
- WildFly
- PostgreSQL
- Nagios
- application deployment
- secrets
- runtime tests
- end-to-end tests

### Infrastructure

```text
madamiuk/oci-terraform-lab
```

Responsibilities:

- OCI networking
- Compute instances
- infrastructure lifecycle
- Terraform state
- Terraform outputs
- infrastructure CI

---

## OCI environment

Current laboratory topology:

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

Application-to-database communication uses private OCI addressing.

---

## Monitoring

Nagios runs on:

```text
oci-ansible02
```

The monitoring environment covers infrastructure and application components including:

```text
PING
SSH
system load
memory
filesystem
WildFly
PostgreSQL
JobTracker
```

Monitoring configuration is maintained by the Ansible laboratory.

---

## Security model

Application and infrastructure secrets are not stored in the JobTracker source repository.

Infrastructure secrets are managed using:

```text
Ansible Vault
```

Examples include:

```text
PostgreSQL credentials
application database password
privilege escalation credentials
```

Terraform credentials and OCI private keys are also maintained outside Git.

WildFly port `8080` is not intentionally exposed publicly for normal application access during the current development stage.

---

## Repository structure

Important project areas:

```text
.
├── .github/
│   └── workflows/
│       └── jobtracker-ci.yml
│
├── database/
│   ├── schema.sql
│   └── test data
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

Several design principles are intentionally followed throughout the project.

### Preserve history

Important recruitment events should not disappear when current information changes.

Examples:

```text
application status changes
interviews
contacts
notes
documents
salary proposals
```

Historical data is treated as useful business information.

### Archive instead of delete

Job offers are archived rather than removed.

This preserves:

```text
historical recruitment context
database relationships
application references
future reporting possibilities
```

### Separate current state from event history

Examples:

```text
job_application.current_status
```

represents the current state, while:

```text
application_status_history
```

represents how that state evolved.

### Infrastructure separation

Application code does not manage:

```text
OCI infrastructure
WildFly configuration
PostgreSQL installation
Nagios configuration
```

These responsibilities belong to Terraform and Ansible.

---

## Current development direction

The next development stage focuses on turning the application details view into the main recruitment workspace.

Planned areas include:

- adding recruitment notes from the web interface
- adding interviews from the web interface
- recruiter management
- contact event management
- document registration
- salary proposal management
- further status workflow improvements
- application lifecycle modelling
- separation of active and completed recruitment processes
- reporting
- recruitment statistics
- dashboard views
- filtering and search

The detailed application lifecycle is intentionally being designed before additional filtering and automatic archival rules are introduced.

This avoids coupling unrelated concepts such as:

```text
job offer lifecycle
```

and:

```text
job application lifecycle
```

before real recruitment scenarios are fully modelled.

---

## Long-term direction

The long-term goal is a complete personal recruitment management system with a reproducible delivery pipeline:

```text
Developer
   |
   v
GitHub
   |
   +-------------------------------+
   |                               |
   v                               v
JobTracker CI                  Infrastructure CI
   |                               |
   v                               v
47+ tests                    Terraform / Ansible
   |
   v
WAR artifact
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
Runtime verification
   |
   v
E2E testing
   |
   v
Monitoring
```

The project is intentionally developed as both:

```text
a useful recruitment application
```

and:

```text
a realistic Java / Linux / DevOps laboratory
```
