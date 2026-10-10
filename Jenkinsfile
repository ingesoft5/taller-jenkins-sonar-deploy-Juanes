pipeline {
    agent any

    environment {
        // Nombre del servidor SonarQube configurado en Administrar Jenkins -> System
        SONAR_SERVER_NAME = 'SonarQube-Server'
        PROJECT_KEY       = 'product-api'
        IMAGE_NAME        = 'product-api'
        APP_PORT          = '8085'
    }

    stages {
        // =========================================================================
        // ETAPA 1: PRUEBAS Y COBERTURA DE CODIGO (JACOCO)
        // =========================================================================
        stage('Pruebas & Cobertura JaCoCo') {
            steps {
                echo '===> [ETAPA 1] Compilando y ejecutando pruebas unitarias e instrumentacion JaCoCo...'
                dir('backend') {
                    sh 'mvn clean verify'
                }
            }
            post {
                always {
                    // Publica reporte visual de pruebas JUnit en la interfaz de Jenkins
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        // =========================================================================
        // ETAPA 2: VALIDACION SAST (SONARQUBE) Y BLOQUEO POR QUALITY GATE
        // =========================================================================
        stage('SonarQube SAST Scan') {
            steps {
                echo '===> [ETAPA 2] Ejecutando analisis estatico SAST con SonarQube...'
                dir('backend') {
                    withSonarQubeEnv("${SONAR_SERVER_NAME}") {
                        sh '''
                            mvn sonar:sonar \
                              -Dsonar.projectKey=${PROJECT_KEY} \
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '===> [ETAPA 2] Esperando veredicto reactivo del Quality Gate via Webhook...'
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "PIPELINE ABORTADO: Quality Gate fallo con veredicto: ${qg.status}. Corrija defectos y cobertura."
                        }
                        echo "Quality Gate APROBADO exitosamente (${qg.status}). Cumple estandares de confiabilidad y seguridad."
                    }
                }
            }
        }

        // =========================================================================
        // ETAPA 3: PUBLICACION DE ARTEFACTOS INMUTABLES (NEXUS + DOCKER)
        // =========================================================================
        stage('Publicacion en Nexus') {
            when {
                branch 'main'
            }
            steps {
                echo '===> [ETAPA 3] Publicando binario JAR inmutable en Sonatype Nexus OSS...'
                dir('backend') {
                    sh 'mvn deploy -DskipTests'
                }
            }
        }

        stage('Empaquetar Imagen Docker') {
            when {
                branch 'main'
            }
            steps {
                echo "===> [ETAPA 3] Construyendo imagen de contenedor Docker: ${IMAGE_NAME}:${BUILD_NUMBER}..."
                sh "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} -t ${IMAGE_NAME}:latest backend/"
            }
        }

        // =========================================================================
        // ETAPA 4: DESPLIEGUE CONTINUO (CD) Y SMOKE TEST / HEALTHCHECK
        // =========================================================================
        stage('Despliegue Continuo (CD)') {
            when {
                branch 'main'
            }
            steps {
                echo "===> [ETAPA 4] Desplegando version ${IMAGE_NAME}:${BUILD_NUMBER} en puerto ${APP_PORT}..."
                sh """
                    docker stop product-api-running || true
                    docker rm product-api-running || true
                    docker run -d --name product-api-running -p ${APP_PORT}:8080 ${IMAGE_NAME}:${BUILD_NUMBER}
                """

                echo '===> [ETAPA 4] Ejecutando Smoke Test / Healthcheck de disponibilidad HTTP...'
                sh """
                    for i in {1..10}; do
                        if curl -s -f http://localhost:${APP_PORT}/api/products; then
                            echo "DESPLIEGUE EXITOSO: El microservicio responde correctamente en http://localhost:${APP_PORT}/api/products"
                            exit 0
                        fi
                        echo "Esperando arranque del servicio... (intento \$i de 10)"
                        sleep 3
                    done
                    echo "ERROR CRITICO: El microservicio no respondio a tiempo tras el despliegue."
                    exit 1
                """
            }
        }
    }

    post {
        success {
            echo '========================================================================='
            echo '🎉 PIPELINE EXITOSO: Integracion, Calidad, Publicacion y Despliegue OK!'
            echo '========================================================================='
        }
        failure {
            echo '========================================================================='
            echo '❌ PIPELINE FALLIDO: Revise la consola del stage que provoco el error.'
            echo '========================================================================='
        }
    }
}
