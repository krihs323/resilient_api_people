CREATE TABLE IF NOT EXISTS person (
	id int NOT NULL AUTO_INCREMENT,
	name varchar(50) NOT NULL,
	email VARCHAR(100) NOT NULL,
    age INT NULL,
	PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS person_x_bootcamps (
	id INT auto_increment NOT NULL,
	id_person INT NOT NULL,
	id_bootcamp INT NOT NULL,
	CONSTRAINT person_x_bootcamps_pk PRIMARY KEY (id),
	CONSTRAINT person_x_bootcamps_unique UNIQUE KEY (id_person,id_bootcamp),
	CONSTRAINT person_x_bootcamps_capacities_FK FOREIGN KEY (id_person) REFERENCES person(id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

