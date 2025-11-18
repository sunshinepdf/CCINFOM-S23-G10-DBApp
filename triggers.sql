-- ============================================================================
-- BARANGAY HEALTH MONITORING SYSTEM - TRIGGERS AND STORED PROCEDURES
-- ============================================================================

USE BHMS_DB;

-- ----------------------------------------------------------------------------
-- PATIENT RECORD TRIGGERS
-- ----------------------------------------------------------------------------

-- Validate birth date is not in the future
DELIMITER $$
DROP TRIGGER IF EXISTS trg_patient_validate_birthdate_insert$$
CREATE TRIGGER trg_patient_validate_birthdate_insert
BEFORE INSERT ON patient
FOR EACH ROW
BEGIN
    IF NEW.birthDate > CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Birth date cannot be in the future';
    END IF;
END$$
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS trg_patient_validate_birthdate_update$$
CREATE TRIGGER trg_patient_validate_birthdate_update
BEFORE UPDATE ON patient
FOR EACH ROW
BEGIN
    IF NEW.birthDate > CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Birth date cannot be in the future';
    END IF;
END$$
DELIMITER ;

-- Prevent patient from being their own emergency contact
DELIMITER $$
DROP TRIGGER IF EXISTS trg_patient_validate_emergency_contact_insert$$
CREATE TRIGGER trg_patient_validate_emergency_contact_insert
BEFORE INSERT ON patient
FOR EACH ROW
BEGIN
    IF NEW.emergencyContact = NEW.primaryPhone THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Patient cannot be their own emergency contact';
    END IF;
END$$
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS trg_patient_validate_emergency_contact_update$$
CREATE TRIGGER trg_patient_validate_emergency_contact_update
BEFORE UPDATE ON patient
FOR EACH ROW
BEGIN
    IF NEW.emergencyContact = NEW.primaryPhone THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Patient cannot be their own emergency contact';
    END IF;
END$$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- MEDICINE INVENTORY TRIGGERS
-- ----------------------------------------------------------------------------

-- Prevent negative stock quantities
DELIMITER $$
DROP TRIGGER IF EXISTS trg_medicine_inventory_validate_quantity_insert$$
CREATE TRIGGER trg_medicine_inventory_validate_quantity_insert
BEFORE INSERT ON medicine_inventory
FOR EACH ROW
BEGIN
    IF NEW.quantityInStock < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Quantity in stock cannot be negative';
    END IF;
END$$
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS trg_medicine_inventory_validate_quantity_update$$
CREATE TRIGGER trg_medicine_inventory_validate_quantity_update
BEFORE UPDATE ON medicine_inventory
FOR EACH ROW
BEGIN
    IF NEW.quantityInStock < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Quantity in stock cannot be negative';
    END IF;
END$$
DELIMITER ;

-- Auto-update inventory status based on quantity
DELIMITER $$
DROP TRIGGER IF EXISTS trg_medicine_inventory_update_status$$
CREATE TRIGGER trg_medicine_inventory_update_status
BEFORE UPDATE ON medicine_inventory
FOR EACH ROW
BEGIN
    DECLARE v_outOfStockStatusID INT;
    DECLARE v_lowStockStatusID INT;
    DECLARE v_availableStatusID INT;
    
    -- Get status IDs
    SELECT statusID INTO v_outOfStockStatusID FROM REF_Status WHERE statusName = 'Out of Stock';
    SELECT statusID INTO v_lowStockStatusID FROM REF_Status WHERE statusName = 'Low Stock';
    SELECT statusID INTO v_availableStatusID FROM REF_Status WHERE statusName = 'Available';
    
    -- Update status based on quantity
    IF NEW.quantityInStock = 0 THEN
        SET NEW.inventoryStatusID = v_outOfStockStatusID;
    ELSEIF NEW.quantityInStock <= 10 THEN
        SET NEW.inventoryStatusID = v_lowStockStatusID;
    ELSE
        SET NEW.inventoryStatusID = v_availableStatusID;
    END IF;
END$$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- MEDICAL CONSULTATION TRIGGERS
-- ----------------------------------------------------------------------------

-- Validate consultation is within facility operating hours
DELIMITER $$
DROP TRIGGER IF EXISTS trg_consultation_validate_hours_insert$$
CREATE TRIGGER trg_consultation_validate_hours_insert
BEFORE INSERT ON medical_consultation
FOR EACH ROW
BEGIN
    DECLARE facility_start TIME;
    DECLARE facility_end TIME;
    
    SELECT shiftStart, shiftEnd INTO facility_start, facility_end
    FROM facility
    WHERE facilityID = NEW.facilityID;
    
    IF NEW.consultationTime < facility_start OR NEW.consultationTime > facility_end THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Consultation time is outside facility operating hours';
    END IF;
END$$
DELIMITER ;

-- Validate health worker is active and assigned to facility
DELIMITER $$
DROP TRIGGER IF EXISTS trg_consultation_validate_worker_insert$$
CREATE TRIGGER trg_consultation_validate_worker_insert
BEFORE INSERT ON medical_consultation
FOR EACH ROW
BEGIN
    DECLARE worker_status VARCHAR(50);
    DECLARE worker_facility INT;
    
    SELECT s.statusName, w.hWorkerFacilityID INTO worker_status, worker_facility
    FROM worker w
    JOIN REF_Status s ON w.hWorkerStatusID = s.statusID
    WHERE w.hWorkerID = NEW.hWorkerID;
    
    IF worker_status != 'Active' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Only active health workers can conduct consultations';
    END IF;
    
    IF worker_facility != NEW.facilityID THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Health worker is not assigned to this facility';
    END IF;
END$$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- PRESCRIPTION RECEIPT TRIGGERS
-- ----------------------------------------------------------------------------

-- Validate prescription before distribution
DELIMITER $$
DROP TRIGGER IF EXISTS trg_prescription_validate_insert$$
CREATE TRIGGER trg_prescription_validate_insert
BEFORE INSERT ON prescription_receipt
FOR EACH ROW
BEGIN
    DECLARE worker_status VARCHAR(50);
    DECLARE medicine_qty INT;
    DECLARE consultation_exists INT;
    DECLARE consultation_facility INT;
    
    -- Check if worker is active
    SELECT s.statusName INTO worker_status
    FROM worker w
    JOIN REF_Status s ON w.hWorkerStatusID = s.statusID
    WHERE w.hWorkerID = NEW.hWorkerID;
    
    IF worker_status != 'Active' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Only active health workers can distribute medicine';
    END IF;
    
    -- Check medicine availability in inventory
    SELECT mc.facilityID INTO consultation_facility
    FROM medical_consultation mc
    WHERE mc.consultationID = NEW.consultationID;
    
    SELECT quantityInStock INTO medicine_qty
    FROM medicine_inventory
    WHERE medicineID = NEW.medicineID
    AND facilityID = consultation_facility;
    
    IF medicine_qty IS NULL OR medicine_qty < NEW.qtyDistributed THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient medicine stock for distribution';
    END IF;
    
    -- Verify consultation exists
    SELECT COUNT(*) INTO consultation_exists
    FROM medical_consultation
    WHERE consultationID = NEW.consultationID;
    
    IF consultation_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid consultation reference';
    END IF;
    
    SET NEW.isValidPrescription = TRUE;
    
    -- Set distribution date to current date if not provided
    IF NEW.distributionDate IS NULL THEN
        SET NEW.distributionDate = CURDATE();
    END IF;
END$$
DELIMITER ;

-- Auto-update inventory after prescription distribution
DELIMITER $$
DROP TRIGGER IF EXISTS trg_prescription_update_inventory$$
CREATE TRIGGER trg_prescription_update_inventory
AFTER INSERT ON prescription_receipt
FOR EACH ROW
BEGIN
    DECLARE consultation_facility INT;
    
    -- Get the facility from the consultation
    SELECT facilityID INTO consultation_facility
    FROM medical_consultation
    WHERE consultationID = NEW.consultationID;
    
    -- Update inventory at the facility
    UPDATE medicine_inventory
    SET quantityInStock = quantityInStock - NEW.qtyDistributed
    WHERE medicineID = NEW.medicineID
    AND facilityID = consultation_facility;
    
    -- Mark inventory as updated
    UPDATE prescription_receipt
    SET inventoryUpdated = TRUE
    WHERE receiptID = NEW.receiptID;
END$$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- IMMUNIZATION ADMINISTRATION TRIGGERS
-- ----------------------------------------------------------------------------

-- Validate immunization administration
DELIMITER $$
DROP TRIGGER IF EXISTS trg_immunization_validate_insert$$
CREATE TRIGGER trg_immunization_validate_insert
BEFORE INSERT ON immunization_administration
FOR EACH ROW
BEGIN
    DECLARE patient_birthdate DATE;
    DECLARE worker_status VARCHAR(50);
    DECLARE worker_facility INT;
    DECLARE vaccine_qty INT;
    DECLARE last_dose_date DATE;
    
    -- Check patient birth date
    SELECT birthDate INTO patient_birthdate
    FROM patient
    WHERE patientID = NEW.patientID;
    
    IF NEW.administrationDate < patient_birthdate THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Administration date cannot be before patient birth date';
    END IF;
    
    -- Check worker status and get facility
    SELECT s.statusName, w.hWorkerFacilityID INTO worker_status, worker_facility
    FROM worker w
    JOIN REF_Status s ON w.hWorkerStatusID = s.statusID
    WHERE w.hWorkerID = NEW.hWorkerID;
    
    IF worker_status != 'Active' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Only active health workers can administer vaccines';
    END IF;
    
    -- Check vaccine availability at worker's facility
    SELECT quantityInStock INTO vaccine_qty
    FROM medicine_inventory
    WHERE medicineID = NEW.medicineID
    AND facilityID = worker_facility;
    
    IF vaccine_qty IS NULL OR vaccine_qty < 1 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient vaccine stock at this facility';
    END IF;
    
    -- Check last dose date for multi-dose vaccines
    IF NEW.dosageNumber > 1 THEN
        SELECT MAX(administrationDate) INTO last_dose_date
        FROM immunization_administration
        WHERE patientID = NEW.patientID
        AND vaccineType = NEW.vaccineType
        AND dosageNumber = NEW.dosageNumber - 1;
        
        IF last_dose_date IS NULL THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Previous dose not found for this patient';
        END IF;
        
        IF NEW.administrationDate <= last_dose_date THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Administration date must be after previous dose';
        END IF;
    END IF;
    
    -- Validate next vaccination date if provided
    IF NEW.nextVaccinationDate IS NOT NULL AND NEW.nextVaccinationDate <= NEW.administrationDate THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Next vaccination date must be after administration date';
    END IF;
END$$
DELIMITER ;

-- Auto-update vaccine inventory after administration
DELIMITER $$
DROP TRIGGER IF EXISTS trg_immunization_update_inventory$$
CREATE TRIGGER trg_immunization_update_inventory
AFTER INSERT ON immunization_administration
FOR EACH ROW
BEGIN
    DECLARE worker_facility INT;
    
    -- Get worker's facility
    SELECT hWorkerFacilityID INTO worker_facility
    FROM worker
    WHERE hWorkerID = NEW.hWorkerID;
    
    -- Update inventory at worker's facility
    UPDATE medicine_inventory
    SET quantityInStock = quantityInStock - 1
    WHERE medicineID = NEW.medicineID
    AND facilityID = worker_facility;
END$$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- WORKER TRIGGERS
-- ----------------------------------------------------------------------------

-- Validate worker is assigned to an active facility
DELIMITER $$
DROP TRIGGER IF EXISTS trg_worker_validate_facility_insert$$
CREATE TRIGGER trg_worker_validate_facility_insert
BEFORE INSERT ON worker
FOR EACH ROW
BEGIN
    DECLARE facility_status VARCHAR(50);
    
    SELECT s.statusName INTO facility_status
    FROM facility f
    JOIN REF_Status s ON f.facilityStatusID = s.statusID
    WHERE f.facilityID = NEW.hWorkerFacilityID;
    
    IF facility_status != 'Active' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Worker can only be assigned to active facilities';
    END IF;
END$$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- RESTOCK INVOICE TRIGGERS
-- ----------------------------------------------------------------------------

-- Validate delivery date
DELIMITER $$
DROP TRIGGER IF EXISTS trg_restock_validate_date_insert$$
CREATE TRIGGER trg_restock_validate_date_insert
BEFORE INSERT ON restock_invoice
FOR EACH ROW
BEGIN
    IF NEW.deliveryDate < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Delivery date cannot be in the past';
    END IF;
END$$
DELIMITER ;

-- ============================================================================
-- SECTION 2: STORED PROCEDURES
-- ============================================================================

-- ----------------------------------------------------------------------------
-- PATIENT MANAGEMENT PROCEDURES
-- ----------------------------------------------------------------------------

-- Add new patient
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_add_patient$$
CREATE PROCEDURE sp_add_patient(
    IN p_lastName VARCHAR(45),
    IN p_firstName VARCHAR(45),
    IN p_birthDate DATE,
    IN p_gender ENUM('Male', 'Female'),
    IN p_bloodType ENUM('O+', 'O-', 'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-'),
    IN p_address VARCHAR(150),
    IN p_primaryPhone CHAR(11),
    IN p_emergencyContact VARCHAR(80),
    OUT p_patientID INT
)
BEGIN
    DECLARE v_statusID INT;
    
    -- Get 'Alive' status ID
    SELECT statusID INTO v_statusID 
    FROM REF_Status 
    WHERE statusName = 'Alive';
    
    INSERT INTO patient (lastName, firstName, birthDate, gender, bloodType, 
                        address, primaryPhone, emergencyContact, patientStatus)
    VALUES (p_lastName, p_firstName, p_birthDate, p_gender, p_bloodType, 
            p_address, p_primaryPhone, p_emergencyContact, v_statusID);
    
    SET p_patientID = LAST_INSERT_ID();
END$$
DELIMITER ;

-- Update patient information
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_update_patient$$
CREATE PROCEDURE sp_update_patient(
    IN p_patientID INT,
    IN p_address VARCHAR(150),
    IN p_primaryPhone CHAR(11),
    IN p_emergencyContact VARCHAR(80)
)
BEGIN
    UPDATE patient
    SET address = p_address,
        primaryPhone = p_primaryPhone,
        emergencyContact = p_emergencyContact
    WHERE patientID = p_patientID;
END$$
DELIMITER ;

-- Get patient full details with consultation history
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_get_patient_details$$
CREATE PROCEDURE sp_get_patient_details(IN p_patientID INT)
BEGIN
    -- Patient basic info
    SELECT 
        p.*,
        s.statusName AS patientStatusName
    FROM patient p
    JOIN REF_Status s ON p.patientStatus = s.statusID
    WHERE p.patientID = p_patientID;
    
    -- Patient consultation history
    SELECT 
        mc.*,
        CONCAT(w.hWorkerLastName, ', ', w.hWorkerFirstName) AS healthWorkerName,
        f.facilityName,
        s.statusName AS consultationStatus
    FROM medical_consultation mc
    JOIN worker w ON mc.hWorkerID = w.hWorkerID
    JOIN facility f ON mc.facilityID = f.facilityID
    JOIN REF_Status s ON mc.consultationStatusID = s.statusID
    WHERE mc.patientID = p_patientID
    ORDER BY mc.consultationDate DESC, mc.consultationTime DESC;
    
    -- Patient immunization history
    SELECT 
        ia.*,
        CONCAT(w.hWorkerLastName, ', ', w.hWorkerFirstName) AS healthWorkerName,
        m.medicineName AS vaccineName,
        s.statusName AS immunizationStatusName
    FROM immunization_administration ia
    JOIN worker w ON ia.hWorkerID = w.hWorkerID
    JOIN medicine m ON ia.medicineID = m.medicineID
    JOIN REF_Status s ON ia.immunizationStatus = s.statusID
    WHERE ia.patientID = p_patientID
    ORDER BY ia.administrationDate DESC;
END$$
DELIMITER ;

-- Update patient status (for marking deceased)
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_update_patient_status$$
CREATE PROCEDURE sp_update_patient_status(
    IN p_patientID INT,
    IN p_statusName VARCHAR(50)
)
BEGIN
    DECLARE v_statusID INT;
    
    SELECT statusID INTO v_statusID
    FROM REF_Status
    WHERE statusName = p_statusName;
    
    IF v_statusID IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid status name';
    END IF;
    
    UPDATE patient
    SET patientStatus = v_statusID
    WHERE patientID = p_patientID;
END$$
DELIMITER ;

-- List all patients with optional filters
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_list_patients$$
CREATE PROCEDURE sp_list_patients(
    IN p_lastName VARCHAR(45),
    IN p_statusName VARCHAR(50)
)
BEGIN
    SELECT 
        p.patientID,
        CONCAT(p.lastName, ', ', p.firstName) AS fullName,
        p.birthDate,
        p.gender,
        p.bloodType,
        p.address,
        p.primaryPhone,
        s.statusName AS patientStatus
    FROM patient p
    JOIN REF_Status s ON p.patientStatus = s.statusID
    WHERE (p_lastName IS NULL OR p.lastName LIKE CONCAT('%', p_lastName, '%'))
    AND (p_statusName IS NULL OR s.statusName = p_statusName)
    ORDER BY p.lastName, p.firstName;
END$$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- MEDICAL CONSULTATION PROCEDURES
-- ----------------------------------------------------------------------------

-- Record medical consultation
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_record_consultation$$
CREATE PROCEDURE sp_record_consultation(
    IN p_patientID INT,
    IN p_hWorkerID INT,
    IN p_facilityID INT,
    IN p_consultationDate DATE,
    IN p_consultationTime TIME,
    IN p_symptoms TEXT,
    IN p_diagnosis TEXT,
    IN p_prescription TEXT,
    OUT p_consultationID INT
)
BEGIN
    DECLARE v_statusID INT;
    
    -- Get 'Completed' status ID
    SELECT statusID INTO v_statusID 
    FROM REF_Status 
    WHERE statusName = 'Completed';
    
    INSERT INTO medical_consultation (
        patientID, hWorkerID, facilityID, consultationDate, consultationTime,
        symptoms, diagnosis, prescription, consultationStatusID
    )
    VALUES (
        p_patientID, p_hWorkerID, p_facilityID, p_consultationDate, p_consultationTime,
        p_symptoms, p_diagnosis, p_prescription, v_statusID
    );
    
    SET p_consultationID = LAST_INSERT_ID();
END$$
DELIMITER ;

-- Get consultations by facility and date range
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_get_consultations_by_facility$$
CREATE PROCEDURE sp_get_consultations_by_facility(
    IN p_facilityID INT,
    IN p_startDate DATE,
    IN p_endDate DATE
)
BEGIN
    SELECT 
        mc.*,
        CONCAT(p.lastName, ', ', p.firstName) AS patientName,
        CONCAT(w.hWorkerLastName, ', ', w.hWorkerFirstName) AS healthWorkerName,
        s.statusName AS consultationStatus
    FROM medical_consultation mc
    JOIN patient p ON mc.patientID = p.patientID
    JOIN worker w ON mc.hWorkerID = w.hWorkerID
    JOIN REF_Status s ON mc.consultationStatusID = s.statusID
    WHERE (p_facilityID IS NULL OR mc.facilityID = p_facilityID)
    AND mc.consultationDate BETWEEN p_startDate AND p_endDate
    ORDER BY mc.consultationDate DESC, mc.consultationTime DESC;
END$$
DELIMITER ;

-- Update consultation
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_update_consultation$$
CREATE PROCEDURE sp_update_consultation(
    IN p_consultationID INT,
    IN p_symptoms TEXT,
    IN p_diagnosis TEXT,
    IN p_prescription TEXT
)
BEGIN
    UPDATE medical_consultation
    SET symptoms = p_symptoms,
        diagnosis = p_diagnosis,
        prescription = p_prescription
    WHERE consultationID = p_consultationID;
END$$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- PRESCRIPTION MANAGEMENT PROCEDURES
-- ----------------------------------------------------------------------------

-- Process prescription and distribute medicine
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_process_prescription$$
CREATE PROCEDURE sp_process_prescription(
    IN p_patientID INT,
    IN p_consultationID INT,
    IN p_medicineID INT,
    IN p_hWorkerID INT,
    IN p_qtyDistributed INT,
    OUT p_receiptID INT
)
BEGIN
    DECLARE v_statusID INT;
    
    -- Get 'Completed' status ID
    SELECT statusID INTO v_statusID 
    FROM REF_Status 
    WHERE statusName = 'Completed';
    
    INSERT INTO prescription_receipt (
        patientID, consultationID, medicineID, hWorkerID,
        distributionDate, qtyDistributed, prescriptionStatusID
    )
    VALUES (
        p_patientID, p_consultationID, p_medicineID, p_hWorkerID,
        CURDATE(), p_qtyDistributed, v_statusID
    );
    
    SET p_receiptID = LAST_INSERT_ID();
END$$
DELIMITER ;

-- Get prescription history for patient
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_get_patient_prescriptions$$
CREATE PROCEDURE sp_get_patient_prescriptions(IN p_patientID INT)
BEGIN
    SELECT 
        pr.*,
        m.medicineName,
        m.dosageForm,
        m.strength,
        CONCAT(w.hWorkerLastName, ', ', w.hWorkerFirstName) AS healthWorkerName,
        s.statusName AS prescriptionStatus,
        f.facilityName
    FROM prescription_receipt pr
    JOIN medicine m ON pr.medicineID = m.medicineID
    JOIN worker w ON pr.hWorkerID = w.hWorkerID
    JOIN REF_Status s ON pr.prescriptionStatusID = s.statusID
    JOIN medical_consultation mc ON pr.consultationID = mc.consultationID
    JOIN facility f ON mc.facilityID = f.facilityID
    WHERE pr.patientID = p_patientID
    ORDER BY pr.distributionDate DESC;
END$$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- MEDICINE & INVENTORY PROCEDURES
-- ----------------------------------------------------------------------------

-- Add new medicine
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_add_medicine$$
CREATE PROCEDURE sp_add_medicine(
    IN p_medicineName VARCHAR(50),
    IN p_medicineDesc TEXT,
    IN p_dosageForm VARCHAR(50),
    IN p_strength VARCHAR(50),
    IN p_batchNumber VARCHAR(50),
    OUT p_medicineID INT
)
BEGIN
    DECLARE v_statusID INT;
    
    -- Get 'Available' status ID
    SELECT statusID INTO v_statusID 
    FROM REF_Status 
    WHERE statusName = 'Available';
    
    INSERT INTO medicine (medicineName, medicineDesc, dosageForm, strength, batchNumber, medicineStatus)
    VALUES (p_medicineName, p_medicineDesc, p_dosageForm, p_strength, p_batchNumber, v_statusID);
    
    SET p_medicineID = LAST_INSERT_ID();
END$$
DELIMITER ;

-- Get immunization coverage by vaccine type
DELIMITER $
DROP PROCEDURE IF EXISTS sp_get_immunization_coverage$
CREATE PROCEDURE sp_get_immunization_coverage(
    IN p_facilityID INT,
    IN p_vaccineType VARCHAR(40),
    IN p_startDate DATE,
    IN p_endDate DATE
)
BEGIN
    SELECT 
        f.facilityName,
        ia.vaccineType,
        COUNT(DISTINCT ia.patientID) AS totalPatientsImmunized,
        COUNT(*) AS totalDosesAdministered,
        AVG(ia.dosageNumber) AS averageDosageNumber,
        SUM(CASE WHEN ia.nextVaccinationDate IS NULL THEN 1 ELSE 0 END) AS completedSeries,
        SUM(CASE WHEN ia.nextVaccinationDate IS NOT NULL THEN 1 ELSE 0 END) AS pendingSeries
    FROM immunization_administration ia
    JOIN worker w ON ia.hWorkerID = w.hWorkerID
    JOIN facility f ON w.hWorkerFacilityID = f.facilityID
    WHERE (p_facilityID IS NULL OR f.facilityID = p_facilityID)
    AND (p_vaccineType IS NULL OR ia.vaccineType = p_vaccineType)
    AND ia.administrationDate BETWEEN p_startDate AND p_endDate
    GROUP BY f.facilityName, ia.vaccineType
    ORDER BY f.facilityName, ia.vaccineType;
END$
DELIMITER ;

-- Get patient immunization history
DELIMITER $
DROP PROCEDURE IF EXISTS sp_get_patient_immunizations$
CREATE PROCEDURE sp_get_patient_immunizations(IN p_patientID INT)
BEGIN
    SELECT 
        ia.*,
        m.medicineName AS vaccineName,
        CONCAT(w.hWorkerLastName, ', ', w.hWorkerFirstName) AS healthWorkerName,
        f.facilityName,
        s.statusName AS immunizationStatusName
    FROM immunization_administration ia
    JOIN medicine m ON ia.medicineID = m.medicineID
    JOIN worker w ON ia.hWorkerID = w.hWorkerID
    JOIN facility f ON w.hWorkerFacilityID = f.facilityID
    JOIN REF_Status s ON ia.immunizationStatus = s.statusID
    WHERE ia.patientID = p_patientID
    ORDER BY ia.administrationDate DESC;
END$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- HEALTH WORKER PROCEDURES
-- ----------------------------------------------------------------------------

-- Add new health worker
DELIMITER $
DROP PROCEDURE IF EXISTS sp_add_worker$
CREATE PROCEDURE sp_add_worker(
    IN p_lastName VARCHAR(45),
    IN p_firstName VARCHAR(45),
    IN p_position VARCHAR(45),
    IN p_contactInfo CHAR(11),
    IN p_facilityID INT,
    OUT p_hWorkerID INT
)
BEGIN
    DECLARE v_statusID INT;
    
    -- Get 'Active' status ID
    SELECT statusID INTO v_statusID 
    FROM REF_Status 
    WHERE statusName = 'Active';
    
    INSERT INTO worker (hWorkerLastName, hWorkerFirstName, hWorkerPosition,
                       hContactInformation, hWorkerFacilityID, hWorkerStatusID)
    VALUES (p_lastName, p_firstName, p_position, p_contactInfo, p_facilityID, v_statusID);
    
    SET p_hWorkerID = LAST_INSERT_ID();
END$
DELIMITER ;

-- Update worker information
DELIMITER $
DROP PROCEDURE IF EXISTS sp_update_worker$
CREATE PROCEDURE sp_update_worker(
    IN p_hWorkerID INT,
    IN p_position VARCHAR(45),
    IN p_contactInfo CHAR(11),
    IN p_facilityID INT
)
BEGIN
    UPDATE worker
    SET hWorkerPosition = p_position,
        hContactInformation = p_contactInfo,
        hWorkerFacilityID = p_facilityID
    WHERE hWorkerID = p_hWorkerID;
END$
DELIMITER ;

-- Update worker status
DELIMITER $
DROP PROCEDURE IF EXISTS sp_update_worker_status$
CREATE PROCEDURE sp_update_worker_status(
    IN p_hWorkerID INT,
    IN p_statusName VARCHAR(50)
)
BEGIN
    DECLARE v_statusID INT;
    
    SELECT statusID INTO v_statusID
    FROM REF_Status
    WHERE statusName = p_statusName;
    
    IF v_statusID IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid status name';
    END IF;
    
    UPDATE worker
    SET hWorkerStatusID = v_statusID
    WHERE hWorkerID = p_hWorkerID;
END$
DELIMITER ;

-- Get health worker workload
DELIMITER $
DROP PROCEDURE IF EXISTS sp_get_worker_workload$
CREATE PROCEDURE sp_get_worker_workload(
    IN p_hWorkerID INT,
    IN p_startDate DATE,
    IN p_endDate DATE
)
BEGIN
    SELECT 
        'Consultations' AS activityType,
        COUNT(*) AS activityCount
    FROM medical_consultation
    WHERE hWorkerID = p_hWorkerID
    AND consultationDate BETWEEN p_startDate AND p_endDate
    
    UNION ALL
    
    SELECT 
        'Immunizations' AS activityType,
        COUNT(*) AS activityCount
    FROM immunization_administration
    WHERE hWorkerID = p_hWorkerID
    AND administrationDate BETWEEN p_startDate AND p_endDate
    
    UNION ALL
    
    SELECT 
        'Prescriptions' AS activityType,
        COUNT(*) AS activityCount
    FROM prescription_receipt
    WHERE hWorkerID = p_hWorkerID
    AND distributionDate BETWEEN p_startDate AND p_endDate;
END$
DELIMITER ;

-- Get worker details with assigned patients
DELIMITER $
DROP PROCEDURE IF EXISTS sp_get_worker_details$
CREATE PROCEDURE sp_get_worker_details(IN p_hWorkerID INT)
BEGIN
    -- Worker basic info
    SELECT 
        w.*,
        f.facilityName,
        f.facilityAddress,
        s.statusName AS workerStatus
    FROM worker w
    JOIN facility f ON w.hWorkerFacilityID = f.facilityID
    JOIN REF_Status s ON w.hWorkerStatusID = s.statusID
    WHERE w.hWorkerID = p_hWorkerID;
    
    -- Recent consultations
    SELECT 
        mc.consultationID,
        mc.consultationDate,
        mc.consultationTime,
        CONCAT(p.lastName, ', ', p.firstName) AS patientName,
        mc.diagnosis,
        s.statusName AS consultationStatus
    FROM medical_consultation mc
    JOIN patient p ON mc.patientID = p.patientID
    JOIN REF_Status s ON mc.consultationStatusID = s.statusID
    WHERE mc.hWorkerID = p_hWorkerID
    ORDER BY mc.consultationDate DESC, mc.consultationTime DESC
    LIMIT 10;
END$
DELIMITER ;

-- List all workers with optional filters
DELIMITER $
DROP PROCEDURE IF EXISTS sp_list_workers$
CREATE PROCEDURE sp_list_workers(
    IN p_facilityID INT,
    IN p_statusName VARCHAR(50)
)
BEGIN
    SELECT 
        w.hWorkerID,
        CONCAT(w.hWorkerLastName, ', ', w.hWorkerFirstName) AS fullName,
        w.hWorkerPosition,
        w.hContactInformation,
        f.facilityName,
        s.statusName AS workerStatus
    FROM worker w
    JOIN facility f ON w.hWorkerFacilityID = f.facilityID
    JOIN REF_Status s ON w.hWorkerStatusID = s.statusID
    WHERE (p_facilityID IS NULL OR w.hWorkerFacilityID = p_facilityID)
    AND (p_statusName IS NULL OR s.statusName = p_statusName)
    ORDER BY w.hWorkerLastName, w.hWorkerFirstName;
END$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- FACILITY PROCEDURES
-- ----------------------------------------------------------------------------

-- Add new facility
DELIMITER $
DROP PROCEDURE IF EXISTS sp_add_facility$
CREATE PROCEDURE sp_add_facility(
    IN p_facilityName VARCHAR(60),
    IN p_facilityAddress VARCHAR(100),
    IN p_facilityContactNum CHAR(11),
    IN p_shiftStart TIME,
    IN p_shiftEnd TIME,
    OUT p_facilityID INT
)
BEGIN
    DECLARE v_statusID INT;
    
    -- Get 'Active' status ID
    SELECT statusID INTO v_statusID 
    FROM REF_Status 
    WHERE statusName = 'Active';
    
    INSERT INTO facility (facilityName, facilityAddress, facilityContactNum, 
                         shiftStart, shiftEnd, facilityStatusID)
    VALUES (p_facilityName, p_facilityAddress, p_facilityContactNum,
            p_shiftStart, p_shiftEnd, v_statusID);
    
    SET p_facilityID = LAST_INSERT_ID();
END$
DELIMITER ;

-- Update facility
DELIMITER $
DROP PROCEDURE IF EXISTS sp_update_facility$
CREATE PROCEDURE sp_update_facility(
    IN p_facilityID INT,
    IN p_facilityAddress VARCHAR(100),
    IN p_facilityContactNum CHAR(11),
    IN p_shiftStart TIME,
    IN p_shiftEnd TIME
)
BEGIN
    UPDATE facility
    SET facilityAddress = p_facilityAddress,
        facilityContactNum = p_facilityContactNum,
        shiftStart = p_shiftStart,
        shiftEnd = p_shiftEnd
    WHERE facilityID = p_facilityID;
END$
DELIMITER ;

-- Get facility details
DELIMITER $
DROP PROCEDURE IF EXISTS sp_get_facility_details$
CREATE PROCEDURE sp_get_facility_details(IN p_facilityID INT)
BEGIN
    -- Facility basic info
    SELECT 
        f.*,
        s.statusName AS facilityStatus
    FROM facility f
    JOIN REF_Status s ON f.facilityStatusID = s.statusID
    WHERE f.facilityID = p_facilityID;
    
    -- Assigned workers
    SELECT 
        w.hWorkerID,
        CONCAT(w.hWorkerLastName, ', ', w.hWorkerFirstName) AS workerName,
        w.hWorkerPosition,
        w.hContactInformation,
        ws.statusName AS workerStatus
    FROM worker w
    JOIN REF_Status ws ON w.hWorkerStatusID = ws.statusID
    WHERE w.hWorkerFacilityID = p_facilityID
    ORDER BY w.hWorkerLastName, w.hWorkerFirstName;
    
    -- Medicine inventory at facility
    SELECT 
        m.medicineID,
        m.medicineName,
        m.dosageForm,
        m.strength,
        mi.quantityInStock,
        s.statusName AS inventoryStatus
    FROM medicine_inventory mi
    JOIN medicine m ON mi.medicineID = m.medicineID
    JOIN REF_Status s ON mi.inventoryStatusID = s.statusID
    WHERE mi.facilityID = p_facilityID
    ORDER BY m.medicineName;
    
    -- Recent activity summary
    SELECT 
        COUNT(DISTINCT mc.consultationID) AS totalConsultations,
        COUNT(DISTINCT mc.patientID) AS uniquePatients,
        MAX(mc.consultationDate) AS lastConsultationDate
    FROM medical_consultation mc
    WHERE mc.facilityID = p_facilityID
    AND mc.consultationDate >= DATE_SUB(CURDATE(), INTERVAL 30 DAY);
END$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- SUPPLIER PROCEDURES
-- ----------------------------------------------------------------------------

-- Add new supplier
DELIMITER $
DROP PROCEDURE IF EXISTS sp_add_supplier$
CREATE PROCEDURE sp_add_supplier(
    IN p_supplierName VARCHAR(50),
    IN p_supplierAddress VARCHAR(100),
    IN p_supplierContactNum CHAR(11),
    IN p_supplierType ENUM('Medical Equipment Supplier', 'Medicine Supplier', 'Vaccine Supplier'),
    IN p_deliveryLeadTime INT,
    IN p_transactionDetails VARCHAR(350),
    OUT p_supplierID INT
)
BEGIN
    DECLARE v_statusID INT;
    
    -- Get 'Active' status ID
    SELECT statusID INTO v_statusID 
    FROM REF_Status 
    WHERE statusName = 'Active';
    
    INSERT INTO supplier (supplierName, supplierAddress, supplierContactNum,
                         supplierType, deliveryLeadTime, transactionDetails, supplierStatusID)
    VALUES (p_supplierName, p_supplierAddress, p_supplierContactNum,
            p_supplierType, p_deliveryLeadTime, p_transactionDetails, v_statusID);
    
    SET p_supplierID = LAST_INSERT_ID();
END$
DELIMITER ;

-- Get supplier details
DELIMITER $
DROP PROCEDURE IF EXISTS sp_get_supplier_details$
CREATE PROCEDURE sp_get_supplier_details(IN p_supplierID INT)
BEGIN
    -- Supplier basic info
    SELECT 
        s.*,
        st.statusName AS supplierStatus
    FROM supplier s
    JOIN REF_Status st ON s.supplierStatusID = st.statusID
    WHERE s.supplierID = p_supplierID;
    
    -- Recent invoices
    SELECT 
        ri.*,
        CONCAT(w.hWorkerLastName, ', ', w.hWorkerFirstName) AS receivedByName,
        ds.statusName AS deliveryStatusName
    FROM restock_invoice ri
    JOIN worker w ON ri.receivedBy = w.hWorkerID
    JOIN REF_Status ds ON ri.deliveryStatus = ds.statusID
    WHERE ri.supplierID = p_supplierID
    ORDER BY ri.deliveryDate DESC
    LIMIT 10;
END$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- RESTOCK INVOICE PROCEDURES
-- ----------------------------------------------------------------------------

-- Create restock invoice
DELIMITER $
DROP PROCEDURE IF EXISTS sp_create_restock_invoice$
CREATE PROCEDURE sp_create_restock_invoice(
    IN p_supplierID INT,
    IN p_purchaseOrderID VARCHAR(16),
    IN p_deliveryDate DATE,
    IN p_receivedBy INT,
    IN p_totalOrderCost DECIMAL(10,2),
    OUT p_invoiceID INT
)
BEGIN
    DECLARE v_statusID INT;
    
    -- Get 'Pending' status ID for delivery
    SELECT statusID INTO v_statusID 
    FROM REF_Status 
    WHERE statusName = 'Pending';
    
    INSERT INTO restock_invoice (supplierID, purchaseOrderID, deliveryDate, 
                                 receivedBy, totalOrderCost, deliveryStatus)
    VALUES (p_supplierID, p_purchaseOrderID, p_deliveryDate,
            p_receivedBy, p_totalOrderCost, v_statusID);
    
    SET p_invoiceID = LAST_INSERT_ID();
END$
DELIMITER ;

-- Update restock invoice status
DELIMITER $
DROP PROCEDURE IF EXISTS sp_update_restock_status$
CREATE PROCEDURE sp_update_restock_status(
    IN p_invoiceID INT,
    IN p_statusName VARCHAR(50)
)
BEGIN
    DECLARE v_statusID INT;
    
    SELECT statusID INTO v_statusID
    FROM REF_Status
    WHERE statusName = p_statusName;
    
    IF v_statusID IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid status name';
    END IF;
    
    UPDATE restock_invoice
    SET deliveryStatus = v_statusID
    WHERE invoiceID = p_invoiceID;
END$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- REPORTING PROCEDURES
-- ----------------------------------------------------------------------------

-- Consultation summary report (Report 1 - Assigned to ASHLEY)
DELIMITER $
DROP PROCEDURE IF EXISTS sp_consultation_summary_report$
CREATE PROCEDURE sp_consultation_summary_report(
    IN p_facilityID INT,
    IN p_startDate DATE,
    IN p_endDate DATE,
    IN p_groupBy VARCHAR(10) -- 'week', 'month', 'year'
)
BEGIN
    IF p_groupBy = 'week' THEN
        SELECT 
            f.facilityName,
            YEAR(mc.consultationDate) AS year,
            WEEK(mc.consultationDate, 1) AS week,
            COUNT(mc.consultationID) AS totalConsultations,
            COUNT(DISTINCT mc.patientID) AS uniquePatients,
            COUNT(DISTINCT mc.hWorkerID) AS healthWorkersInvolved,
            SUM(CASE WHEN s.statusName = 'Completed' THEN 1 ELSE 0 END) AS completedConsultations,
            SUM(CASE WHEN s.statusName = 'Pending' THEN 1 ELSE 0 END) AS pendingConsultations
        FROM medical_consultation mc
        JOIN facility f ON mc.facilityID = f.facilityID
        JOIN REF_Status s ON mc.consultationStatusID = s.statusID
        WHERE (p_facilityID IS NULL OR mc.facilityID = p_facilityID)
        AND mc.consultationDate BETWEEN p_startDate AND p_endDate
        GROUP BY f.facilityName, YEAR(mc.consultationDate), WEEK(mc.consultationDate, 1)
        ORDER BY year, week;
    ELSEIF p_groupBy = 'month' THEN
        SELECT 
            f.facilityName,
            YEAR(mc.consultationDate) AS year,
            MONTH(mc.consultationDate) AS month,
            COUNT(mc.consultationID) AS totalConsultations,
            COUNT(DISTINCT mc.patientID) AS uniquePatients,
            COUNT(DISTINCT mc.hWorkerID) AS healthWorkersInvolved,
            SUM(CASE WHEN s.statusName = 'Completed' THEN 1 ELSE 0 END) AS completedConsultations,
            SUM(CASE WHEN s.statusName = 'Pending' THEN 1 ELSE 0 END) AS pendingConsultations
        FROM medical_consultation mc
        JOIN facility f ON mc.facilityID = f.facilityID
        JOIN REF_Status s ON mc.consultationStatusID = s.statusID
        WHERE (p_facilityID IS NULL OR mc.facilityID = p_facilityID)
        AND mc.consultationDate BETWEEN p_startDate AND p_endDate
        GROUP BY f.facilityName, YEAR(mc.consultationDate), MONTH(mc.consultationDate)
        ORDER BY year, month;
    ELSE -- year
        SELECT 
            f.facilityName,
            YEAR(mc.consultationDate) AS year,
            COUNT(mc.consultationID) AS totalConsultations,
            COUNT(DISTINCT mc.patientID) AS uniquePatients,
            COUNT(DISTINCT mc.hWorkerID) AS healthWorkersInvolved,
            SUM(CASE WHEN s.statusName = 'Completed' THEN 1 ELSE 0 END) AS completedConsultations,
            SUM(CASE WHEN s.statusName = 'Pending' THEN 1 ELSE 0 END) AS pendingConsultations
        FROM medical_consultation mc
        JOIN facility f ON mc.facilityID = f.facilityID
        JOIN REF_Status s ON mc.consultationStatusID = s.statusID
        WHERE (p_facilityID IS NULL OR mc.facilityID = p_facilityID)
        AND mc.consultationDate BETWEEN p_startDate AND p_endDate
        GROUP BY f.facilityName, YEAR(mc.consultationDate)
        ORDER BY year;
    END IF;
END$
DELIMITER ;

-- Immunization impact report (Report 2 - Assigned to ASHLEY)
DELIMITER $
DROP PROCEDURE IF EXISTS sp_immunization_impact_report$
CREATE PROCEDURE sp_immunization_impact_report(
    IN p_facilityID INT,
    IN p_startDate DATE,
    IN p_endDate DATE,
    IN p_groupBy VARCHAR(10) -- 'week', 'month', 'year'
)
BEGIN
    IF p_groupBy = 'week' THEN
        SELECT 
            f.facilityName,
            YEAR(ia.administrationDate) AS year,
            WEEK(ia.administrationDate, 1) AS week,
            COUNT(DISTINCT ia.patientID) AS patientsImmunized,
            COUNT(DISTINCT ia.vaccineType) AS vaccinesUsed,
            SUM(CASE WHEN s.statusName = 'Pending' THEN 1 ELSE 0 END) AS missedVaccinations,
            SUM(CASE WHEN s.statusName = 'Completed' THEN 1 ELSE 0 END) AS completedVaccinations,
            ROUND(SUM(CASE WHEN s.statusName = 'Completed' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS pctCompletedVaccinations
        FROM immunization_administration ia
        JOIN worker w ON ia.hWorkerID = w.hWorkerID
        JOIN facility f ON w.hWorkerFacilityID = f.facilityID
        JOIN REF_Status s ON ia.immunizationStatus = s.statusID
        WHERE (p_facilityID IS NULL OR f.facilityID = p_facilityID)
        AND ia.administrationDate BETWEEN p_startDate AND p_endDate
        GROUP BY f.facilityName, YEAR(ia.administrationDate), WEEK(ia.administrationDate, 1)
        ORDER BY year, week;
    ELSEIF p_groupBy = 'month' THEN
        SELECT 
            f.facilityName,
            YEAR(ia.administrationDate) AS year,
            MONTH(ia.administrationDate) AS month,
            COUNT(DISTINCT ia.patientID) AS patientsImmunized,
            COUNT(DISTINCT ia.vaccineType) AS vaccinesUsed,
            SUM(CASE WHEN s.statusName = 'Pending' THEN 1 ELSE 0 END) AS missedVaccinations,
            SUM(CASE WHEN s.statusName = 'Completed' THEN 1 ELSE 0 END) AS completedVaccinations,
            ROUND(SUM(CASE WHEN s.statusName = 'Completed' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS pctCompletedVaccinations
        FROM immunization_administration ia
        JOIN worker w ON ia.hWorkerID = w.hWorkerID
        JOIN facility f ON w.hWorkerFacilityID = f.facilityID
        JOIN REF_Status s ON ia.immunizationStatus = s.statusID
        WHERE (p_facilityID IS NULL OR f.facilityID = p_facilityID)
        AND ia.administrationDate BETWEEN p_startDate AND p_endDate
        GROUP BY f.facilityName, YEAR(ia.administrationDate), MONTH(ia.administrationDate)
        ORDER BY year, month;
    ELSE -- year
        SELECT 
            f.facilityName,
            YEAR(ia.administrationDate) AS year,
            COUNT(DISTINCT ia.patientID) AS patientsImmunized,
            COUNT(DISTINCT ia.vaccineType) AS vaccinesUsed,
            SUM(CASE WHEN s.statusName = 'Pending' THEN 1 ELSE 0 END) AS missedVaccinations,
            SUM(CASE WHEN s.statusName = 'Completed' THEN 1 ELSE 0 END) AS completedVaccinations,
            ROUND(SUM(CASE WHEN s.statusName = 'Completed' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS pctCompletedVaccinations
        FROM immunization_administration ia
        JOIN worker w ON ia.hWorkerID = w.hWorkerID
        JOIN facility f ON w.hWorkerFacilityID = f.facilityID
        JOIN REF_Status s ON ia.immunizationStatus = s.statusID
        WHERE (p_facilityID IS NULL OR f.facilityID = p_facilityID)
        AND ia.administrationDate BETWEEN p_startDate AND p_endDate
        GROUP BY f.facilityName, YEAR(ia.administrationDate)
        ORDER BY year;
    END IF;
END$
DELIMITER ;

-- Disease monitoring report (Report 3 - Assigned to SPENCER)
DELIMITER $
DROP PROCEDURE IF EXISTS sp_disease_monitoring_report$
CREATE PROCEDURE sp_disease_monitoring_report(
    IN p_facilityID INT,
    IN p_startDate DATE,
    IN p_endDate DATE,
    IN p_groupBy VARCHAR(10) -- 'week', 'month', 'year'
)
BEGIN
    IF p_groupBy = 'week' THEN
        SELECT 
            mc.diagnosis,
            COUNT(*) AS caseCount,
            COUNT(DISTINCT mc.patientID) AS affectedPatients,
            f.facilityName,
            YEAR(mc.consultationDate) AS year,
            WEEK(mc.consultationDate, 1) AS week
        FROM medical_consultation mc
        JOIN facility f ON mc.facilityID = f.facilityID
        WHERE (p_facilityID IS NULL OR mc.facilityID = p_facilityID)
        AND mc.consultationDate BETWEEN p_startDate AND p_endDate
        GROUP BY mc.diagnosis, f.facilityName, YEAR(mc.consultationDate), WEEK(mc.consultationDate, 1)
        ORDER BY year DESC, week DESC, caseCount DESC;
    ELSEIF p_groupBy = 'month' THEN
        SELECT 
            mc.diagnosis,
            COUNT(*) AS caseCount,
            COUNT(DISTINCT mc.patientID) AS affectedPatients,
            f.facilityName,
            YEAR(mc.consultationDate) AS year,
            MONTH(mc.consultationDate) AS month
        FROM medical_consultation mc
        JOIN facility f ON mc.facilityID = f.facilityID
        WHERE (p_facilityID IS NULL OR mc.facilityID = p_facilityID)
        AND mc.consultationDate BETWEEN p_startDate AND p_endDate
        GROUP BY mc.diagnosis, f.facilityName, YEAR(mc.consultationDate), MONTH(mc.consultationDate)
        ORDER BY year DESC, month DESC, caseCount DESC;
    ELSE -- year
        SELECT 
            mc.diagnosis,
            COUNT(*) AS caseCount,
            COUNT(DISTINCT mc.patientID) AS affectedPatients,
            f.facilityName,
            YEAR(mc.consultationDate) AS year
        FROM medical_consultation mc
        JOIN facility f ON mc.facilityID = f.facilityID
        WHERE (p_facilityID IS NULL OR mc.facilityID = p_facilityID)
        AND mc.consultationDate BETWEEN p_startDate AND p_endDate
        GROUP BY mc.diagnosis, f.facilityName, YEAR(mc.consultationDate)
        ORDER BY year DESC, caseCount DESC;
    END IF;
END$
DELIMITER ;

-- Medicine inventory and utilization report (Report 4 - Assigned to RAPHY)
DELIMITER $
DROP PROCEDURE IF EXISTS sp_medicine_utilization_report$
CREATE PROCEDURE sp_medicine_utilization_report(
    IN p_facilityID INT,
    IN p_startDate DATE,
    IN p_endDate DATE,
    IN p_groupBy VARCHAR(10) -- 'week', 'month', 'year'
)
BEGIN
    IF p_groupBy = 'week' THEN
        SELECT 
            m.medicineName,
            m.dosageForm,
            m.strength,
            SUM(pr.qtyDistributed) AS totalDistributed,
            COUNT(DISTINCT pr.patientID) AS patientsServed,
            AVG(pr.qtyDistributed) AS averageQuantity,
            f.facilityName,
            YEAR(pr.distributionDate) AS year,
            WEEK(pr.distributionDate, 1) AS week
        FROM prescription_receipt pr
        JOIN medicine m ON pr.medicineID = m.medicineID
        JOIN medical_consultation mc ON pr.consultationID = mc.consultationID
        JOIN facility f ON mc.facilityID = f.facilityID
        WHERE (p_facilityID IS NULL OR mc.facilityID = p_facilityID)
        AND pr.distributionDate BETWEEN p_startDate AND p_endDate
        GROUP BY m.medicineName, m.dosageForm, m.strength, f.facilityName, YEAR(pr.distributionDate), WEEK(pr.distributionDate, 1)
        ORDER BY year DESC, week DESC, totalDistributed DESC;
    ELSEIF p_groupBy = 'month' THEN
        SELECT 
            m.medicineName,
            m.dosageForm,
            m.strength,
            SUM(pr.qtyDistributed) AS totalDistributed,
            COUNT(DISTINCT pr.patientID) AS patientsServed,
            AVG(pr.qtyDistributed) AS averageQuantity,
            f.facilityName,
            YEAR(pr.distributionDate) AS year,
            MONTH(pr.distributionDate) AS month
        FROM prescription_receipt pr
        JOIN medicine m ON pr.medicineID = m.medicineID
        JOIN medical_consultation mc ON pr.consultationID = mc.consultationID
        JOIN facility f ON mc.facilityID = f.facilityID
        WHERE (p_facilityID IS NULL OR mc.facilityID = p_facilityID)
        AND pr.distributionDate BETWEEN p_startDate AND p_endDate
        GROUP BY m.medicineName, m.dosageForm, m.strength, f.facilityName, YEAR(pr.distributionDate), MONTH(pr.distributionDate)
        ORDER BY year DESC, month DESC, totalDistributed DESC;
    ELSE -- year
        SELECT 
            m.medicineName,
            m.dosageForm,
            m.strength,
            SUM(pr.qtyDistributed) AS totalDistributed,
            COUNT(DISTINCT pr.patientID) AS patientsServed,
            AVG(pr.qtyDistributed) AS averageQuantity,
            f.facilityName,
            YEAR(pr.distributionDate) AS year
        FROM prescription_receipt pr
        JOIN medicine m ON pr.medicineID = m.medicineID
        JOIN medical_consultation mc ON pr.consultationID = mc.consultationID
        JOIN facility f ON mc.facilityID = f.facilityID
        WHERE (p_facilityID IS NULL OR mc.facilityID = p_facilityID)
        AND pr.distributionDate BETWEEN p_startDate AND p_endDate
        GROUP BY m.medicineName, m.dosageForm, m.strength, f.facilityName, YEAR(pr.distributionDate)
        ORDER BY year DESC, totalDistributed DESC;
    END IF;
END$
DELIMITER ;