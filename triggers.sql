-- ============================================================================
-- BARANGAY HEALTH MONITORING SYSTEM - TRIGGERS AND STORED PROCEDURES
-- ============================================================================

USE BHMS_DB;

-- ==========================================================
-- HELPER FUNCTION: SAFE STATUS LOOKUP
-- ==========================================================
DROP FUNCTION IF EXISTS fn_getStatusID$$
DELIMITER $$
CREATE FUNCTION fn_getStatusID(p_statusName VARCHAR(50), p_categoryName VARCHAR(50))
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE v_id INT;

    SELECT s.statusID INTO v_id
    FROM REF_Status s
    JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
    WHERE s.statusName = p_statusName AND c.categoryName = p_categoryName
    LIMIT 1;

    RETURN v_id;
END$$
DELIMITER ;

-- ==========================================================
-- PATIENT TRIGGERS (VALIDATION)
-- ==========================================================
DROP TRIGGER IF EXISTS trg_patient_birthdate_insert;
DROP TRIGGER IF EXISTS trg_patient_birthdate_update;
DROP TRIGGER IF EXISTS trg_patient_contact_insert;
DROP TRIGGER IF EXISTS trg_patient_contact_update;

-- Validate Patient birthdate (must not be in the future)
DELIMITER $$
CREATE TRIGGER trg_patient_birthdate_insert
BEFORE INSERT ON patient
FOR EACH ROW
BEGIN
    IF NEW.birthDate > CURDATE() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Birth date cannot be in the future';
    END IF;
END$$
DELIMITER ;

-- Validate Patient birthdate (must not be in the future)
DELIMITER $$
CREATE TRIGGER trg_patient_birthdate_update
BEFORE UPDATE ON patient
FOR EACH ROW
BEGIN
    IF NEW.birthDate > CURDATE() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Birth date cannot be in the future';
    END IF;
END$$
DELIMITER ;

-- Validate Patient Contact
DELIMITER $$
CREATE TRIGGER trg_patient_contact_insert
BEFORE INSERT ON patient
FOR EACH ROW
BEGIN
    IF NEW.emergencyContact = NEW.primaryPhone THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Emergency contact cannot be primary phone';
    END IF;
END$$
DELIMITER ;

-- Validate Patient Contact
DELIMITER $$
CREATE TRIGGER trg_patient_contact_update
BEFORE UPDATE ON patient
FOR EACH ROW
BEGIN
    IF NEW.emergencyContact = NEW.primaryPhone THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Emergency contact cannot be primary phone';
    END IF;
END$$
DELIMITER 

-- ==========================================================
-- MEDICINE INVENTORY TRIGGERS
-- ==========================================================
DROP TRIGGER IF EXISTS trg_inventory_quantity_insert;
DROP TRIGGER IF EXISTS trg_inventory_quantity_update;
DROP TRIGGER IF EXISTS trg_block_discontinued_inventory;
DROP TRIGGER IF EXISTS trg_medicine_inventory_update_status;

-- Prevent negative stock quantities
DELIMITER $$
CREATE TRIGGER trg_inventory_quantity_insert
BEFORE INSERT ON medicine_inventory
FOR EACH ROW
BEGIN
    IF NEW.quantityInStock < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Quantity cannot be negative';
    END IF;
END$$
DELIMITER ;

-- Prevent negative stock quantities
DELIMITER $$
CREATE TRIGGER trg_inventory_quantity_update
BEFORE UPDATE ON medicine_inventory
FOR EACH ROW
BEGIN
    IF NEW.quantityInStock < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Quantity cannot be negative';
    END IF;

    IF NEW.quantityInStock = 0 THEN
        SET NEW.inventoryStatusID = fn_getStatusID('Out of Stock','MedicineInventoryStatus');
    ELSEIF NEW.quantityInStock <= 10 THEN
        SET NEW.inventoryStatusID = fn_getStatusID('Low Stock','MedicineInventoryStatus');
    ELSE
        SET NEW.inventoryStatusID = fn_getStatusID('Available','MedicineInventoryStatus');
    END IF;
END$$
DELIMITER ;

-- Prevent updates on discontinued medicine
DELIMITER $$
CREATE TRIGGER trg_block_discontinued_inventory
BEFORE UPDATE ON medicine_inventory
FOR EACH ROW
BEGIN
    DECLARE v_status VARCHAR(50);
    SELECT s.statusName INTO v_status
    FROM medicine m
    JOIN REF_Status s ON m.medicineStatusID = s.statusID
    JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
    WHERE m.medicineID = NEW.medicineID AND c.categoryName='MedicineStatus';

    IF v_status = 'Discontinued' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot update inventory of discontinued medicine';
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
    SELECT s.statusID INTO v_outOfStockStatusID
	FROM REF_Status s JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
	WHERE s.statusName = 'Out of Stock' AND c.categoryName = 'InventoryStatus';


	SELECT s.statusID INTO v_lowStockStatusID
	FROM REF_Status s JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
	WHERE s.statusName = 'Low Stock' AND c.categoryName = 'InventoryStatus';


	SELECT s.statusID INTO v_availableStatusID
	FROM REF_Status s JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
	WHERE s.statusName = 'Available' AND c.categoryName = 'InventoryStatus';
    
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

-- ==========================================================
-- CONSULTATION TRIGGERS
-- ==========================================================
DROP TRIGGER IF EXISTS trg_block_inactive_patient_consult;
DROP TRIGGER IF EXISTS trg_block_inactive_worker_consult;
DROP TRIGGER IF EXISTS trg_consultation_validate_date_insert$$;
DROP TRIGGER IF EXISTS trg_consult_hours;
DROP TRIGGER IF EXISTS trg_consult_validate_date_birth;
DROP TRIGGER IF EXISTS trg_consultation_validate_worker_insert;


-- Validate Patient Status (not inactive)
DELIMITER $$
CREATE TRIGGER trg_block_inactive_patient_consult
BEFORE INSERT ON medical_consultation
FOR EACH ROW
BEGIN
    IF (
        SELECT s.statusName
        FROM patient p
        JOIN REF_Status s ON p.patientStatusID = s.statusID
        JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
        WHERE p.patientID = NEW.patientID AND c.categoryName = 'PatientStatus'
    ) = 'Inactive' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Inactive patients cannot have consultations';
    END IF;
END$$
DELIMITER ;

-- Validate Healthworker Status (not inactive)
DELIMITER $$
CREATE TRIGGER trg_block_inactive_worker_consult
BEFORE INSERT ON medical_consultation
FOR EACH ROW
BEGIN
    IF (
        SELECT s.statusName
        FROM worker w
        JOIN REF_Status s ON w.hWorkerStatusID = s.statusID
        JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
        WHERE w.hWorkerID = NEW.hWorkerID AND c.categoryName='HealthWorkerStatus'
    ) = 'Inactive' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Inactive workers cannot conduct consultations';
    END IF;
    
END$$
DELIMITER ;

-- Validate consultation date is not set in the future
DELIMITER $$
CREATE TRIGGER trg_consultation_validate_date_insert
BEFORE INSERT ON medical_consultation
FOR EACH ROW
BEGIN
    IF NEW.consultationDate > CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Consultation date cannot be in the future';
    END IF;
END$$
DELIMITER ;


-- Validate consultation is within facility operating hours
DELIMITER $$
CREATE TRIGGER trg_consult_hours
BEFORE INSERT ON medical_consultation
FOR EACH ROW
BEGIN
    DECLARE t_start TIME;
    DECLARE t_end TIME;

    SELECT shiftStart, shiftEnd INTO t_start, t_end FROM facility WHERE facilityID = NEW.facilityID;

    IF NEW.consultationTime < t_start OR NEW.consultationTime > t_end THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Consultation time outside facility operating hours';
    END IF;
END$$
DELIMITER ;

-- Validate consultation date is not set before the patient birthdate
DELIMITER $$
CREATE TRIGGER trg_consult_validate_date_birth
BEFORE INSERT ON medical_consultation
FOR EACH ROW
BEGIN
    DECLARE v_birth DATE;
    SELECT birthDate INTO v_birth FROM patient WHERE patientID = NEW.patientID;
    IF NEW.consultationDate < v_birth THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Consultation date cannot be before patient birth date';
    END IF;
END$$
DELIMITER ;

-- Validate health worker is active and assigned to facility
DELIMITER $$
CREATE TRIGGER trg_consultation_validate_worker_insert
BEFORE INSERT ON medical_consultation
FOR EACH ROW
BEGIN
DECLARE worker_status VARCHAR(50);
DECLARE worker_facility INT;


-- Validate health worker is Active and assigned to same facility
SELECT s.statusName, w.hWorkerFacilityID
INTO worker_status, worker_facility
FROM worker w
JOIN REF_Status s ON w.hWorkerStatusID = s.statusID
JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
WHERE w.hWorkerID = NEW.hWorkerID
AND c.categoryName = 'HealthWorkerStatus';


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

-- ==========================================================
-- PRESCRIPTION TRIGGERS
-- ==========================================================
DROP TRIGGER IF EXISTS trg_prescription_validate_insert;
DROP TRIGGER IF EXISTS trg_prescription_update_inventory;
DROP TRIGGER IF EXISTS trg_block_archived_consult_prescription;

-- Validate prescription before distribution
DELIMITER $$
CREATE TRIGGER trg_prescription_validate_insert
BEFORE INSERT ON prescription_receipt
FOR EACH ROW
BEGIN
    DECLARE worker_status VARCHAR(50);
    DECLARE medicine_qty INT;
    DECLARE consultation_exists INT;
    DECLARE consultation_facility INT;

    SELECT s.statusName INTO worker_status
    FROM worker w
    JOIN REF_Status s ON w.hWorkerStatusID = s.statusID
    JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
    WHERE w.hWorkerID = NEW.hWorkerID AND c.categoryName = 'HealthWorkerStatus';

    IF worker_status != 'Active' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Only active health workers can distribute medicine';
    END IF;

    SELECT mc.facilityID INTO consultation_facility
    FROM medical_consultation mc
    WHERE mc.consultationID = NEW.consultationID;

    SELECT quantityInStock INTO medicine_qty
    FROM medicine_inventory
    WHERE medicineID = NEW.medicineID
    AND facilityID = consultation_facility;

    IF medicine_qty IS NULL OR medicine_qty < NEW.qtyDistributed THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient medicine stock for distribution';
    END IF;

    SELECT COUNT(*) INTO consultation_exists
    FROM medical_consultation
    WHERE consultationID = NEW.consultationID;

    IF consultation_exists = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid consultation reference';
    END IF;

    SET NEW.isValidPrescription = TRUE;

    IF NEW.distributionDate IS NULL THEN
        SET NEW.distributionDate = CURDATE();
    END IF;
END$$
DELIMITER ;

-- Auto-update inventory after prescription distribution
DELIMITER $$
CREATE TRIGGER trg_prescription_update_inventory
AFTER INSERT ON prescription_receipt
FOR EACH ROW
BEGIN
    DECLARE consultation_facility INT;

    SELECT facilityID INTO consultation_facility
    FROM medical_consultation
    WHERE consultationID = NEW.consultationID;

    UPDATE medicine_inventory
    SET quantityInStock = quantityInStock - NEW.qtyDistributed
    WHERE medicineID = NEW.medicineID
    AND facilityID = consultation_facility;

    UPDATE prescription_receipt
    SET inventoryUpdated = TRUE
    WHERE receiptID = NEW.receiptID;
END$$
DELIMITER ;

-- Prevent updates on archived consultations
DELIMITER $$
CREATE TRIGGER trg_block_archived_consult_prescription
BEFORE INSERT ON prescription_receipt
FOR EACH ROW
BEGIN
    IF (
        SELECT s.statusName FROM medical_consultation mc
        JOIN REF_Status s ON mc.consultationStatusID = s.statusID
        JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
        WHERE mc.consultationID = NEW.consultationID AND c.categoryName = 'ConsultationStatus'
    ) = 'Archived' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot add prescriptions to archived consultations.';
    END IF;
END$$
DELIMITER ;

-- ==========================================================
-- IMMUNIZATION TRIGGERS
-- ==========================================================
DROP TRIGGER IF EXISTS trg_immunization_validate_insert;
DROP TRIGGER IF EXISTS trg_immunization_update_inventory;

-- Validate immunization administration
DELIMITER $$
CREATE TRIGGER trg_immunization_validate_insert
BEFORE INSERT ON immunization_administration
FOR EACH ROW
BEGIN
    DECLARE patient_birthdate DATE;
    DECLARE worker_status VARCHAR(50);
    DECLARE worker_facility INT;
    DECLARE vaccine_qty INT;
    DECLARE last_dose_date DATE;

    SELECT birthDate INTO patient_birthdate
    FROM patient
    WHERE patientID = NEW.patientID;

    IF NEW.administrationDate < patient_birthdate THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Administration date cannot be before patient birth date';
    END IF;

    SELECT s.statusName, w.hWorkerFacilityID INTO worker_status, worker_facility
    FROM worker w
    JOIN REF_Status s ON w.hWorkerStatusID = s.statusID
    JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
    WHERE w.hWorkerID = NEW.hWorkerID AND c.categoryName = 'HealthWorkerStatus';

    IF worker_status != 'Active' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Only active health workers can administer vaccines';
    END IF;

    SELECT quantityInStock INTO vaccine_qty
    FROM medicine_inventory
    WHERE medicineID = NEW.medicineID
    AND facilityID = worker_facility;

    IF vaccine_qty IS NULL OR vaccine_qty < 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient vaccine stock at this facility';
    END IF;

    IF NEW.dosageNumber > 1 THEN
        SELECT MAX(administrationDate) INTO last_dose_date
        FROM immunization_administration
        WHERE patientID = NEW.patientID
        AND vaccineType = NEW.vaccineType
        AND dosageNumber = NEW.dosageNumber - 1;

        IF last_dose_date IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Previous dose not found for this patient';
        END IF;

        IF NEW.administrationDate <= last_dose_date THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Administration date must be after previous dose';
        END IF;
    END IF;

    IF NEW.nextVaccinationDate IS NOT NULL AND NEW.nextVaccinationDate <= NEW.administrationDate THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Next vaccination date must be after administration date';
    END IF;
END$$
DELIMITER ;


-- Auto-update vaccine inventory after administration
DELIMITER $$
CREATE TRIGGER trg_immunization_update_inventory
AFTER INSERT ON immunization_administration
FOR EACH ROW
BEGIN
    DECLARE worker_facility INT;

    SELECT hWorkerFacilityID INTO worker_facility
    FROM worker
    WHERE hWorkerID = NEW.hWorkerID;

    UPDATE medicine_inventory
    SET quantityInStock = quantityInStock - 1
    WHERE medicineID = NEW.medicineID
    AND facilityID = worker_facility;
END$$
DELIMITER ;

-- ==========================================================
-- WORKER TRIGGERS
-- ==========================================================
DROP TRIGGER IF EXISTS trg_worker_validate_facility_insert;
DROP TRIGGER IF EXISTS trg_prevent_restore_worker_closed_facility;

-- Validate worker is assigned to an active facility
DELIMITER $$
CREATE TRIGGER trg_worker_validate_facility_insert
BEFORE INSERT ON worker
FOR EACH ROW
BEGIN
    DECLARE facility_status VARCHAR(50);

    SELECT s.statusName INTO facility_status
    FROM facility f
    JOIN REF_Status s ON f.facilityStatusID = s.statusID
    JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
    WHERE f.facilityID = NEW.hWorkerFacilityID AND c.categoryName = 'FacilityStatus';

    IF facility_status != 'Operational' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Worker can only be assigned to operational facilities';
    END IF;
END$$
DELIMITER ;

-- Prevent adding workers to inactive facility
DELIMITER $$
CREATE TRIGGER trg_prevent_restore_worker_closed_facility
BEFORE UPDATE ON worker
FOR EACH ROW
BEGIN
    DECLARE v_fac_status VARCHAR(50);
    IF (SELECT s.statusName FROM REF_Status s JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID WHERE s.statusID = NEW.hWorkerStatusID AND c.categoryName='HealthWorkerStatus') = 'Active' THEN
        SELECT s.statusName INTO v_fac_status
        FROM facility f
        JOIN REF_Status s ON f.facilityStatusID = s.statusID
        JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID
        WHERE f.facilityID = NEW.hWorkerFacilityID AND c.categoryName = 'FacilityStatus';

        IF v_fac_status != 'Operational' THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot activate worker: assigned facility is not operational';
        END IF;
    END IF;
END$$
DELIMITER ;

-- ==========================================================
-- RESTOCK INVOICE TRIGGERS
-- ==========================================================
DROP TRIGGER IF EXISTS trg_restock_validate_date_insert;

-- Validate delivery date
DELIMITER $$
CREATE TRIGGER trg_restock_validate_date_insert
BEFORE INSERT ON restock_invoice
FOR EACH ROW
BEGIN
    IF NEW.deliveryDate < CURDATE() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Delivery date cannot be in the past';
    END IF;
END$$
DELIMITER ;

-- ==========================================================
-- SOFT DELETE PROCEDURES
-- ==========================================================
DROP PROCEDURE IF EXISTS sp_delete_patient;
DROP PROCEDURE IF EXISTS sp_delete_worker;
DROP PROCEDURE IF EXISTS sp_delete_facility;
DROP PROCEDURE IF EXISTS sp_delete_medicine;
DROP PROCEDURE IF EXISTS sp_delete_supplier;
DROP PROCEDURE IF EXISTS sp_delete_restock;
DROP PROCEDURE IF EXISTS sp_delete_consult;
DROP PROCEDURE IF EXISTS sp_delete_prescription;
DROP PROCEDURE IF EXISTS sp_delete_immunization;

-- Patient Soft Delete
DELIMITER $$
CREATE PROCEDURE sp_delete_patient(IN id INT)
BEGIN
    UPDATE patient SET patientStatusID = fn_getStatusID('Inactive','PatientStatus')
    WHERE patientID = id;
END$$
DELIMITER ;

-- Worker Soft Delete
DELIMITER $$
CREATE PROCEDURE sp_delete_worker(IN id INT)
BEGIN
    UPDATE worker SET hWorkerStatusID = fn_getStatusID('Inactive','HealthWorkerStatus')
    WHERE hWorkerID = id;
END$$
DELIMITER ;

-- Facility Soft Delete
DELIMITER $$
CREATE PROCEDURE sp_delete_facility(IN id INT)
BEGIN
    UPDATE facility SET facilityStatusID = fn_getStatusID('Closed','FacilityStatus')
    WHERE facilityID = id;
END$$
DELIMITER ;

-- Medicine Soft Delete
DELIMITER $$
CREATE PROCEDURE sp_delete_medicine(IN id INT)
BEGIN
    UPDATE medicine SET medicineStatusID = fn_getStatusID('Discontinued','MedicineStatus')
    WHERE medicineID = id;
END$$
DELIMITER ;

-- Supplier Soft Delete
DELIMITER $$
CREATE PROCEDURE sp_delete_supplier(IN id INT)
BEGIN
    UPDATE supplier SET supplierStatusID = fn_getStatusID('Closed','SupplierStatus')
    WHERE supplierID = id;
END$$
DELIMITER ;

-- Restock Invoice Soft Delete
DELIMITER $$
CREATE PROCEDURE sp_delete_restock(IN id INT)
BEGIN
    UPDATE restock_invoice SET deliveryStatus = fn_getStatusID('Cancelled','DeliveryStatus')
    WHERE invoiceID = id;
END$$
DELIMITER ;

-- Consultation Soft Delete
DELIMITER $$
CREATE PROCEDURE sp_delete_consult(IN id INT)
BEGIN
    UPDATE medical_consultation
    SET consultationStatusID = fn_getStatusID('Archived','ConsultationStatus')
    WHERE consultationID = id;
END$$
DELIMITER ;

-- Prescription Soft Delete
DELIMITER $$
CREATE PROCEDURE sp_delete_prescription(IN id INT)
BEGIN
    UPDATE prescription_receipt
    SET prescriptionStatusID = fn_getStatusID('Cancelled','PrescriptionStatus')
    WHERE receiptID = id;
END$$
DELIMITER ;

-- Immunization Soft Delete 
DELIMITER $$
CREATE PROCEDURE sp_delete_immunization(IN id INT)
BEGIN
    UPDATE immunization_administration
    SET immunizationStatusID = fn_getStatusID('Archived','ImmunizationStatus')
    WHERE immunizationID = id;
END$$
DELIMITER ;

-- ==========================================================
-- RESTORE PROCEDURES (ALL RESTORE TO COMPLETED)
-- ==========================================================
DROP PROCEDURE IF EXISTS sp_restore_consult;
DROP PROCEDURE IF EXISTS sp_restore_prescription;
DROP PROCEDURE IF EXISTS sp_restore_immunization;
DROP PROCEDURE IF EXISTS sp_restore_patient;
DROP PROCEDURE IF EXISTS sp_restore_worker;
DROP PROCEDURE IF EXISTS sp_restore_facility;
DROP PROCEDURE IF EXISTS sp_restore_medicine;
DROP PROCEDURE IF EXISTS sp_restore_supplier;
DROP PROCEDURE IF EXISTS sp_restore_restock;

-- Restore Consultation 
DELIMITER $$
CREATE PROCEDURE sp_restore_consult(IN id INT)
BEGIN
    UPDATE medical_consultation
    SET consultationStatusID = fn_getStatusID('Completed','ConsultationStatus')
    WHERE consultationID = id;
END$$
DELIMITER ;

-- Restore Prescription
DELIMITER $$
CREATE PROCEDURE sp_restore_prescription(IN id INT)
BEGIN
    UPDATE prescription_receipt
    SET prescriptionStatusID = fn_getStatusID('Completed','PrescriptionStatus')
    WHERE receiptID = id;
END$$
DELIMITER ;

-- Restore Immunization
DELIMITER $$
CREATE PROCEDURE sp_restore_immunization(IN id INT)
BEGIN
    UPDATE immunization_administration
    SET immunizationStatusID = fn_getStatusID('Completed','ImmunizationStatus')
    WHERE immunizationID = id;
END$$
DELIMITER ;

-- Restore Restock Invoice
DELIMITER $$
CREATE PROCEDURE sp_restore_restock(IN id INT)
BEGIN
    UPDATE restock_invoice SET deliveryStatus = fn_getStatusID('Pending','DeliveryStatus') WHERE invoiceID = id;
END$$
DELIMITER ;

-- Restore Patient
DELIMITER $$
CREATE PROCEDURE sp_restore_patient(IN id INT)
BEGIN
    UPDATE patient SET patientStatusID = fn_getStatusID('Active','PatientStatus') WHERE patientID = id;
END$$
DELIMITER ;

-- Restore Worker
DELIMITER $$
CREATE PROCEDURE sp_restore_worker(IN id INT)
BEGIN
    UPDATE worker SET hWorkerStatusID = fn_getStatusID('Active','HealthWorkerStatus') WHERE hWorkerID = id;
END$$
DELIMITER ;

-- Restore Facility
DELIMITER $$
CREATE PROCEDURE sp_restore_facility(IN id INT)
BEGIN
    UPDATE facility SET facilityStatusID = fn_getStatusID('Operational','FacilityStatus') WHERE facilityID = id;
END$$
DELIMITER ;

-- Restore Medicine
DELIMITER $$
CREATE PROCEDURE sp_restore_medicine(IN id INT)
BEGIN
    UPDATE medicine SET medicineStatusID = fn_getStatusID('Available','MedicineStatus') WHERE medicineID = id;
END$$
DELIMITER ;

-- Restore Supplier
DELIMITER $$
CREATE PROCEDURE sp_restore_supplier(IN id INT)
BEGIN
    UPDATE supplier SET supplierStatusID = fn_getStatusID('Active','SupplierStatus') WHERE supplierID = id;
END$$
DELIMITER ;
