-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';


-- -----------------------------------------------------
-- Schema pms
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema pms
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `pms` DEFAULT CHARACTER SET utf8mb4 ;
USE `pms` ;

-- -----------------------------------------------------
-- Table `pms`.`category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`category` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(35) NOT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `pms`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`product` (
  `parcode` VARCHAR(14) NOT NULL,
  `Name` VARCHAR(40) NOT NULL,
  `Price` FLOAT NOT NULL,
  `Uints` INT(11) NOT NULL,
  `Category_ID` INT(11) NOT NULL,
  PRIMARY KEY (`parcode`, `Category_ID`),
  INDEX `fk_product_Category1_idx` (`Category_ID` ASC) ,
  CONSTRAINT `fk_product_Category1`
    FOREIGN KEY (`Category_ID`)
    REFERENCES `pms`.`category` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`batch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`batch` (
  `Batch_number` VARCHAR(14) NOT NULL,
  `cost` FLOAT NOT NULL,
  `expire_date` DATE NOT NULL,
  `Quantaty` DOUBLE NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  PRIMARY KEY (`Batch_number`, `Product_parcode`),
  INDEX `fk_Batch_Product1` (`Product_parcode` ASC) ,
  CONSTRAINT `fk_Batch_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `pms`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`bransh`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`bransh` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `Adress` VARCHAR(45) NOT NULL,
  `Name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`bransh_has_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`bransh_has_product` (
  `Bransh_ID` INT(11) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  PRIMARY KEY (`Bransh_ID`, `Product_parcode`),
  INDEX `fk_Bransh_has_Product_Bransh1_idx` (`Bransh_ID` ASC) ,
  INDEX `fk_Bransh_has_Product_Product1` (`Product_parcode` ASC) ,
  CONSTRAINT `fk_Bransh_has_Product_Bransh1`
    FOREIGN KEY (`Bransh_ID`)
    REFERENCES `pms`.`bransh` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Bransh_has_Product_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `pms`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`cosmetic`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`cosmetic` (
  `Brand` VARCHAR(20) NOT NULL,
  `Gender` CHAR(1) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  PRIMARY KEY (`Product_parcode`),
  CONSTRAINT `fk_Cosmetic_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `pms`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`person`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`person` (
  `ID` VARCHAR(5) NOT NULL,
  `Phone` VARCHAR(11) NOT NULL,
  `name` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE INDEX `Phone_UNIQUE` (`Phone` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`customer` (
  `points` FLOAT NOT NULL,
  `Person_ID` VARCHAR(5) NOT NULL,
  PRIMARY KEY (`Person_ID`),
  CONSTRAINT `fk_Customer_Person1`
    FOREIGN KEY (`Person_ID`)
    REFERENCES `pms`.`person` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`customer_buy_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`customer_buy_product` (
  `Customer_Person_ID` VARCHAR(5) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  `Quantaty` FLOAT NOT NULL,
  PRIMARY KEY (`Customer_Person_ID`, `Product_parcode`),
  INDEX `fk_Customer_has_Product_Customer1_idx` (`Customer_Person_ID` ASC) ,
  INDEX `fk_Customer_has_Product_Product1` (`Product_parcode` ASC) ,
  CONSTRAINT `fk_Customer_has_Product_Customer1`
    FOREIGN KEY (`Customer_Person_ID`)
    REFERENCES `pms`.`customer` (`Person_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Customer_has_Product_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `pms`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`dosage_form`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`dosage_form` (
  `active_ing` VARCHAR(45) NOT NULL,
  `ID` INT(11) NOT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`employee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`employee` (
  `User_name` VARCHAR(10) NOT NULL,
  `salary` FLOAT NOT NULL,
  `StartDate` DATE NOT NULL,
  `Password` VARCHAR(20) NOT NULL,
  `Person_ID` VARCHAR(5) NOT NULL,
  `bransh_ID` INT(11) NOT NULL,
  PRIMARY KEY (`User_name`, `Person_ID`, `bransh_ID`),
  INDEX `fk_Employee_Person` (`Person_ID` ASC) ,
  INDEX `fk_employee_bransh1` (`bransh_ID` ASC) ,
  CONSTRAINT `fk_Employee_Person`
    FOREIGN KEY (`Person_ID`)
    REFERENCES `pms`.`person` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_employee_bransh1`
    FOREIGN KEY (`bransh_ID`)
    REFERENCES `pms`.`bransh` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`inventory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`inventory` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `Bransh_ID` INT(11) NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `fk_Inventory_Bransh1` (`Bransh_ID` ASC) ,
  CONSTRAINT `fk_Inventory_Bransh1`
    FOREIGN KEY (`Bransh_ID`)
    REFERENCES `pms`.`bransh` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 51
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`inventory_has_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`inventory_has_product` (
  `Inventory_ID` INT(11) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  `Quntaty` FLOAT NOT NULL,
  `reordr_level` INT(11) NOT NULL,
  PRIMARY KEY (`Inventory_ID`, `Product_parcode`),
  INDEX `fk_Inventory_has_Product_Inventory1_idx` (`Inventory_ID` ASC) ,
  INDEX `fk_Inventory_has_Product_Product1` (`Product_parcode` ASC) ,
  CONSTRAINT `fk_Inventory_has_Product_Inventory1`
    FOREIGN KEY (`Inventory_ID`)
    REFERENCES `pms`.`inventory` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Inventory_has_Product_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `pms`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`invoice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`invoice` (
  `ID` INT(11) NOT NULL,
  `date` DATE NOT NULL,
  `price` FLOAT NOT NULL,
  `employee_User_name` VARCHAR(10) NOT NULL,
  `employee_Person_ID` VARCHAR(5) NOT NULL,
  `employee_bransh_ID` INT(11) NOT NULL,
  PRIMARY KEY (`ID`, `employee_User_name`, `employee_Person_ID`, `employee_bransh_ID`),
  INDEX `fk_invoice_employee1` (`employee_User_name` ASC, `employee_Person_ID` ASC, `employee_bransh_ID` ASC) ,
  CONSTRAINT `fk_invoice_employee1`
    FOREIGN KEY (`employee_User_name` , `employee_Person_ID` , `employee_bransh_ID`)
    REFERENCES `pms`.`employee` (`User_name` , `Person_ID` , `bransh_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`invoice_has_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`invoice_has_product` (
  `Invoice_ID` INT(11) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  `units` DOUBLE NOT NULL,
  PRIMARY KEY (`Invoice_ID`, `Product_parcode`),
  INDEX `fk_Invoice_has_Product_Invoice1_idx` (`Invoice_ID` ASC) ,
  INDEX `fk_Invoice_has_Product_Product1` (`Product_parcode` ASC) ,
  CONSTRAINT `fk_Invoice_has_Product_Invoice1`
    FOREIGN KEY (`Invoice_ID`)
    REFERENCES `pms`.`invoice` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Invoice_has_Product_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `pms`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`medicine`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`medicine` (
  `Product_parcode` VARCHAR(14) NOT NULL,
  PRIMARY KEY (`Product_parcode`),
  CONSTRAINT `fk_Medicine_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `pms`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`supplier`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`supplier` (
  `nane` VARCHAR(45) NOT NULL,
  `phone` VARCHAR(11) NOT NULL,
  `adress` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`nane`, `phone`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`purchase_invoce`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`purchase_invoce` (
  `money_paid` FLOAT NOT NULL,
  `remaing_money` FLOAT NULL DEFAULT NULL,
  `Invoice_ID` INT(11) NOT NULL,
  `Supplier_nane` VARCHAR(45) NOT NULL,
  `Supplier_phone` VARCHAR(11) NOT NULL,
  PRIMARY KEY (`Invoice_ID`),
  INDEX `fk_purchase invoce_Supplier1` (`Supplier_nane` ASC, `Supplier_phone` ASC) ,
  CONSTRAINT `fk_purchase invoce_Invoice1`
    FOREIGN KEY (`Invoice_ID`)
    REFERENCES `pms`.`invoice` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_purchase invoce_Supplier1`
    FOREIGN KEY (`Supplier_nane` , `Supplier_phone`)
    REFERENCES `pms`.`supplier` (`nane` , `phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`purchase_invoce_has_batch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`purchase_invoce_has_batch` (
  `purchase_invoce_Invoice_ID` INT(11) NOT NULL,
  `Batch_Batch_number` VARCHAR(14) NOT NULL,
  `Batch_Product_parcode` VARCHAR(14) NOT NULL,
  `purchase_invoce_has_Batchcol` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`purchase_invoce_Invoice_ID`, `Batch_Batch_number`, `Batch_Product_parcode`),
  INDEX `fk_purchase invoce_has_Batch_purchase invoce1_idx` (`purchase_invoce_Invoice_ID` ASC) ,
  INDEX `fk_purchase invoce_has_Batch_Batch1` (`Batch_Batch_number` ASC, `Batch_Product_parcode` ASC) ,
  CONSTRAINT `fk_purchase invoce_has_Batch_Batch1`
    FOREIGN KEY (`Batch_Batch_number` , `Batch_Product_parcode`)
    REFERENCES `pms`.`batch` (`Batch_number` , `Product_parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_purchase invoce_has_Batch_purchase invoce1`
    FOREIGN KEY (`purchase_invoce_Invoice_ID`)
    REFERENCES `pms`.`purchase_invoce` (`Invoice_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`sell_invoice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`sell_invoice` (
  `Discount` FLOAT NOT NULL,
  `Invoice_ID` INT(11) NOT NULL,
  `Customer_Person_ID` VARCHAR(5) NOT NULL,
  PRIMARY KEY (`Invoice_ID`),
  INDEX `fk_Sell invoice_Customer1` (`Customer_Person_ID` ASC) ,
  CONSTRAINT `fk_Sell invoice_Customer1`
    FOREIGN KEY (`Customer_Person_ID`)
    REFERENCES `pms`.`customer` (`Person_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Sell invoice_Invoice1`
    FOREIGN KEY (`Invoice_ID`)
    REFERENCES `pms`.`invoice` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`supplier_has_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`supplier_has_product` (
  `Supplier_nane` VARCHAR(45) NOT NULL,
  `Supplier_phone` VARCHAR(11) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  PRIMARY KEY (`Supplier_nane`, `Supplier_phone`, `Product_parcode`),
  INDEX `fk_Supplier_has_Product_Supplier1_idx` (`Supplier_nane` ASC, `Supplier_phone` ASC) ,
  INDEX `fk_Supplier_has_Product_Product1` (`Product_parcode` ASC) ,
  CONSTRAINT `fk_Supplier_has_Product_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `pms`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Supplier_has_Product_Supplier1`
    FOREIGN KEY (`Supplier_nane` , `Supplier_phone`)
    REFERENCES `pms`.`supplier` (`nane` , `phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`treasury`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`treasury` (
  `treasuryid` VARCHAR(45) NOT NULL,
  `Bransh_ID` INT(11) NOT NULL,
  `date_and_time` DATETIME NOT NULL,
  `amount_of_money` DOUBLE NOT NULL,
  `invoice_ID` INT(11) NOT NULL,
  PRIMARY KEY (`treasuryid`),
  INDEX `fk_Treasury_Bransh1` (`Bransh_ID` ASC) ,
  INDEX `fk_treasury_invoice1` (`invoice_ID` ASC) ,
  CONSTRAINT `fk_Treasury_Bransh1`
    FOREIGN KEY (`Bransh_ID`)
    REFERENCES `pms`.`bransh` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_treasury_invoice1`
    FOREIGN KEY (`invoice_ID`)
    REFERENCES `pms`.`invoice` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pms`.`medicine_has_dosage_form`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pms`.`medicine_has_dosage_form` (
  `medicine_Product_parcode` VARCHAR(14) NOT NULL,
  `dosage_form_ID` INT(11) NOT NULL,
  `Strength` DOUBLE NOT NULL,
  PRIMARY KEY (`medicine_Product_parcode`, `dosage_form_ID`),
  INDEX `fk_medicine_has_dosage_form_dosage_form1_idx` (`dosage_form_ID` ASC) ,
  INDEX `fk_medicine_has_dosage_form_medicine1_idx` (`medicine_Product_parcode` ASC) ,
  CONSTRAINT `fk_medicine_has_dosage_form_medicine1`
    FOREIGN KEY (`medicine_Product_parcode`)
    REFERENCES `pms`.`medicine` (`Product_parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_medicine_has_dosage_form_dosage_form1`
    FOREIGN KEY (`dosage_form_ID`)
    REFERENCES `pms`.`dosage_form` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
