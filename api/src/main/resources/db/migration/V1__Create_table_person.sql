CREATE TABLE person (
  id bigint NOT NULL AUTO_INCREMENT,
  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL,
  address varchar(255) NOT NULL,
  gender varchar(1) DEFAULT NULL,
  PRIMARY KEY (id)
);
