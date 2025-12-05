-- =====================================================
-- DEBUG: Check Panadol batches and fix quantity type
-- Run this script to diagnose the issue
-- =====================================================

USE `pms`;

-- Step 1: Check if batches exist for Panadol
SELECT 'Checking Panadol Batches:' as Debug_Step;
SELECT 
    b.Batch_number,
    b.Product_parcode,
    b.Quantaty,
    b.expire_date,
    p.Name as Product_Name
FROM batch b
JOIN product p ON b.Product_parcode = p.parcode
WHERE p.Name LIKE '%Panadol%' OR b.Product_parcode = '62230000000123';

-- Step 2: Check current column type
SELECT 'Current batch table structure:' as Debug_Step;
DESCRIBE batch;

-- Step 3: Check inventory for Panadol
SELECT 'Inventory for Panadol:' as Debug_Step;
SELECT 
    ihp.Inventory_ID,
    ihp.Product_parcode,
    ihp.Quntaty,
    p.Name
FROM inventory_has_product ihp
JOIN product p ON ihp.Product_parcode = p.parcode
WHERE p.Name LIKE '%Panadol%';

-- Step 4: ALTER TABLE to DOUBLE (run this!)
ALTER TABLE `batch` MODIFY COLUMN `Quantaty` DOUBLE NOT NULL;

-- Step 5: Verify the change
SELECT 'After ALTER - batch table structure:' as Debug_Step;
DESCRIBE batch;

-- Step 6: Check batches again
SELECT 'Batches after ALTER:' as Debug_Step;
SELECT 
    b.Batch_number,
    b.Quantaty,
    b.expire_date
FROM batch b
WHERE b.Product_parcode = '62230000000123'
ORDER BY b.expire_date ASC;
