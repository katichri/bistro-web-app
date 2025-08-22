# Bistro Web App

## Voraussetzungen

- Docker & Docker Compose
- Java 21+
- Maven 3

## Datenbank mit Docker Compose starten

Im Projekt befindet sich eine `docker-compose.yml`, die eine PostgreSQL-Datenbank bereitstellt.

```bash
docker-compose up -d
```

Die Datenbank ist dann unter `localhost:5432` erreichbar.

## Anwendung starten (mit Run-Profile)

Das Spring Boot-Profil `dev` ist für lokale Entwicklung vorgesehen und nutzt die Docker-Datenbank.

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Wichtige Umgebungsvariablen

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Diese sind im Profil `dev` vorkonfiguriert. Die eingechecke .env Datei sollte normalerweise nie eingecheckt werden - hier nur zu demonstrationszwecken.

## Postman Collections

Im Ordner `postman/` findest du Beispiel-Collections für die API.

### Import und Nutzung

1. Öffne Postman.
2. Wähle `Import` und lade die gewünschte Collection aus dem `postman/`-Ordner.
3. Passe ggf. die Umgebungsvariablen (z.B. Host, Port) an.
4. Führe die Requests aus, um die API zu testen.

## Flyway Migrationen

Die Datenbankmigrationen werden automatisch beim Start der Anwendung mit [Flyway](https://flywaydb.org/) ausgeführt. 
Lege neue Migrationen im Verzeichnis `src/main/resources/db/migration` als SQL-Dateien ab. 
Flyway sorgt dafür, dass die Datenbank immer auf dem aktuellen Stand ist.

## OpenAPI Dokumentation

Die API-Dokumentation wird automatisch per OpenAPI (Swagger) bereitgestellt.

Die API Files werden via Open-Api Generator Plugin in Java-Klassen generiert und über die RestController implementiert.

## CSV-Import

Eine CSV-Datei für Produktdaten wird über Spring Integration importiert. 

## Authentifizierung

Die API verwendet BASIC Auth zur Absicherung der Endpunkte. 
Die Creds sind konfiguriert in der .env.

## Hinweise

- Beende die Datenbank mit `docker-compose down`.
- Für Produktivbetrieb verwende ein anderes Profil und sichere die Zugangsdaten.
