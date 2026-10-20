CREATE DATABASE CleanArchitectureDB;
GO

USE CleanArchitectureDB;
GO

CREATE TABLE Requirement (
    requirementId INT IDENTITY(1,1) PRIMARY KEY,
    requirementName NVARCHAR(255) NOT NULL,
    -- 1 when the required value is a grade (number between 0 and 5.0)
    isGrade BIT NOT NULL DEFAULT 0
);
GO

CREATE TABLE Student (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nameStudent NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) NOT NULL,
    program NVARCHAR(255) NOT NULL,
    -- Grade point average, between 0 and 5.0
    average DECIMAL(3,2) NOT NULL,
    stratum INT NOT NULL
);
GO

CREATE TABLE ConvocationType (
    convocationTypeId INT IDENTITY(1,1) PRIMARY KEY,
    convocationTypeName NVARCHAR(255) NOT NULL
);
GO

-- Insert data into Requirement table
INSERT INTO Requirement (requirementName, isGrade) VALUES
    (N'Promedio académico superior a 4.0', 1),
    (N'No tener sanciones disciplinarias', 0),
    (N'Haber cursado mínimo 2 semestres', 0),
    (N'Disponibilidad de 20 horas semanales', 0);
GO

-- Insert example students: different programs, averages and strata to test requirements that are met and not met
INSERT INTO Student (nameStudent, email, program, average, stratum) VALUES
    (N'Laura Gómez Ríos', N'laura.gomez@ucaldas.test', N'Ingeniería de Sistemas', 4.6, 2),
    (N'Andrés Felipe Torres', N'andres.torres@ucaldas.test', N'Ingeniería de Sistemas', 3.8, 3),
    (N'Valentina Herrera Castaño', N'valentina.herrera@ucaldas.test', N'Biología', 4.2, 1),
    (N'Santiago Ramírez López', N'santiago.ramirez@ucaldas.test', N'Derecho', 3.2, 4),
    (N'Camila Ospina Duque', N'camila.ospina@ucaldas.test', N'Medicina', 4.9, 2);
GO

-- Users that can sign in (issue #22). Passwords are BCrypt hashes, never plain text.
-- Development credentials: admin@ucaldas.test / Admin2026* and each student with its email / Estudiante2026*
CREATE TABLE Users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    email NVARCHAR(255) NOT NULL UNIQUE,
    passwordHash NVARCHAR(100) NOT NULL,
    -- ADMIN or STUDENT
    role NVARCHAR(20) NOT NULL CHECK (role IN (N'ADMIN', N'STUDENT')),
    -- Only students are linked to the Student table
    studentId INT NULL REFERENCES Student(id),
    CONSTRAINT CK_Users_StudentRole CHECK ((role = N'STUDENT' AND studentId IS NOT NULL) OR (role = N'ADMIN' AND studentId IS NULL))
);
GO

INSERT INTO Users (email, passwordHash, role, studentId) VALUES
    (N'admin@ucaldas.test', N'$2b$10$3wXOllnDtaSMxT45KyWYPufJ4LzeOoXSy4T/AZK0RcDK6mBpgzjUm', N'ADMIN', NULL),
    (N'laura.gomez@ucaldas.test', N'$2b$10$75laSHdxP/hhmEmxcD9dAu9oJTYGcFaxxBKAp.rBoH/b5iPjg5nNC', N'STUDENT', 1),
    (N'andres.torres@ucaldas.test', N'$2b$10$HAbQ14CUI.w5YCIg7WaGIunkX/UteCm15qzLSHF1L2PggakIOvLwC', N'STUDENT', 2),
    (N'valentina.herrera@ucaldas.test', N'$2b$10$f10X2wM2n083jTBHqcguweh6REpDdxmCBitPEsAveM3V3zUZPhBCS', N'STUDENT', 3),
    (N'santiago.ramirez@ucaldas.test', N'$2b$10$OaYTxSfYdSyqI4ghhgkIfuKPqHdifXCcUOKMcFDIiCbm01y.lfzcC', N'STUDENT', 4),
    (N'camila.ospina@ucaldas.test', N'$2b$10$X.DK.04nNcEwBnuHgTqKjOK8lbCY1Hev4Lw.YrX5WtFOgzgojOJs2', N'STUDENT', 5);
GO

-- Insert data into ConvocationType table
-- The ids must match EnumConvocationType: MONITORING=1, GRANT=2, RESIDENCE=3
INSERT INTO ConvocationType (convocationTypeName) VALUES
    (N'Monitoria Académica'),
    (N'Beca de Investigación'),
    (N'Residencia Estudiantil');
GO
