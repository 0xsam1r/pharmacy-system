USE `pms`;

-- CLEANUP
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

-- 1) CATEGORY
INSERT INTO `category` (`ID`, `name`) VALUES
(1, 'Painkiller'),
(2, 'Antibiotic'),
(3, 'Antipyretic'),
(4, 'Antacid'),
(5, 'Cough Suppressant'),
(6, 'Antihistamine'),
(7, 'Vitamin'),
(8, 'Skincare'),
(9, 'Nasal Decongestant'),
(10,'Antidiabetic');

-- 2) PRODUCT
INSERT INTO `product` (`parcode`, `Name`, `Price`, `Uints`, `Category_ID`) VALUES
('62230000000123', 'Panadol',                    35.00, 4, 1),
('62230000000124', 'Augmentin 625',             155.00, 1, 2),
('62230000000125', 'Cataflam 50',                75.00, 2, 1),
('62230000000126', 'Brufen 400',                 85.00, 2, 1),
('62230000000127', 'Zyrtec',                     60.00, 2, 6),
('62230000000128', 'Vitamin C 1000',             95.00, 4, 7),
('62230000000129', 'Rennie',                     50.00, 2, 4),
('62230000000130', 'Vicks Cough Syrup',          90.00, 1, 5),
('62230000000131', 'Nivea Soft Cream 100ml',    120.00, 1, 8),
('62230000000132', 'Sinutab',                     70.00, 1, 9),
('62230000000133', 'Panadol Cold & Flu',         55.00, 2, 3),
('62230000000134', 'Glucophage 500',             95.00, 4, 10),
('62230000000135', 'Adol Extra',                  45.00, 2, 1),
('62230000000136', 'Aerius 5mg',                 110.00, 2, 6);

-- 3) MEDICINE
INSERT INTO `medicine` (`Product_parcode`) VALUES
('62230000000123'), ('62230000000124'), ('62230000000125'), ('62230000000126'),
('62230000000127'), ('62230000000128'), ('62230000000129'), ('62230000000130'),
('62230000000132'), ('62230000000133'), ('62230000000134'), ('62230000000135'),
('62230000000136');

-- 4) COSMETIC
INSERT INTO `cosmetic` (`Brand`, `Gender`, `Product_parcode`) VALUES
('Nivea', 'U', '62230000000131');

-- 5) DOSAGE_FORM
INSERT INTO `dosage_form` (`active_ing`, `ID`) VALUES
('Paracetamol', 1), ('Amoxicillin + Clavulanic Acid', 2), ('Diclofenac Potassium', 3),
('Ibuprofen', 4), ('Cetirizine', 5), ('Ascorbic Acid (Vitamin C)', 6),
('Calcium Carbonate + Magnesium', 7), ('Dextromethorphan + Guaifenesin', 8),
('Paracetamol + Pseudoephedrine + Others', 9), ('Metformin', 10), ('Desloratadine', 11);

-- 6) MEDICINE_HAS_DOSAGE_FORM
INSERT INTO `medicine_has_dosage_form` (`medicine_Product_parcode`, `dosage_form_ID`, `Strength`) VALUES
('62230000000123', 1, 500), ('62230000000124', 2, 625), ('62230000000125', 3, 50),
('62230000000126', 4, 400), ('62230000000127', 5, 10), ('62230000000128', 6, 1000),
('62230000000129', 7, 680), ('62230000000130', 8, 15), ('62230000000132', 9, 500),
('62230000000133', 1, 500), ('62230000000134', 10, 500), ('62230000000135', 1, 650),
('62230000000136', 11, 5);

-- 7) BRANCHES
INSERT INTO `bransh` (`ID`, `Adress`, `Name`) VALUES
(1, 'Shoubra, Cairo', 'PharmaPlus Shoubra'),
(2, 'Imbaba, Giza', 'PharmaPlus Imbaba'),
(3, 'Nasr City, Cairo', 'PharmaPlus Nasr City');

-- 8) PERSON
INSERT INTO `person` (`ID`, `Phone`, `name`) VALUES
('10001', '01012345678', 'Ahmed Saleh'),
('10002', '01123456789', 'Mona Nasser'),
('10003', '01299887766', 'Karim Adel'),
('10004', '01055667722', 'Dina Farid'),
('m001',  '01098765432', 'Mohamed Hassan'),
('m1',    '01062959625', 'Ziad Ahmed'),
('p002',  '01176543210', 'Sara Ali'),
('c003',  '01234567890', 'Omar Farouk');

-- 9) CUSTOMER
INSERT INTO `customer` (`points`, `Person_ID`) VALUES
(120.0, '10001'), (60.0, '10002'), (45.0, '10003'), (90.0, '10004');

-- 10) EMPLOYEE
INSERT INTO `employee` (`User_name`, `salary`, `StartDate`, `Password`, `Person_ID`, `bransh_ID`) VALUES
('manager1', 18000.00, '2023-03-01', 'M@12345', 'm001', 1),
('pharma1',  12000.00, '2023-06-15', 'P@12345', 'p002', 1),
('ziad',     20000.00, '2023-06-15', '12345',   'm1',   1),
('cash1',     8000.00, '2024-01-10', 'C@12345', 'c003', 2);

-- 11) INVENTORY (One per branch)
INSERT INTO `inventory` (`ID`, `Bransh_ID`) VALUES (1, 1), (2, 2), (3, 3);

-- 12) SUPPLY BATCHES (Linking batches to specific products)
INSERT INTO `batch` (`Batch_number`, `Product_parcode`, `cost`, `Quantaty`, `expire_date`) VALUES
-- Panadol (123): 120 Total
('BN202501001', '62230000000123', 22.00, 50, '2026-06-30'),
('BN202501014', '62230000000123', 22.00, 40, '2026-03-15'),
('BN202501015', '62230000000123', 21.00, 30, '2026-09-20'),
-- Augmentin (124): 60 Total
('BN202501002', '62230000000124', 95.00, 30, '2026-02-28'),
('BN202501016', '62230000000124', 95.00, 30, '2026-01-31'),
-- Cataflam (125): 90 Total
('BN202501003', '62230000000125', 40.00, 40, '2026-05-31'),
('BN202501017', '62230000000125', 40.00, 50, '2026-02-28'),
-- Brufen (126): 100 Total
('BN202501004', '62230000000126', 45.00, 50, '2026-07-31'),
('BN202501018', '62230000000126', 45.00, 50, '2026-04-15'),
-- Zyrtec (127): 80 Total
('BN202501005', '62230000000127', 30.00, 40, '2026-08-31'),
('BN202501019', '62230000000127', 30.00, 40, '2026-05-20'),
-- Vitamin C (128): 50 Total
('BN202501006', '62230000000128', 60.00, 30, '2026-04-30'),
('BN202501020', '62230000000128', 60.00, 20, '2026-03-10'),
-- Rennie (129): 70 Total
('BN202501007', '62230000000129', 28.00, 70, '2026-03-31'),
-- Vicks (130): 30 Total
('BN202501008', '62230000000130', 55.00, 30, '2026-01-31'),
-- Nivea (131): 100 Total
('BN202501024', '62230000000131', 75.00, 40, '2027-12-31'),
('BN202501025', '62230000000131', 75.00, 60, '2027-06-30'),
-- Sinutab (132): 90 Total
('BN202501011', '62230000000132', 48.00, 90, '2026-10-31'),
-- Panadol C&F (133): 110 Total
('BN202501009', '62230000000133', 33.00, 50, '2026-06-15'),
('BN202501021', '62230000000133', 33.00, 60, '2026-02-20'),
-- Glucophage (134): 85 Total
('BN202501012', '62230000000134', 62.00, 45, '2027-02-28'),
('BN202501022', '62230000000134', 62.00, 40, '2026-08-15'),
-- Adol (135): 95 Total
('BN202501013', '62230000000135', 26.00, 45, '2026-11-30'),
('BN202501023', '62230000000135', 26.00, 50, '2026-07-10'),
-- Aerius (136): 70 Total
('BN202501010', '62230000000136', 50.00, 70, '2027-01-31');


-- 13) INVENTORY_HAS_PRODUCT (CONSOLIDATED TO BRANCH 1 FOR TESTING)
-- ALL products and ALL batch quantities are assigned to Inventory ID 1 (Branch 1)
-- so the manager can see and test everything.
INSERT INTO `bransh_has_product` (`Bransh_ID`, `Product_parcode`) VALUES
(1, '62230000000123'), (1, '62230000000124'), (1, '62230000000125'), (1, '62230000000126'),
(1, '62230000000127'), (1, '62230000000128'), (1, '62230000000129'), (1, '62230000000130'),
(1, '62230000000131'), (1, '62230000000132'), (1, '62230000000133'), (1, '62230000000134'),
(1, '62230000000135'), (1, '62230000000136');

INSERT INTO `inventory_has_product` (`Inventory_ID`, `Product_parcode`, `Quntaty`, `reordr_level`) VALUES
(1, '62230000000123', 120, 30), -- Panadol
(1, '62230000000124',  60, 15), -- Augmentin
(1, '62230000000125',  90, 20), -- Cataflam
(1, '62230000000126', 100, 25), -- Brufen (Was in Inv 2)
(1, '62230000000127',  80, 20), -- Zyrtec (Was in Inv 2)
(1, '62230000000128',  50, 15), -- Vit C (Was in Inv 2)
(1, '62230000000129',  70, 20), -- Rennie (Was in Inv 2)
(1, '62230000000130',  30, 10), -- Vicks (Was in Inv 2)
(1, '62230000000131', 100, 10), -- Nivea (Available in Inv 1)
(1, '62230000000132',  90, 20), -- Sinutab (Was in Inv 3)
(1, '62230000000133', 110, 25), -- Panadol C&F
(1, '62230000000134',  85, 20), -- Glucophage (Was in Inv 3)
(1, '62230000000135',  95, 25), -- Adol (Was in Inv 3)
(1, '62230000000136',  70, 20); -- Aerius


-- 14) SUPPLIERS
INSERT INTO `supplier` (`nane`, `phone`, `adress`) VALUES
('Al-Motaheda Distribution', '01055667788', 'Nasr City, Cairo'),
('PharmaTrade Egypt',         '01166778899', 'Dokki, Giza');

INSERT INTO `supplier_has_product` (`Supplier_nane`, `Supplier_phone`, `Product_parcode`) VALUES
('Al-Motaheda Distribution', '01055667788', '62230000000123'),
('Al-Motaheda Distribution', '01055667788', '62230000000124');

-- 15) INVOICES
INSERT INTO `invoice` (`ID`, `date`, `price`, `employee_User_name`, `employee_Person_ID`, `employee_bransh_ID`) VALUES
(2001, '2025-11-20', 25000.00, 'manager1', 'm001', 1), -- MAIN PURCHASE (High value)
(1001, '2025-11-21',   420.00, 'pharma1',  'p002', 1), -- Sales...
(1002, '2025-11-22',   310.00, 'cash1',    'c003', 2);

-- 16) PURCHASE DETAILS
INSERT INTO `purchase_invoce` (`money_paid`, `remaing_money`, `Invoice_ID`, `Supplier_nane`, `Supplier_phone`) VALUES
(20000.00, 5000.00, 2001, 'Al-Motaheda Distribution', '01055667788');

-- 17) PURCHASE ITEMS
INSERT INTO `invoice_has_product` (`Invoice_ID`, `Product_parcode`, `units`) VALUES
(2001, '62230000000123', 500),
(2001, '62230000000124', 200),
(2001, '62230000000125', 200),
(1001, '62230000000123', 2),
(1001, '62230000000124', 1);

-- 18) SELL INVOICE
INSERT INTO `sell_invoice` (`Discount`, `Invoice_ID`, `Customer_Person_ID`) VALUES
(20.00, 1001, '10001'), (10.00, 1002, '10002');

-- 19) TREASURY
INSERT INTO `treasury` (`treasuryid`, `Bransh_ID`, `amount_of_money`, `date_and_time`, `invoice_ID`) VALUES
('TR-OPEN', 1, 20000.0, NOW(), 2001);
