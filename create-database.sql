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

-- Insert data into ConvocationType table
-- The ids must match EnumConvocationType: MONITORING=1, GRANT=2, RESIDENCE=3
INSERT INTO ConvocationType (convocationTypeName) VALUES
    (N'Monitoria Académica'),
    (N'Beca de Investigación'),
    (N'Residencia Estudiantil');
GO
