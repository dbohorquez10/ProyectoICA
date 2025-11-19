# 🟢 **ICA-FITO – Sistema de Gestión Fitosanitaria**

### Proyecto Integrador – Ingeniería de Sistemas

**Universidad de Investigación y Desarrollo (UDI)**
**Entrega Final – Versión Completa**

---

# 🎥 Video del prototipo funcionando

🔗 [https://www.youtube.com/watch?v=8E8faTxI7a0](https://www.youtube.com/watch?v=8E8faTxI7a0)

---

# 📌 Descripción general

**ICA-FITO** es una aplicación de escritorio desarrollada en **Java Swing** con conexión a **Oracle Database**, diseñada para apoyar los procesos fitosanitarios del **Instituto Colombiano Agropecuario (ICA)**.

El sistema integra, en un solo flujo completo:

* ✔ Registro y aprobación de **técnicos y productores**
* ✔ Gestión completa de **cultivos, plagas y lotes**
* ✔ Relación cultivo–plaga
* ✔ Solicitud y asignación de **visitas técnicas**
* ✔ Registro de **inspecciones**
* ✔ **Alertas automáticas** por infestación
* ✔ Generación de **reportes PDF**
* ✔ Panel moderno con **logo ICA**, estilo institucional y navegación con **CardLayout**

Todo implementado con arquitectura **MVC**, principios de POO e interfaz de uso profesional.

---

# 🎯 Objetivos del sistema

* Gestionar la información relacionada con el estado fitosanitario de cultivos.
* Mejorar el proceso de seguimiento técnico entre productores y técnicos ICA.
* Reducir errores de digitación mediante listas dinámicas, validaciones y restricciones.
* Ofrecer un flujo automatizado desde la solicitud de visita hasta la generación del reporte.
* Implementar triggers inteligentes que automaticen tareas críticas.

---

## 🗂️ Estructura del Código Fuente

```plaintext
src/
│
├── Modelo/
│   ├── ConexionBD.java
│   ├── Sesion.java
│   ├── RolUsuario.java
│   ├── .png/
│   │    └── logo_ica.png
│   │
│   ├── Modelo.dao/
│   │    ├── AlertaInfestacionDAO.java
│   │    ├── AsignacionLoteDAO.java
│   │    ├── CultivoDAO.java
│   │    ├── DAOUtils.java
│   │    ├── InspeccionDAO.java
│   │    ├── LoteDAO.java
│   │    ├── PlagaCultivoDAO.java
│   │    ├── PlagaDAO.java
│   │    ├── ProductorDAO.java
│   │    ├── ReportePlagaDAO.java
│   │    ├── TecnicoDAO.java
│   │    ├── UsuarioDAO.java
│   │    └── VisitaDAO.java
│   │
│   ├── Modelo.entidades/
│        ├── Usuario.java
│        ├── Productor.java
│        ├── Tecnico.java
│        ├── Cultivo.java
│        ├── Plaga.java
│        ├── Lote.java
│        ├── Visita.java
│        ├── Inspeccion.java
│        ├── CultivoPlaga.java
│        ├── ReportePlaga.java
│        └── AlertaInfestacion.java
│
├── controlador/
│   ├── AuthController.java
│   ├── CultivoController.java
│   ├── PlagaController.java
│   ├── LoteController.java
│   ├── CultivoPlagaController.java
│   ├── ProductorController.java
│   ├── TecnicoController.java
│   ├── AsignacionLoteController.java
│   ├── VisitaController.java
│   ├── InspeccionController.java
│   └── ReportePlagaController.java
│
└── vista/
    ├── LoginFrame.java
    ├── MainFrame.java
    ├── UIStyle.java
    ├── RegistroProductorDialog.java
    ├── RegistroTecnicoDialog.java
    │
    ├── vista.admin/
    │    └── AprobacionUsuariosForm.java
    │
    ├── vista.cultivo/
    │    ├── CultivoForm.java
    │    └── PlagasPorCultivoDialog.java
    │
    ├── vista.lote/
    │    └── LoteForm.java
    │
    ├── vista.plaga/
    │    ├── PlagaForm.java
    │    └── CultivosPlagaDialog.java
    │
    ├── vista.visita/
    │    ├── CombItem.java
    │    └── VisitaForm.java
    │
    └── vista.inspeccion/
         └── InspeccionForm.java

```

---

# 🔐 Roles del sistema

### 🟩 **ADMIN**

El rol con mayores privilegios. Puede:

* Aprobar o desactivar usuarios (técnicos/productores)
* Registrar cultivos, plagas y lotes
* Relacionar cultivos con plagas
* Asignar lotes a técnicos
* Gestionar visitas y su asignación
* Consultar inspecciones, reportes y alertas
* Generar reportes PDF

### 🟦 **TECNICO ICA**

Su responsabilidad es la operación de campo:

* Ver lotes asignados
* Registrar visitas realizadas
* Registrar inspecciones
* Generar reportes PDF de inspección
* Atender alertas activas

### 🟧 **PRODUCTOR**

Gestiona sus cultivos:

* Solicitar visitas técnicas
* Consultar estados de visitas
* Ver inspecciones realizadas por técnicos
* Visualizar alertas generadas para su lote

---

# 🗄️ Base de datos Oracle

Conexión utilizada:

| Parámetro | Valor             |
| --------- | ----------------- |
| Servidor  | `192.168.254.215` |
| Puerto    | `1521`            |
| SID       | `orcl`            |
| Usuario   | `ADMINICA`        |
| Clave     | `adminica123`     |

---

# 🚨 Triggers implementados

A continuación se listan los **triggers reales** utilizados en el proyecto:

---

## **1️⃣ Alerta automática cuando la infestación supera 30%**

```sql
CREATE OR REPLACE TRIGGER trg_alerta_auto
AFTER INSERT ON INSPECCION
FOR EACH ROW
BEGIN
    IF :NEW.PORCENTAJE_INFESTACION > 30 THEN
        INSERT INTO ALERTA_INFESTACION (
            ID_ALERTA, ID_INSPECCION, NIVEL_CRITICO,
            ESTADO_ALERTA, MENSAJE_ALERTA, FECHA_ALERTA
        ) VALUES (
            'ALR-' || SEQ_ALERTA.NEXTVAL,
            :NEW.ID_INSPECCION,
            :NEW.PORCENTAJE_INFESTACION,
            'ACTIVA',
            'Infestación superior al 30% detectada automáticamente.',
            SYSDATE
        );
    END IF;
END;
/
```

---

## **2️⃣ Cerrar alertas cuando la infestación baja de 10%**

```sql
CREATE OR REPLACE TRIGGER trg_cerrar_alerta
AFTER UPDATE ON INSPECCION
FOR EACH ROW
BEGIN
    IF :NEW.PORCENTAJE_INFESTACION < 10 THEN
        UPDATE ALERTA_INFESTACION
        SET ESTADO_ALERTA = 'CERRADA'
        WHERE ID_INSPECCION = :NEW.ID_INSPECCION
          AND ESTADO_ALERTA = 'ACTIVA';
    END IF;
END;
/
```

---

## **3️⃣ Limitar a 5 lotes por técnico**

```sql
CREATE OR REPLACE TRIGGER trg_limite_asignaciones
BEFORE INSERT ON ASIGNACION_LOTE
FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO   v_count
    FROM   ASIGNACION_LOTE
    WHERE  ID_TECNICO = :NEW.ID_TECNICO;

    IF v_count >= 5 THEN
        RAISE_APPLICATION_ERROR(
            -20001,
            'El técnico ya tiene 5 lotes asignados.'
        );
    END IF;
END;
/
```

---

## **4️⃣ Estado y fecha por defecto para alertas manuales**

```sql
CREATE OR REPLACE TRIGGER trg_estado_alerta
BEFORE INSERT ON ALERTA_INFESTACION
FOR EACH ROW
BEGIN
    IF :NEW.ESTADO_ALERTA IS NULL THEN
        :NEW.ESTADO_ALERTA := 'ACTIVA';
    END IF;

    IF :NEW.FECHA_ALERTA IS NULL THEN
        :NEW.FECHA_ALERTA := SYSDATE;
    END IF;
END;
/
```

---

## **5️⃣ Marcar VISITA como REALIZADA al registrar una inspección**

```sql
CREATE OR REPLACE TRIGGER trg_visita_realizada_ins
AFTER INSERT ON INSPECCION
FOR EACH ROW
BEGIN
    UPDATE VISITA
    SET ESTADO = 'REALIZADA',
        FECHA_VISITA = SYSDATE
    WHERE ID_LOTE = :NEW.ID_LOTE
      AND ID_TECNICO = :NEW.ID_TECNICO
      AND ESTADO = 'ASIGNADA';
END;
/
```

---

# 📊 Estado del proyecto (FINAL)

| Módulo                 | Estado                   |
| ---------------------- | ------------------------ |
| Conexión Oracle        | ✔                        |
| Login y roles          | ✔                        |
| CRUD Cultivos          | ✔                        |
| CRUD Plagas            | ✔                        |
| CRUD Productores       | ✔                        |
| CRUD Técnicos          | ✔                        |
| CRUD Lotes             | ✔                        |
| Relación Cultivo–Plaga | ✔                        |
| Asignación de Lotes    | ✔                        |
| Registro y Aprobación  | ✔                        |
| Visitas                | ✔                        |
| Inspecciones           | ✔                        |
| Alertas                | ✔                        |
| Reportes PDF           | ✔                        |
| Interfaz gráfica       | ✔                        |

---

# 🛠️ Tecnologías utilizadas

| Categoría    | Tecnología         |
| ------------ | ------------------ |
| Lenguaje     | Java SE 21         |
| IDE          | Apache NetBeans 25 |
| Interfaz     | Java Swing         |
| BD           | Oracle 10g XE      |
| Driver       | ojdbc11.jar        |
| Arquitectura | MVC                |
| Paradigma    | POO                |
| Reportes PDF | Jasper     |

---

# 🚀 Mejoras futuras

* Panel estadístico (cultivos más afectados, zonas críticas)
* Integración con servicios web del ICA
* Aplicación móvil para técnicos
* Mapa georreferenciado de lotes

---

# ✒️ Autores

**Darwing Yailang Bohórquez Jaimes**
**Karen Rocío Cristancho Fajardo**
Estudiantes de Ingeniería de Sistemas – IV Semestre
**Universidad de Investigación y Desarrollo (UDI)**
📅 2025
mire este es mi readme
