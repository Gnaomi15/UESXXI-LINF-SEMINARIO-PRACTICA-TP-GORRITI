# TP3 – Sistema de Gestión de Turnos y Comunicación con Pacientes

**Hospital Área Programa «Dr. Fernando Rocha» – Luis Beltrán, Río Negro**

Prototipo de escritorio desarrollado en Java con interfaz gráfica Swing y conexión a MySQL mediante JDBC.

## Funcionalidades

- Registro y consulta de pacientes.
- Registro de especialidades y profesionales.
- Gestión de disponibilidad y horarios de atención.
- Asignación, consulta y cancelación de turnos.

## Tecnologías

Java, Apache NetBeans, Maven, Swing, JDBC y MySQL.

## Estructura del proyecto

- `src/main/java/com/gorriti/tp3/`: código fuente Java.
- `pom.xml`: configuración del proyecto y dependencias.

## Base de datos

El script de creación de la base de datos se encuentra en `tp2/database/gestion_turnos_hospital.sql`, dentro de este mismo repositorio.

**Importante:** el script contiene instrucciones para recrear la base de datos. Ejecutarlo puede eliminar los registros existentes.

## Ejecución

1. Contar con Java 21 o superior, Apache Maven y MySQL.
2. Crear la base de datos utilizando el script del TP2, en una instalación de prueba.
3. Configurar la conexión a MySQL según los datos establecidos en `ConexionBD.java`, incluida la variable de entorno `HOSPITAL_DB_PASSWORD`.
4. Abrir la carpeta `tp3` como proyecto Maven en NetBeans.
5. Ejecutar la clase principal `com.gorriti.tp3.TP3`.

Este prototipo implementa las funcionalidades principales de gestión de turnos. El módulo de comunicación con pacientes no forma parte de esta versión.
