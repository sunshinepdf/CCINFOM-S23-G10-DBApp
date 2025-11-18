-- HEALTH WORKER DETAILS VIEW (Assigned to ASHLEY)  --
CREATE VIEW healthworker_assigned_patients_view AS
SELECT 
    w.hWorkerID AS 'Worker ID',
    CONCAT(w.hWorkerLastName, ', ', w.hWorkerFirstName) AS 'Health Worker Name',
    w.hWorkerPosition AS 'Position',
    w.hContactInformation AS 'Contact Information', 
    s.StatusName AS 'Worker Status',
    p.patientID AS 'Patient ID',
    CONCAT(p.lastName, ', ', p.firstName) AS 'Patient Name', 
    p.birthDate AS 'Date of Birth',
    p.gender AS 'Gender',
    p.bloodType AS 'Blood Type',
    p.address AS 'Address',
    p.primaryPhone AS 'Primary Phone',
    p.emergencyContact AS 'Emergency Contact',
    mc.consultationDate AS 'Last Consultation Date',
    mc.consultationTime AS 'Last Consultation Time',
    mc.diagnosis AS 'Latest Diagnosis',
    cs.StatusName AS 'Consultation Status'

FROM worker w INNER JOIN REF_Status s ON w.hWorkerStatusID = s.StatusID
			  LEFT JOIN medical_consultation mc ON w.hWorkerID = mc.hWorkerID
			  LEFT JOIN patient p ON mc.patientID = p.patientID
              LEFT JOIN REF_Status cs ON mc.consultationStatusID = cs.StatusID 
									  WHERE mc.consultationID IS NULL OR mc.consultationID IN(
											SELECT MAX(consultationID)
											FROM medical_consultation
											GROUP BY hWorkerID, patientID)
ORDER BY w.hWorkerID, mc.consultationDate DESC;

-- FACILITY DETAILS VIEW (Assigned to KHYLE) --
CREATE VIEW facilityDetails_view AS
SELECT 
    f.facilityID AS 'Facility ID', 
    f.facilityName AS 'Facility Name', 
    f.facilityAddress AS 'Facility Address', 
    f.facilityContactNum AS 'Facility Contact Number',
    f.shiftStart AS 'Facility Opening Time',
    f.shiftEnd AS 'Facility Closing Time', 
    hw.hWorkerID AS 'Worker ID', 
    CONCAT(hw.hWorkerLastName, ', ', hw.hWorkerFirstName) AS 'HealthWorkerName', 
    m.medicineID AS 'Medicine ID', 
    m.medicineName AS 'Medicine Name', 
    mi.quantityInStock AS 'Medicine Quantity In Stock'
    
FROM facility f LEFT JOIN worker hw ON hw.hWorkerFacilityID = f.facilityID
				LEFT JOIN medicine_inventory mi ON mi.facilityID = f.facilityID
                LEFT JOIN medicine m ON m.medicineID = mi.medicineID				
ORDER BY f.FacilityID;

-- PATIENT CONSULTATIONS VIEW (Assigned to RAPHY) --
CREATE VIEW patientConsultations_view AS
SELECT 
    p.patientID,
    CONCAT(p.lastName, ', ', p.firstName) AS patientName,
    p.birthDate,
    p.gender,
    p.bloodType,
    p.address,
    p.primaryPhone,
    p.emergencyContact,
    p.patientStatus,
/* to be added
    mc.consultationID,
    mc.consultationDate,
    mc.diagnosis,
    mc.symptoms,
    mc.prescription,
    mc.status
*/
FROM patient p
/*
LEFT JOIN medical_consultation mc
    ON p.patientID = mc.patientID;
*/

-- MEDICINE INVENTORY UTILIZATION REPORT (Assigned to RAPHY) --
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
