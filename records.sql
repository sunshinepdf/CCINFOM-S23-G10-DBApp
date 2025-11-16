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

-- BRGY HEALTH WORKER RECORDS (Assigned to ASHLEY) --
CREATE TABLE brgy_health_worker (
	hWorkerID			INT				AUTO_INCREMENT,
	hWorkerLastName		VARCHAR(45)		NOT NULL,
	hWorkerFirstName	VARCHAR(45)		NOT NULL,
	hWorkerPosition		VARCHAR(45)		NOT NULL,
	hContactInformation	VARCHAR(11)		NOT NULL,
	hWorkerStatusID INT NOT NULL,
    FOREIGN KEY (hWorkerStatusID) REFERENCES REF_Status(StatusID)
);

-- BRGY FACILITY RECORDS (Assigned to KHYLE) --
CREATE TABLE brgy_facility (
	facilityID 			INT 			AUTO_INCREMENT,
    facilityName		VARCHAR(60)		NOT NULL,
    facilityAddress 	VARCHAR(100)	NOT NULL,
    facilityContactNum	VARCHAR(11)		NOT NULL,
    shiftStart			TIME			NOT NULL,
    shiftEnd			TIME			NOT NULL,
    facilityStatus		ENUM('Operational',
							 'Under Maintenance',
                             'Closed')	DEFAULT 'Operational',
CONSTRAINT brgyfacility_pk PRIMARY KEY (facilityID),
CONSTRAINT brgyfacility_uqname UNIQUE (facilityName));

-- SUPPLIER RECORDS (Assigned to KHYLE) --
CREATE TABLE supplier (
	supplierID 				INT 			AUTO_INCREMENT,
    supplierName 			VARCHAR(50)		NOT NULL,
    supplierAddress 		VARCHAR(100)	NOT NULL,
    supplierContactNum		VARCHAR(11)		NOT NULL,
    supplierType			ENUM('Medical Equipment Supplier',
								 'Medicine Supplier',
								 'Vaccine Supplier')	
											NOT NULL,
	deliveryLeadTime		INT 			NOT NULL, 
    transactionDetails 		VARCHAR(350)	NOT NULL,  -- TODO: Create a separate table to reference instead (?) --
    supplierStatus      ENUM('Active', 'Inactive') DEFAULT 'Active',
    
    CONSTRAINT supplier_pk PRIMARY KEY (supplierID),
    CONSTRAINT supplier_name_uq UNIQUE (supplierName),
     CONSTRAINT supplier_leadtime_chk CHECK (deliveryLeadTime > 0));

-- SUPPLIER RECORDS (Assigned to RAPHY) --
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
    PRIMARY KEY (patientID)
);

/*inc
CREATE TABLE immunization_administration (
	immunizationID,
    patientID,
    vaccineID,
    hWorkerID,
    administrationDate,
    vaccineType,
    batchNumber,
    dosageNumber,
    nextVaccinationDate,
    immunizationStatus,
    sideEffects
);
*/