# JobTracker Database Schema v1.0

## Overview

The database model represents the complete recruitment process.

The central relationship is:

Company -> JobOffer -> JobApplication

A job application may be connected with recruiters, interviews, status history,
technologies, contact events, documents, salary proposals and notes.

## Core entities

### company

Represents an employer.

Fields:

- id
- name
- website
- industry
- city
- country
- description
- notes
- created_at
- updated_at

### job_offer

Represents a discovered job advertisement.

Fields:

- id
- company_id
- position_title
- offer_url
- description
- original_text
- source_type
- source_url
- source_details
- work_mode
- employment_type
- location
- salary_min
- salary_max
- salary_currency
- salary_period
- found_at
- published_at
- expires_at
- status
- created_at
- updated_at

### job_application

Represents an application submitted for a job offer.

Fields:

- id
- job_offer_id
- applied_at
- current_status
- priority
- fit_score
- interest_score
- expected_salary_min
- expected_salary_max
- expected_salary_currency
- final_result
- closed_at
- closing_reason
- created_at
- updated_at

### application_status_history

Stores the history of recruitment stages.

Fields:

- id
- application_id
- status
- changed_at
- comment

### recruiter

Represents a recruiter or another person involved in the recruitment process.

Fields:

- id
- company_id
- first_name
- last_name
- position
- email
- phone
- linkedin_url
- notes
- created_at
- updated_at

### interview

Represents a recruitment meeting or interview.

Fields:

- id
- application_id
- interview_type
- scheduled_at
- duration_minutes
- location_type
- location_details
- result
- summary
- questions
- my_notes
- created_at
- updated_at

### technology

Represents a technology required or preferred in a job offer.

Fields:

- id
- name
- category

### job_offer_technology

Many-to-many relationship between job offers and technologies.

Fields:

- job_offer_id
- technology_id
- requirement_level

### contact_event

Represents communication during the recruitment process.

Fields:

- id
- application_id
- recruiter_id
- contact_type
- contact_at
- direction
- subject
- description
- follow_up_required
- follow_up_at
- follow_up_done
- created_at

### document

Represents a document used during recruitment.

Fields:

- id
- application_id
- document_type
- file_name
- file_path
- version
- sent_at
- sha256
- notes
- created_at

### salary_proposal

Stores salary expectations and proposals.

Fields:

- id
- application_id
- proposal_type
- amount_min
- amount_max
- currency
- period
- proposed_at
- notes

### note

Stores free-form notes related to an application.

Fields:

- id
- application_id
- created_at
- content
