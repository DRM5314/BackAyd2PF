ALTER TABLE Student CHANGE carnet id BIGINT NOT NULL AUTO_INCREMENT;

ALTER TABLE Student ADD carnet VARCHAR(10) NOT NULL;

ALTER TABLE Student ADD status TINYINT(1) DEFAULT 1;