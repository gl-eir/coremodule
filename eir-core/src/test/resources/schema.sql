DROP TABLE IF EXISTS CITY;
CREATE TABLE `CITY` (
`City_code` int AUTO_INCREMENT   PRIMARY KEY,
`city_name` varchar(50) NOT NULL,
`city_pincode` int NOT NULL
);


DROP TABLE IF EXISTS ALLOWED_TAC;
CREATE TABLE `ALLOWED_TAC` (
  `sno` int AUTO_INCREMENT   PRIMARY KEY,
  `tac` varchar(20) NOT NULL
) ;


DROP TABLE IF EXISTS BLACK_LIST;
CREATE TABLE `BLACK_LIST` (
  `sno` int AUTO_INCREMENT   PRIMARY KEY,
  `IMEI_ESN_MEID` varchar(20) DEFAULT NULL,
  `IMEI` varchar(20) DEFAULT NULL,
  `MSISDN` varchar(20) DEFAULT NULL,
  `ACTUAL_IMEI` varchar(20) DEFAULT NULL
);

DROP TABLE IF EXISTS BLOCKED_TAC;
CREATE TABLE `BLOCKED_TAC` (
  `sno` int NOT NULL,
  `tac` varchar(20) NOT NULL
);

DROP TABLE IF EXISTS configuration_call_flag;
CREATE TABLE `configuration_call_flag` (
  `sno` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `flag` varchar(20) NOT NULL
);



