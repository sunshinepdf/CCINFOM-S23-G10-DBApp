
USE BHMS_DB;
-- CONSULTATION SUMMARY REPORT (Assigned to ASHLEY) --
CREATE OR REPLACE VIEW ConsultationSummary_Week AS 
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

CREATE OR REPLACE VIEW ConsultationSummary_Month AS 
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

CREATE OR REPLACE VIEW ConsultationSummary_Year AS
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
CREATE OR REPLACE VIEW ImmunizationImpact_Week AS
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
                  JOIN REF_Status rs ON rs.statusID = ia.immunizationStatusID
									JOIN REF_StatusCategory rsc ON rsc.statusCategoryID = rs.statusCategoryID
									WHERE rsc.categoryName = 'ImmunizationStatusID'
GROUP BY f.facilityName, YEAR(ia.administrationDate), WEEK(ia.administrationDate, 1)
ORDER BY f.facilityName, year, week;

CREATE OR REPLACE VIEW ImmunizationImpact_Month AS
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
                  JOIN REF_Status rs ON rs.statusID = ia.immunizationStatusID
									JOIN REF_StatusCategory rsc ON rsc.statusCategoryID = rs.statusCategoryID
									WHERE rsc.categoryName = 'ImmunizationStatusID'

GROUP BY f.facilityName, YEAR(ia.administrationDate), MONTH(ia.administrationDate)
ORDER BY f.facilityName, year, month;

CREATE OR REPLACE VIEW ImmunizationImpact_Year AS
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
                  JOIN REF_Status rs ON rs.statusID = ia.immunizationStatusID
									JOIN REF_StatusCategory rsc ON rsc.statusCategoryID = rs.statusCategoryID
									WHERE rsc.categoryName = 'ImmunizationStatusID'
GROUP BY f.facilityName, YEAR(ia.administrationDate)
ORDER BY f.facilityName, year;

-- MEDICINE INVENTORY UTILIZATION REPORT (Assigned to RAPHY) --
-- WEEKLY
CREATE OR REPLACE VIEW MedicineInventoryUtilization_Week AS
SELECT
    f.facilityName,
    m.medicineName,
    YEAR(pr.distributionDate) AS year,
    WEEK(pr.distributionDate, 1) AS week,
    SUM(pr.qtyDistributed) AS totalDistributed,
    mi.quantityInStock AS currentStock,
    s.supplierName,
    SUM(ri.totalOrderCost) AS totalRestockCost
FROM medicine m
JOIN medicine_inventory mi ON m.medicineID = mi.medicineID
JOIN facility f ON mi.facilityID = f.facilityID
LEFT JOIN prescription_receipt pr ON pr.medicineID = m.medicineID
LEFT JOIN restock_invoice ri ON ri.supplierID IS NOT NULL
LEFT JOIN supplier s ON ri.supplierID = s.supplierID
GROUP BY f.facilityName, m.medicineName, YEAR(pr.distributionDate), WEEK(pr.distributionDate, 1), s.supplierName, mi.quantityInStock
ORDER BY f.facilityName, m.medicineName, year, week;

-- MONTHLY
CREATE OR REPLACE VIEW MedicineInventoryUtilization_Month AS
SELECT
    f.facilityName,
    m.medicineName,
    YEAR(pr.distributionDate) AS year,
    MONTH(pr.distributionDate) AS month,
    SUM(pr.qtyDistributed) AS totalDistributed,
    mi.quantityInStock AS currentStock,
    s.supplierName,
    SUM(ri.totalOrderCost) AS totalRestockCost
FROM medicine m
JOIN medicine_inventory mi ON m.medicineID = mi.medicineID
JOIN facility f ON mi.facilityID = f.facilityID
LEFT JOIN prescription_receipt pr ON pr.medicineID = m.medicineID
LEFT JOIN restock_invoice ri ON ri.supplierID IS NOT NULL
LEFT JOIN supplier s ON ri.supplierID = s.supplierID
GROUP BY f.facilityName, m.medicineName, YEAR(pr.distributionDate), MONTH(pr.distributionDate), s.supplierName, mi.quantityInStock
ORDER BY f.facilityName, m.medicineName, year, month;

-- YEARLY
CREATE OR REPLACE VIEW MedicineInventoryUtilization_Year AS
SELECT
    f.facilityName,
    m.medicineName,
    YEAR(pr.distributionDate) AS year,
    SUM(pr.qtyDistributed) AS totalDistributed,
    mi.quantityInStock AS currentStock,
    s.supplierName,
    SUM(ri.totalOrderCost) AS totalRestockCost
FROM medicine m
JOIN medicine_inventory mi ON m.medicineID = mi.medicineID
JOIN facility f ON mi.facilityID = f.facilityID
LEFT JOIN prescription_receipt pr ON pr.medicineID = m.medicineID
LEFT JOIN restock_invoice ri ON ri.supplierID IS NOT NULL
LEFT JOIN supplier s ON ri.supplierID = s.supplierID
GROUP BY f.facilityName, m.medicineName, YEAR(pr.distributionDate), s.supplierName, mi.quantityInStock
ORDER BY f.facilityName, m.medicineName, year;

-- DISEASE MONITORING REPORT (Assigned to SPENCER, CREATED BY ASHLEY) --
CREATE OR REPLACE VIEW DiseaseCaseMonitoring_Week AS
SELECT
    f.facilityID,
    f.facilityName,
    YEAR(mc.consultationDate) AS year,
    WEEK(mc.consultationDate, 1) AS week,
    mc.diagnosis AS diseaseType,
    COUNT(mc.consultationID) AS numberOfCases
FROM medical_consultation mc
JOIN facility f ON mc.facilityID = f.facilityID
WHERE mc.diagnosis IS NOT NULL AND mc.diagnosis <> ''
GROUP BY f.facilityID, f.facilityName, YEAR(mc.consultationDate), WEEK(mc.consultationDate, 1), mc.diagnosis
ORDER BY f.facilityName, year, week, diseaseType;

CREATE OR REPLACE VIEW DiseaseCaseMonitoring_Month AS
SELECT
    f.facilityID,
    f.facilityName,
    YEAR(mc.consultationDate) AS year,
    MONTH(mc.consultationDate) AS month,
    mc.diagnosis AS diseaseType,
    COUNT(mc.consultationID) AS numberOfCases
FROM medical_consultation mc
JOIN facility f ON mc.facilityID = f.facilityID
WHERE mc.diagnosis IS NOT NULL AND mc.diagnosis <> ''
GROUP BY f.facilityID, f.facilityName, YEAR(mc.consultationDate), MONTH(mc.consultationDate), mc.diagnosis
ORDER BY f.facilityName, year, month, diseaseType;

CREATE OR REPLACE VIEW DiseaseCaseMonitoring_Year AS
SELECT
    f.facilityID,
    f.facilityName,
    YEAR(mc.consultationDate) AS year,
    mc.diagnosis AS diseaseType,
    COUNT(mc.consultationID) AS numberOfCases
FROM medical_consultation mc
JOIN facility f ON mc.facilityID = f.facilityID
WHERE mc.diagnosis IS NOT NULL AND mc.diagnosis <> ''
GROUP BY f.facilityID, f.facilityName, YEAR(mc.consultationDate), mc.diagnosis
ORDER BY f.facilityName, year, diseaseType;
