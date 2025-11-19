USE BHMS_DB;

-- MEDICINE DISTRIBUTION DETAILS VIEW (Assigned to SPENCER, Created by ASHLEY) --
CREATE VIEW medicine_distribution_history AS
SELECT
    m.medicineID AS 'Medicine ID',
    m.medicineName AS 'Medicine Name',
    m.medicineDesc AS 'Description',
    m.dosageForm AS 'Dosage Form',
    m.strength AS 'Strength',
    m.batchNumber AS 'Batch Number',
    s.statusName AS 'Status',
    pr.receiptID AS 'Distribution ID',
    pr.distributionDate AS 'Distribution Date',
    pr.qtyDistributed AS 'Quantity Distributed',
    p.patientID AS 'Patient ID',
    CONCAT(p.lastName, ', ', p.firstName) AS 'Patient Name',
    p.birthDate AS 'Patient Birth Date',
    p.gender AS 'Patient Gender',
    p.address AS 'Patient Address'
FROM medicine m
JOIN REF_Status s ON m.medicineStatus = s.statusID
LEFT JOIN prescription_receipt pr ON m.medicineID = pr.medicineID
LEFT JOIN patient p ON pr.patientID = p.patientID;

-- MEDICINE INVENTORY DETAILS VIEW (Assigned to SPENCER, Created by ASHLEY) --
CREATE VIEW medicine_inventory_status AS
SELECT
    mi.inventoryID,
    f.facilityID,
    f.facilityName,
    m.medicineID,
    m.medicineName,
    mi.quantityInStock,
    rs_inv.statusName AS inventoryStatus,
    CASE
        WHEN mi.quantityInStock < 10 THEN 'Low'
        ELSE 'OK'
    END AS stockLevel
FROM medicine_inventory mi
JOIN REF_Status rs_inv ON mi.inventoryStatusID = rs_inv.statusID
JOIN facility f ON mi.facilityID = f.facilityID
JOIN medicine m ON mi.medicineID = m.medicineID;

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
    mc.consultationID,
    mc.consultationDate,
    mc.diagnosis,
    mc.symptoms,
    mc.prescription,
    mc.consultationStatusID
FROM patient p
LEFT JOIN medical_consultation mc
    ON p.patientID = mc.patientID;

-- IMMUNIZATION ADMINISTRATION VIEW (Assigned to RAPHY) --
CREATE VIEW immunization_summary AS
SELECT
    ia.immunizationID,
    p.patientID,
    CONCAT(p.lastName, ', ', p.firstName) AS patientName,
    w.hWorkerID,
    CONCAT(w.hWorkerLastName, ', ', w.hWorkerFirstName) AS workerName,
    m.medicineID,
    m.medicineName AS vaccineName,
    ia.administrationDate,
    ia.dosageNumber,
    ia.nextVaccinationDate,
    ia.immunizationStatus,
    ia.sideEffects
FROM immunization_administration ia
JOIN patient p ON ia.patientID = p.patientID
JOIN worker w ON ia.hWorkerID = w.hWorkerID
JOIN medicine m ON ia.medicineID = m.medicineID;
