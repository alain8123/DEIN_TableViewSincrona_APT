-- Este script se ejecuta automáticamente la primera vez que se crea
-- el contenedor de MariaDB (carpeta /docker-entrypoint-initdb.d).
-- Si el volumen holamundo_data ya existe de un arranque anterior,
-- NO se vuelve a ejecutar (borra el volumen si quieres forzarlo).

CREATE TABLE IF NOT EXISTS personas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    fecha_nacimiento DATE NOT NULL
);
