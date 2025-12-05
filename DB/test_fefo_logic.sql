-- =====================================================
-- FEFO (First Expire First Out) Testing Script
-- =====================================================
-- This script demonstrates how batch quantities reduce
-- based on expiry date (oldest first)
-- =====================================================

USE `pms`;

-- ===== BEFORE SALE =====
SELECT '===== BEFORE SALE - Panadol Batches =====' as '';
SELECT 
    Batch_number, 
    expire_date, 
    Quantaty as Quantity,
    DATEDIFF(expire_date, CURDATE()) as Days_Until_Expiry
FROM batch 
WHERE Product_parcode = '62230000000123'
ORDER BY expire_date ASC;

-- Total in inventory
SELECT '===== Total in Inventory =====' as '';
SELECT Quntaty as Total_Quantity
FROM inventory_has_product
WHERE Product_parcode = '62230000000123' AND Inventory_ID = 1;

-- ===== SIMULATE SALE OF 60 UNITS =====
-- According to FEFO, it should take:
-- - 40 units from BN202501014 (expires 2026-03-15) - will be depleted
-- - 20 units from BN202501001 (expires 2026-06-30)
-- Total: 60 units

SELECT '===== SIMULATING SALE OF 60 UNITS =====' as '';

START TRANSACTION;

-- Reduce from batch that expires first (2026-03-15)
UPDATE batch 
SET Quantaty = Quantaty - 40
WHERE Batch_number = 'BN202501014' AND Product_parcode = '62230000000123';

-- Reduce remaining 20 from next batch (2026-06-30)
UPDATE batch 
SET Quantaty = Quantaty - 20
WHERE Batch_number = 'BN202501001' AND Product_parcode = '62230000000123';

-- Update inventory total
UPDATE inventory_has_product
SET Quntaty = Quntaty - 60
WHERE Product_parcode = '62230000000123' AND Inventory_ID = 1;

COMMIT;

-- ===== AFTER SALE =====
SELECT '===== AFTER SALE - Panadol Batches =====' as '';
SELECT 
    Batch_number, 
    expire_date, 
    Quantaty as Quantity,
    DATEDIFF(expire_date, CURDATE()) as Days_Until_Expiry,
    CASE 
        WHEN Quantaty = 0 THEN 'DEPLETED'
        WHEN DATEDIFF(expire_date, CURDATE()) < 180 THEN 'EXPIRING SOON'
        ELSE 'OK'
    END as Status
FROM batch 
WHERE Product_parcode = '62230000000123'
ORDER BY expire_date ASC;

-- Total in inventory after sale
SELECT '===== Total in Inventory After Sale =====' as '';
SELECT Quntaty as Total_Quantity
FROM inventory_has_product
WHERE Product_parcode = '62230000000123' AND Inventory_ID = 1;

-- ===== VERIFICATION =====
-- Check that total in batches matches inventory total
SELECT '===== VERIFICATION =====' as '';
SELECT 
    (SELECT SUM(Quantaty) FROM batch WHERE Product_parcode = '62230000000123') as Batch_Total,
    (SELECT Quntaty FROM inventory_has_product WHERE Product_parcode = '62230000000123' AND Inventory_ID = 1) as Inventory_Total,
    CASE 
        WHEN (SELECT SUM(Quantaty) FROM batch WHERE Product_parcode = '62230000000123') = 
             (SELECT Quntaty FROM inventory_has_product WHERE Product_parcode = '62230000000123' AND Inventory_ID = 1)
        THEN 'MATCH ✓'
        ELSE 'MISMATCH ✗'
    END as Verification;

-- =====================================================
-- Example 2: Augmentin (Multiple batches)
-- =====================================================
SELECT '===== Example 2: Augmentin Batches =====' as '';
SELECT 
    Batch_number, 
    expire_date, 
    Quantaty as Quantity,
    DATEDIFF(expire_date, CURDATE()) as Days_Until_Expiry
FROM batch 
WHERE Product_parcode = '62230000000124'
ORDER BY expire_date ASC;

-- Simulate selling 35 units
-- Should take all 30 from BN202501016 (expires 2026-01-31)
-- Then 5 from BN202501002 (expires 2026-02-28)

SELECT '===== Simulating Sale of 35 units of Augmentin =====' as '';
START TRANSACTION;

UPDATE batch 
SET Quantaty = Quantaty - 30
WHERE Batch_number = 'BN202501016' AND Product_parcode = '62230000000124';

UPDATE batch 
SET Quantaty = Quantaty - 5
WHERE Batch_number = 'BN202501002' AND Product_parcode = '62230000000124';

UPDATE inventory_has_product
SET Quntaty = Quntaty - 35
WHERE Product_parcode = '62230000000124' AND Inventory_ID = 1;

COMMIT;

SELECT '===== After Sale - Augmentin Batches =====' as '';
SELECT 
    Batch_number, 
    expire_date, 
    Quantaty as Quantity,
    CASE 
        WHEN Quantaty = 0 THEN 'DEPLETED'
        ELSE 'Available'
    END as Status
FROM batch 
WHERE Product_parcode = '62230000000124'
ORDER BY expire_date ASC;
