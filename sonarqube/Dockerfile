# =========================================================================
# Dockerfile para SonarQube Community Edition 10.x
# Ingenieria de Software V - Universidad Icesi
# =========================================================================
FROM sonarqube:10-community

# Metadatos informativos
LABEL maintainer="IasLab - Universidad Icesi"
LABEL description="SonarQube Community Edition 10.x para analisis SAST y Quality Gates en Jenkins"
LABEL course="Ingenieria de Software V (Semana 10)"

# Puerto web oficial de la interfaz y la API de SonarQube
EXPOSE 9000

# Desactiva validaciones agresivas de bootstrap de Elasticsearch para ejecucion rapida en laboratorio
ENV SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true

# Directorio de trabajo
WORKDIR /opt/sonarqube

# Healthcheck para verificar disponibilidad del servicio
HEALTHCHECK --interval=30s --timeout=10s --retries=5 --start-period=60s \
  CMD curl -f http://localhost:9000/api/system/status || exit 1
