# 🚀 Taller Práctico Formativo: Pipeline CI/CD en Jenkins
### Test (JaCoCo) • Calidad SAST (SonarQube Quality Gate) • Publicación (Nexus) • Despliegue (Docker CD)

**Universidad Icesi**  
**Facultad de Ingeniería, Diseño y Ciencias Aplicadas**  
**Departamento de Computación y Sistemas Inteligentes (DCSI)**  
**Curso:** Ingeniería de Software V (09776 - TIC)  
**Período:** 2026-2 (Semana 10)  
**Modalidad:** Taller Formativo Guiado en Laboratorio (Sala 205M)  
**Política IAG:** Nivel 3 — Colaboración Asistida  

---

## 📄 Guía y Material de Apoyo
Los documentos oficiales de referencia y conceptualización se encuentran en el repositorio:
* **[Guía Oficial del Taller: CI/CD, SonarQube & Deploy](./Taller_Semana_10_CI_CD_Sonar_Deploy.pdf)**
* **[Presentación Teórica: Calidad, Pruebas, SonarQube & Pipelines CI/CD](./presentacion/sesion_18_calidad_pruebas_sonarqube_branching.pdf)**

---

## 🎯 Objetivos de Aprendizaje
1. **Instrumentar Cobertura de Código:** Configurar Maven y JaCoCo para recopilar métricas de cobertura de líneas y ramas durante `clean verify`.
2. **Inspección Continua (SAST):** Conectar Jenkins con SonarQube 10.x Community mediante el plugin oficial y `withSonarQubeEnv`.
3. **Bloqueo Reactivo por Quality Gate:** Detener el pipeline mediante `waitForQualityGate()` sincronizado vía Webhook de SonarQube ante incumplimiento de umbrales.
4. **Remediación de Vulnerabilidades:** Detectar y corregir defectos inyectados en el código (*CWE-330: Random débil* y *CWE-476: Null Pointer Dereference*).
5. **Gobernanza y Publicación Inmutable:** Restringir la publicación a la rama `main` para subir el binario `.jar` a Sonatype Nexus OSS y construir la imagen Docker inmutable.
6. **Despliegue Continuo (CD) y Smoke Testing:** Automatizar la sustitución del contenedor en el puerto `8085` y validar disponibilidad mediante sondeo HTTP (`/api/products`).

---

## 📁 Estructura del Repositorio

```text
.
├── Taller_Semana_10_CI_CD_Sonar_Deploy.pdf  # Guía práctica oficial del taller
├── presentacion/                            # Diapositivas teóricas y flujo CI/CD animado
│   ├── sesion_18_calidad_pruebas_sonarqube_branching.pdf
│   └── sesion_18_calidad_pruebas_sonarqube_branching.tex
├── Jenkinsfile                              # Pipeline declarativo con las 4 etapas
├── docker-compose.yml                       # Orquestación de SonarQube, backend y frontend
├── sonarqube/                               # Servicio contenerizado SonarQube 10 Community
│   └── Dockerfile                           # Definición Dockerfile para SonarQube
├── backend/                                 # Microservicio comercial Spring Boot 3
│   ├── pom.xml                              # Maven configurado con JaCoCo y Sonar plugin
│   ├── Dockerfile                           # Contenedor de producción JRE 17 Alpine
│   └── src/
│       ├── main/java/com/icesi/ingesoftv/...
│       │   ├── controller/ProductController.java
│       │   ├── service/ProductService.java       (Contiene bug CWE-476 para remediar)
│       │   ├── service/ProductSkuGenerator.java  (Contiene hotspot CWE-330 para remediar)
│       │   └── model/Product.java
│       └── test/java/com/icesi/ingesoftv/...
│           ├── ProductControllerTest.java
│           └── ProductServiceTest.java
├── frontend/                                # Cliente web React + Vite + Nginx
│   ├── Dockerfile
│   └── nginx.conf
└── README.md                                # Esta guía de inicio
```

---

## 🛠️ Requisitos Previos
* **Docker & Docker Compose v2** funcionando en la máquina de trabajo.
* **Jenkins 2.x** en ejecución (puerto `8080`) con el plugin **SonarQube Scanner** instalado.
* **Sonatype Nexus OSS 3.x** configurado (del taller de la Semana 8/9).
* **Java 17 JDK** y **Maven 3.9+** (opcional si se usa agente Docker en Jenkins).

---

## 🚀 Puesta en Marcha Paso a Paso

### 1. Iniciar SonarQube con Docker Compose
En la terminal, ejecute:
```bash
docker compose up -d sonarqube
```
Monitoree el arranque hasta ver `SonarQube is operational`:
```bash
docker logs -f sonarqube
```

### 2. Configurar SonarQube (`http://localhost:9000`)
1. **Credenciales iniciales:** Ingrese con `admin` / `admin` y cambie la contraseña.
2. **Crear Proyecto:** *Create Project* $\rightarrow$ *Manually* con Project Key: `product-api`.
3. **Generar Token:** En *Locally*, genere un token llamado `sonarqube-token` y guárdelo.
4. **Definir Quality Gate Estricto:**
   * En *Quality Gates*, cree `Icesi-DevOps-Gate`.
   * Condiciones en *New Code*:
     * Cobertura $\ge 80.0\%$
     * Nuevos Bugs $= 0$
     * Nuevas Vulnerabilidades $= 0$
   * Asócielo como predeterminado o al proyecto `product-api`.
5. **Configurar Webhook hacia Jenkins:**
   * En *Administration* $\rightarrow$ *Configuration* $\rightarrow$ *Webhooks* $\rightarrow$ *Create*:
     * **Name:** `Jenkins-Webhook`
     * **URL:** `http://<IP_HOST_JENKINS>:8080/sonarqube-webhook/` *(barra final obligatoria)*.

### 3. Configurar Jenkins (`http://localhost:8080`)
1. **Credencial del Token:** En *Administrar Jenkins* $\rightarrow$ *Credentials* $\rightarrow$ Crear credencial tipo **Secret text** con el token de SonarQube e ID: `sonarqube-token`.
2. **Servidor SonarQube:** En *Administrar Jenkins* $\rightarrow$ *System* $\rightarrow$ *SonarQube servers*:
   * **Name:** `SonarQube-Server`
   * **Server URL:** `http://<IP_HOST_SONARQUBE>:9000`
   * **Server authentication token:** `sonarqube-token`.

### 4. Probar el Pipeline y Corregir Defectos
1. Conecte este repositorio a un Pipeline en Jenkins apuntando a `Jenkinsfile`.
2. En la primera ejecución, el pipeline fallará en la etapa `Quality Gate` debido a los defectos intencionales:
   * **Hotspot (CWE-330):** En `backend/.../service/ProductSkuGenerator.java` reemplace `new Random()` por `new SecureRandom()`.
   * **Bug (CWE-476):** En `backend/.../service/ProductService.java` proteja la desreferencia de `Optional`.
3. Haga `git commit` y `git push`.
4. El pipeline pasará a estado **VERDE**, publicará en Nexus, construirá la imagen Docker y realizará el despliegue con validación HTTP en `http://localhost:8085/api/products`.

---

## 📊 Autoverificación Técnica
Consulte la Sección 4 del [PDF del Taller](./Taller_Semana_10_CI_CD_Sonar_Deploy.pdf) para verificar que las 4 etapas cumplieron todas las condiciones requeridas.

---
**Universidad Icesi • Cali, Colombia • 2026**
