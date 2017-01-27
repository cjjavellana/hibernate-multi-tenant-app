CREATE DATABASE `SG` CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE DATABASE `IN` CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE DATABASE `HK` CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE USER 'SINGAPORE'@'%' IDENTIFIED by 'password';
GRANT ALL ON `SG`.* TO 'SINGAPORE'@'%';

CREATE USER 'INDIA'@'%' IDENTIFIED by 'password';
GRANT ALL ON `IN`.* TO 'INDIA'@'%';

CREATE USER 'HONGKONG'@'%' IDENTIFIED by 'password';
GRANT ALL ON `HK`.* TO 'HONGKONG'@'%';
