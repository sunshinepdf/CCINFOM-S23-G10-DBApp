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
FROM worker w
INNER JOIN REF_Status s ON w.hWorkerStatusID = s.StatusID
LEFT JOIN medical_consultation mc ON w.hWorkerID = mc.hWorkerID
LEFT JOIN patient p ON mc.patientID = p.patientID
LEFT JOIN REF_Status cs ON mc.consultationStatusID = cs.StatusID
WHERE mc.consultationID IS NULL OR mc.consultationID IN (
    SELECT MAX(consultationID)
    FROM medical_consultation
    GROUP BY hWorkerID, patientID
)
ORDER BY w.hWorkerID, mc.consultationDate DESC;

-- FACILITY DETAILS VIEW (Assigned to KHYLE) --
CREATE VIEW facilityDetails_view AS
SELECT 
    f.facilityID, f.facilityName, f.facilityAddress, f.facilityContactNum, f.shiftStart,
    f.shiftEnd, 
    hw.hWorkerID, CONCAT(hw.hWorkerLastName, ', ', hw.hWorkerFirstName) AS HealthWorkerName, 
    m.medicineID, m.medicineName, m.quantityInStock
FROM facility f LEFT JOIN worker hw 		  	  ON f.facilityID = hw.facilityID
				# TODO: add other joins once medicine inventory and facility link table is created 
ORDER BY f.FacilityID;
