-- CONSULTATION SUMMARY REPORT (Assigned to ASHLEY) --
CREATE VIEW ConsultationSummary_Week AS 
SELECT 
	f.facilityName,
    YEAR(mc.consultationDate) AS year,
    WEEK(mc.consultationDate, 1) AS week,
    COUNT(mc.consultationID) AS totalConsultations,
    COUNT(DISTINCT mc.patientID) AS uniquePatients,
    SUM(CASE WHEN rs.statusName = 'Completed' THEN 1 ELSE 0 END) AS completedConsultations,
    SUM(CASE WHEN rs.statusName = 'Pending' THEN 1 ELSE 0 END) AS pendingConsultations,
    ROUND(
        SUM(CASE WHEN rs.statusName = 'Completed' THEN 1 ELSE 0 END) * 100.0 /
        COUNT(mc.consultationID), 2
    ) AS pctCompleted    
FROM medical_consultation mc JOIN facility f ON mc.facilityID = f.facilityID
							 JOIN REF_Status rs ON rs.statusID = mc.consultationStatusID
                             JOIN REF_StatusCategory rsc ON rsc.statusCategoryID = rs.statusCategoryID
                             WHERE rsc.categoryName = 'ConsultationStatus'
GROUP BY f.facilityName, YEAR(mc.consultationDate), WEEK(mc.consultationDate, 1)
ORDER BY f.facilityName, year, week;

CREATE VIEW ConsultationSummary_Month AS 
SELECT
	f.facilityName,
    YEAR(mc.consultationDate) AS year,
    MONTH(mc.consultationDate) AS month,
    COUNT(mc.consultationID) AS totalConsultations,
    COUNT(DISTINCT mc.patientID) AS uniquePatients,
    SUM(CASE WHEN rs.statusName = 'Completed' THEN 1 ELSE 0 END) AS completedConsultations,
    SUM(CASE WHEN rs.statusName = 'Pending' THEN 1 ELSE 0 END) AS pendingConsultations,
    ROUND(
        SUM(CASE WHEN rs.statusName = 'Completed' THEN 1 ELSE 0 END) * 100.0 /
        COUNT(mc.consultationID), 2
    ) AS pctCompleted
FROM medical_consultation mc JOIN facility f ON mc.facilityID = f.facilityID
							 JOIN REF_Status rs ON rs.statusID = mc.consultationStatusID
                             JOIN REF_StatusCategory rsc ON rsc.statusCategoryID = rs.statusCategoryID
                             WHERE rsc.categoryName = 'ConsultationStatus'
GROUP BY f.facilityName, YEAR(mc.consultationDate), MONTH(mc.consultationDate)
ORDER BY f.facilityName, year, month;

CREATE VIEW ConsultationSummary_Year AS
SELECT
    f.facilityName,
    YEAR(mc.consultationDate) AS year,
    COUNT(mc.consultationID) AS totalConsultations,
    COUNT(DISTINCT mc.patientID) AS uniquePatients,
    SUM(CASE WHEN rs.statusName = 'Completed' THEN 1 ELSE 0 END) AS completedConsultations,
    SUM(CASE WHEN rs.statusName = 'Pending' THEN 1 ELSE 0 END) AS pendingConsultations,
    ROUND(
        SUM(CASE WHEN rs.statusName = 'Completed' THEN 1 ELSE 0 END) * 100.0 /
        COUNT(mc.consultationID), 2
    ) AS pctCompleted
FROM medical_consultation mc JOIN facility f ON mc.facilityID = f.facilityID
							 JOIN REF_Status rs ON rs.statusID = mc.consultationStatusID
                             JOIN REF_StatusCategory rsc ON rsc.statusCategoryID = rs.statusCategoryID
                             WHERE rsc.categoryName = 'ConsultationStatus'
GROUP BY f.facilityName, YEAR(mc.consultationDate)
ORDER BY f.facilityName, year;

-- IMMUNIZATION IMPACT REPORT (Assigned to ASHLEY) --
CREATE VIEW ImmunizationImpact_Week AS
SELECT
    f.facilityName,
    YEAR(ia.administrationDate) AS year,
    WEEK(ia.administrationDate, 1) AS week,
    COUNT(DISTINCT ia.patientID) AS patientsImmunized,
    COUNT(DISTINCT ia.vaccineType) AS vaccinesUsed,
    SUM(CASE WHEN rs.statusName = 'Pending' THEN 1 ELSE 0 END) AS missedVaccinations,
    SUM(CASE WHEN rs.statusName = 'Completed' THEN 1 ELSE 0 END) AS completedVaccinations,
    ROUND(
        SUM(CASE WHEN rs.statusName = 'Completed' THEN 1 ELSE 0 END) * 100.0 /
        COUNT(ia.immunizationID), 2
    ) AS pctCompletedVaccinations
FROM immunization_administration ia JOIN worker w ON ia.hWorkerID = w.hWorkerID
									JOIN facility f ON w.hWorkerFacilityID = f.facilityID
                                    JOIN REF_Status rs ON rs.statusID = ia.immunizationStatus
									JOIN REF_StatusCategory rsc ON rsc.statusCategoryID = rs.statusCategoryID
									WHERE rsc.categoryName = 'ImmunizationStatus'
GROUP BY f.facilityName, YEAR(ia.administrationDate), WEEK(ia.administrationDate, 1)
ORDER BY f.facilityName, year, week;

CREATE VIEW ImmunizationImpact_Month AS
SELECT
    f.facilityName,
    YEAR(ia.administrationDate) AS year,
    MONTH(ia.administrationDate) AS month,
    COUNT(DISTINCT ia.patientID) AS patientsImmunized,
    COUNT(DISTINCT ia.vaccineType) AS vaccinesUsed,
	SUM(CASE WHEN rs.statusName = 'Pending' THEN 1 ELSE 0 END) AS missedVaccinations,
    SUM(CASE WHEN rs.statusName = 'Completed' THEN 1 ELSE 0 END) AS completedVaccinations,
    ROUND(
        SUM(CASE WHEN rs.statusName = 'Completed' THEN 1 ELSE 0 END) * 100.0 /
        COUNT(ia.immunizationID), 2
    ) AS pctCompletedVaccinations
FROM immunization_administration ia JOIN worker w ON ia.hWorkerID = w.hWorkerID
									JOIN facility f ON w.hWorkerFacilityID = f.facilityID
                                    JOIN REF_Status rs ON rs.statusID = ia.immunizationStatus
									JOIN REF_StatusCategory rsc ON rsc.statusCategoryID = rs.statusCategoryID
									WHERE rsc.categoryName = 'ImmunizationStatus'
GROUP BY f.facilityName, YEAR(ia.administrationDate), MONTH(ia.administrationDate)
ORDER BY f.facilityName, year, month;

CREATE VIEW ImmunizationImpact_Year AS
SELECT
    f.facilityName,
    YEAR(ia.administrationDate) AS year,
    COUNT(DISTINCT ia.patientID) AS patientsImmunized,
    COUNT(DISTINCT ia.vaccineType) AS vaccinesUsed,
	SUM(CASE WHEN rs.statusName = 'Pending' THEN 1 ELSE 0 END) AS missedVaccinations,
    SUM(CASE WHEN rs.statusName = 'Completed' THEN 1 ELSE 0 END) AS completedVaccinations,
    ROUND(
        SUM(CASE WHEN rs.statusName = 'Completed' THEN 1 ELSE 0 END) * 100.0 /
        COUNT(ia.immunizationID), 2
    ) AS pctCompletedVaccinations
FROM immunization_administration ia JOIN worker w ON ia.hWorkerID = w.hWorkerID
									JOIN facility f ON w.hWorkerFacilityID = f.facilityID
                                    JOIN REF_Status rs ON rs.statusID = ia.immunizationStatus
									JOIN REF_StatusCategory rsc ON rsc.statusCategoryID = rs.statusCategoryID
									WHERE rsc.categoryName = 'ImmunizationStatus'
GROUP BY f.facilityName, YEAR(ia.administrationDate)
ORDER BY f.facilityName, year;

-- MEDICINE INVENTORY UTILIZATION REPORT (Assigned to RAPHY) --
CREATE VIEW MedicineInventoryUtilization AS
SELECT
    f.facilityName,
    DATE_FORMAT(pr.distributionDate, '%Y-%u') AS weekYear,
    DATE_FORMAT(pr.distributionDate, '%Y-%m') AS monthYear,
    YEAR(pr.distributionDate) AS year,
    m.medicineName,
    SUM(pr.qtyDistributed) AS totalDistributed
FROM prescription_receipt pr JOIN facility f ON pr.facilityID = f.facilityID
							 JOIN medicine m ON pr.medicineID = m.medicineID
GROUP BY f.facilityName, weekYear, monthYear, year, m.medicineName, m.medicineType;

