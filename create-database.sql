CREATE DATABASE CleanArchitectureDB;
GO

USE CleanArchitectureDB;
GO

CREATE TABLE Requirement (
    requirementId INT IDENTITY(1,1) PRIMARY KEY,
    requirementName NVARCHAR(255) NOT NULL
);
GO

CREATE TABLE ConvocationType (
    convocationTypeId INT IDENTITY(1,1) PRIMARY KEY,
    convocationTypeName NVARCHAR(255) NOT NULL
);
GO

-- Insert data into Requirement table
INSERT INTO Requirement (requirementName) VALUES
    (N'Promedio académico superior a 4.0'),
    (N'No tener sanciones disciplinarias'),
    (N'Haber cursado mínimo 2 semestres'),
    (N'Disponibilidad de 20 horas semanales');
GO

-- Insert data into ConvocationType table
-- The ids must match EnumConvocationType: MONITORING=1, GRANT=2, RESIDENCE=3
INSERT INTO ConvocationType (convocationTypeName) VALUES
    (N'Monitoria Académica'),
    (N'Beca de Investigación'),
    (N'Residencia Estudiantil');
GO
