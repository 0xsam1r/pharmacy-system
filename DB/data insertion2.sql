USE `pms`;

-- =======================================================
-- FIX: RELEASE ANY RESTANT LOCKS
-- =======================================================
UNLOCK TABLES;

-- =======================================================
-- CLEANUP EXISTING DATA
-- =======================================================
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

-- =======================================================
-- INSERT DATA
-- =======================================================

-- 1. Branches
INSERT INTO `bransh` VALUES (1,'Shoubra, Cairo','PharmaPlus Shoubra'),(2,'Imbaba, Giza','PharmaPlus Imbaba'),(3,'Nasr City, Cairo','PharmaPlus Nasr City');

-- 2. Categories
INSERT INTO `category` VALUES (1,'Painkiller'),(2,'Antibiotic'),(3,'Antipyretic'),(4,'Antacid'),(5,'Cough Suppressant'),(6,'Antihistamine'),(7,'Vitamin'),(8,'Skincare'),(9,'Nasal Decongestant'),(10,'Antidiabetic');

-- 3. Products
INSERT INTO `product` VALUES ('62230000000123','Panadol',35,4,1),('62230000000124','Augmentin 625',155,1,2),('62230000000125','Cataflam 50',75,2,1),('62230000000126','Brufen 400',85,2,1),('62230000000127','Zyrtec',60,2,6),('62230000000128','Vitamin C 1000',95,4,7),('62230000000129','Rennie',50,2,4),('62230000000130','Vicks Cough Syrup',90,1,5),('62230000000131','Nivea Soft Cream 100ml',120,1,8),('62230000000132','Sinutab',70,1,9),('62230000000133','Panadol Cold & Flu',55,2,3),('62230000000134','Glucophage 500',95,4,10),('62230000000135','Adol Extra',45,2,1),('62230000000136','Aerius 5mg',110,2,6);

-- 4. Dosage Forms
INSERT INTO `dosage_form` VALUES ('Paracetamol',1),('Amoxicillin + Clavulanic Acid',2),('Diclofenac Potassium',3),('Ibuprofen',4),('Cetirizine',5),('Ascorbic Acid (Vitamin C)',6),('Calcium Carbonate + Magnesium',7),('Dextromethorphan + Guaifenesin',8),('Paracetamol + Pseudoephedrine + Others',9),('Metformin',10),('Desloratadine',11);

-- 5. Sub-types (Medicine, Cosmetic)
INSERT INTO `medicine` VALUES ('62230000000123'),('62230000000124'),('62230000000125'),('62230000000126'),('62230000000127'),('62230000000128'),('62230000000129'),('62230000000130'),('62230000000132'),('62230000000133'),('62230000000134'),('62230000000135'),('62230000000136');
INSERT INTO `cosmetic` VALUES ('Nivea','U','62230000000131');
INSERT INTO `medicine_has_dosage_form` VALUES ('62230000000123',1,500),('62230000000124',2,625),('62230000000125',3,50),('62230000000126',4,400),('62230000000127',5,10),('62230000000128',6,1000),('62230000000129',7,680),('62230000000130',8,15),('62230000000132',9,500),('62230000000133',1,500),('62230000000134',10,500),('62230000000135',1,650),('62230000000136',11,5);

-- 6. People (Persons, Employees, Customers)
INSERT INTO `person` VALUES ('10001','01012345678','Ahmed Saleh'),('10002','01123456789','Mona Nasser'),('10003','01299887766','Karim Adel'),('10004','01055667722','Dina Farid'),('c003','01234567890','Omar Farouk'),('m001','01098765432','Mohamed Hassan'),('m1','01062959625','Ziad Ahmed'),('p002','01176543210','Sara Ali');
INSERT INTO `employee` VALUES ('cash1',8000,'2024-01-10','C@12345','c003',2),('manager1',18000,'2023-03-01','M@12345','m001',1),('pharma1',12000,'2023-06-15','P@12345','p002',1),('ziad',20000,'2023-06-15','12345','m1',1);
INSERT INTO `customer` VALUES (130.5,'10001'),(88,'10002'),(45,'10003'),(102,'10004');

-- 7. Suppliers
INSERT INTO `supplier` VALUES ('Al-Motaheda Distribution','01055667788','Nasr City, Cairo'),('PharmaTrade Egypt','01166778899','Dokki, Giza');
INSERT INTO `supplier_has_product` VALUES ('Al-Motaheda Distribution','01055667788','62230000000123'),('Al-Motaheda Distribution','01055667788','62230000000124');

-- 8. Batches
INSERT INTO `batch` VALUES 
('B-251206-1928',80,'2025-12-31',100,'62230000000136'),
('B-251206-1929',40,'2027-12-02',10,'62230000000127'),
('BN202501001',22,'2026-06-30',50,'62230000000123'),
('BN202501002',95,'2026-02-28',30,'62230000000124'),
('BN202501003',40,'2026-05-31',40,'62230000000125'),
('BN202501004',45,'2026-07-31',50,'62230000000126'),
('BN202501005',30,'2026-08-31',40,'62230000000127'),
('BN202501006',60,'2026-04-30',30,'62230000000128'),
('BN202501007',28,'2026-03-31',70,'62230000000129'),
('BN202501008',55,'2026-01-31',29,'62230000000130'),
('BN202501009',33,'2026-06-15',50,'62230000000133'),
('BN202501010',50,'2027-01-31',70,'62230000000136'),
('BN202501011',48,'2026-10-31',90,'62230000000132'),
('BN202501012',62,'2027-02-28',45,'62230000000134'),
('BN202501013',26,'2026-11-30',45,'62230000000135'),
('BN202501014',22,'2026-03-15',39,'62230000000123'),
('BN202501015',21,'2026-09-20',30,'62230000000123'),
('BN202501016',95,'2026-01-31',29,'62230000000124'),
('BN202501017',40,'2026-02-28',50,'62230000000125'),
('BN202501018',45,'2026-04-15',50,'62230000000126'),
('BN202501019',30,'2026-05-20',39,'62230000000127'),
('BN202501020',60,'2026-03-10',19,'62230000000128'),
('BN202501021',33,'2026-02-20',60,'62230000000133'),
('BN202501022',62,'2026-08-15',39,'62230000000134'),
('BN202501023',26,'2026-07-10',49,'62230000000135'),
('BN202501024',75,'2027-12-31',40,'62230000000131'),
('BN202501025',75,'2027-06-30',59,'62230000000131');

-- 9. Inventory & BranchStock
INSERT INTO `inventory` VALUES (1,1),(2,2),(3,3);
INSERT INTO `bransh_has_product` VALUES (1,'62230000000123'),(1,'62230000000124'),(1,'62230000000125'),(1,'62230000000126'),(1,'62230000000127'),(1,'62230000000128'),(1,'62230000000129'),(1,'62230000000130'),(1,'62230000000131'),(1,'62230000000132'),(1,'62230000000133'),(1,'62230000000134'),(1,'62230000000135'),(1,'62230000000136');
INSERT INTO `inventory_has_product` VALUES (1,'62230000000123',119,30),(1,'62230000000124',59,15),(1,'62230000000125',90,20),(1,'62230000000126',100,25),(1,'62230000000127',89,20),(1,'62230000000128',49,15),(1,'62230000000129',70,20),(1,'62230000000130',29,10),(1,'62230000000131',99,10),(1,'62230000000132',90,20),(1,'62230000000133',110,25),(1,'62230000000134',84,20),(1,'62230000000135',94,25),(1,'62230000000136',170,20);

-- 10. Invoices (Sales & Purchase)
INSERT INTO `invoice` VALUES (1001,'2025-11-21',420,'pharma1','p002',1),(1002,'2025-11-22',310,'cash1','c003',2),(2001,'2025-11-20',25000,'manager1','m001',1),(2002,'2025-12-06',190,'ziad','m1',1),(2003,'2025-12-06',105,'ziad','m1',1),(2004,'2025-12-06',120,'ziad','m1',1),(2005,'2025-12-06',8400,'ziad','m1',1),(2006,'2025-12-06',280,'ziad','m1',1);
INSERT INTO `invoice_has_product` VALUES (1001,'62230000000123',2),(1001,'62230000000124',1),(2001,'62230000000123',500),(2001,'62230000000124',200),(2001,'62230000000125',200),(2002,'62230000000123',4),(2002,'62230000000124',1),(2003,'62230000000127',2),(2003,'62230000000135',2),(2004,'62230000000131',1),(2006,'62230000000128',4),(2006,'62230000000130',1),(2006,'62230000000134',4);

INSERT INTO `purchase_invoce` VALUES (20000,5000,2001,'Al-Motaheda Distribution','01055667788'),(4000,4400,2005,'PharmaTrade Egypt','01166778899');
INSERT INTO `purchase_invoce_has_batch` VALUES (2005,'B-251206-1928','62230000000136','Standard Purchase'),(2005,'B-251206-1929','62230000000127','Standard Purchase');

-- Fix: The original dump had 4 columns (Discount, Invoice_ID, Customer_Person_ID, points_used) 
-- but explicitly or implicitly only provided 4 values, where the schema might expect 3 or 4.
-- The dump had: (20,1001,'10001',0).
-- My previous write failed because I didn't see the column count mismatch.
-- Assuming implicit matching to table schema: (Discount, Invoice_ID, Customer_Person_ID).
-- BUT the dump shows 4 values: 20, 1001, '10001', 0.
-- Let's check schema: sell_invoice usually has Discount, Invoice_ID, Customer_Person_ID.
-- Wait, I added `points_used` in a previous turn (Step 227 - ensureSchemaColumns).
-- But in `DB Creation2.sql` it's NOT there.
-- HOWEVER, since your system is running, that column MIGHT exist if my previous code ran.
-- To be safe given the schema file I see (3 columns), I will remove the 4th value (0) if it's meant for `points_used`.
-- IF the table strictly has 3 columns (Discount, InvoiceID, CustomerID), the dump providing 4 values causes the error.
-- I'll trim it to 3 values to match `DB Creation2.sql`.

INSERT INTO `sell_invoice` (`Discount`, `Invoice_ID`, `Customer_Person_ID`) VALUES (20,1001,'10001'),(10,1002,'10002'),(0,2003,'10001'),(0,2004,'10004'),(0,2006,'10002');

-- 11. Treasury
INSERT INTO `treasury` VALUES ('TR-1765041961160',1,'2025-12-06 19:26:01',190,2002),('TR-1765041993666',1,'2025-12-06 19:26:33',105,2003),('TR-1765042006190',1,'2025-12-06 19:26:46',120,2004),('TR-1765042258736',1,'2025-12-06 19:30:58',280,2006),('TR-OPEN',1,'2025-12-06 19:24:42',20000,2001),('TR-PUR-a86d34dc',1,'2025-12-06 19:29:55',-4000,2005);
