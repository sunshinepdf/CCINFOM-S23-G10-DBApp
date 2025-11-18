-- ====================================================
-- INSERT STATUSES IN REFERENCE TABLES
-- ====================================================
USE BHMS_DB;

INSERT INTO REF_StatusCategory (categoryName)
VALUES ('FacilityStatus'),
	   ('HealthWorkerStatus'),
       ('PatientStatus'),
       ('MedicineStatus'),
       ('SupplierStatus'),
       ('MedicineInventoryStatus'),
       ('ConsultationStatus'),
       ('PrescriptionStatus'),
       ('ImmunizationStatus'),
       ('DeliveryStatus');

SELECT * FROM REF_StatusCategory 
ORDER BY statusCategoryID;

INSERT INTO REF_Status (statusCategoryID, statusName)
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
