
# TP4 – Sistema de Gestión de Turnos y Comunicación con Pacientes

**Hospital Área Programa «Dr. Fernando Rocha» – Luis Beltrán, Río Negro**

Prototipo de escritorio desarrollado en Java con interfaz gráfica Swing y conexión a MySQL mediante JDBC. Esta entrega amplía las funcionalidades del TP3 e incorpora mejoras en la gestión administrativa de turnos.

## Funcionalidades

- Registro, consulta y edición de pacientes.
- Registro, consulta y edición de profesionales.
- Baja lógica y reactivación de pacientes y profesionales.
- Gestión de especialidades y lugares de atención.
- Generación automática de horarios mediante intervalos configurables.
- Consulta de disponibilidades y filtrado de horarios vencidos.
- Asignación, consulta y cancelación de turnos.
- Separación de turnos vigentes e historial.
- Exportación de información en formato CSV e impresión.

## Tecnologías

Java 21, Apache NetBeans, Maven, Swing, JDBC y MySQL.

## Estructura del proyecto

- `src/main/java/com/gorriti/tp3/`: código fuente Java.
- `pom.xml`: configuración del proyecto y dependencias.
- `database/gestion_turnos_hospital.sql`: estructura actualizada de la base de datos y registros ficticios de prueba.

## Base de datos

El script de creación de la base de datos se encuentra en `tp4/database/gestion_turnos_hospital.sql`, dentro de este mismo repositorio.

Incluye las modificaciones realizadas en el TP4, como la incorporación del campo `activo` en las tablas de pacientes y profesionales.

**Importante:** el script contiene instrucciones para recrear la base de datos. Ejecutarlo puede eliminar los registros existentes.

## Ejecución

1. Contar con Java 21 o superior, Apache Maven y MySQL.
2. Crear la base de datos utilizando el script del TP4, en una instalación de prueba.
3. Configurar la conexión a MySQL según los datos establecidos en `ConexionBD.java`, incluida la variable de entorno `HOSPITAL_DB_PASSWORD`.
4. Abrir la carpeta `tp4` como proyecto Maven en NetBeans.
5. Ejecutar la clase principal `com.gorriti.tp3.TP3`.

Este prototipo amplía las funcionalidades administrativas desarrolladas en el TP3. Se verificaron las operaciones de edición, baja lógica, reactivación, generación de horarios, asignación y cancelación de turnos. El módulo de comunicación automática con pacientes y el acceso simultáneo desde varias computadoras no forman parte de esta versión.
