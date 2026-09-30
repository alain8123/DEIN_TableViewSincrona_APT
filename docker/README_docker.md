# MariaDB con Docker Compose (Docker Desktop)

## Ficheros

- `docker-compose.yml` — define el contenedor de MariaDB, lee credenciales de `.env`
- `.env.example` — plantilla; cópiala a `.env` y rellena con tus valores reales
- `init/01_schema.sql` — se ejecuta solo la primera vez que arranca el contenedor y crea la tabla `personas`

## 1. Preparar el `.env`

```
cd docker
cp .env.example .env
```

Edita `.env` y pon tus propios valores (usuario, contraseña, nombre de la BD, puerto). Añade `.env` a tu `../.gitignore` — nunca lo subas al repositorio.

## 2. Levantar el contenedor

Con Docker Desktop abierto, desde la carpeta ``:

```
docker compose up -d
```

Docker Desktop mostrará el contenedor `holamundo-mariadb` corriendo. La primera vez tarda unos segundos en inicializarse y crear la tabla automáticamente a partir de `init/01_schema.sql`.

Para comprobar que está sano:
```
docker compose ps
```
(la columna STATUS debe decir `healthy`)

Para ver los logs si algo falla:
```
docker compose logs -f mariadb
```

## 3. Configurar la app Java con estos MISMOS datos

La app (`DBConnection.java`) sigue leyendo `DB_URL`, `DB_USER`, `DB_PASSWORD` de variables de entorno o de `~/.holamundo/db.properties`. Usa los valores de tu `.env`, por ejemplo si tu `.env` tiene:

```
DB_NAME=holamundo
DB_USER=holamundo_user
DB_PASSWORD=cambia_esta_contrasena
DB_PORT=3306
```

entonces configuras la app con:

```
DB_URL=jdbc:mariadb://localhost:3306/holamundo
DB_USER=holamundo_user
DB_PASSWORD=cambia_esta_contrasena
```

(mismo `DB_USER`/`DB_PASSWORD` que en `.env`, mismo puerto, y `DB_URL` apuntando a `localhost` porque el puerto está mapeado al host).

## Parar / reiniciar desde cero

```
docker compose down          # para el contenedor, conserva los datos
docker compose down -v       # para el contenedor Y borra el volumen (borra todos los datos)
docker compose up -d         # vuelve a arrancar
```

Si borraste el volumen con `-v`, el script `init/01_schema.sql` se volverá a ejecutar y recreará la tabla vacía.
