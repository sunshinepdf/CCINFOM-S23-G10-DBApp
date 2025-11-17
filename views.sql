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