INSERT INTO Editorial (name) VALUES ('Editorial altense');
INSERT INTO Editorial (name) VALUES ('Editorial la marquesa');
INSERT INTO Editorial (name) VALUES ('Editorial la juanera');

INSERT INTO Book (code, title, auth, quantity, datePublication, idEditorial) VALUES ('BOOK1','Leithold','luis leithold',20,'2010-01-01',1);
INSERT INTO Book (code, title, auth, quantity, datePublication, idEditorial) VALUES ('BOOK2','Calculo Stewart','Stewart',5,'2010-01-01',2);
INSERT INTO Book (code, title, auth, quantity, datePublication, idEditorial) VALUES ('BOOK3','Calculo inical','Stewart' ,5,'2010-01-01',2);
INSERT INTO Book (code, title, auth, quantity, datePublication, idEditorial) VALUES ('BOOK4','Fisica basica 1 ','luis leithold',10,'2010-01-01',3);

INSERT INTO Career (name) VALUES ('Ingenieria en sistemas');
INSERT INTO Career (name) VALUES ('Ingenieria mecanica');
INSERT INTO Career (name) VALUES ('Ingenieria industrial');
INSERT INTO Career (name) VALUES ('Ingenieria civil');

INSERT INTO Student (name, idCareer, dteBirth, carnet) VALUES ('David',1,'1998-01-01','201623145');
INSERT INTO Student (name, idCareer, dteBirth, carnet) VALUES ('Mario',2,'1995-01-05','201623146');
INSERT INTO Student (name, idCareer, dteBirth, carnet) VALUES ('Rene',3,'1995-08-05','201623147');

