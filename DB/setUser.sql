-- ======================= add db user for java =========
CREATE USER 'pmsadmin'@'localhost' IDENTIFIED BY '1234';

GRANT ALL PRIVILEGES ON PMS.* TO 'pmsadmin'@'localhost' WITH GRANT OPTION;

FLUSH PRIVILEGES;

-- ============================== done ================== 
