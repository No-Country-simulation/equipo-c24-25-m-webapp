# Imagen base (Java 17 JDK)
FROM eclipse-temurin:17.0.13_11-jdk

# Definir directorio de trabajo en el contenedor
WORKDIR /root

# Copiar el archivo pom.xml al contenedor
COPY ./pom.xml /root

# Copiar el directorio .mvn al contenedor
COPY ./.mvn /root/.mvn

# Copiar el script del Maven Wrapper(mvn comprimido)
COPY ./mvnw /root

# Descargar dependencias de Maven
RUN ./mvnw dependency:go-offline

# Copiar el código fuente al contenedor
COPY ./src /root/src

# Construir la aplicación sin ejecutar los tests
RUN ./mvnw clean install -DskipTests

# Informar el puerto donde se ejecutará la aplicación
EXPOSE 8080

# Iniciar la aplicación cuando el contenedor se ejecute
ENTRYPOINT ["java", "-jar", "/root/target/banka-0.0.1-SNAPSHOT.jar"]