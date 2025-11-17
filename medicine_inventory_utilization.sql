CREATE VIEW MedicineInventoryUtilization AS
SELECT
    f.facilityName,
    DATE_FORMAT(pr.distributionDate, '%Y-%u') AS weekYear,
    DATE_FORMAT(pr.distributionDate, '%Y-%m') AS monthYear,
    YEAR(pr.distributionDate) AS year,
    m.medicineName,
    m.medicineType,
    SUM(pr.qtyDistributed) AS totalDistributed
FROM prescription_receipt pr
JOIN facility f ON pr.facilityID = f.facilityID
JOIN medicine m ON pr.medicineID = m.medicineID
GROUP BY f.facilityName, weekYear, monthYear, year, m.medicineName, m.medicineType;