# ===== Build =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ===== Runtime =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /deployments

COPY --from=build /build/target/*.jar app.jar

# Dossier des PDFs uploadés (voir FileUploadController: chemin relatif "uploads").
# À monter comme volume persistant sur OpenShift, sinon les fichiers sont perdus
# à chaque redéploiement du pod.
RUN mkdir -p /deployments/uploads

# OpenShift lance les conteneurs avec un UID arbitraire non-root appartenant
# au groupe racine (gid 0) : il faut donc que /deployments soit accessible
# en écriture par le groupe, pas seulement par un utilisateur fixe.
RUN chgrp -R 0 /deployments && chmod -R g=u /deployments

EXPOSE 9090
USER 1001

ENTRYPOINT ["java", "-jar", "/deployments/app.jar"]