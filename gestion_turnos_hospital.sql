-- =========================================================
-- SISTEMA DE GESTIÓN DE TURNOS Y COMUNICACIÓN CON PACIENTES
-- Hospital Área Programa "Dr. Fernando Rocha"
-- Base de datos de prueba
-- =========================================================


-- =========================================================
-- 1. CREACIÓN DE LA BASE DE DATOS
-- =========================================================

-- Elimina la base de datos si ya existe para permitir
-- reconstruirla completamente desde cero.
DROP DATABASE IF EXISTS gestion_turnos_hospital;

-- Crea nuevamente la base de datos.
CREATE DATABASE gestion_turnos_hospital;

USE gestion_turnos_hospital;

-- =========================================================
-- 2. CREACIÓN DE TABLAS
-- =========================================================

-- Almacena los datos personales y de contacto de los pacientes.
CREATE TABLE paciente (
    id_paciente INT AUTO_INCREMENT PRIMARY KEY,
    documento VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    telefono VARCHAR(30),
    email VARCHAR(100)
);

-- Almacena las credenciales y el rol de los usuarios del sistema.
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    clave_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Registra las especialidades disponibles.
CREATE TABLE especialidad (
    id_especialidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

-- Registra los diferentes lugares donde se brinda atención.
CREATE TABLE lugar_atencion (
    id_lugar INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(150)
);

-- Relaciona al personal administrativo con su usuario de acceso.
CREATE TABLE personal_administrativo (
    id_personal INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    id_usuario INT NOT NULL UNIQUE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Registra a los profesionales y la especialidad a la que pertenecen.
CREATE TABLE profesional (
    id_profesional INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    id_especialidad INT NOT NULL,
    FOREIGN KEY (id_especialidad) REFERENCES especialidad(id_especialidad)
);

-- Registra las fechas y horarios disponibles de cada profesional
-- y el lugar en el que se realizará la atención.
CREATE TABLE disponibilidad (
    id_disponibilidad INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    horario TIME NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'LIBRE',
    id_profesional INT NOT NULL,
    id_lugar INT NOT NULL,

    FOREIGN KEY (id_profesional)
        REFERENCES profesional(id_profesional),

    FOREIGN KEY (id_lugar)
        REFERENCES lugar_atencion(id_lugar),

    -- Evita registrar dos veces el mismo horario para un profesional.
    UNIQUE (id_profesional, fecha, horario)
);

-- Registra los turnos asignados y los relaciona con
-- un paciente y una disponibilidad.
CREATE TABLE turno (
    id_turno INT AUTO_INCREMENT PRIMARY KEY,
    fecha_asignacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    id_paciente INT NOT NULL,
    id_disponibilidad INT NOT NULL,

    FOREIGN KEY (id_paciente)
        REFERENCES paciente(id_paciente),

    FOREIGN KEY (id_disponibilidad)
        REFERENCES disponibilidad(id_disponibilidad)
);

-- Registra pacientes que esperan un turno para una especialidad.
CREATE TABLE lista_espera (
    id_lista_espera INT AUTO_INCREMENT PRIMARY KEY,
    fecha_solicitud DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    id_paciente INT NOT NULL,
    id_especialidad INT NOT NULL,

    FOREIGN KEY (id_paciente)
        REFERENCES paciente(id_paciente),

    FOREIGN KEY (id_especialidad)
        REFERENCES especialidad(id_especialidad)
);

-- Almacena las comunicaciones generadas por el sistema.
CREATE TABLE comunicacion (
    id_comunicacion INT AUTO_INCREMENT PRIMARY KEY,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(30) NOT NULL,
    contenido VARCHAR(500) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
);

-- Relaciona cada comunicación con uno o varios pacientes
-- y permite registrar el estado del envío para cada destinatario.
CREATE TABLE comunicacion_destinatario (
    id_comunicacion INT NOT NULL,
    id_paciente INT NOT NULL,
    estado_envio VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',

    PRIMARY KEY (id_comunicacion, id_paciente),

    FOREIGN KEY (id_comunicacion)
        REFERENCES comunicacion(id_comunicacion),

    FOREIGN KEY (id_paciente)
        REFERENCES paciente(id_paciente)
);


-- =========================================================
-- 3. CARGA DE DATOS DE PRUEBA
-- =========================================================

-- Registra un paciente ficticio para realizar las pruebas.
INSERT INTO paciente (documento, nombre, apellido, telefono, email)
VALUES ('30111222', 'Ana', 'Pérez', '2984123456', 'ana.perez@email.com');

-- Registra una especialidad ficticia para las pruebas.
INSERT INTO especialidad (nombre)
VALUES ('Cardiología');

-- Registra un profesional ficticio asociado a Cardiología.
INSERT INTO profesional (nombre, apellido, id_especialidad)
VALUES ('Laura', 'Gómez', 1);

-- Registra un lugar de atención.
INSERT INTO lugar_atencion (nombre, direccion)
VALUES ('Hospital Dr. Fernando Rocha', 'Luis Beltrán, Río Negro');


-- =========================================================
-- 4. REGISTRO DE UNA DISPONIBILIDAD
-- =========================================================

-- Crea una disponibilidad libre para el profesional registrado.
INSERT INTO disponibilidad
(fecha, horario, estado, id_profesional, id_lugar)
VALUES
('2026-10-15', '09:00:00', 'LIBRE', 1, 1);


-- =========================================================
-- 5. ASIGNACIÓN DE UN TURNO
-- =========================================================

-- Asigna al paciente la disponibilidad creada anteriormente.
INSERT INTO turno (estado, id_paciente, id_disponibilidad)
VALUES ('ACTIVO', 1, 1);

-- Una vez asignado el turno, la disponibilidad deja de estar libre.
UPDATE disponibilidad
SET estado = 'OCUPADA'
WHERE id_disponibilidad = 1;


-- =========================================================
-- 6. PRUEBA DE BORRADO DE UN REGISTRO
-- =========================================================

-- Crea un paciente ficticio únicamente para comprobar
-- el funcionamiento de la operación DELETE.
INSERT INTO paciente (documento, nombre, apellido, telefono, email)
VALUES ('99999999', 'Paciente', 'Prueba', '0000000000', 'prueba@email.com');

-- Elimina el paciente creado específicamente para la prueba.
DELETE FROM paciente
WHERE documento = '99999999';


-- =========================================================
-- 7. CONSULTA DE TURNOS ASIGNADOS
-- =========================================================

-- Combina las tablas relacionadas para mostrar la información
-- completa de un turno: paciente, especialidad, profesional,
-- fecha, horario, lugar de atención y estado.
SELECT
    t.id_turno,
    CONCAT(p.nombre, ' ', p.apellido) AS paciente,
    e.nombre AS especialidad,
    CONCAT(pr.nombre, ' ', pr.apellido) AS profesional,
    d.fecha,
    d.horario,
    l.nombre AS lugar_atencion,
    t.estado AS estado_turno
FROM turno t
INNER JOIN paciente p
    ON t.id_paciente = p.id_paciente
INNER JOIN disponibilidad d
    ON t.id_disponibilidad = d.id_disponibilidad
INNER JOIN profesional pr
    ON d.id_profesional = pr.id_profesional
INNER JOIN especialidad e
    ON pr.id_especialidad = e.id_especialidad
INNER JOIN lugar_atencion l
    ON d.id_lugar = l.id_lugar;


-- =========================================================
-- 8. CONSULTA DE DISPONIBILIDADES LIBRES
-- =========================================================

-- Busca únicamente los horarios que se encuentran disponibles.
-- En este punto la consulta no devuelve resultados porque
-- la única disponibilidad registrada se encuentra ocupada.
SELECT
    d.id_disponibilidad,
    e.nombre AS especialidad,
    CONCAT(pr.nombre, ' ', pr.apellido) AS profesional,
    d.fecha,
    d.horario,
    l.nombre AS lugar_atencion
FROM disponibilidad d
INNER JOIN profesional pr
    ON d.id_profesional = pr.id_profesional
INNER JOIN especialidad e
    ON pr.id_especialidad = e.id_especialidad
INNER JOIN lugar_atencion l
    ON d.id_lugar = l.id_lugar
WHERE d.estado = 'LIBRE';


-- =========================================================
-- 9. REGISTRO DE UNA SEGUNDA DISPONIBILIDAD
-- =========================================================

-- Se agrega un nuevo horario libre para comprobar que
-- la consulta anterior permite localizar disponibilidades.
INSERT INTO disponibilidad
(fecha, horario, estado, id_profesional, id_lugar)
VALUES
('2026-10-15', '10:00:00', 'LIBRE', 1, 1);


-- =========================================================
-- 10. VERIFICACIÓN DE DISPONIBILIDADES LIBRES
-- =========================================================

-- Se repite la consulta. Ahora debe aparecer la disponibilidad
-- de las 10:00 como resultado.
SELECT
    d.id_disponibilidad,
    e.nombre AS especialidad,
    CONCAT(pr.nombre, ' ', pr.apellido) AS profesional,
    d.fecha,
    d.horario,
    l.nombre AS lugar_atencion
FROM disponibilidad d
INNER JOIN profesional pr
    ON d.id_profesional = pr.id_profesional
INNER JOIN especialidad e
    ON pr.id_especialidad = e.id_especialidad
INNER JOIN lugar_atencion l
    ON d.id_lugar = l.id_lugar
WHERE d.estado = 'LIBRE';