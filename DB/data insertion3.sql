-- CLEANUP (Order is important due to Foreign Keys)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE purchase_invoce_has_batch;
TRUNCATE TABLE purchase_invoce;
TRUNCATE TABLE invoice_has_product;
TRUNCATE TABLE sell_invoice;
TRUNCATE TABLE treasury;
TRUNCATE TABLE invoice;
TRUNCATE TABLE inventory_has_product;
TRUNCATE TABLE inventory;
TRUNCATE TABLE bransh_has_product;
TRUNCATE TABLE batch;
TRUNCATE TABLE supplier_has_product;
TRUNCATE TABLE supplier;
TRUNCATE TABLE customer_buy_product;
TRUNCATE TABLE customer;
TRUNCATE TABLE employee;
TRUNCATE TABLE medicine_has_dosage_form;
TRUNCATE TABLE cosmetic;
TRUNCATE TABLE medicine;
TRUNCATE TABLE product;
TRUNCATE TABLE dosage_form;
TRUNCATE TABLE category;
TRUNCATE TABLE bransh;
TRUNCATE TABLE person;
SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- 1. BRANCHES
-- ==========================================
INSERT INTO bransh (ID, Name, Adress) VALUES 
(1, 'Central Pharmacy', '1 Main Square, Cairo'),
(2, 'North Branch', '15 Alexandria St, Alex'),
(3, 'South Branch', '88 Nile Road, Luxor');

-- ==========================================
-- 2. CATEGORIES
-- ==========================================
INSERT INTO category (ID, name) VALUES 
(1, 'Antibiotics'),
(2, 'Pain Relievers'),
(3, 'Vitamins & Supplements'),
(4, 'Cough & Cold'),
(5, 'Digestive Health'),
(6, 'First Aid'),
(7, 'Skin Care'),
(8, 'Dental Care'),
(9, 'Hygiene'),
(10, 'Chronic Care');

-- ==========================================
-- 3. DOSAGE FORMS (Active Ingredients)
-- ==========================================
INSERT INTO dosage_form (ID, active_ing) VALUES 
(1, 'Paracetamol'),
(2, 'Ibuprofen'),
(3, 'Amoxicillin'),
(4, 'Cetirizine'),
(5, 'Vitamin C'),
(6, 'Omega-3'),
(7, 'Pantoprazole'),
(8, 'Insulin Glargine'),
(9, 'Metformin'),
(10, 'Atorvastatin'),
(11, 'Loratadine'),
(12, 'Azithromycin'),
(13, 'Calcium Carbonate'),
(14, 'Diclofenac Sodium'),
(15, 'Hyaluronic Acid');

-- ==========================================
-- 4. SUPPLIERS
-- ==========================================
INSERT INTO supplier (nane, phone, adress) VALUES 
('PharmaOverseas', '01011112222', 'Cairo Industrial Zone'),
('United Pharma', '01233334444', 'Giza Sector 6'),
('Ibn Sina', '01155556666', 'Obour City'),
('Medico Supply', '01099998888', 'Alexandria Port'),
('Global Health', '01277778888', 'Maadi Tech Park');

-- ==========================================
-- 5. PEOPLE (Employees & Customers)
-- ==========================================
-- Employees
INSERT INTO person (ID, Name, Phone) VALUES ('100', 'Super Admin', '01000000000');
INSERT INTO employee (Person_ID, User_name, Password, salary, StartDate, bransh_ID) VALUES ('100', 'admin', 'admin123', 15000, '2023-01-01', 1);

INSERT INTO person (ID, Name, Phone) VALUES ('101', 'Dr. Ahmed Ali', '01112223333');
INSERT INTO employee (Person_ID, User_name, Password, salary, StartDate, bransh_ID) VALUES ('101', 'ahmed', 'pass123', 6000, '2023-05-01', 1);

INSERT INTO person (ID, Name, Phone) VALUES ('102', 'Dr. Sarah Smith', '01223334444');
INSERT INTO employee (Person_ID, User_name, Password, salary, StartDate, bransh_ID) VALUES ('102', 'sarah', 'pass123', 6500, '2023-06-15', 2);

INSERT INTO person (ID, Name, Phone) VALUES ('103', 'Amr Warehouse', '01556667777');
INSERT INTO employee (Person_ID, User_name, Password, salary, StartDate, bransh_ID) VALUES ('103', 'amr', 'pass123', 4500, '2024-01-10', 1);

-- Customers
INSERT INTO person (ID, Name, Phone) VALUES ('201', 'Mohamed Hassan', '01099887766');
INSERT INTO customer (Person_ID, points) VALUES ('201', 1250); -- High value

INSERT INTO person (ID, Name, Phone) VALUES ('202', 'Noha Youssef', '01188776655');
INSERT INTO customer (Person_ID, points) VALUES ('202', 450);

INSERT INTO person (ID, Name, Phone) VALUES ('203', 'Karim Ezzat', '01211223344');
INSERT INTO customer (Person_ID, points) VALUES ('203', 50);

INSERT INTO person (ID, Name, Phone) VALUES ('204', 'Laila Mahmoud', '01544332211');
INSERT INTO customer (Person_ID, points) VALUES ('204', 3000); -- VIP

INSERT INTO person (ID, Name, Phone) VALUES ('205', 'Walk-in Guest', '00000000000');
INSERT INTO customer (Person_ID, points) VALUES ('205', 0);

-- ==========================================
-- 6. PRODUCTS (Medicines & Cosmetics)
-- ==========================================
-- 6.1 Painkillers
INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622001', 'Panadol Extra', 45.0, 2, 2); -- 2 strips per box
INSERT INTO medicine (Product_parcode) VALUES ('622001');
INSERT INTO medicine_has_dosage_form VALUES ('622001', 1, 500);

INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622002', 'Brufen 400mg', 35.0, 3, 2);
INSERT INTO medicine (Product_parcode) VALUES ('622002');
INSERT INTO medicine_has_dosage_form VALUES ('622002', 2, 400);

INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622003', 'Cataflam 50mg', 55.0, 2, 2);
INSERT INTO medicine (Product_parcode) VALUES ('622003');
INSERT INTO medicine_has_dosage_form VALUES ('622003', 14, 50);

-- 6.2 Antibiotics
INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622004', 'Augmentin 1g', 90.0, 14, 1); -- 14 tabs
INSERT INTO medicine (Product_parcode) VALUES ('622004');
INSERT INTO medicine_has_dosage_form VALUES ('622004', 3, 1000);

INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622005', 'Zithromax 500mg', 85.0, 3, 1);
INSERT INTO medicine (Product_parcode) VALUES ('622005');
INSERT INTO medicine_has_dosage_form VALUES ('622005', 12, 500);

-- 6.3 Chronic
INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622006', 'Lipitor 20mg', 120.0, 3, 10);
INSERT INTO medicine (Product_parcode) VALUES ('622006');
INSERT INTO medicine_has_dosage_form VALUES ('622006', 10, 20);

INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622007', 'Glucophage 1000', 60.0, 3, 10);
INSERT INTO medicine (Product_parcode) VALUES ('622007');
INSERT INTO medicine_has_dosage_form VALUES ('622007', 9, 1000);

INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622008', 'Lantus SoloStar', 450.0, 5, 10); -- 5 pens
INSERT INTO medicine (Product_parcode) VALUES ('622008');
INSERT INTO medicine_has_dosage_form VALUES ('622008', 8, 100);

-- 6.4 Vitamins
INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622009', 'C-Retard 500mg', 25.0, 1, 3);
INSERT INTO medicine (Product_parcode) VALUES ('622009');
INSERT INTO medicine_has_dosage_form VALUES ('622009', 5, 500);

INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622010', 'Omega-3 Plus', 75.0, 3, 3);
INSERT INTO medicine (Product_parcode) VALUES ('622010');
INSERT INTO medicine_has_dosage_form VALUES ('622010', 6, 1000);

-- 6.5 Cosmetics
INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622011', 'Nivea Soft Cream', 65.0, 1, 7);
INSERT INTO cosmetic (Product_parcode, Brand, Gender) VALUES ('622011', 'Nivea', 'F');

INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622012', 'Rexona Men Spray', 55.0, 1, 9);
INSERT INTO cosmetic (Product_parcode, Brand, Gender) VALUES ('622012', 'Rexona', 'M');

INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622013', 'Listerine Mouthwash', 95.0, 1, 8);
INSERT INTO cosmetic (Product_parcode, Brand, Gender) VALUES ('622013', 'Listerine', 'B');

-- 6.6 Allergy
INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES ('622014', 'Zyrtec Tablets', 40.0, 2, 4);
INSERT INTO medicine (Product_parcode) VALUES ('622014');
INSERT INTO medicine_has_dosage_form VALUES ('622014', 4, 10);

-- ==========================================
-- 7. BATCHES (FEFO Logic)
-- ==========================================
-- Panadol (622001): 50 + 150 = 200 Total
INSERT INTO batch VALUES ('B-PAN-001', 30.0, DATE_ADD(CURRENT_DATE, INTERVAL 1 MONTH), 50, '622001');
INSERT INTO batch VALUES ('B-PAN-002', 32.0, DATE_ADD(CURRENT_DATE, INTERVAL 2 YEAR), 150, '622001');

-- Brufen (622002): 100 Total
INSERT INTO batch VALUES ('B-BRU-100', 25.0, DATE_ADD(CURRENT_DATE, INTERVAL 1 YEAR), 100, '622002');

-- Cataflam (622003): 0 Total (Stock Out)

-- Augmentin (622004): 15 Total
INSERT INTO batch VALUES ('B-AUG-055', 70.0, DATE_ADD(CURRENT_DATE, INTERVAL 6 MONTH), 15, '622004');

-- Zithromax (622005): 0 Total

-- Lipitor (622006): 60 Total
INSERT INTO batch VALUES ('B-LIP-222', 90.0, DATE_ADD(CURRENT_DATE, INTERVAL 18 MONTH), 60, '622006');

-- Glucophage (622007): 200 Total
INSERT INTO batch VALUES ('B-GLU-333', 40.0, DATE_ADD(CURRENT_DATE, INTERVAL 18 MONTH), 200, '622007');

-- Insulin (622008): 10 Total
INSERT INTO batch VALUES ('B-INS-001', 350.0, DATE_ADD(CURRENT_DATE, INTERVAL 5 MONTH), 10, '622008');

-- C-Retard (622009): 20 + 100 = 120 Total
INSERT INTO batch VALUES ('B-VITC-01', 15.0, DATE_ADD(CURRENT_DATE, INTERVAL 1 WEEK), 20, '622009');
INSERT INTO batch VALUES ('B-VITC-02', 18.0, DATE_ADD(CURRENT_DATE, INTERVAL 1 YEAR), 100, '622009');

-- Omega-3 (622010): 0 Total

-- Nivea (622011): 50 Total
INSERT INTO batch VALUES ('B-NIV-999', 45.0, DATE_ADD(CURRENT_DATE, INTERVAL 3 YEAR), 50, '622011');

-- Rexona (622012): 40 Total
INSERT INTO batch VALUES ('B-REX-888', 35.0, DATE_ADD(CURRENT_DATE, INTERVAL 3 YEAR), 40, '622012');

-- Listerine (622013): 0 Total

-- Zyrtec (622014): 0 Total (Explicit 0 batch)
INSERT INTO batch VALUES ('B-ZYR-000', 25.0, '2025-01-01', 0, '622014');

-- ==========================================
-- 8. INVENTORY
-- ==========================================
INSERT INTO inventory (ID, Bransh_ID) VALUES (1, 1);

-- SYNCHRONIZED INVENTORY COUNTS (Inventory = Sum of Batches)
INSERT INTO inventory_has_product (Inventory_ID, Product_parcode, Quntaty, reordr_level) VALUES 
(1, '622001', 200, 20), -- Panadol (Batch 50 + 150 = 200)
(1, '622002', 100, 20), -- Brufen (Batch 100)
(1, '622003', 0, 10),   -- Cataflam (0 Batches)
(1, '622004', 15, 5),   -- Augmentin (Batch 15)
(1, '622005', 0, 5),    -- Zithromax (0 Batches)
(1, '622006', 60, 10),  -- Lipitor (Batch 60)
(1, '622007', 200, 30), -- Glucophage (Batch 200)
(1, '622008', 10, 2),   -- Insulin (Batch 10)
(1, '622009', 120, 10), -- Vit C (Batch 20 + 100 = 120)
(1, '622010', 0, 5),    -- Omega 3 (0 Batches)
(1, '622011', 50, 5),   -- Nivea (Batch 50)
(1, '622012', 40, 5),   -- Rexona (Batch 40)
(1, '622013', 0, 5),    -- Listerine (0 Batches)
(1, '622014', 0, 10);   -- Zyrtec (Batch 0)

-- ==========================================
-- 9. INITIAL TREASURY & INVOICE
-- ==========================================
-- Dummy invoice for initial capital
INSERT INTO invoice (ID, date, price, employee_User_name, employee_Person_ID, employee_bransh_ID) 
VALUES (1, CURRENT_DATE, 0, 'admin', '100', 1);

INSERT INTO treasury (treasuryid, Bransh_ID, date_and_time, amount_of_money, invoice_ID) 
VALUES ('TR-OPENING', 1, CURRENT_TIMESTAMP, 15000.0, 1);
