"use strict";

const API = {
    companies: "api/companies",
    offers: "api/offers",
    archivedOffers: "api/offers/archived",
    applications: "api/applications"
};


const statusLabels = {
    PLANNED: "Planowana",
    APPLIED: "Wysłana",
    HR_SCREENING: "HR screening",
    HR_INTERVIEW: "Rozmowa HR",
    TECH_INTERVIEW: "Rozmowa techniczna",
    TASK: "Zadanie",
    MANAGER_INTERVIEW: "Rozmowa z managerem",
    FINAL_INTERVIEW: "Final interview",
    WAITING: "Oczekiwanie",
    OFFER: "Oferta",
    REJECTED: "Odrzucona",
    WITHDRAWN: "Wycofana",
    HIRED: "Zatrudnienie"
};

let selectedApplication = null;

function numberOrNull(value) {
    return value === "" ? null : Number(value);
}


function valueOrNull(value) {
    return value === "" ? null : value;
}


function textOrDash(value) {

    if (value === null || value === undefined || value === "") {
        return "—";
    }

    return String(value);
}


function formatDate(value) {

    if (!value) {
        return "—";
    }

    return new Date(value).toLocaleString("pl-PL");
}


function formatSalary(min, max, currency) {

    if (min == null && max == null) {
        return "—";
    }

    currency = currency || "";

    if (min != null && max != null) {
        return `${min}–${max} ${currency}`;
    }

    if (min != null) {
        return `od ${min} ${currency}`;
    }

    return `do ${max} ${currency}`;
}


async function requestJson(url, options = {}) {

    const response = await fetch(url, options);

    if (!response.ok) {

        const text = await response.text();

        throw new Error(
            `HTTP ${response.status}: ${text}`
        );
    }

    return response.json();
}


async function loadCompanies() {

    const companies =
        await requestJson(API.companies);

    const select =
        document.getElementById("offerCompanyId");

    select.innerHTML =
        '<option value="">Wybierz firmę</option>';

    for (const company of companies) {

        const option =
            document.createElement("option");

        option.value = company.id;
        option.textContent = company.name;

        select.appendChild(option);
    }

    select.disabled =
        companies.length === 0;

    if (companies.length === 0) {

        select.innerHTML =
            '<option value="">Najpierw dodaj firmę</option>';
    }
}


async function loadOffers() {

    const offers =
        await requestJson(API.offers);

    const select =
        document.getElementById("jobOfferId");

    select.innerHTML =
        '<option value="">Wybierz ofertę</option>';

    for (const offer of offers) {

        const option =
            document.createElement("option");

        option.value = offer.id;

        option.textContent =
            `${offer.companyName} — ${offer.positionTitle}`;

        select.appendChild(option);
    }

    select.disabled =
        offers.length === 0;

    if (offers.length === 0) {

        select.innerHTML =
            '<option value="">Najpierw dodaj ofertę</option>';
    }

    renderOffers(offers);
}

function renderOffers(offers) {

    const tableBody =
        document.getElementById(
            "offersTableBody"
        );

    const counter =
        document.getElementById(
            "offerCount"
        );

    counter.textContent =
        `${offers.length} ofert`;

    tableBody.innerHTML = "";

    if (offers.length === 0) {

        tableBody.innerHTML = `
            <tr>
                <td colspan="6">
                    Brak aktywnych ofert.
                </td>
            </tr>
        `;

        return;
    }

    for (const offer of offers) {

        const row =
            document.createElement("tr");

        const values = [
            offer.id,
            offer.companyName,
            offer.positionTitle,
            offer.location || "—",
            formatSalary(
                offer.salaryMin,
                offer.salaryMax,
                offer.salaryCurrency
            )
        ];

        for (const value of values) {

            const cell =
                document.createElement("td");

            cell.textContent =
                String(value);

            row.appendChild(cell);
        }

        const actionCell =
            document.createElement("td");

        const archiveButton =
            document.createElement("button");

        archiveButton.type =
            "button";

        archiveButton.className =
            "archive-button";

        archiveButton.textContent =
            "Archiwizuj";

        archiveButton.addEventListener(
            "click",
            () => archiveOffer(offer)
        );

        actionCell.appendChild(
            archiveButton
        );

        row.appendChild(
            actionCell
        );

        tableBody.appendChild(
            row
        );
    }
}

async function loadArchivedOffers() {

    const offers =
        await requestJson(
            API.archivedOffers
        );

    const tableBody =
        document.getElementById(
            "archivedOffersTableBody"
        );

    const counter =
        document.getElementById(
            "archivedOfferCount"
        );

    counter.textContent =
        `${offers.length} ofert`;

    tableBody.innerHTML = "";

    if (offers.length === 0) {

        tableBody.innerHTML = `
            <tr>
                <td colspan="5">
                    Brak ofert archiwalnych.
                </td>
            </tr>
        `;

        return;
    }

    for (const offer of offers) {

        const row =
            document.createElement("tr");

        const values = [
            offer.id,
            offer.companyName,
            offer.positionTitle,
            offer.location || "—",
            formatSalary(
                offer.salaryMin,
                offer.salaryMax,
                offer.salaryCurrency
            )
        ];

        for (const value of values) {

            const cell =
                document.createElement("td");

            cell.textContent =
                String(value);

            row.appendChild(cell);
        }

        tableBody.appendChild(row);
    }
}

async function archiveOffer(offer) {

    const confirmed =
        window.confirm(
            `Zarchiwizować ofertę "${offer.companyName} — ${offer.positionTitle}"?`
        );

    if (!confirmed) {
        return;
    }

    try {

        await requestJson(
            `${API.offers}/${offer.id}/archive`,
            {
                method: "POST"
            }
        );

        await Promise.all([
            loadOffers(),
            loadArchivedOffers()
        ]);

    } catch (error) {

        console.error(
            "Błąd archiwizacji oferty:",
            error
        );

        window.alert(
            "Nie udało się zarchiwizować oferty."
        );
    }
}

async function loadApplications() {

    const applications =
        await requestJson(API.applications);

    renderApplications(applications);
}


function renderApplications(applications) {

    const tableBody =
        document.getElementById("applicationsTableBody");

    const counter =
        document.getElementById("applicationCount");

    counter.textContent =
        `${applications.length} aplikacji`;

    tableBody.innerHTML = "";

    if (applications.length === 0) {

        tableBody.innerHTML = `
            <tr>
                <td colspan="9">
                    Brak zapisanych aplikacji.
                </td>
            </tr>
        `;

        return;
    }

    for (const application of applications) {

        const row =
            document.createElement("tr");

        row.classList.add("clickable-row");
        row.tabIndex = 0;

        const values = [
            application.id,
            application.companyName,
            application.positionTitle,
            statusLabels[
                application.currentStatus
            ] || application.currentStatus,
            formatDate(application.appliedAt),
            application.priority ?? "—",
            application.fitScore ?? "—",
            application.interestScore ?? "—",
            formatSalary(
                application.expectedSalaryMin,
                application.expectedSalaryMax,
                application.expectedSalaryCurrency
            )
        ];

        for (const value of values) {

            const cell =
                document.createElement("td");

            cell.textContent =
                String(value);

            row.appendChild(cell);
        }

        row.addEventListener(
            "click",
            () => loadApplicationDetails(application)
        );

        row.addEventListener(
            "keydown",
            event => {

                if (
                    event.key === "Enter" ||
                    event.key === " "
                ) {

                    event.preventDefault();

                    loadApplicationDetails(application);
                }
            }
        );

        tableBody.appendChild(row);
    }
}


function renderList(containerId, items, renderer) {

    const container =
        document.getElementById(containerId);

    container.innerHTML = "";

    if (!items || items.length === 0) {

        const empty =
            document.createElement("p");

        empty.className = "empty-state";
        empty.textContent = "Brak danych.";

        container.appendChild(empty);

        return;
    }

    const list =
        document.createElement("div");

    list.className = "detail-list";

    for (const item of items) {

        const element =
            document.createElement("div");

        element.className = "detail-item";

        renderer(element, item);

        list.appendChild(element);
    }

    container.appendChild(list);
}


async function loadApplicationDetails(application) {

    selectedApplication = application;

    document.getElementById(
        "statusUpdateValue"
    ).value = application.currentStatus;

    setStatusChangedAtNow();

    document.getElementById(
        "statusComment"
    ).value = "";

    document.getElementById(
        "statusUpdateMessage"
    ).textContent = "";

    const panel =
        document.getElementById(
            "applicationDetailsPanel"
        );

    panel.classList.remove("hidden");

    document.getElementById(
        "applicationDetailsTitle"
    ).textContent =
        `${application.companyName} — ${application.positionTitle}`;

    renderApplicationSummary(application);

    const applicationId =
        application.id;

    const endpoints = {
        history:
            `${API.applications}/${applicationId}/history`,

        interviews:
            `${API.applications}/${applicationId}/interviews`,

        recruiters:
            `${API.applications}/${applicationId}/recruiters`,

        contacts:
            `${API.applications}/${applicationId}/contacts`,

        notes:
            `${API.applications}/${applicationId}/notes`,

        documents:
            `${API.applications}/${applicationId}/documents`,

        salaries:
            `${API.applications}/${applicationId}/salary-proposals`
    };

    try {

        const [
            history,
            interviews,
            recruiters,
            contacts,
            notes,
            documents,
            salaries
        ] = await Promise.all([
            requestJson(endpoints.history),
            requestJson(endpoints.interviews),
            requestJson(endpoints.recruiters),
            requestJson(endpoints.contacts),
            requestJson(endpoints.notes),
            requestJson(endpoints.documents),
            requestJson(endpoints.salaries)
        ]);

        renderHistory(history);
        renderInterviews(interviews);
        renderRecruiters(recruiters);
        renderContacts(contacts);
        renderNotes(notes);
        renderDocuments(documents);
        renderSalaryProposals(salaries);

    } catch (error) {

        console.error(
            "Błąd pobierania szczegółów aplikacji:",
            error
        );
    }

    panel.scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}


function renderApplicationSummary(application) {

    const container =
        document.getElementById(
            "applicationSummary"
        );

    container.innerHTML = "";

    const entries = [
        [
            "Status",
            statusLabels[
                application.currentStatus
            ] || application.currentStatus
        ],

        [
            "Data aplikacji",
            formatDate(application.appliedAt)
        ],

        [
            "Priorytet",
            application.priority
        ],

        [
            "Dopasowanie",
            application.fitScore
        ],

        [
            "Zainteresowanie",
            application.interestScore
        ],

        [
            "Oczekiwania",
            formatSalary(
                application.expectedSalaryMin,
                application.expectedSalaryMax,
                application.expectedSalaryCurrency
            )
        ]
    ];

    for (const [label, value] of entries) {

        const item =
            document.createElement("div");

        item.className = "summary-item";

        const labelElement =
            document.createElement("span");

        labelElement.className =
            "summary-label";

        labelElement.textContent =
            label;

        const valueElement =
            document.createElement("strong");

        valueElement.textContent =
            textOrDash(value);

        item.appendChild(labelElement);
        item.appendChild(valueElement);

        container.appendChild(item);
    }
}


function renderHistory(items) {

    renderList(
        "historyDetails",
        items,
        (element, item) => {

            const title =
                document.createElement("strong");

            title.textContent =
                statusLabels[item.status] || item.status;

            const date =
                document.createElement("span");

            date.textContent =
                formatDate(item.changedAt);

            const comment =
                document.createElement("p");

            comment.textContent =
                textOrDash(item.comment);

            element.append(
                title,
                date,
                comment
            );
        }
    );
}


function renderInterviews(items) {

    renderList(
        "interviewDetails",
        items,
        (element, item) => {

            const title =
                document.createElement("strong");

            title.textContent =
                item.interviewType;

            const date =
                document.createElement("span");

            date.textContent =
                formatDate(item.scheduledAt);

            const description =
                document.createElement("p");

            description.textContent =
                [
                    item.locationType,
                    item.locationDetails,
                    item.result
                ]
                    .filter(Boolean)
                    .join(" · ") || "—";

            element.append(
                title,
                date,
                description
            );
        }
    );
}


function renderRecruiters(items) {

    renderList(
        "recruiterDetails",
        items,
        (element, item) => {

            const title =
                document.createElement("strong");

            title.textContent =
                `${item.firstName || ""} ${item.lastName || ""}`.trim()
                || "Rekruter";

            const position =
                document.createElement("span");

            position.textContent =
                textOrDash(item.position);

            const contact =
                document.createElement("p");

            contact.textContent =
                [
                    item.email,
                    item.phone
                ]
                    .filter(Boolean)
                    .join(" · ") || "—";

            element.append(
                title,
                position,
                contact
            );
        }
    );
}


function renderContacts(items) {

    renderList(
        "contactDetails",
        items,
        (element, item) => {

            const title =
                document.createElement("strong");

            title.textContent =
                item.subject || item.contactType;

            const date =
                document.createElement("span");

            date.textContent =
                formatDate(item.contactAt);

            const description =
                document.createElement("p");

            description.textContent =
                textOrDash(item.description);

            element.append(
                title,
                date,
                description
            );
        }
    );
}


function renderNotes(items) {

    renderList(
        "noteDetails",
        items,
        (element, item) => {

            const date =
                document.createElement("span");

            date.textContent =
                formatDate(item.createdAt);

            const content =
                document.createElement("p");

            content.textContent =
                textOrDash(item.content);

            element.append(
                date,
                content
            );
        }
    );
}


function renderDocuments(items) {

    renderList(
        "documentDetails",
        items,
        (element, item) => {

            const title =
                document.createElement("strong");

            title.textContent =
                item.fileName || item.documentType;

            const meta =
                document.createElement("span");

            meta.textContent =
                [
                    item.documentType,
                    item.version,
                    formatDate(item.sentAt)
                ]
                    .filter(
                        value =>
                            value &&
                            value !== "—"
                    )
                    .join(" · ");

            element.append(
                title,
                meta
            );
        }
    );
}


function renderSalaryProposals(items) {

    renderList(
        "salaryDetails",
        items,
        (element, item) => {

            const title =
                document.createElement("strong");

            title.textContent =
                item.proposalType;

            const amount =
                document.createElement("span");

            amount.textContent =
                formatSalary(
                    item.amountMin,
                    item.amountMax,
                    item.currency
                );

            const date =
                document.createElement("p");

            date.textContent =
                formatDate(item.proposedAt);

            element.append(
                title,
                amount,
                date
            );
        }
    );
}


async function createCompany(event) {

    event.preventDefault();

    const message =
        document.getElementById(
            "companyMessage"
        );

    message.textContent =
        "Zapisywanie...";

    const payload = {

        name:
            document.getElementById(
                "companyName"
            ).value.trim(),

        website:
            valueOrNull(
                document.getElementById(
                    "companyWebsite"
                ).value.trim()
            ),

        industry:
            valueOrNull(
                document.getElementById(
                    "companyIndustry"
                ).value.trim()
            ),

        city:
            valueOrNull(
                document.getElementById(
                    "companyCity"
                ).value.trim()
            ),

        country:
            valueOrNull(
                document.getElementById(
                    "companyCountry"
                ).value.trim()
            ),

        description: null,
        notes: null
    };

    try {

        const company =
            await requestJson(
                API.companies,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(payload)
                }
            );

        message.textContent =
            `Dodano firmę #${company.id}`;

        document
            .getElementById("companyForm")
            .reset();

        document
            .getElementById("companyCountry")
            .value = "Poland";

        await loadCompanies();

        document
            .getElementById("offerCompanyId")
            .value =
                String(company.id);

    } catch (error) {

        console.error(error);

        message.textContent =
            "Nie udało się zapisać firmy.";
    }
}


async function createOffer(event) {

    event.preventDefault();

    const message =
        document.getElementById(
            "offerMessage"
        );

    message.textContent =
        "Zapisywanie...";

    const payload = {

        companyId:
            Number(
                document.getElementById(
                    "offerCompanyId"
                ).value
            ),

        positionTitle:
            document.getElementById(
                "offerPositionTitle"
            ).value.trim(),

        offerUrl: null,
        description: null,
        originalText: null,

        sourceType:
            document.getElementById(
                "offerSourceType"
            ).value,

        sourceUrl: null,
        sourceDetails: null,

        workMode:
            valueOrNull(
                document.getElementById(
                    "offerWorkMode"
                ).value
            ),

        employmentType:
            valueOrNull(
                document.getElementById(
                    "offerEmploymentType"
                ).value
            ),

        location:
            valueOrNull(
                document.getElementById(
                    "offerLocation"
                ).value.trim()
            ),

        salaryMin:
            numberOrNull(
                document.getElementById(
                    "offerSalaryMin"
                ).value
            ),

        salaryMax:
            numberOrNull(
                document.getElementById(
                    "offerSalaryMax"
                ).value
            ),

        salaryCurrency:
            valueOrNull(
                document.getElementById(
                    "offerSalaryCurrency"
                ).value
            ),

        salaryPeriod:
            valueOrNull(
                document.getElementById(
                    "offerSalaryPeriod"
                ).value
            ),

        status: null
    };

    try {

        const offer =
            await requestJson(
                API.offers,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(payload)
                }
            );

        message.textContent =
            `Dodano ofertę #${offer.id}`;

        document
            .getElementById("offerForm")
            .reset();

        await Promise.all([
            loadCompanies(),
            loadOffers()
        ]);

        document
            .getElementById("jobOfferId")
            .value =
                String(offer.id);

    } catch (error) {

        console.error(error);

        message.textContent =
            "Nie udało się zapisać oferty.";
    }
}


async function createApplication(event) {

    event.preventDefault();

    const message =
        document.getElementById(
            "applicationMessage"
        );

    message.textContent =
        "Zapisywanie...";

    const appliedAtInput =
        document.getElementById(
            "appliedAt"
        ).value;

    const payload = {

        jobOfferId:
            Number(
                document.getElementById(
                    "jobOfferId"
                ).value
            ),

        appliedAt:
            appliedAtInput
                ? new Date(
                    appliedAtInput
                ).toISOString()
                : null,

        currentStatus:
            document.getElementById(
                "currentStatus"
            ).value,

        priority:
            numberOrNull(
                document.getElementById(
                    "priority"
                ).value
            ),

        fitScore:
            numberOrNull(
                document.getElementById(
                    "fitScore"
                ).value
            ),

        interestScore:
            numberOrNull(
                document.getElementById(
                    "interestScore"
                ).value
            ),

        expectedSalaryMin:
            numberOrNull(
                document.getElementById(
                    "expectedSalaryMin"
                ).value
            ),

        expectedSalaryMax:
            numberOrNull(
                document.getElementById(
                    "expectedSalaryMax"
                ).value
            ),

        expectedSalaryCurrency:
            valueOrNull(
                document.getElementById(
                    "expectedSalaryCurrency"
                ).value
            )
    };

    try {

        const application =
            await requestJson(
                API.applications,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(payload)
                }
            );

        message.textContent =
            `Dodano aplikację #${application.id}`;

        await loadApplications();

    } catch (error) {

        console.error(error);

        message.textContent =
            "Nie udało się zapisać aplikacji.";
    }
}

function setStatusChangedAtNow() {

    const input =
        document.getElementById(
            "statusChangedAt"
        );

    const now =
        new Date();

    const local =
        new Date(
            now.getTime() -
            now.getTimezoneOffset() * 60000
        );

    input.value =
        local
            .toISOString()
            .slice(0, 16);
}


async function updateApplicationStatus(event) {

    event.preventDefault();

    if (!selectedApplication) {
        return;
    }

    const message =
        document.getElementById(
            "statusUpdateMessage"
        );

    message.textContent =
        "Zapisywanie...";

    const changedAt =
        document.getElementById(
            "statusChangedAt"
        ).value;

    const payload = {

        status:
            document.getElementById(
                "statusUpdateValue"
            ).value,

        changedAt:
            changedAt
                ? new Date(changedAt).toISOString()
                : null,

        comment:
            valueOrNull(
                document.getElementById(
                    "statusComment"
                ).value.trim()
            )
    };

    try {

        const historyEntry =
            await requestJson(
                `${API.applications}/${selectedApplication.id}/status`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(payload)
                }
            );

        selectedApplication = {
            ...selectedApplication,
            currentStatus: historyEntry.status
        };

        message.textContent =
            "Status został zapisany.";

        await loadApplications();

        await loadApplicationDetails(
            selectedApplication
        );

        document.getElementById(
            "statusUpdateMessage"
        ).textContent =
            "Status został zapisany.";

    } catch (error) {

        console.error(
            "Błąd zmiany statusu:",
            error
        );

        message.textContent =
            "Nie udało się zmienić statusu.";
    }
}

function setDefaultApplicationDate() {

    const input =
        document.getElementById(
            "appliedAt"
        );

    const now =
        new Date();

    const local =
        new Date(
            now.getTime() -
            now.getTimezoneOffset() * 60000
        );

    input.value =
        local
            .toISOString()
            .slice(0, 16);
}


async function refreshAll() {

    await Promise.all([
        loadCompanies(),
        loadOffers(),
        loadArchivedOffers(),
        loadApplications()
    ]);
}


document
    .getElementById("companyForm")
    .addEventListener(
        "submit",
        createCompany
    );


document
    .getElementById("offerForm")
    .addEventListener(
        "submit",
        createOffer
    );


document
    .getElementById("applicationForm")
    .addEventListener(
        "submit",
        createApplication
    );


document
    .getElementById("refreshButton")
    .addEventListener(
        "click",
        refreshAll
    );


document
    .getElementById("closeDetailsButton")
    .addEventListener(
        "click",
        () => {

            document
                .getElementById(
                    "applicationDetailsPanel"
                )
                .classList
                .add("hidden");
        }
    );

document
    .getElementById("statusUpdateForm")
    .addEventListener(
        "submit",
        updateApplicationStatus
    );

async function init() {

    setDefaultApplicationDate();

    try {

        await refreshAll();

    } catch (error) {

        console.error(
            "Błąd inicjalizacji JobTracker:",
            error
        );
    }
}


init();
