-- ============================================================
-- BARANGAY HEALTH MONITORING SYSTEM DATABASE
-- Database Schema Script
-- =====================================================
DROP SCHEMA IF EXISTS BHMS_DB;

CREATE DATABASE IF NOT EXISTS BHMS_DB;
USE BHMS_DB;

-- ============================================================
-- STATUS TABLES (lookup/reference)
-- ===========================================================
DROP TABLE IF EXISTS REF_StatusCategory;
DROP TABLE IF EXISTS REF_Status;

CREATE TABLE IF NOT EXISTS REF_StatusCategory (
    statusCategoryID INT AUTO_INCREMENT,
    categoryName VARCHAR(50) NOT NULL,
    
    CONSTRAINT status_category_pk PRIMARY KEY (statusCategoryID),
    CONSTRAINT category_uq UNIQUE (categoryName)
);

CREATE TABLE IF NOT EXISTS REF_Status (
    statusID INT AUTO_INCREMENT,
    statusCategoryID INT NOT NULL,
    statusName VARCHAR(50) NOT NULL,
    
	CONSTRAINT status_reference_pk PRIMARY KEY (statusID),
    CONSTRAINT status_reference_category_fk FOREIGN KEY (statusCategoryID) REFERENCES REF_StatusCategory(statusCategoryID),
    CONSTRAINT status_reference_uq UNIQUE (statusName, statusCategoryID)
);


-- ============================================================
-- TABLES: CORE RECORDS
-- ============================================================

DROP TABLE IF EXISTS medicine;
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS patient;
DROP TABLE IF EXISTS worker;
DROP TABLE IF EXISTS facility;

-- BRGY FACILITY RECORDS (Assigned to KHYLE) --
CREATE TABLE IF NOT EXISTS facility (
	facilityID 			INT 			AUTO_INCREMENT,
    facilityName		VARCHAR(60)		NOT NULL,
    facilityAddress 	VARCHAR(100)	NOT NULL, 
    facilityContactNum	CHAR(11)		NOT NULL,
    shiftStart			TIME			NOT NULL,
    shiftEnd			TIME			NOT NULL,
    facilityStatusID	INT 			NOT NULL,
    
CONSTRAINT facility_pk PRIMARY KEY (facilityID),
CONSTRAINT facilitystatus_fk FOREIGN KEY (facilityStatusID) REFERENCES REF_Status(statusID),
CONSTRAINT facility_uqname UNIQUE (facilityName), 
CONSTRAINT facility_contactCheck CHECK (facilityContactNum REGEXP '^[0-9]{11}$'),
CONSTRAINT facility_shiftCheck CHECK (shiftEnd > shiftStart));

-- BRGY HEALTH WORKER RECORDS (Assigned to ASHLEY) --
CREATE TABLE IF NOT EXISTS worker (
	hWorkerID			INT				AUTO_INCREMENT,
	hWorkerLastName		VARCHAR(45)		NOT NULL,
	hWorkerFirstName	VARCHAR(45)		NOT NULL,
	hWorkerPosition		VARCHAR(45)		NOT NULL,
	hContactInformation	CHAR(11)		NOT NULL,
    hWorkerFacilityID 	INT				NOT NULL,
	hWorkerStatusID 	INT				NOT NULL,
    
    CONSTRAINT worker_pk PRIMARY KEY (hWorkerID),
    CONSTRAINT workerstatus_fk FOREIGN KEY (hWorkerStatusID) REFERENCES REF_Status(statusID),
    CONSTRAINT workerfacility_fk FOREIGN KEY (hWorkerFacilityID) REFERENCES facility(facilityID),
    CONSTRAINT worker_uqcontact UNIQUE (hContactInformation),
    CONSTRAINT worker_lastnameCheck CHECK (hWorkerLastName REGEXP '^[A-Za-z]+$'),  
    CONSTRAINT worker_firstnameCheck CHECK (hWorkerFirstName REGEXP '^[A-Za-z]+$'),
    CONSTRAINT worker_positionCheck CHECK (hWorkerPosition REGEXP '^[A-Za-z ]+$'),  
    CONSTRAINT worker_contactCheck CHECK (hContactInformation REGEXP '^[0-9]{11}$')
);

-- PATIENT RECORDS (Assigned to RAPHY) --
CREATE TABLE IF NOT EXISTS patient (
	patientID			INT				AUTO_INCREMENT,
    lastName			VARCHAR(45)		NOT NULL,
    firstName			VARCHAR(45)		NOT NULL,
    birthDate			DATE 			NOT NULL,
    gender				ENUM('Male', 'Female')		NOT NULL,
    bloodType			ENUM('O+', 'O-', 
							'A+', 'A-', 
							'B+', 'B-', 
							'AB+', 'AB-')			NOT NULL,
	address				VARCHAR(150)	NOT NULL,
    primaryPhone		CHAR(11)		NOT NULL,
    emergencyContact	VARCHAR(80)		NOT NULL,
    patientStatus		INT				NOT NULL,
	CONSTRAINT patient_pk PRIMARY KEY (patientID),
    CONSTRAINT patient_phoneCheck CHECK (primaryPhone REGEXP '^[0-9]{11}$'),
	CONSTRAINT patientstatus_fk FOREIGN KEY (patientStatus) REFERENCES REF_Status(statusID)
);

-- SUPPLIER RECORDS (Assigned to KHYLE) --
CREATE TABLE IF NOT EXISTS supplier (
	supplierID 				INT 			AUTO_INCREMENT,
    supplierName 			VARCHAR(50)		NOT NULL,
    supplierAddress 		VARCHAR(100)	NOT NULL,
    supplierContactNum		CHAR(11)		NOT NULL,
    supplierType			ENUM('Medical Equipment Supplier',
								 'Medicine Supplier',
								 'Vaccine Supplier')	
											NOT NULL,
	deliveryLeadTime		INT 			NOT NULL, 
    transactionDetails 		VARCHAR(350)	NOT NULL, 
    supplierStatusID     	INT				NOT NULL,
    
    CONSTRAINT supplier_pk PRIMARY KEY (supplierID),
    CONSTRAINT supplierstatus_fk FOREIGN KEY (supplierStatusID) REFERENCES REF_Status(statusID),
    CONSTRAINT supplier_name_uq UNIQUE (supplierName),
    CONSTRAINT supplier_contact_chk CHECK (supplierContactNum REGEXP '^[0-9]{11}$'), # CHECKS to ensure only 11 digits from 0-9 are included in the contact number
	CONSTRAINT supplier_leadtime_chk CHECK (deliveryLeadTime > 0));

-- MEDICINE RECORDS (Assigned to SPENCER) --
CREATE TABLE IF NOT EXISTS medicine (
    medicineID	 		INT 			AUTO_INCREMENT,
    medicineName		VARCHAR(50) 	NOT NULL,
    medicineDesc		TEXT,
	dosageForm 			VARCHAR(50),
    strength 			VARCHAR(50),
	batchNumber   		VARCHAR(50),
    medicineStatus 		INT 			NOT NULL,
    
    CONSTRAINT medicine_pk PRIMARY KEY (medicineID),
    CONSTRAINT medicinestatus_fk FOREIGN KEY (medicineStatus) REFERENCES REF_Status(statusID),
    CONSTRAINT medicine_uqname UNIQUE (medicineName),
    CONSTRAINT medicine_uqbatchnum UNIQUE (batchNumber)
);


-- ============================================================
-- LINKING TABLES
-- ============================================================
DROP TABLE IF EXISTS medicine_inventory;

-- MEDICINE INVENTORY TABLE  --
CREATE TABLE IF NOT EXISTS medicine_inventory (
	inventoryID      INT    AUTO_INCREMENT,
    facilityID       INT    NOT NULL,
    medicineID       INT    NOT NULL,
    quantityInStock  INT    NOT NULL,
    inventoryStatusID INT   NOT NULL,

	CONSTRAINT inventory_pk PRIMARY KEY (inventoryID),
    CONSTRAINT inventory_facility_fk FOREIGN KEY (facilityID) REFERENCES facility(facilityID),
    CONSTRAINT inventory_medicine_fk FOREIGN KEY (medicineID) REFERENCES medicine(medicineID),
    CONSTRAINT inventory_status_fk FOREIGN KEY (inventoryStatusID) REFERENCES REF_Status(statusID),
    CONSTRAINT inventory_qty_chk CHECK (quantityInStock >= 0),
    CONSTRAINT inventory_facility_medicine_uq UNIQUE (facilityID, medicineID)
    );


-- ============================================================
-- TRANSACTIONAL RECORDS
-- ============================================================

DROP TABLE IF EXISTS medical_consultation;
DROP TABLE IF EXISTS prescription_receipt;
DROP TABLE IF EXISTS immunization_administration;
DROP TABLE IF EXISTS restock_invoice;

-- MEDICAL CONSULTATIONS TRANSACTION RECORDS (Assigned to ASHLEY) --
CREATE TABLE IF NOT EXISTS medical_consultation (
    consultationID 		INT 	AUTO_INCREMENT,
    patientID 			INT 	NOT NULL,
    hWorkerID 			INT 	NOT NULL,
    facilityID 			INT 	NOT NULL,
    consultationDate 	DATE 	NOT NULL,
    consultationTime 	TIME 	NOT NULL,
    symptoms 			TEXT 	NOT NULL, 
    diagnosis 			TEXT 	NOT NULL,
    prescription 		TEXT,
    consultationStatusID INT 	NOT NULL,
    lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT consultation_pk PRIMARY KEY (consultationID),
    CONSTRAINT consultation_patient_fk FOREIGN KEY (patientID) REFERENCES patient(patientID),
    CONSTRAINT consultation_worker_fk FOREIGN KEY (hWorkerID) REFERENCES worker(hWorkerID),
    CONSTRAINT consultation_facility_fk FOREIGN KEY (facilityID) REFERENCES facility(facilityID),
    CONSTRAINT consultation_status_fk FOREIGN KEY (consultationStatusID) REFERENCES REF_Status(statusID)
);

-- PRESCRIPTION RECEIPT TRANSACTION RECORDS (Assigned to KHYLE) --
CREATE TABLE IF NOT EXISTS prescription_receipt (
	receiptID			 INT 	 AUTO_INCREMENT,
    patientID			 INT	 NOT NULL,
    consultationID 		 INT	 NOT NULL,
    medicineID			 INT 	 NOT NULL,
    hWorkerID			 INT	 NOT NULL,
    distributionDate	 DATE	 NOT NULL,
    qtyDistributed 		 INT	 NOT NULL,
    isValidPrescription  BOOLEAN DEFAULT FALSE,
    inventoryUpdated	 BOOLEAN DEFAULT FALSE,
    prescriptionStatusID INT	 NOT NULL,
    
    CONSTRAINT prescription_pk PRIMARY KEY (receiptID),
    CONSTRAINT prescription_patient_fk FOREIGN KEY (patientID) REFERENCES patient(patientID),
    CONSTRAINT prescription_medconsult_fk FOREIGN KEY (consultationID) REFERENCES medical_consultation(consultationID),
    CONSTRAINT prescription_medicine_fk FOREIGN KEY (medicineID) REFERENCES medicine(medicineID),
    CONSTRAINT prescription_healthworker_fk FOREIGN KEY (hWorkerID) REFERENCES worker(hWorkerID),
    CONSTRAINT prescription_status_fk FOREIGN KEY (prescriptionStatusID) REFERENCES REF_Status(statusID),
    CONSTRAINT prescreceipt_qty_chk CHECK (qtyDistributed > 0));
    
    
-- IMMUNIZATION ADMINISTRATION TRANSACTION RECORDS (Assigned to RAPHY) --
CREATE TABLE IF NOT EXISTS immunization_administration (
	immunizationID			INT 		AUTO_INCREMENT,
    patientID 				INT			NOT NULL,
    medicineID				INT 		NOT NULL,
    hWorkerID				INT 		NOT NULL,
    administrationDate		DATE		NOT NULL,
    vaccineType				VARCHAR(40)	NOT NULL,
    dosageNumber 			INT			NOT NULL,
    nextVaccinationDate 	DATE ,
    immunizationStatus		INT			NOT NULL,
    sideEffects				VARCHAR(100),
    
    CONSTRAINT immunization_pk PRIMARY KEY (immunizationID),
    CONSTRAINT immunization_patient_fk FOREIGN KEY (patientID) REFERENCES patient(patientID),
    CONSTRAINT immunization_medicine_fk FOREIGN KEY (medicineID) REFERENCES medicine(medicineID),
    CONSTRAINT immunization_worker_fk FOREIGN KEY (hWorkerID) REFERENCES worker(hWorkerID),
    CONSTRAINT immunization_status_fk FOREIGN KEY (immunizationStatus) REFERENCES REF_Status(statusID));

-- RESTOCK INVOICE TRANSACTION RECORDS (Assigned to SPENCER) --
CREATE TABLE IF NOT EXISTS restock_invoice (
    invoiceID 		INT 			AUTO_INCREMENT,
    supplierID 		INT 			NOT NULL,
    purchaseOrderID VARCHAR(16) 	NOT NULL,
    deliveryDate 	DATE 			NOT NULL,
    receivedBy 		INT	 			NOT NULL,
    totalOrderCost 	DECIMAL(10,2) 	NOT NULL DEFAULT 0,
    deliveryStatus 	INT				NOT NULL,
    
    CONSTRAINT invoice_pk PRIMARY KEY (invoiceID),
    CONSTRAINT invoice_supplier_fk FOREIGN KEY (supplierID) REFERENCES supplier(supplierID),
    CONSTRAINT invoice_receivedBy_fk FOREIGN KEY (receivedBy) REFERENCES worker(hWorkerID),
    CONSTRAINT invoice_status_fk FOREIGN KEY (deliveryStatus) REFERENCES REF_Status(statusID),
    CONSTRAINT invoice_po_id_uq UNIQUE (purchaseOrderID),
    CONSTRAINT check_positive_order_cost CHECK (totalOrderCost >= 0)
);
