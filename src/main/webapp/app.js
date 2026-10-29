"use strict";

const API = {
    companies: "api/companies",
    offers: "api/offers",
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


function numberOrNull(value) {
    return value === "" ? null : Number(value);
}


function valueOrNull(value) {
    return value === "" ? null : value;
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
                <td colspan="5">
                    Brak zapisanych ofert.
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


async function loadApplications() {

    const applications =
        await requestJson(
            API.applications
        );

    const tableBody =
        document.getElementById(
            "applicationsTableBody"
        );

    const counter =
        document.getElementById(
            "applicationCount"
        );

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

        const values = [
            application.id,
            application.companyName,
            application.positionTitle,
            statusLabels[
                application.currentStatus
            ] || application.currentStatus,
            formatDate(
                application.appliedAt
            ),
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

        tableBody.appendChild(row);
    }
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
