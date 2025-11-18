-- IMMUNIZATION IMPACT REPORT (Assigned to ASHLEY) --
SELECT
    f.facilityName,
    YEAR(ia.administrationDate) AS year,
    CASE 
        WHEN :period = 'week' THEN WEEK(ia.administrationDate, 1)
        WHEN :period = 'month' THEN MONTH(ia.administrationDate)
        ELSE NULL
    END AS period,
    COUNT(DISTINCT ia.patientID) AS patientsImmunized,
    COUNT(DISTINCT ia.vaccineType) AS vaccinesUsed,
    SUM(CASE WHEN ia.immunizationStatus = 'pending' THEN 1 ELSE 0 END) AS missedVaccinations
FROM immunization_administration ia
JOIN worker w ON ia.hWorkerID = w.hWorkerID
JOIN facility f ON w.facilityID = f.facilityID
GROUP BY
    f.facilityName,
    YEAR(ia.administrationDate),
    CASE 
        WHEN :period = 'week' THEN WEEK(ia.administrationDate, 1)
        WHEN :period = 'month' THEN MONTH(ia.administrationDate)
        ELSE NULL
    END
ORDER BY f.facilityName, year, period;
