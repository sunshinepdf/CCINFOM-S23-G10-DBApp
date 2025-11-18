# REFERENCE RECORDS
-- Status Category Table --
CREATE TABLE REF_StatusCategory (
    StatusCategoryID INT AUTO_INCREMENT,
    CategoryName VARCHAR(50) NOT NULL
);

-- Status Table --
CREATE TABLE REF_Status (
    StatusID INT AUTO_INCREMENT,
    StatusCategoryID INT NOT NULL,
    StatusName VARCHAR(50) NOT NULL,
    FOREIGN KEY (StatusCategoryID) REFERENCES REF_StatusCategory(StatusCategoryID)
);

# CORE RECORDS
-- BRGY HEALTH WORKER RECORDS (Assigned to ASHLEY) --
CREATE TABLE worker (
	hWorkerID			INT				AUTO_INCREMENT,
	hWorkerLastName		VARCHAR(45)		NOT NULL,
	hWorkerFirstName	VARCHAR(45)		NOT NULL,
	hWorkerPosition		VARCHAR(45)		NOT NULL,
	hContactInformation	VARCHAR(11)		NOT NULL,
	hWorkerStatusID INT NOT NULL,
    FOREIGN KEY (hWorkerStatusID) REFERENCES REF_Status(StatusID),
    CONSTRAINT worker_pk PRIMARY KEY (hWorkerID),
    CONSTRAINT workerstatus_fk FOREIGN KEY (hWorkerStatusID) REFERENCES REF_Status(StatusID),
    CONSTRAINT workerfacility_fk FOREIGN KEY (facilityID) REFERENCES facility(facilityID),
    CONSTRAINT worker_uqcontact UNIQUE (hContactInformation),
    CONSTRAINT worker_lastnameCheck CHECK (hWorkerLastName REGEXP '^[A-Za-z]+$'),  
    CONSTRAINT worker_firstnameCheck CHECK (hWorkerFirstName REGEXP '^[A-Za-z]+$'),
    CONSTRAINT worker_positionCheck CHECK (hWorkerPosition REGEXP '^[A-Za-z ]+$'),  
    CONSTRAINT worker_contactCheck CHECK (hContactInformation REGEXP '^[0-9]{11}$')  
);

-- BRGY FACILITY RECORDS (Assigned to KHYLE) --
CREATE TABLE facility (
	facilityID 			INT 			AUTO_INCREMENT,
    facilityName		VARCHAR(60)		NOT NULL,
    facilityAddress 	VARCHAR(100)	NOT NULL, 
    facilityContactNum	VARCHAR(11)		NOT NULL,
    shiftStart			TIME			NOT NULL,
    shiftEnd			TIME			NOT NULL,
    facilityStatusID	INT 			NOT NULL,
    
CONSTRAINT facility_pk PRIMARY KEY (facilityID),
CONSTRAINT facilitystatus_fk FOREIGN KEY (facilityStatusID) REFERENCES REF_Status(StatusID),
CONSTRAINT facility_uqname UNIQUE (facilityName), 
CONSTRAINT facility_contactCheck CHECK (facilityContactNum REGEXP '^[0-9]{11}$'), # CHECKS to ensure only 11 digits from 0-9 are included in the contact number
CONSTRAINT facility_shiftCheck CHECK (shiftEnd > shiftStart));

-- SUPPLIER RECORDS (Assigned to KHYLE) --
CREATE TABLE supplier (
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
    CONSTRAINT supplierstatus_fk FOREIGN KEY (supplierStatusID) REFERENCES REF_Status(StatusID),
    CONSTRAINT supplier_name_uq UNIQUE (supplierName),
    CONSTRAINT supplier_contact_chk CHECK (supplierContactNum REGEXP '^[0-9]{11}$'), # CHECKS to ensure only 11 digits from 0-9 are included in the contact number
	CONSTRAINT supplier_leadtime_chk CHECK (deliveryLeadTime > 0));

-- PATIENT RECORDS (Assigned to RAPHY) --
CREATE TABLE patient (
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
    primaryPhone		INT				NOT NULL,
    emergencyContact	VARCHAR(80)		NOT NULL,
    -- will edit
    patientStatus		ENUM('dead', 'alive')		NOT NULL,
    PRIMARY KEY (patientID),
    CONSTRAINT patient_phoneCheck CHECK (primaryPhone REGEXP '^[0-9]{11}$')
);

-- MEDICINE INVENTORY RECORDS (Assigned to SPENCER) --
CREATE TABLE medicine_inventory (
    medID	 		INT 			AUTO_INCREMENT,
    medName		 	VARCHAR(50) 	NOT NULL UNIQUE,
    medType	 		ENUM('vaccine', 'medicine') NOT NULL,
    medDesc		 	VARCHAR(200) 	NOT NULL,
    quantityInStock INT 			NOT NULL DEFAULT 0,
    expiryDate 		DATE 			NOT NULL,
    medStatus	 	ENUM('available', 'expired', 'out of stock') NOT NULL DEFAULT ("available"),
    PRIMARY KEY (medID),
    
    CONSTRAINT check_quantity_non_negative CHECK (quantityInStock >= 0),
    CONSTRAINT check_future_expiry_date CHECK (expiryDate > CURDATE()),
    CONSTRAINT chk_status_consistency CHECK ((quantityInStock > 0 AND expiryDate > CURDATE() AND medStatus = 'Available') OR
        (quantityInStock = 0 AND medStatus = 'Out of Stock') OR
        (expiryDate <= CURDATE() AND medStatus = 'Expired'))
);

# TRANSACTION RECORDS
-- IMMUNIZATION ADMINISTRATION TRANSACTION RECORDS (Assigned to RAPHY) --
CREATE TABLE immunization_administration (
	immunizationID			INT 		AUTO_INCREMENT,
    patientID 				INT			NOT NULL,
    medID					INT 		NOT NULL,
    hWorkerID				INT 		NOT NULL,
    administrationDate		DATE		NOT NULL,
    vaccineType				VARCHAR(40)	NOT NULL,
    batchNumber				INT,
    dosageNumber 			INT			NOT NULL,
    nextVaccinationDate 	DATE ,
    immunizationStatus		ENUM('pending',
								 'completed')
										NOT NULL,
    sideEffects				VARCHAR(100),
    PRIMARY KEY (immunizationID),
    FOREIGN KEY (patientID) REFERENCES patient(patientID),
    FOREIGN KEY (medID) REFERENCES medicine_inventory(medID),
    FOREIGN KEY (hWorkerID) REFERENCES worker(hWorkerID)
);


-- MEDICAL CONSULTATIONS TRANSACTION RECORDS (Assigned to ASHLEY) --
CREATE TABLE medical_consultation (
    consultationID INT AUTO_INCREMENT,
    patientID INT NOT NULL,
    hWorkerID INT NOT NULL,
    facilityID INT NOT NULL,
    consultationDate DATE NOT NULL,
    consultationTime TIME NOT NULL,
    symptoms TEXT NOT NULL, 
    diagnosis TEXT NOT NULL,
    prescription TEXT,
    consultationStatusID INT NOT NULL,
    lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT consultation_pk PRIMARY KEY (consultationID),
    CONSTRAINT consultation_patient_fk FOREIGN KEY (patientID) REFERENCES patient(patientID),
    CONSTRAINT consultation_worker_fk FOREIGN KEY (hWorkerID) REFERENCES worker(hWorkerID),
    CONSTRAINT consultation_facility_fk FOREIGN KEY (facilityID) REFERENCES facility(facilityID),
    CONSTRAINT consultation_status_fk FOREIGN KEY (consultationStatusID) REFERENCES REF_Status(StatusID),
    CONSTRAINT consultation_dateCheck CHECK (consultationDate <= CURDATE())
);

-- PRESCRIPTION RECEIPT TRANSACTION RECORDS (Assigned to KHYLE) --
CREATE TABLE prescription_receipt (
	receiptID			 INT 	 AUTO_INCREMENT,
    patientID			 INT	 NOT NULL,
    consultationID 		 INT	 NOT NULL,
    medicineID			 INT 	 NOT NULL,
    workerID			 INT	 NOT NULL,
    distributionDate	 DATE,
    qtyDistributed 		 INT	 NOT NULL,
    isValidPrescription  BOOLEAN DEFAULT FALSE,
    inventoryUpdated	 BOOLEAN DEFAULT FALSE,
    prescriptionStatusID INT	 NOT NULL,
    
    CONSTRAINT prescription_pk PRIMARY KEY (receiptID),
    CONSTRAINT prescription_patient_fk FOREIGN KEY (patientID) REFERENCES patient(patientID),
    CONSTRAINT prescription_medconsult_fk FOREIGN KEY (consultationID) REFERENCES medical_consultation(consultationID),
    CONSTRAINT prescription_medicine_fk FOREIGN KEY (medicineID) REFERENCES medicine_inventory(medID),
    CONSTRAINT prescription_healthworker_fk FOREIGN KEY (workerID) REFERENCES worker(hWorkerID),
    CONSTRAINT prescreceipt_qty_chk CHECK (qtyDistributed > 0));
    
-- RESTOCK INVOICE TRANSACTION RECORDS (Assigned to SPENCER) --
CREATE TABLE restock_invoice (
    invoiceID 		INT 			AUTO_INCREMENT,
    supplierID 		INT 			NOT NULL,
    purchaseOrderID INT 			NOT NULL UNIQUE,
    deliveryDate 	DATE 			NOT NULL,
    receivedBy 		VARCHAR(100) 	NOT NULL,
    totalOrderCost 	DECIMAL(10,2) 	NOT NULL DEFAULT 0,
    deliveryStatus 	ENUM('pending', 'ongoing', 'delivered') NOT NULL DEFAULT 'pending',
    PRIMARY KEY (invoiceID),
    FOREIGN KEY (supplierID) REFERENCES supplier(supplierID),
    
    CONSTRAINT check_valid_delivery_date CHECK (deliveryDate >= CURDATE()),
    CONSTRAINT check_positive_order_cost CHECK (totalOrderCost >= 0),
    CONSTRAINT check_positive_po_id CHECK (purchaseOrderID > 0)
);

