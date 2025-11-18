-- IMMUNIZATION IMPACT REPORT (Assigned to ASHLEY) --
CREATE VIEW ImmunizationImpact_Week AS
SELECT
    f.facilityName,
    YEAR(ia.administrationDate) AS year,
    WEEK(ia.administrationDate, 1) AS week,
    COUNT(DISTINCT ia.patientID) AS patientsImmunized,
    COUNT(DISTINCT ia.vaccineType) AS vaccinesUsed,
    SUM(CASE WHEN ia.immunizationStatus = 'pending' THEN 1 ELSE 0 END) AS missedVaccinations,
    ROUND(
        SUM(CASE WHEN ia.immunizationStatus = 'completed' THEN 1 ELSE 0 END) * 100.0 /
        COUNT(ia.immunizationID), 2
    ) AS pctCompletedVaccinations
FROM immunization_administration ia
JOIN worker w ON ia.hWorkerID = w.hWorkerID
JOIN facility f ON w.facilityID = f.facilityID
GROUP BY f.facilityName, YEAR(ia.administrationDate), WEEK(ia.administrationDate, 1)
ORDER BY f.facilityName, year, week;

CREATE VIEW ImmunizationImpact_Month AS
SELECT
    f.facilityName,
    YEAR(ia.administrationDate) AS year,
    MONTH(ia.administrationDate) AS month,
    COUNT(DISTINCT ia.patientID) AS patientsImmunized,
    COUNT(DISTINCT ia.vaccineType) AS vaccinesUsed,
    SUM(CASE WHEN ia.immunizationStatus = 'pending' THEN 1 ELSE 0 END) AS missedVaccinations,
    ROUND(
        SUM(CASE WHEN ia.immunizationStatus = 'completed' THEN 1 ELSE 0 END) * 100.0 /
        COUNT(ia.immunizationID), 2
    ) AS pctCompletedVaccinations
FROM immunization_administration ia
JOIN worker w ON ia.hWorkerID = w.hWorkerID
JOIN facility f ON w.facilityID = f.facilityID
GROUP BY f.facilityName, YEAR(ia.administrationDate), MONTH(ia.administrationDate)
ORDER BY f.facilityName, year, month;

CREATE VIEW ImmunizationImpact_Year AS
SELECT
    f.facilityName,
    YEAR(ia.administrationDate) AS year,
    COUNT(DISTINCT ia.patientID) AS patientsImmunized,
    COUNT(DISTINCT ia.vaccineType) AS vaccinesUsed,
    SUM(CASE WHEN ia.immunizationStatus = 'pending' THEN 1 ELSE 0 END) AS missedVaccinations,
    ROUND(
        SUM(CASE WHEN ia.immunizationStatus = 'completed' THEN 1 ELSE 0 END) * 100.0 /
        COUNT(ia.immunizationID), 2
    ) AS pctCompletedVaccinations
FROM immunization_administration ia
JOIN worker w ON ia.hWorkerID = w.hWorkerID
JOIN facility f ON w.facilityID = f.facilityID
GROUP BY f.facilityName, YEAR(ia.administrationDate)
ORDER BY f.facilityName, year;

-- MEDICINE INVENTORY UTILIZATION REPORT (Assigned to RAPHY) --
CREATE VIEW MedicineInventoryUtilization AS
SELECT
    f.facilityName,
    m.medicineName,
    m.medicineType,
    DATE_FORMAT(pr.distributionDate, '%Y-%u') AS weekYear,
    DATE_FORMAT(pr.distributionDate, '%Y-%m') AS monthYear,
    YEAR(pr.distributionDate) AS year,
    SUM(pr.qtyDistributed) AS totalDistributed,
    mi.quantityInStock AS currentStock,
    s.supplierName,
    SUM(ri.totalOrderCost) AS totalRestockCost
FROM medicine m
JOIN medicine_inventory mi ON m.medicineID = mi.medicineID
JOIN facility f ON mi.facilityID = f.facilityID
LEFT JOIN prescription_receipt pr ON pr.medicineID = m.medicineID
	AND pr.facilityID = f.facilityID
LEFT JOIN restock_invoice ri ON ri.supplierID - s.supplierID
LEFT JOIN supplier s ON ri.supplierID - s.supplierID
GROUP BY f.facilityName, m.medicineName, m.medicineType, weekYear, monthYear, year, s.supplierName, mi.quantityInStock;