# JobTracker

[![JobTracker CI](https://github.com/madamiuk/jobtracker/actions/workflows/jobtracker-ci.yml/badge.svg)](https://github.com/madamiuk/jobtracker/actions/workflows/jobtracker-ci.yml)

JobTracker is a job application tracking system built as a practical Java / Jakarta EE / WildFly / PostgreSQL project.

The project is also used as the application layer of a larger Infrastructure as Code and automation laboratory combining Terraform, Ansible, Oracle Cloud Infrastructure, WildFly, PostgreSQL, Nagios and GitHub Actions.

## Project status

JobTracker currently provides:

- Java 21 application packaged as a WAR
- Jakarta EE 11
- REST API based on Jakarta REST
- JPA / Hibernate persistence
- PostgreSQL database model
- WildFly JTA datasource integration
- Maven build
- health endpoint for deployment and monitoring checks
- GitHub Actions CI build
- Ansible-based deployment infrastructure
- OCI runtime environment
- Nagios monitoring infrastructure

The application is currently being developed toward the first integrated `0.1` release.

## Architecture

```text
GitHub
   |
   +------------------+
   |                  |
   v                  v
JobTracker        Ansible Lab
   |                  |
 Maven                |
   |                  |
   v                  |
jobtracker.war        |
   |                  |
   +--------+---------+
            |
            v
      OCI WildFly
      oci-ansible01
            |
            | JDBC
            v
      PostgreSQL 18
      oci-ansible03

      Nagios
      oci-ansible02
            |
            +---- monitors application,
                  database and hosts
```

Infrastructure provisioning is maintained separately in the `oci-terraform-lab` repository.

Configuration management, deployment and monitoring configuration are maintained in the `ansible-lab` repository.

## Technology stack

- Java 21
- Jakarta EE 11
- Jakarta REST
- Jakarta Persistence
- Hibernate
- WildFly
- PostgreSQL 18
- Maven
- Ansible
- Terraform
- Oracle Cloud Infrastructure
- Nagios
- GitHub Actions

## REST API

The REST application is exposed under:

```text
/api
```

Current resources include:

```text
GET  /api/health

/api/companies
/api/offers
/api/applications

/api/applications/{applicationId}/history
/api/applications/{applicationId}/contacts
/api/applications/{applicationId}/interviews
/api/applications/{applicationId}/recruiters
/api/applications/{applicationId}/documents
/api/applications/{applicationId}/salary-proposals
/api/applications/{applicationId}/notes
```

The API already supports creation of companies, job offers and job applications, together with retrieval of recruitment-related information.

## Recruitment model

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

A job application may additionally contain:

- recruiters
- recruitment status history
- interviews and meetings
- communication history
- required and preferred technologies
- documents
- salary expectations and proposals
- notes
- follow-up activities

## Database

The PostgreSQL schema is maintained in:

```text
database/schema.sql
```

The database model includes, among others:

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

Database schema generation by Hibernate is intentionally disabled. The database schema is managed explicitly.

## Build

Build the application locally with:

```bash
mvn clean verify
```

The resulting artifact is:

```text
target/jobtracker.war
```

The WAR is intended for deployment to WildFly.

## CI

GitHub Actions automatically validates the application on pushes and pull requests to `main`.

The CI workflow:

```text
checkout
   |
Java 21
   |
Maven
   |
mvn clean verify
   |
verify jobtracker.war
   |
verify health endpoint class
   |
verify persistence.xml
```

Automated application tests will be added as the next development stage.

## Runtime environment

The current OCI laboratory architecture is:

```text
oci-ansible01
  WildFly
  JobTracker

oci-ansible02
  Nagios

oci-ansible03
  PostgreSQL 18
  database: jobtracker
```

Terraform manages the OCI infrastructure.

Ansible manages operating system configuration, WildFly, PostgreSQL, monitoring and application deployment.

JobTracker remains independent from infrastructure configuration and contains only application code, database design and application-level CI.

## Deployment model

The intended deployment flow is:

```text
Git push
   |
   v
GitHub Actions
   |
   v
Maven build
   |
   v
jobtracker.war
   |
   v
Ansible deployment
   |
   v
WildFly on OCI
   |
   v
REST smoke test
   |
   v
Nagios monitoring
```

## Version 0.1 goals

The first integrated release will include:

- reproducible Maven build
- GitHub Actions CI
- PostgreSQL schema
- WildFly datasource
- automated Ansible deployment
- REST health check
- database connectivity verification
- deployment smoke test
- Nagios application monitoring

After the deployment pipeline is complete, development will continue with:

- automated tests
- additional business logic
- reporting
- recruitment statistics
- frontend / web interface
- document handling improvements
- CI/CD automation
