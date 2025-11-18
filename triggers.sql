-- ============================================================================
-- BARANGAY HEALTH MONITORING SYSTEM - TRIGGERS AND STORED PROCEDURES
-- ============================================================================

USE BHMS_DB;

-- ============================================================================
-- SECTION 1: TRIGGERS FOR DATA VALIDATION AND AUTOMATION
-- ============================================================================

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