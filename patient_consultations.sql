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