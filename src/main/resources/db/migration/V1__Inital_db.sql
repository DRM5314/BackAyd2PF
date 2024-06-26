CREATE TABLE IF NOT EXISTS Editorial (
	id BIGINT NOT NULL Auto_Increment,
	name VARCHAR (60) NOT NULL UNIQUE,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Book (
    id BIGINT NOT NULL Auto_Increment,
	code VARCHAR(20) NOT NULL UNIQUE,
	title VARCHAR (60) NOT NULL,
	auth VARCHAR (60) NOT NULL,
	quantity INT NOT NULL,
	datePublication TIMESTAMP NOT NULL,
	idEditorial BIGINT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (idEditorial)
	REFERENCES Editorial(id)
);

CREATE TABLE IF NOT EXISTS Career (
	id BIGINT NOT NULL Auto_Increment,
	name VARCHAR (60) NOT NULL UNIQUE,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Student (
	carnet BIGINT NOT NULL Auto_Increment,
	name VARCHAR (60) NOT NULL,
	idCareer BIGINT NOT NULL,
	dteBirth TIMESTAMP NOT NULL,
	PRIMARY KEY (carnet),
	FOREIGN KEY (idCareer)
	REFERENCES Career (id)
);

CREATE TABLE IF NOT EXISTS Loan (
	id BIGINT NOT NULL Auto_Increment,
	bookCode VARCHAR (60) NOT NULL,
	carnet BIGINT NOT NULL,
	laonDate TIMESTAMP NOT NULL,
	returnDate TIMESTAMP NOT NULL,
	state ENUM ('borrowed','cancelled','penalized','sanction'),
	PRIMARY KEY (id),
	FOREIGN KEY (carnet)
	REFERENCES Student (carnet),
	FOREIGN KEY (bookCode)
	REFERENCES Book (code)
);

CREATE TABLE IF NOT EXISTS Payments(
	id BIGINT NOT NULL Auto_Increment,
	idLoan BIGINT NOT NULL,
	type ENUM ('normal','penalized','sanction'),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS User(
    userId BIGINT NOT NULL AUTO_INCREMENT,
    role ENUM ('ADMIN','STUDENT'),
    name VARCHAR (60) NOT NULL,
    email VARCHAR (60) NOT NULL,
    username VARCHAR(45) NOT NULL,
    password VARCHAR (500) NOT NULL,
    status TINYINT (1) NULL DEFAULT 1,
    PRIMARY KEY (userId)
 );