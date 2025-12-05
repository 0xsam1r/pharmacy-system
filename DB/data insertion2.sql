
USE `pms`;

-- 1) CATEGORY (drug types)
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

-- 2) PRODUCT (medicines & cosmetics common in Egypt)
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

-- 3) MEDICINE (subset of products)
INSERT INTO `medicine` (`Product_parcode`) VALUES
('62230000000123'),
('62230000000124'),
('62230000000125'),
('62230000000126'),
('62230000000127'),
('62230000000128'),
('62230000000129'),
('62230000000130'),
('62230000000132'),
('62230000000133'),
('62230000000134'),
('62230000000135'),
('62230000000136');

-- 4) COSMETIC
INSERT INTO `cosmetic` (`Brand`, `Gender`, `Product_parcode`) VALUES
('Nivea', 'U', '62230000000131'); -- U = Unisex

-- 5) DOSAGE_FORM (active ingredients)
INSERT INTO `dosage_form` (`active_ing`, `ID`) VALUES
('Paracetamol',                            1),
('Amoxicillin + Clavulanic Acid',          2),
('Diclofenac Potassium',                   3),
('Ibuprofen',                              4),
('Cetirizine',                             5),
('Ascorbic Acid (Vitamin C)',              6),
('Calcium Carbonate + Magnesium',          7),
('Dextromethorphan + Guaifenesin',         8),
('Paracetamol + Pseudoephedrine + Others', 9),
('Metformin',                              10),
('Desloratadine',                          11);

-- 6) MEDICINE_HAS_DOSAGE_FORM (strengths)
INSERT INTO `medicine_has_dosage_form` (`medicine_Product_parcode`, `dosage_form_ID`, `Strength`) VALUES
('62230000000123', 1, 500),
('62230000000124', 2, 625),
('62230000000125', 3, 50),
('62230000000126', 4, 400),
('62230000000127', 5, 10),
('62230000000128', 6, 1000),
('62230000000129', 7, 680),
('62230000000130', 8, 15),
('62230000000132', 9, 500),
('62230000000133', 1, 500),
('62230000000134', 10, 500),
('62230000000135', 1, 650),
('62230000000136', 11, 5);

-- 7) BRANSH (branches)
INSERT INTO `bransh` (`ID`, `Adress`, `Name`) VALUES
(1, 'Shoubra, Cairo',      'PharmaPlus Shoubra'),
(2, 'Imbaba, Giza',        'PharmaPlus Imbaba'),
(3, 'Nasr City, Cairo',    'PharmaPlus Nasr City');

-- 8) PERSON (customers & employees per your ID rules)
INSERT INTO `person` (`ID`, `Phone`, `name`) VALUES
-- Customers (IDs are digits only)
('10001', '01012345678', 'Ahmed Saleh'),
('10002', '01123456789', 'Mona Nasser'),
('10003', '01299887766', 'Karim Adel'),
('10004', '01055667722', 'Dina Farid'),
-- Employees (ID starts with role letter)
('m001',  '01098765432', 'Mohamed Hassan'),  -- Manager
('p002',  '01176543210', 'Sara Ali'),        -- Pharmacist
('c003',  '01234567890', 'Omar Farouk'),     -- Cashier
('a004',  '01033445566', 'Laila Hussein'),   -- Accountant
('s005',  '01122334455', 'Mostafa Gamal');   -- Storekeeper

-- 9) CUSTOMER
INSERT INTO `customer` (`points`, `Person_ID`) VALUES
(120.0, '10001'),
(60.0,  '10002'),
(45.0,  '10003'),
(90.0,  '10004');

-- 10) EMPLOYEE (linked to branches)
INSERT INTO `employee` (`User_name`, `salary`, `StartDate`, `Password`, `Person_ID`, `bransh_ID`) VALUES
('manager1',  18000.00, '2023-03-01', 'M@12345', 'm001', 1),
('pharma1',   12000.00, '2023-06-15', 'P@12345', 'p002', 1),
('cash1',      8000.00, '2024-01-10', 'C@12345', 'c003', 2),
('acct1',     10000.00, '2024-02-01', 'A@12345', 'a004', 3),
('store1',     9000.00, '2024-03-12', 'S@12345', 's005', 3);

-- 11) INVENTORY (one per branch)
INSERT INTO `inventory` (`ID`, `Bransh_ID`) VALUES
(1, 1),
(2, 2),
(3, 3);

-- 12) BRANSH_HAS_PRODUCT (product availability per branch)
INSERT INTO `bransh_has_product` (`Bransh_ID`, `Product_parcode`) VALUES
(1, '62230000000123'), (1, '62230000000124'), (1, '62230000000125'), (1, '62230000000131'),
(1, '62230000000133'), (1, '62230000000136'),
(2, '62230000000126'), (2, '62230000000127'), (2, '62230000000128'), (2, '62230000000129'), (2, '62230000000130'),
(3, '62230000000132'), (3, '62230000000134'), (3, '62230000000135'), (3, '62230000000131');

-- 13) INVENTORY_HAS_PRODUCT (stock & reorder levels)
INSERT INTO `inventory_has_product` (`Inventory_ID`, `Product_parcode`, `Quntaty`, `reordr_level`) VALUES
(1, '62230000000123', 120, 30),
(1, '62230000000124',  60, 15),
(1, '62230000000125',  90, 20),
(1, '62230000000131',  40, 10),
(1, '62230000000133', 110, 25),
(1, '62230000000136',  70, 20),
(2, '62230000000126', 100, 25),
(2, '62230000000127',  80, 20),
(2, '62230000000128',  50, 15),
(2, '62230000000129',  70, 20),
(2, '62230000000130',  30, 10),
(3, '62230000000132',  90, 20),
(3, '62230000000134',  85, 20),
(3, '62230000000135',  95, 25),
(3, '62230000000131',  60, 15);

-- 14) BATCH (supply batches with expiry)
INSERT INTO `batch` (`Batch_number`, `cost`, `expire_date`, `Quantaty`, `Product_parcode`) VALUES
('BN202501001', 22.00, '2026-06-30', 200, '62230000000123'),
('BN202501002', 95.00, '2026-02-28', 120, '62230000000124'),
('BN202501003', 40.00, '2026-05-31', 150, '62230000000125'),
('BN202501004', 45.00, '2026-07-31', 120, '62230000000126'),
('BN202501005', 30.00, '2026-08-31', 140, '62230000000127'),
('BN202501006', 60.00, '2026-04-30', 110, '62230000000128'),
('BN202501007', 28.00, '2026-03-31', 160, '62230000000129'),
('BN202501008', 55.00, '2026-01-31',  90, '62230000000130'),
('BN202501009', 33.00, '2026-06-15', 130, '62230000000133'),
('BN202501010', 50.00, '2027-01-31', 100, '62230000000136'),
('BN202501011', 48.00, '2026-10-31', 120, '62230000000132'),
('BN202501012', 62.00, '2027-02-28', 100, '62230000000134'),
('BN202501013', 26.00, '2026-11-30', 140, '62230000000135');

-- 15) SUPPLIER (generic realistic names)
INSERT INTO `supplier` (`nane`, `phone`, `adress`) VALUES
('Al-Motaheda Distribution', '01055667788', 'Nasr City, Cairo'),
('PharmaTrade Egypt',         '01166778899', 'Dokki, Giza'),
('United Pharma Supply',      '01277889900', 'Maadi, Cairo');

-- 16) SUPPLIER_HAS_PRODUCT (supplier-product links)
INSERT INTO `supplier_has_product` (`Supplier_nane`, `Supplier_phone`, `Product_parcode`) VALUES
('Al-Motaheda Distribution', '01055667788', '62230000000123'),
('Al-Motaheda Distribution', '01055667788', '62230000000124'),
('PharmaTrade Egypt',         '01166778899', '62230000000125'),
('PharmaTrade Egypt',         '01166778899', '62230000000126'),
('PharmaTrade Egypt',         '01166778899', '62230000000127'),
('Al-Motaheda Distribution', '01055667788', '62230000000128'),
('United Pharma Supply',      '01277889900', '62230000000129'),
('Al-Motaheda Distribution', '01055667788', '62230000000130'),
('PharmaTrade Egypt',         '01166778899', '62230000000131'),
('United Pharma Supply',      '01277889900', '62230000000133'),
('United Pharma Supply',      '01277889900', '62230000000134'),
('PharmaTrade Egypt',         '01166778899', '62230000000135'),
('Al-Motaheda Distribution', '01055667788', '62230000000136');

-- 17) INVOICE (linked to employees)
INSERT INTO `invoice` (`ID`, `date`, `price`, `employee_User_name`, `employee_Person_ID`, `employee_bransh_ID`) VALUES
(2001, '2025-11-20', 15000.00, 'manager1', 'm001', 1),  -- Purchase base
(2002, '2025-11-23', 18000.00, 'acct1',    'a004', 3),  -- Purchase base
(1001, '2025-11-21',   420.00, 'pharma1',  'p002', 1),  -- Sale Shoubra
(1002, '2025-11-22',   310.00, 'cash1',    'c003', 2),  -- Sale Imbaba
(1003, '2025-11-24',   375.00, 'pharma1',  'p002', 1),  -- Sale Shoubra
(1004, '2025-11-25',   520.00, 'store1',   's005', 3);  -- Sale Nasr City

-- 18) PURCHASE_INVOCE (purchase details)
INSERT INTO `purchase_invoce` (`money_paid`, `remaing_money`, `Invoice_ID`, `Supplier_nane`, `Supplier_phone`) VALUES
(12000.00, 3000.00, 2001, 'Al-Motaheda Distribution', '01055667788'),
(15000.00, 1000.00, 2002, 'United Pharma Supply',      '01277889900');

-- 19) PURCHASE_INVOCE_HAS_BATCH (link batches to purchases)
INSERT INTO `purchase_invoce_has_batch` (`purchase_invoce_Invoice_ID`, `Batch_Batch_number`, `Batch_Product_parcode`, `purchase_invoce_has_Batchcol`) VALUES
(2001, 'BN202501001', '62230000000123', 'Initial load'),
(2001, 'BN202501002', '62230000000124', 'Initial load'),
(2002, 'BN202501009', '62230000000133', 'Restock cold&flu'),
(2002, 'BN202501012', '62230000000134', 'Restock antidiabetic');

-- 20) SELL_INVOICE (discounts & customer links)
INSERT INTO `sell_invoice` (`Discount`, `Invoice_ID`, `Customer_Person_ID`) VALUES
(20.00, 1001, '10001'),
(10.00, 1002, '10002'),
(15.00, 1003, '10003'),
(25.00, 1004, '10004');

-- 21) INVOICE_HAS_PRODUCT (line items)
INSERT INTO `invoice_has_product` (`Invoice_ID`, `Product_parcode`, `units`) VALUES
-- Sales
(1001, '62230000000123', 2),
(1001, '62230000000124', 1),
(1001, '62230000000131', 1),
(1002, '62230000000126', 2),
(1002, '62230000000127', 1),
(1002, '62230000000129', 1),
(1003, '62230000000133', 2),
(1003, '62230000000125', 1),
(1003, '62230000000136', 1),
(1004, '62230000000132', 1),
(1004, '62230000000134', 1),
(1004, '62230000000135', 2),
-- Purchases (stock in)
(2001, '62230000000123', 200),
(2001, '62230000000124', 120),
(2002, '62230000000133', 130),
(2002, '62230000000134', 100);

-- 22) CUSTOMER_BUY_PRODUCT (customer purchase history)
INSERT INTO `customer_buy_product` (`Customer_Person_ID`, `Product_parcode`, `Quantaty`) VALUES
('10001', '62230000000123', 2),
('10001', '62230000000124', 1),
('10001', '62230000000131', 1),
('10002', '62230000000126', 2),
('10002', '62230000000127', 1),
('10002', '62230000000129', 1),
('10003', '62230000000133', 2),
('10003', '62230000000125', 1),
('10003', '62230000000136', 1),
('10004', '62230000000132', 1),
('10004', '62230000000134', 1),
('10004', '62230000000135', 2);

-- 23) TREASURY (cash movements)
INSERT INTO `treasury` (`treasuryid`, `Bransh_ID`, `date_and_time`, `amount_of_money`, `invoice_ID`) VALUES
('TRS-2025-11-21-01', 1, '2025-11-21 12:15:00',   400.00, 1001),
('TRS-2025-11-22-01', 2, '2025-11-22 18:30:00',   300.00, 1002),
('TRS-2025-11-24-01', 1, '2025-11-24 11:10:00',   360.00, 1003),
('TRS-2025-11-25-01', 3, '2025-11-25 16:40:00',   500.00, 1004),
('TRS-2025-11-20-01', 1, '2025-11-20 10:05:00', -12000.00, 2001),
('TRS-2025-11-23-01', 3, '2025-11-23 09:30:00', -15000.00, 2002);

START TRANSACTION;

-- ====== ضبط تواريخ الفواتير لشهر نوفمبر 2025 ======
-- فواتير الشراء (IDs: 2001, 2002)
UPDATE `invoice`
SET `date` = '2025-11-20'
WHERE `ID` = 2001;

UPDATE `invoice`
SET `date` = '2025-11-23'
WHERE `ID` = 2002;

-- فواتير البيع (IDs: 1001..1004)
UPDATE `invoice`
SET `date` = '2025-11-21'
WHERE `ID` = 1001;

UPDATE `invoice`
SET `date` = '2025-11-22'
WHERE `ID` = 1002;

UPDATE `invoice`
SET `date` = '2025-11-24'
WHERE `ID` = 1003;

UPDATE `invoice`
SET `date` = '2025-11-25'
WHERE `ID` = 1004;

-- ====== اختيارية: ضبط حركة الخزنة على نفس أيام الفواتير ======
UPDATE `treasury`
SET `date_and_time` = '2025-11-21 12:15:00'
WHERE `invoice_ID` = 1001;

UPDATE `treasury`
SET `date_and_time` = '2025-11-22 18:30:00'
WHERE `invoice_ID` = 1002;

UPDATE `treasury`
SET `date_and_time` = '2025-11-24 11:10:00'
WHERE `invoice_ID` = 1003;

UPDATE `treasury`
SET `date_and_time` = '2025-11-25 16:40:00'
WHERE `invoice_ID` = 1004;

UPDATE `treasury`
SET `date_and_time` = '2025-11-20 10:05:00'
WHERE `invoice_ID` = 2001;

UPDATE `treasury`
SET `date_and_time` = '2025-11-23 09:30:00'
WHERE `invoice_ID` = 2002;

COMMIT;
