@echo off
echo ==========================================
echo      Pharmacy System Database Fixer
echo ==========================================
echo.
echo This script will create the missing 'supplier' table.
echo.
echo Please enter your MySQL root password when prompted.
echo.
mysql -u root -p pharmacy_system < DB/create_suppliers_table.sql
echo.
echo Done! You can now restart the application.
pause
