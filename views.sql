-- FACILITY DETAILS VIEW (Assigned to KHYLE) --
CREATE VIEW facilityDetails_view AS
SELECT 
    f.facilityID, f.facilityName, f.facilityAddress, f.facilityContactNum, f.shiftStart,
    f.shiftEnd, 
    hw.hWorkerID, CONCAT(hw.hWorkerLastName, ', ', hw.hWorkerFirstName) AS HealthWorkerName, 
    m.medicineID, m.medicineName, m.quantityInStock
FROM facility f LEFT JOIN worker hw 		  	  ON f.facilityID = hw.facilityID
				# TODO: add other joins once medicine inventory and facility link table is created 
ORDER BY f.FacilityID;