# JobTracker Functional Model

## Core domain

The central entity is a job application.

A recruitment process connects:

Company -> Job Offer -> Job Application

A job application may contain:

- recruiter contacts
- interviews
- recruitment stages
- status history
- technologies
- salary proposals
- documents
- notes
- follow-up events

## Main entities

- Company
- JobOffer
- JobApplication
- Recruiter
- Interview
- ApplicationStatusHistory
- Technology
- ContactEvent
- Document
- SalaryProposal
- Note

## Main goals

The application should allow analysis of:

- number of applications
- recruitment sources
- conversion between recruitment stages
- time to first response
- recruitment duration
- most frequently required technologies
- salary expectations vs offers
- reasons for rejection
- effectiveness of different job sources
