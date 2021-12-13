CREATE TABLE majors (
    majorID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    major VARCHAR(255) NOT NULL
)engine=InnoDB DEFAULT charset=utf8;

CREATE TABLE TstudentInfo (
    id      CHAR(10) NOT NULL PRIMARY KEY,
    name    VARCHAR(64) NOT NULL,
    gender  VARCHAR(8),
    majorID INT,
    FOREIGN KEY (majorID) REFERENCES majors(majorID)
)engine=InnoDB DEFAULT charset=utf8;

DELIMITER $$
CREATE PROCEDURE add_student(_id CHAR(10), _name VARCHAR(64), _gender VARCHAR(8), _major VARCHAR(255))
BEGIN
    DECLARE _majorID INT;
    SELECT majorID INTO _majorID FROM majors WHERE major=_major;
    IF _majorID IS NULL THEN
        INSERT INTO majors (major) VALUES (_major);
        SET _majorID = LAST_INSERT_ID();
    END IF;
    INSERT INTO TstudentInfo VALUES (_id, _name, _gender, _majorID);
END $$
DELIMITER ;