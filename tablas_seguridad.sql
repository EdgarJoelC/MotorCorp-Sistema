-- 1. Crear primero la tabla roles (es independiente)
CREATE TABLE roles (
    id_rol SERIAL PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL
);

-- 2. Crear la tabla usuarios (depende de roles)
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    codigo_secreto VARCHAR(50), 
    id_rol INT NOT NULL,
    CONSTRAINT fk_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);