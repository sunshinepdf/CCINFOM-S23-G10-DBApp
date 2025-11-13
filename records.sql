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