-- Creación de tablas en orden para respetar las dependencias

-- 1. Periodo
CREATE TABLE periodo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    inicio TIME NOT NULL,
    fin TIME NOT NULL
);

-- 2. Horario
CREATE TABLE horario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(255),
    inicio TIME,
    fin TIME,
    periodo VARCHAR(255)
);

-- 3. GrupoZona
CREATE TABLE grupo_zona (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255)
);

-- 4. Zona
CREATE TABLE zona (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    grupo_zona_id BIGINT,
    FOREIGN KEY (grupo_zona_id) REFERENCES grupo_zona(id)
);

-- 5. ZonaObligatoria
CREATE TABLE zona_obligatoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    zona_id BIGINT NOT NULL,
    dia_de_la_semana VARCHAR(255),
    FOREIGN KEY (zona_id) REFERENCES zona(id)
);

-- 6. PlantillaHorario
CREATE TABLE plantilla_horario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255)
);

-- 7. DetallePlantillaHorario
CREATE TABLE detalle_plantilla_horario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plantilla_horario_id BIGINT,
    horario_id BIGINT,
    dia_de_la_semana VARCHAR(255),
    FOREIGN KEY (plantilla_horario_id) REFERENCES plantilla_horario(id),
    FOREIGN KEY (horario_id) REFERENCES horario(id)
);

-- 8. Empleado
CREATE TABLE empleado (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    plantilla_horario_id BIGINT,
    FOREIGN KEY (plantilla_horario_id) REFERENCES plantilla_horario(id)
);

-- 9. AsignacionHorario
CREATE TABLE asignacion_horario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id BIGINT,
    horario_id BIGINT,
    dia_de_la_semana VARCHAR(255),
    periodo VARCHAR(255),
    FOREIGN KEY (empleado_id) REFERENCES empleado(id),
    FOREIGN KEY (horario_id) REFERENCES horario(id)
);

-- 10. AsignacionZona
CREATE TABLE asignacion_zona (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id BIGINT,
    zona_id BIGINT,
    dia_de_la_semana VARCHAR(255),
    periodo_id BIGINT NOT NULL,
    inicio TIME NOT NULL,
    fin TIME NOT NULL,
    FOREIGN KEY (empleado_id) REFERENCES empleado(id),
    FOREIGN KEY (zona_id) REFERENCES zona(id),
    FOREIGN KEY (periodo_id) REFERENCES periodo(id)
);

-- 11. CategoriaHorario
CREATE TABLE categoria_horario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

-- 12. CategoriaHorarioDetalle
CREATE TABLE categoria_horario_detalle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    categoria_horario_id BIGINT NOT NULL,
    horario_id BIGINT NOT NULL,
    grupo_zona_id BIGINT,
    FOREIGN KEY (categoria_horario_id) REFERENCES categoria_horario(id),
    FOREIGN KEY (horario_id) REFERENCES horario(id),
    FOREIGN KEY (grupo_zona_id) REFERENCES grupo_zona(id)
);