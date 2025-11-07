-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema PMS
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema PMS
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `PMS` DEFAULT CHARACTER SET utf8 ;
USE `PMS` ;

-- -----------------------------------------------------
-- Table `PMS`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`product` (
  `parcode` VARCHAR(14) NOT NULL,
  `Name` VARCHAR(20) NOT NULL,
  `Price` FLOAT NOT NULL,
  `Uints` INT(11) NOT NULL,
  `Catagory` VARCHAR(20) NOT NULL,
  `Productcol` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`parcode`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`batch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`batch` (
  `Batch_number` VARCHAR(14) NOT NULL,
  `cost` FLOAT NOT NULL,
  `expire_date` DATE NOT NULL,
  `Quantaty` INT(11) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  PRIMARY KEY (`Batch_number`, `Product_parcode`),
  INDEX `fk_Batch_Product1_idx` (`Product_parcode` ASC) ,
  CONSTRAINT `fk_Batch_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `PMS`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`bransh`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`bransh` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `Adress` VARCHAR(45) NOT NULL,
  `Name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`bransh_has_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`bransh_has_product` (
  `Bransh_ID` INT(11) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  PRIMARY KEY (`Bransh_ID`, `Product_parcode`),
  INDEX `fk_Bransh_has_Product_Product1_idx` (`Product_parcode` ASC) ,
  INDEX `fk_Bransh_has_Product_Bransh1_idx` (`Bransh_ID` ASC) ,
  CONSTRAINT `fk_Bransh_has_Product_Bransh1`
    FOREIGN KEY (`Bransh_ID`)
    REFERENCES `PMS`.`bransh` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Bransh_has_Product_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `PMS`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`cosmetic`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`cosmetic` (
  `Brand` VARCHAR(20) NOT NULL,
  `Gender` CHAR(1) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  PRIMARY KEY (`Product_parcode`),
  CONSTRAINT `fk_Cosmetic_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `PMS`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`person`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`person` (
  `ID` VARCHAR(5) NOT NULL,
  `Phone` VARCHAR(11) NOT NULL,
  `name` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`ID`, `Phone`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`customer` (
  `points` FLOAT NOT NULL,
  `Person_ID` VARCHAR(5) NOT NULL,
  `Person_Phone` VARCHAR(11) NOT NULL,
  PRIMARY KEY (`Person_ID`, `Person_Phone`),
  CONSTRAINT `fk_Customer_Person1`
    FOREIGN KEY (`Person_ID` , `Person_Phone`)
    REFERENCES `PMS`.`person` (`ID` , `Phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`customer_buy_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`customer_buy_product` (
  `Customer_Person_ID` VARCHAR(5) NOT NULL,
  `Customer_Person_Phone` VARCHAR(11) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  `Quantaty` FLOAT NOT NULL,
  PRIMARY KEY (`Customer_Person_ID`, `Customer_Person_Phone`, `Product_parcode`),
  INDEX `fk_Customer_has_Product_Product1_idx` (`Product_parcode` ASC) ,
  INDEX `fk_Customer_has_Product_Customer1_idx` (`Customer_Person_ID` ASC, `Customer_Person_Phone` ASC) ,
  CONSTRAINT `fk_Customer_has_Product_Customer1`
    FOREIGN KEY (`Customer_Person_ID` , `Customer_Person_Phone`)
    REFERENCES `PMS`.`customer` (`Person_ID` , `Person_Phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Customer_has_Product_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `PMS`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`dosage_form`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`dosage_form` (
  `strength` INT(11) NOT NULL,
  `active_ing` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`strength`, `active_ing`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`employee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`employee` (
  `User_name` VARCHAR(10) NOT NULL,
  `salary` FLOAT NOT NULL,
  `StartDate` DATE NOT NULL,
  `Password` VARCHAR(20) NOT NULL,
  `Person_ID` VARCHAR(5) NOT NULL,
  `Person_Phone` VARCHAR(11) NOT NULL,
  PRIMARY KEY (`User_name`, `Person_ID`, `Person_Phone`),
  INDEX `fk_Employee_Person_idx` (`Person_ID` ASC, `Person_Phone` ASC) ,
  CONSTRAINT `fk_Employee_Person`
    FOREIGN KEY (`Person_ID` , `Person_Phone`)
    REFERENCES `PMS`.`person` (`ID` , `Phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`treasury`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`treasury` (
  `Bransh_ID` INT(11) NOT NULL,
  PRIMARY KEY (`Bransh_ID`),
  CONSTRAINT `fk_Treasury_Bransh1`
    FOREIGN KEY (`Bransh_ID`)
    REFERENCES `PMS`.`bransh` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`invoice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`invoice` (
  `ID` INT(11) NOT NULL,
  `date` DATE NOT NULL,
  `price` FLOAT NOT NULL,
  `Treasury_Bransh_ID` INT(11) NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `fk_Invoice_Treasury1_idx` (`Treasury_Bransh_ID` ASC) ,
  CONSTRAINT `fk_Invoice_Treasury1`
    FOREIGN KEY (`Treasury_Bransh_ID`)
    REFERENCES `PMS`.`treasury` (`Bransh_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`employee_has_invoice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`employee_has_invoice` (
  `Employee_User_name` VARCHAR(10) NOT NULL,
  `Employee_Person_ID` VARCHAR(5) NOT NULL,
  `Employee_Person_Phone` VARCHAR(11) NOT NULL,
  `Invoice_ID` INT(11) NOT NULL,
  PRIMARY KEY (`Employee_User_name`, `Employee_Person_ID`, `Employee_Person_Phone`, `Invoice_ID`),
  INDEX `fk_Employee_has_Invoice_Invoice1_idx` (`Invoice_ID` ASC) ,
  INDEX `fk_Employee_has_Invoice_Employee1_idx` (`Employee_User_name` ASC, `Employee_Person_ID` ASC, `Employee_Person_Phone` ASC) ,
  CONSTRAINT `fk_Employee_has_Invoice_Employee1`
    FOREIGN KEY (`Employee_User_name` , `Employee_Person_ID` , `Employee_Person_Phone`)
    REFERENCES `PMS`.`employee` (`User_name` , `Person_ID` , `Person_Phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Employee_has_Invoice_Invoice1`
    FOREIGN KEY (`Invoice_ID`)
    REFERENCES `PMS`.`invoice` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`inventory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`inventory` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `Bransh_ID` INT(11) NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `fk_Inventory_Bransh1_idx` (`Bransh_ID` ASC) ,
  CONSTRAINT `fk_Inventory_Bransh1`
    FOREIGN KEY (`Bransh_ID`)
    REFERENCES `PMS`.`bransh` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`inventory_has_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`inventory_has_product` (
  `Inventory_ID` INT(11) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  `Quntaty` FLOAT NOT NULL,
  `reordr_level` INT(11) NOT NULL,
  PRIMARY KEY (`Inventory_ID`, `Product_parcode`),
  INDEX `fk_Inventory_has_Product_Product1_idx` (`Product_parcode` ASC) ,
  INDEX `fk_Inventory_has_Product_Inventory1_idx` (`Inventory_ID` ASC) ,
  CONSTRAINT `fk_Inventory_has_Product_Inventory1`
    FOREIGN KEY (`Inventory_ID`)
    REFERENCES `PMS`.`inventory` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Inventory_has_Product_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `PMS`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`invoice_has_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`invoice_has_product` (
  `Invoice_ID` INT(11) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  PRIMARY KEY (`Invoice_ID`, `Product_parcode`),
  INDEX `fk_Invoice_has_Product_Product1_idx` (`Product_parcode` ASC) ,
  INDEX `fk_Invoice_has_Product_Invoice1_idx` (`Invoice_ID` ASC) ,
  CONSTRAINT `fk_Invoice_has_Product_Invoice1`
    FOREIGN KEY (`Invoice_ID`)
    REFERENCES `PMS`.`invoice` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Invoice_has_Product_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `PMS`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`medicine`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`medicine` (
  `Product_parcode` VARCHAR(14) NOT NULL,
  PRIMARY KEY (`Product_parcode`),
  CONSTRAINT `fk_Medicine_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `PMS`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`medicine_has_dosage_form`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`medicine_has_dosage_form` (
  `Medicine_Product_parcode` VARCHAR(14) NOT NULL,
  `dosage_form_strength` INT(11) NOT NULL,
  `dosage_form_active_ing` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`Medicine_Product_parcode`, `dosage_form_strength`, `dosage_form_active_ing`),
  INDEX `fk_Medicine_has_dosage form_dosage form1_idx` (`dosage_form_strength` ASC, `dosage_form_active_ing` ASC) ,
  INDEX `fk_Medicine_has_dosage form_Medicine1_idx` (`Medicine_Product_parcode` ASC) ,
  CONSTRAINT `fk_Medicine_has_dosage form_Medicine1`
    FOREIGN KEY (`Medicine_Product_parcode`)
    REFERENCES `PMS`.`medicine` (`Product_parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Medicine_has_dosage form_dosage form1`
    FOREIGN KEY (`dosage_form_strength` , `dosage_form_active_ing`)
    REFERENCES `PMS`.`dosage_form` (`strength` , `active_ing`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`supplier`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`supplier` (
  `nane` VARCHAR(45) NOT NULL,
  `phone` VARCHAR(11) NOT NULL,
  `adress` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`nane`, `phone`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`purchase_invoce`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`purchase_invoce` (
  `money_paid` FLOAT NOT NULL,
  `remaing_money` FLOAT NULL DEFAULT NULL,
  `Invoice_ID` INT(11) NOT NULL,
  `Supplier_nane` VARCHAR(45) NOT NULL,
  `Supplier_phone` VARCHAR(11) NOT NULL,
  PRIMARY KEY (`Invoice_ID`),
  INDEX `fk_purchase invoce_Supplier1_idx` (`Supplier_nane` ASC, `Supplier_phone` ASC) ,
  CONSTRAINT `fk_purchase invoce_Invoice1`
    FOREIGN KEY (`Invoice_ID`)
    REFERENCES `PMS`.`invoice` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_purchase invoce_Supplier1`
    FOREIGN KEY (`Supplier_nane` , `Supplier_phone`)
    REFERENCES `PMS`.`supplier` (`nane` , `phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`purchase_invoce_has_batch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`purchase_invoce_has_batch` (
  `purchase_invoce_Invoice_ID` INT(11) NOT NULL,
  `Batch_Batch_number` VARCHAR(14) NOT NULL,
  `Batch_Product_parcode` VARCHAR(14) NOT NULL,
  `purchase_invoce_has_Batchcol` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`purchase_invoce_Invoice_ID`, `Batch_Batch_number`, `Batch_Product_parcode`),
  INDEX `fk_purchase invoce_has_Batch_Batch1_idx` (`Batch_Batch_number` ASC, `Batch_Product_parcode` ASC) ,
  INDEX `fk_purchase invoce_has_Batch_purchase invoce1_idx` (`purchase_invoce_Invoice_ID` ASC) ,
  CONSTRAINT `fk_purchase invoce_has_Batch_Batch1`
    FOREIGN KEY (`Batch_Batch_number` , `Batch_Product_parcode`)
    REFERENCES `PMS`.`batch` (`Batch_number` , `Product_parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_purchase invoce_has_Batch_purchase invoce1`
    FOREIGN KEY (`purchase_invoce_Invoice_ID`)
    REFERENCES `PMS`.`purchase_invoce` (`Invoice_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`sell_invoice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`sell_invoice` (
  `Discount` FLOAT NOT NULL,
  `Invoice_ID` INT(11) NOT NULL,
  `Customer_Person_ID` VARCHAR(5) NOT NULL,
  `Customer_Person_Phone` VARCHAR(11) NOT NULL,
  PRIMARY KEY (`Invoice_ID`),
  INDEX `fk_Sell invoice_Customer1_idx` (`Customer_Person_ID` ASC, `Customer_Person_Phone` ASC) ,
  CONSTRAINT `fk_Sell invoice_Customer1`
    FOREIGN KEY (`Customer_Person_ID` , `Customer_Person_Phone`)
    REFERENCES `PMS`.`customer` (`Person_ID` , `Person_Phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Sell invoice_Invoice1`
    FOREIGN KEY (`Invoice_ID`)
    REFERENCES `PMS`.`invoice` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PMS`.`supplier_has_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PMS`.`supplier_has_product` (
  `Supplier_nane` VARCHAR(45) NOT NULL,
  `Supplier_phone` VARCHAR(11) NOT NULL,
  `Product_parcode` VARCHAR(14) NOT NULL,
  PRIMARY KEY (`Supplier_nane`, `Supplier_phone`, `Product_parcode`),
  INDEX `fk_Supplier_has_Product_Product1_idx` (`Product_parcode` ASC) ,
  INDEX `fk_Supplier_has_Product_Supplier1_idx` (`Supplier_nane` ASC, `Supplier_phone` ASC) ,
  CONSTRAINT `fk_Supplier_has_Product_Product1`
    FOREIGN KEY (`Product_parcode`)
    REFERENCES `PMS`.`product` (`parcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Supplier_has_Product_Supplier1`
    FOREIGN KEY (`Supplier_nane` , `Supplier_phone`)
    REFERENCES `PMS`.`supplier` (`nane` , `phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
