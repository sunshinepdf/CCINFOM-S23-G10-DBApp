-- ====================================================
-- INSERT STATUSES IN REFERENCE TABLES
-- ====================================================
USE BHMS_DB;

INSERT IGNORE INTO REF_StatusCategory (categoryName)
VALUES 
    ('FacilityStatus'),
    ('HealthWorkerStatus'),
    ('PatientStatus'),
    ('MedicineStatus'),
    ('SupplierStatus'),
    ('MedicineInventoryStatus'),
    ('ConsultationStatus'),
    ('PrescriptionStatus'),
    ('ImmunizationStatus'),
    ('DeliveryStatus');


INSERT IGNORE INTO REF_Status (statusCategoryID, statusName)
VALUES (1, 'Operational'), (1, 'Closed'), (1, 'Under Maintenance'),
	   (2, 'Active'), (2, 'Inactive'),
       (3, 'Active'), (3, 'Inactive'),
       (4, 'Available'), (4, 'Discontinued'), (4, 'Batch Recalled'),
       (5, 'Operational'), (5, 'Closed'),
       (6, 'Available'), (6, 'Expired'), (6, 'Low Stock'), (6, 'Out of Stock'),
       (7, 'Completed'), (7, 'Pending'), (7, 'Archived'),
	   (8, 'Completed'), (8, 'Pending'), (8, 'Archived'),
       (9, 'Completed'), (9, 'Overdue'), (9, 'Archived'),
       (10, 'Received'), (10, 'In Transit'), (10, 'Order Placed'), (10, 'Cancelled'), (10, 'Pending');
SELECT * FROM REF_Status 
ORDER BY statusCategoryID, statusID;

-- ====================================================
-- INSERT RECORDS FOR CORE AND TRANSACTIONS
-- ====================================================
INSERT IGNORE INTO facility  (facilityName, facilityAddress, facilityContactNum, shiftStart, shiftEnd, facilityStatusID) 
VALUES	('Barangay Health Center Malolos', '123 Mabini Street, Barangay 112, Malolos City', '09171223342', '08:00:00', '17:00:00', 1),
		('Barangay Health Center Bicutan', '119 Dona Soledad Avenue, Barangay 982, Paranaque City', '09190983123', '07:30:00', '16:30:00', 1),
		('Barangay Health Center Taguig', '789 Mabini Street, Barangay 644, Taguig City', '09197860012', '08:30:00', '18:00:00', 1),
		('Barangay Health Center Tondo', '189 Kalakal Street, Barangay 222, Tondo City', '09200111115', '09:00:00', '18:30:00', 1),
		('Barangay Health Center Pasay', '654 Harrison Road, Barangay 342, Pasay City', '09215262999', '06:30:00', '17:00:00', 1),
		('Barangay Health Center Pasig', '987 Manalo Street, Barangay 109, Pasig City', '09221196524', '07:30:00', '16:30:00', 1),
		('Barangay Health Center Aurora', '147 Ermitano Boulevard, Barangay 567, Aurora City', '09231969712', '08:00:00', '17:30:00', 2),
		('Barangay Health Center Manila', '258 Roxas Avenue, Barangay 282, Manila City', '09240985672', '09:00:00', '18:00:00', 1),
		('Barangay Health Center Binan', '369 Mayon Road, Barangay 762, Binan City', '09257382281', '08:00:00', '17:00:00', 1),
		('Barangay Health Center Sta. Ana', '741 Diamante Street, Barangay 192, Sta. Ana City', '09260657712', '07:00:00', '16:00:00', 1);

INSERT IGNORE INTO worker (hWorkerLastName, hWorkerFirstName, hWorkerPosition, hContactInformation, hWorkerFacilityID, hWorkerStatusID) 
VALUES	('Dela Cruz', 'Ashanti Mae', 'Day Care Worker', '09171884564', 1, 4),
		('Tan', 'Anton', 'Barangay Nutrition Scholar', '09192346778', 1, 4),
		('Gomez', 'Mariano', 'Doctor', '09193456000', 2, 4),
		('Garcia', 'Isaiah', 'Barangay Health Worker', '09224567790', 2, 4),
		('Zamora', 'Jacinto', 'Community Health Aide', '09215678901', 5, 4),
		('Lim', 'Ramon', 'Barangay Health Worker', '09066789011', 3, 4),
		('Tuazon', 'Mielle', 'RHU Midwife', '09257893053', 4, 4),
		('Villanueva', 'Patrisha', 'Barangay Health Worker', '09248881230', 4, 5),
		('Rizal', 'Sofia', 'Nurse', '09257382305', 10, 4),
		('Granger', 'Akeirra', 'Barangay Nutrition Scholar', '09260093257', 8, 4);


INSERT IGNORE INTO patient (lastName, firstName, birthDate, gender, bloodType, address, primaryPhone, emergencyContact, patientStatusID) 
VALUES	('Santos', 'Juliet', '1989-05-26', 'Female', 'O+', '75 Rizal Avenue, Barangay 112, Malolos City', '09171233367', 'Romeo Santos - 09171734567', 6),
		('Cheng', 'Jossh Robert', '1990-07-01', 'Male', 'AB+', '78 Antonio Street, Barangay 982, Paranaque City', '09182341288', 'Analita Cheng - 09189076179', 6),
		('Cruz', 'Clayne Cherry', '1978-12-20', 'Female', 'B+', '234 Champaca Road, Barangay 644, Taguig City', '09193456789', 'Miguel Cruz - 09190206710', 6),
		('Siahingco', 'Ethan', '1994-04-05', 'Male', 'AB+', '567 Bonifacio Avenue, Barangay 222, Tondo City', '09201326683', 'Petra Siahingco - 09256567999', 6),
		('Fernandez', 'Elena Rose', '1995-09-11', 'Female', 'O-', '890 Diokno Street, Barangay 342, Pasay City', '09215678901', 'Regine Fernandeza - 09215678902', 6),
		('Torres', 'Chandler', '2000-10-26', 'Male', 'A-', '123 Manalo Avenue, Barangay 109, Pasig City', '09226789012', 'Pedro Fernandez - 09221239010', 6),
		('Ramos', 'Emma Jane', '2005-12-24', 'Female', 'B-', '456 Valencia Road, Barangay 567, Aurora City', '09237890123', 'Ramonn Ramos - 09237890111', 7),
		('Lumbang', 'Juan Mamigo', '2010-08-17', 'Male', 'AB-', '789 Taft Street, Barangay 282, Manila City', '09248111234', 'Philippe Lumbang - 09248901111', 7),
		('Lin', 'Josephine', '1999-08-28', 'Female', 'O+', '321 Waki-Waki Boulevard, Barangay 762, Binan City', '09259013145', 'Margarita Lin - 09259732316', 6),
		('Kollet', 'Gabriel Will', '1970-10-03', 'Male', 'A+', '654 Onyx Street, Barangay 192, Sta. Ana City', '09260111821', 'Teresa Kollet - 09260123113', 6);

INSERT IGNORE INTO supplier (supplierName, supplierAddress, supplierContactNum, supplierType, deliveryLeadTime, transactionDetails, supplierStatusID) 
VALUES	('Getz Healthcare Philippines', '1006 Antel Global Corporate Center, Julia Vargas Avenue, Ortigas Center, Pasig City', '09171234501', 'Medical Equipment Supplier', 7, 'Leading distributor of medical devices and equipment. Provides end-to-end services including marketing, supply chain, regulatory and technical support. Minimum order PHP 50,000. Payment terms: 30 days net.', 11),
		('Zuellig Pharma Corporation', '7th Floor Zuellig Building, Makati Avenue corner Paseo de Roxas, Makati City', '09182345602', 'Medicine Supplier', 3, 'Leading healthcare solutions company distributing pharmaceuticals. Over 100 years of service. Serves 200,000+ medical facilities. Payment upon delivery. Returns within 7 days for damaged items.', 11),
		('UNICEF Philippines Vaccine Supply', 'UNICEF House, 31st Street corner 3rd Avenue, Crescent Park West, BGC, Taguig City', '09193456703', 'Vaccine Supplier', 14, 'Supports DOH National Immunization Program. Provides BCG, DTwP-HepB-Hib, MMR, Rotavirus, HPV vaccines with cold chain logistics. Minimum 500 doses. Net 60 payment terms.', 11),
		('RMG Hospital Supply Inc', 'Unit 103 P&G Complex, Libis, Quezon City, Metro Manila', '09204567804', 'Medical Equipment Supplier', 10, 'ISO-certified provider of medical disposable products and devices. Exclusive distributor of trusted global brands. Installation services included. Payment: 45 days net.', 11),
		('Medshop Philippines', '2nd Floor Bonifacio Technology Center, 31st Street, BGC, Taguig City', '09215678905', 'Medicine Supplier', 2, 'Online medical supplies provider since 2005. Wide range of medicines and healthcare products. Minimum order PHP 10,000. Same-day delivery in Metro Manila for urgent orders.', 11),
		('RITM Vaccine Storage Distribution', 'Research Institute for Tropical Medicine, Alabang, Muntinlupa City', '09226789006', 'Vaccine Supplier', 21, 'National storage and distributor for DOH National Immunization Program. Houses 5.8 million doses capacity. Cold chain certified. Minimum 1000 doses. Net 90 payment.', 11),
		('Asya Medika Inc', '115 Carlos Palanca Jr. Street, Legaspi Village, Makati City', '09237890107', 'Medical Equipment Supplier', 5, 'Trusted distributor of global medical equipment brands. Provides warranty and maintenance services. Government purchase orders accepted. Payment net 30 days.', 12),
		('Philippine Medical Supplies', '88 Scout Borromeo Street, Quezon City, Metro Manila', '09248901208', 'Medicine Supplier', 4, 'Reliable source for medical equipment, hospital furniture and PPE. Competitive bulk pricing. Delivers nationwide. Payment upon invoice.', 11),
		('Pacific Surgical Inc', '1507 Jorge Bocobo Street, Ermita, Manila City', '09259012309', 'Medical Equipment Supplier', 10, 'Specializes in surgical instruments and hospital equipment. FDA-registered distributor. Government-accredited supplier. Payment terms negotiable.', 11),
		('Biosyn Healthcare Systems Inc', 'Unit 301 Salcedo Village, Makati City, Metro Manila', '09260123410', 'Medical Equipment Supplier', 6, 'Medical device manufacturing and distribution. Complete hospital supplies and installation services. Bulk discounts available. Payment terms negotiable.', 11);

INSERT IGNORE INTO medicine (medicineName, medicineDesc, dosageForm, strength, batchNumber, medicineStatusID) 
VALUES	('Paracetamol', 'Analgesic and antipyretic used to treat pain and fever. Commonly used for headaches, muscle aches, and reducing fever.', 'Tablet', '500mg', 'PARA2024-001', 8),
		('Amoxicillin', 'Antibiotic used to treat bacterial infections including respiratory tract infections, urinary tract infections, and skin infections.', 'Capsule', '500mg', 'AMOX2024-002', 8),
		('Cetirizine', 'Antihistamine used to relieve allergy symptoms such as watery eyes, runny nose, itching, and sneezing.', 'Tablet', '10mg', 'CETI2024-003', 8),
		('Metformin', 'Oral diabetes medicine that helps control blood sugar levels in patients with type 2 diabetes mellitus.', 'Tablet', '500mg', 'METF2024-004', 8),
		('Salbutamol', 'Bronchodilator used to treat or prevent bronchospasm in patients with asthma or chronic obstructive pulmonary disease.', 'Syrup', '2mg/5mL', 'SALB2024-006', 8),
		('Ibuprofen', 'Nonsteroidal anti-inflammatory drug used to reduce fever and treat pain or inflammation caused by conditions such as arthritis.', 'Tablet', '400mg', 'IBUP2024-007', 9),
		('Omeprazole', 'Proton pump inhibitor that decreases the amount of acid produced in the stomach, used to treat gastroesophageal reflux disease.', 'Capsule', '20mg', 'OMEP2024-008', 8),
		('Lagundi', 'Herbal medicine used as an expectorant to relieve cough and asthma, and for the symptomatic relief of common cold and flu.', 'Syrup', '600mg/5mL', 'LAGU2024-010', 8),
		('BCG Vaccine', 'Bacillus Calmette-Guerin vaccine used to prevent tuberculosis, especially in children. Part of DOH Expanded Program on Immunization.', 'Injectable', '0.05mL', 'BCG2024-011', 8),
		('Measles-Mumps-Rubella Vaccine', 'Combined vaccine that protects against measles, mumps, and rubella. Given as part of routine childhood immunization schedule.', 'Injectable', '0.5mL', 'MMR2024-012', 8);

INSERT IGNORE INTO restock_invoice (supplierID, purchaseOrderID, deliveryDate, receivedBy, totalOrderCost, deliveryStatus) 
VALUES	(1, 'PO-2025-001', '2025-11-20', 1, 1250000.00, 26),
		(2, 'PO-2025-002', '2025-11-21', 3, 45000.00, 26),
		(3, 'PO-2025-003', '2025-11-22', 6, 85000.00, 26),
		(4, 'PO-2025-004', '2025-11-23', 7, 67500.00, 26),
		(5, 'PO-2025-005', '2025-11-24', 9, 32000.00, 26),
		(6, 'PO-2025-006', '2025-11-25', 10, 150000.00, 27),
		(2, 'PO-2025-007', '2025-11-26', 3, 38000.00, 28),
		(5, 'PO-2025-008', '2025-11-27', 1, 28500.00, 30),
		(8, 'PO-2025-009', '2025-11-28', 6, 52000.00, 26),
		(9, 'PO-2025-010', '2025-11-29', 7, 95000.00, 26);
        
INSERT IGNORE INTO medicine_inventory (facilityID, medicineID, quantityInStock, inventoryStatusID) 
VALUES	(1, 1, 500, 13),   
		(2, 2, 250, 13),  
		(3, 3, 150, 13),  
		(4, 4, 180, 13),  
		(5, 5, 45, 15),   
		(6, 6, 80, 13),    
		(7, 8, 53, 16),    
		(8, 10, 200, 13),  
		(9, 7, 35, 14),   
		(10, 9, 100, 16); 

INSERT IGNORE INTO medical_consultation (patientID, hWorkerID, facilityID, consultationDate, consultationTime, symptoms, diagnosis, prescription, consultationStatusID) 
VALUES	(1, 3, 2, '2024-11-15', '09:30:00', 'Fever, headache, body aches for 2 days', 'Acute viral infection', 'Paracetamol 500mg, 1 tablet every 6 hours for 3 days. Increase fluid intake and rest.', 17),
		(2, 3, 2, '2024-11-16', '10:00:00', 'Persistent cough, sore throat, mild fever', 'Upper respiratory tract infection', 'Amoxicillin 500mg, 1 capsule 3 times daily for 7 days. Lagundi syrup 5mL 3 times daily.', 17),
		(3, 6, 3, '2024-11-17', '14:30:00', 'Skin rash, itching, watery eyes, sneezing', 'Allergic reaction', 'Cetirizine 10mg, 1 tablet once daily. Avoid allergen exposure. Apply calamine lotion on affected areas.', 17),
		(4, 3, 2, '2024-11-18', '11:15:00', 'Frequent urination, increased thirst, fatigue', 'Type 2 Diabetes Mellitus - follow up', 'Continue Metformin 500mg twice daily. Monitor blood sugar levels. Schedule follow-up in 1 month.', 17),
		(5, 4, 2, '2024-11-18', '15:45:00', 'Difficulty breathing, wheezing, chest tightness', 'Acute asthma exacerbation', 'Salbutamol syrup 5mL every 6 hours as needed. Advise to avoid triggers. Emergency referral if worsens.', 17),
		(6, 4, 2, '2024-11-19', '08:30:00', 'Abdominal pain, bloating, acid reflux', 'Gastroesophageal reflux disease (GERD)', 'Omeprazole 20mg, 1 capsule once daily before breakfast for 14 days. Avoid spicy foods and late meals.', 18),
		(6, 5, 5, '2024-12-12', '13:00:00', 'Routine check-up, no complaints', 'Healthy - routine examination', 'No prescription needed. Maintain healthy lifestyle. Annual check-up recommended.', 17),
		(1, 3, 2, '2024-12-24', '16:00:00', 'High fever, severe headache, muscle pain', 'Suspected dengue fever', 'Paracetamol for fever. Increase fluid intake. Complete blood count ordered. Refer to hospital if platelet count drops.', 17),
		(9, 6, 3, '2024-12-27', '10:30:00', 'Joint pain, swelling in knees', 'Osteoarthritis', 'Ibuprofen 400mg, 1 tablet twice daily after meals. Apply warm compress. Physical therapy recommended.', 17),
		(10, 7, 4, '2024-12-29', '14:00:00', 'Persistent cough with phlegm, chest discomfort', 'Acute bronchitis', 'Lagundi syrup 10mL three times daily. Amoxicillin 500mg three times daily for 5 days. Follow-up in 1 week.', 17);

UPDATE medicine_inventory mi
JOIN REF_Status s 
  ON s.statusName = CASE 
      WHEN mi.quantityInStock = 0 THEN 'Out of Stock'
      WHEN mi.quantityInStock <= 10 THEN 'Low Stock'
      ELSE 'Available'
  END
JOIN REF_StatusCategory c 
  ON c.categoryName = 'MedicineInventoryStatus' 
  AND s.statusCategoryID = c.statusCategoryID
SET mi.inventoryStatusID = s.statusID
WHERE mi.inventoryStatusID IS NULL;

INSERT IGNORE INTO prescription_receipt (patientID, consultationID, medicineID, hWorkerID, distributionDate, qtyDistributed, isValidPrescription, inventoryUpdated, prescriptionStatusID) 
VALUES	(1, 1, 2, 3, '2024-11-15', 18, TRUE, TRUE, 20),
		(2, 2, 2, 3, '2024-11-16', 21, TRUE, TRUE, 20),
		(2, 2, 2, 3, '2024-11-16', 1, TRUE, TRUE, 20),
		(3, 3, 3, 6, '2024-11-17', 7, TRUE, TRUE, 20),
		(4, 4, 2, 3, '2024-11-18', 60, TRUE, TRUE, 20),
		(5, 5, 2, 4, '2024-11-18', 1, TRUE, TRUE, 20),
		(6, 6, 2, 4, '2024-11-19', 14, TRUE, FALSE, 21),
		(6, 7, 5, 5, '2024-12-12', 14, TRUE, FALSE, 21),
		(1, 8, 2, 3, '2024-12-24', 14, TRUE, TRUE, 20),
		(9, 9, 3, 6, '2024-12-27', 15, TRUE, TRUE, 20);

INSERT IGNORE INTO immunization_administration (patientID, medicineID, hWorkerID, administrationDate, vaccineType, dosageNumber, nextVaccinationDate, immunizationStatusID, sideEffects) 
VALUES	(1, 9,  9, '2024-10-15', 'BCG', 1, NULL, 23, 'Mild redness at injection site'),
(2, 9,  9, '2024-10-20', 'BCG', 1, NULL, 23, 'None'),
(5, 9,  9, '2024-11-18', 'BCG', 1, NULL, 23, 'Slight swelling'),
(7, 9,  9, '2024-07-10', 'BCG', 1, NULL, 25, 'None'),
(9, 9,  9, '2024-11-25', 'BCG', 1, NULL, 23, 'None'),
(3, 10, 10, '2024-09-12', 'MMR', 1, '2025-09-12', 23, 'Low-grade fever for 1 day'),
(4, 10, 10, '2024-11-05', 'MMR', 1, '2025-11-05', 23, 'None'),
(6, 10, 10, '2024-08-20', 'MMR', 1, '2025-08-20', 23, 'None'),
(8, 10, 10, '2023-12-15', 'MMR', 1, '2024-12-15', 24, 'Mild rash'),
(10,10, 10, '2024-09-30', 'MMR', 1, '2025-09-30', 23, 'Low-grade fever');

