
USE PMS;

-- 1. Person
INSERT INTO Person (ID, Phone, name) VALUES
('P001', '01001234567', 'Ahmed Khaled'),
('P002', '01112345678', 'Menna Al-Sayed'),
('P003', '01222223333', 'Muhammad Ali'),
('P004', '01099887766', 'Sarah Abdullah'),
('P005', '01055443322', 'Mustafa Adel');

-- 2. Employee
INSERT INTO Employee (User_name, salary, StartDate, Password, Person_ID, Person_Phone) VALUES
('ahmedk', 8500, '2023-04-01', 'ahm123', 'P001', '01001234567'),
('menna', 9200, '2023-05-10', 'menna456', 'P002', '01112345678');

-- 3. Customer
INSERT INTO Customer (points, Person_ID, Person_Phone) VALUES
(120.5, 'P003', '01222223333'),
(80.0, 'P004', '01099887766'),
(230.75, 'P005', '01055443322');

-- 4. Bransh
INSERT INTO Bransh (Adress, Name, ID) VALUES
('Revolution Street - Heliopolis', 'Al-Thawra Pharmacy' , 1),
('Faisal Main Street', 'Faisal Pharmacy', 2);

-- 5. Treasury
INSERT INTO Treasury (Bransh_ID) VALUES
(1),
(2);

-- 6. Product
INSERT INTO Product (parcode, Name, Price, Uints, Catagory, Productcol) VALUES
('622300000001', 'Panadol Extra', 25.00, 4, 'painkiller', ''),
('622300000002', 'Augmentin 625mg', 85.00, 1, 'antibiotic', ''),
('622300000003', 'Vitamin C 1000', 50.00, 1, 'vitamin', ''),
('622300000004', 'Nivea Cream', 60.00, 1, 'makeup', ''),
('622300000005', 'Fair &amp; Lovely', 70.00, 1, 'makeup', '');

-- 7. Cosmetic
INSERT INTO Cosmetic (Brand, Gender, Product_parcode) VALUES
('Nivea', 'F', '622300000004'),
('Fair&amp;Lovely', 'F', '622300000005');

-- 8. Medicine
INSERT INTO Medicine (Product_parcode) VALUES
('622300000001'),
('622300000002'),
('622300000003');

-- 9. dosage_form
INSERT INTO dosage_form (strength, active_ing) VALUES
(500, 'Paracetamol'),
(625, 'Amoxicillin + Clavulanic Acid'),
(1000, 'Vitamin C');

-- 10. Medicine_has_dosage_form
INSERT INTO Medicine_has_dosage_form (Medicine_Product_parcode, dosage_form_strength, dosage_form_active_ing) VALUES
('622300000001', 500, 'Paracetamol'),
('622300000002', 625, 'Amoxicillin + Clavulanic Acid'),
('622300000003', 1000, 'Vitamin C');

-- 11. Supplier
INSERT INTO Supplier (nane, phone, adress) VALUES
('Eva Pharma', '0224144556', 'Nasr City - Cairo'),
('Glaxosmithkline', '0222222222', 'Engineers - Giza'),
('Amon Pharma', '0233300011', '10th of Ramadan - Sharqia');

-- 12. Supplier_has_Product
INSERT INTO Supplier_has_Product (Supplier_nane, Supplier_phone, Product_parcode) VALUES
('Eva Pharma', '0224144556', '622300000001'),
('Glaxosmithkline', '0222222222', '622300000002'),
('Amon Pharma', '0233300011', '622300000003');

-- 13. Batch
INSERT INTO Batch (Batch_number, cost, expire_date, Quantaty, Product_parcode) VALUES
('B001', 12.5, '2026-01-01', 8, '622300000001'),
('B002', 45.0, '2025-12-01', 50, '622300000002'),
('B003', 22.0, '2026-06-01', 200, '622300000003');

-- 14. Inventory
INSERT INTO Inventory (Bransh_ID, ID) VALUES
(1 , 1),
(2 , 2);

-- 15. Inventory_has_Product
INSERT INTO Inventory_has_Product (Inventory_ID, Product_parcode, Quntaty, reordr_level) VALUES
(1, '622300000001', 50, 10),
(1, '622300000002', 30, 5),
(2, '622300000003', 100, 20);

-- 16. Invoice
INSERT INTO Invoice (ID, date, price, Treasury_Bransh_ID) VALUES
(1, '2025-11-05', 120.00, 1),
(2, '2025-11-05', 250.00, 1),
(3, '2025-11-06', 180.00, 2);

-- 17. Sell_invoice
INSERT INTO Sell_invoice (Discount, Invoice_ID, Customer_Person_ID, Customer_Person_Phone) VALUES
(10.0, 1, 'P003', '01222223333'),
(15.0, 2, 'P004', '01099887766');

-- 18. purchase_invoce
INSERT INTO purchase_invoce (money_paid, remaing_money, Invoice_ID, Supplier_nane, Supplier_phone) VALUES
(400.00, 100.00, 3, 'Eva Pharma', '0224144556');

-- 19. Employee_has_Invoice
INSERT INTO Employee_has_Invoice (Employee_User_name, Employee_Person_ID, Employee_Person_Phone, Invoice_ID) VALUES
('ahmedk', 'P001', '01001234567', 1),
('menna', 'P002', '01112345678', 2);

-- 20. Invoice_has_Product
INSERT INTO Invoice_has_Product (Invoice_ID, Product_parcode) VALUES
(1, '622300000001'),
(2, '622300000004'),
(3, '622300000002');

-- 21. purchase_invoce_has_Batch
INSERT INTO purchase_invoce_has_Batch (purchase_invoce_Invoice_ID, Batch_Batch_number, Batch_Product_parcode, purchase_invoce_has_Batchcol) VALUES
(3, 'B001', '622300000001', 'batch-linked');



-- New Dataset
-- 1. Person
INSERT INTO Person (ID, Phone, name) VALUES
('P006', '01011112222', 'Yasmine Hassan'),
('P007', '01133334444', 'Omar Tarek'),
('P008', '01255556666', 'Laila Mahmoud'),
('P009', '01077778888', 'Khaled Mostafa'),
('P010', '01099990000', 'Nour El-Din');

-- 2. Employee
INSERT INTO Employee (User_name, salary, StartDate, Password, Person_ID, Person_Phone) VALUES
('yasmineh', 8700, '2023-06-15', 'yas789', 'P006', '01011112222'),
('omart', 9100, '2023-07-20', 'omar321', 'P007', '01133334444');

-- 3. Customer
INSERT INTO Customer (points, Person_ID, Person_Phone) VALUES
(150.0, 'P008', '01255556666'),
(95.5, 'P009', '01077778888'),
(210.25, 'P010', '01099990000');

-- 4. Bransh
INSERT INTO Bransh (Adress, Name, ID) VALUES
('Nasr Street - Nasr City', 'Nasr Pharmacy', 3),
('Tanta Main Road', 'Tanta Pharmacy', 4);

-- 5. Treasury
INSERT INTO Treasury (Bransh_ID) VALUES
(3),
(4);

-- 6. Product
INSERT INTO Product (parcode, Name, Price, Uints, Catagory, Productcol) VALUES
('622300000006', 'Adol 500mg', 20.00, 2, 'painkiller', ''),
('622300000007', 'Zithromax 500mg', 90.00, 1, 'antibiotic', ''),
('622300000008', 'Vitamin D3', 45.00, 1, 'vitamin', ''),
('622300000009', 'L’Oreal Cream', 75.00, 1, 'makeup', ''),
('622300000010', 'Garnier Light', 65.00, 1, 'makeup', '');

-- 7. Cosmetic
INSERT INTO Cosmetic (Brand, Gender, Product_parcode) VALUES
('L’Oreal', 'F', '622300000009'),
('Garnier', 'F', '622300000010');

-- 8. Medicine
INSERT INTO Medicine (Product_parcode) VALUES
('622300000006'),
('622300000007'),
('622300000008');

-- 9. dosage_form
INSERT INTO dosage_form (strength, active_ing) VALUES
(500, 'Ibuprofen'),
(500, 'Azithromycin'),
(1000, 'Vitamin D3');

-- 10. Medicine_has_dosage_form
INSERT INTO Medicine_has_dosage_form (Medicine_Product_parcode, dosage_form_strength, dosage_form_active_ing) VALUES
('622300000006', 500, 'Ibuprofen'),
('622300000007', 500, 'Azithromycin'),
('622300000008', 1000, 'Vitamin D3');

-- 11. Supplier
INSERT INTO Supplier (nane, phone, adress) VALUES
('Pharco', '0225555666', 'Maadi - Cairo'),
('Pfizer', '0226666777', 'Dokki - Giza'),
('Hikma Pharma', '0233344556', '6th October - Giza');

-- 12. Supplier_has_Product
INSERT INTO Supplier_has_Product (Supplier_nane, Supplier_phone, Product_parcode) VALUES
('Pharco', '0225555666', '622300000006'),
('Pfizer', '0226666777', '622300000007'),
('Hikma Pharma', '0233344556', '622300000008');

-- 13. Batch
INSERT INTO Batch (Batch_number, cost, expire_date, Quantaty, Product_parcode) VALUES
('B004', 10.0, '2026-02-01', 120, '622300000006'),
('B005', 50.0, '2025-11-30', 60, '622300000007'),
('B006', 18.0, '2026-07-01', 150, '622300000008');

-- 14. Inventory
INSERT INTO Inventory (Bransh_ID, ID) VALUES
(3 , 3),
(4 , 4);

-- 15. Inventory_has_Product
INSERT INTO Inventory_has_Product (Inventory_ID, Product_parcode, Quntaty, reordr_level) VALUES
(3, '622300000006', 40, 8),
(3, '622300000007', 25, 5),
(4, '622300000008', 90, 15);

-- 16. Invoice
INSERT INTO Invoice (ID, date, price, Treasury_Bransh_ID) VALUES
(4, '2025-11-07', 100.00, 3),
(5, '2025-11-08', 220.00, 3),
(6, '2025-11-09', 160.00, 4);

-- 17. Sell_invoice
INSERT INTO Sell_invoice (Discount, Invoice_ID, Customer_Person_ID, Customer_Person_Phone) VALUES
(5.0, 4, 'P008', '01255556666'),
(20.0, 5, 'P009', '01077778888');

-- 18. purchase_invoce
INSERT INTO purchase_invoce (money_paid, remaing_money, Invoice_ID, Supplier_nane, Supplier_phone) VALUES
(350.00, 50.00, 6, 'Pharco', '0225555666');

-- 19. Employee_has_Invoice
INSERT INTO Employee_has_Invoice (Employee_User_name, Employee_Person_ID, Employee_Person_Phone, Invoice_ID) VALUES
('yasmineh', 'P006', '01011112222', 4),
('omart', 'P007', '01133334444', 5);

-- 20. Invoice_has_Product
INSERT INTO Invoice_has_Product (Invoice_ID, Product_parcode) VALUES
(4, '622300000006'),
(5, '622300000009'),
(6, '622300000007');

-- 21. purchase_invoce_has_Batch
INSERT INTO purchase_invoce_has_Batch (purchase_invoce_Invoice_ID, Batch_Batch_number, Batch_Product_parcode, purchase_invoce_has_Batchcol) VALUES
(6, 'B004', '622300000006', 'batch-linked');
