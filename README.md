# Programmdokumentation für JSON- und XML-Generierung und Validierung

## Dokumentation für das Projekt "data-formats"

Diese Dokumentation bietet eine umfassende Übersicht über das Projekt "data-formats", das die Generierung und Validierung von JSON- und XML-Daten aus einer MongoDB-Datenbank mittels Java umfasst. Das Projekt nutzt Docker für die MongoDB-Instanz, JSON Schema für die JSON-Validierung und XML Schema für die XML-Validierung.

## Inhaltsverzeichnis

1. [Einleitung](#1-einleitung)
2. [Architekturübersicht](#2-architekturübersicht)
3. [Klassen und deren Funktionalität](#3-klassen-und-deren-funktionalität)
    - [AbstractGenerator](#abstractgenerator)
    - [JSONGenerator](#jsongenerator)
    - [XMLGenerator](#xmlgenerator)
4. [Verwendung von Docker](#4-verwendung-von-docker)
5. [JSON-Generierung und Validierung](#5-json-generierung-und-validierung)
6. [XML-Generierung und Validierung](#6-xml-generierung-und-validierung)
7. [Schema-Dateien](#7-schema-dateien)
    - [JSON Schema](#json-schema)
    - [XML Schema](#xml-schema)
8. [Abhängigkeiten und Build-Konfiguration](#8-abhängigkeiten-und-build-konfiguration)

---

## 1. Einleitung

Das Projekt "data-formats" demonstriert die Implementierung eines Systems zur Generierung und Validierung von JSON- und XML-Daten aus einer MongoDB-Datenbank. Es verwendet Java für die Implementierung der Datenverarbeitung und -validierung, Docker für die Bereitstellung der MongoDB-Instanz sowie JSON Schema und XML Schema für die Validierung der generierten Daten.

## 2. Architekturübersicht

Die Architektur besteht aus drei Hauptkomponenten:

- **Java-Anwendung:** Verwendet die MongoDB Java Driver, JSON Schema und XML Schema Libraries für die Datenverarbeitung und -validierung.
- **Docker:** Hostet eine MongoDB-Instanz zur Bereitstellung der Datenbank.
- **Schema-Dateien:** Definieren die Struktur der generierten JSON- und XML-Daten und werden für die Validierung verwendet.

## 3. Klassen und deren Funktionalität

### AbstractGenerator

Die abstrakte Klasse `AbstractGenerator` stellt die grundlegende Infrastruktur für die Generierung und Validierung von Daten bereit. Sie enthält die Verbindung zur MongoDB-Datenbank und abstrakte Methoden, die in den konkreten Implementierungen verwendet werden.

- **Methoden:**
    - `getMongoCollection(String collectionName)`: Stellt eine Verbindung zur MongoDB-Datenbank her und gibt die angeforderte Sammlung zurück.

### JSONGenerator

Die Klasse `JSONGenerator` implementiert die Spezifika für die Generierung und Validierung von JSON-Daten.

- **Funktionalität:**
    - **Generierung:** Liest Daten aus den MongoDB-Sammlungen `clients`, `openOrders` und `closedOrders` aus und generiert eine JSON-Datei basierend auf der definierten Struktur.
    - **Validierung:** Überprüft die generierte JSON-Datei gegen ein JSON Schema.

### XMLGenerator

Die Klasse `XMLGenerator` implementiert die Spezifika für die Generierung und Validierung von XML-Daten.

- **Funktionalität:**
    - **Generierung:** Liest Daten aus den MongoDB-Sammlungen `clients`, `openOrders` und `closedOrders` aus und generiert eine XML-Datei basierend auf der definierten Struktur.
    - **Validierung:** Überprüft die generierte XML-Datei gegen ein XML Schema.

## 4. Verwendung von Docker

Das Projekt verwendet Docker zur Bereitstellung einer MongoDB-Instanz. Die Konfiguration erfolgt über eine `docker-compose.yaml`-Datei, die die MongoDB mit den erforderlichen Umgebungsvariablen für Benutzername und Passwort konfiguriert.

- **docker-compose.yaml:**

    ```yaml
    version: '3.7'
    services:
      mongodb_container:
        image: mongo:latest
        environment:
          MONGO_INITDB_ROOT_USERNAME: root
          MONGO_INITDB_ROOT_PASSWORD: root
        ports:
          - 27017:27017
        volumes:
          - mongodb_data_container:/data/db
    
    volumes:
      mongodb_data_container:
    
    ```

## 5. JSON-Generierung und Validierung

Die JSON-Generierung und Validierung erfolgt in der Klasse `JSONGenerator`.

- **Generierung:**
    - Liest Daten aus MongoDB-Sammlungen.
    - Erstellt JSON-Objekte entsprechend der Datenstruktur.
    - Schreibt die generierten Daten in eine JSON-Datei.
- **Validierung:**
    - Lädt das JSON Schema.
    - Validiert die generierte JSON-Datei gegen das Schema.

## 6. XML-Generierung und Validierung

Die XML-Generierung und Validierung erfolgt in der Klasse `XMLGenerator`.

- **Generierung:**
    - Liest Daten aus MongoDB-Sammlungen.
    - Erstellt XML-Dokumente entsprechend der Datenstruktur.
    - Schreibt die generierten Daten in eine XML-Datei.
- **Validierung:**
    - Lädt das XML Schema.
    - Validiert die generierte XML-Datei gegen das Schema.

## 7. Schema-Dateien

### JSON Schema

Das JSON Schema definiert die Struktur der generierten JSON-Daten für `clients`, `openOrders` und `closedOrders`.

- **Beispiel:**

    ```json
    {
      "$schema": "<http://json-schema.org/draft-07/schema#>",
      "type": "object",
      "properties": {
        "clients": {
          "type": "array",
          "items": {
            "type": "object",
            "properties": {
              "firstname": { "type": "string" },
              "lastname": { "type": "string" },
              "birthday": { "type": "string", "format": "date-time" },
              "address": {
                "type": "object",
                "properties": {
                  "street": { "type": "string" },
                  "city": { "type": "string" },
                  "postalCode": { "type": "string" },
                  "country": { "type": "string" }
                },
                "required": ["street", "city", "postalCode", "country"]
              }
            },
            "required": ["firstname", "lastname", "birthday", "address"]
          }
        },
        // Weitere Definitionen für openOrders und closedOrders
      },
      "required": ["clients", "openOrders", "closedOrders"]
    }
    
    ```

### XML Schema

Das XML Schema definiert die Struktur der generierten XML-Daten für `clients`, `openOrders` und `closedOrders`.

- **Beispiel:**

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <xs:schema xmlns:xs="<http://www.w3.org/2001/XMLSchema>">
        <xs:element name="database">
            <xs:complexType>
                <xs:sequence>
                    <xs:element name="clients">
                        <xs:complexType>
                            <xs:sequence>
                                <xs:element name="client" maxOccurs="unbounded">
                                    <xs:complexType>
                                        <xs:sequence>
                                            <xs:element name="firstname" type="xs:string"/>
                                            <xs:element name="lastname" type="xs:string"/>
                                            <xs:element name="birthday" type="xs:dateTime"/>
                                            <xs:element name="address">
                                                <xs:complexType>
                                                    <xs:sequence>
                                                        <xs:element name="street" type="xs:string"/>
                                                        <xs:element name="city" type="xs:string"/>
                                                        <xs:element name="postalCode" type="xs:string"/>
                                                        <xs:element name="country" type="xs:string"/>
                                                    </xs:sequence>
                                                </xs:complexType>
                                            </xs:element>
                                        </xs:sequence>
                                    </xs:complexType>
                                </xs:element>
                            </xs:sequence>
                        </xs:complexType>
                    </xs:element>
                    // Weitere Definitionen für openOrders und closedOrders
                </xs:sequence>
            </xs:complexType>
        </xs:element>
    </xs:schema>
    
    ```

## 8. Abhängigkeiten und Build-Konfiguration

Das Projekt verwendet Maven zur Verwaltung von Abhängigkeiten und zum Build-Prozess. Es sind verschiedene Bibliotheken eingebunden, darunter MongoDB Java Driver, JSON Schema, XML Schema und andere.

- **Maven Build-Konfiguration:**

    ```xml
    <dependencies>
        <!-- Abhängigkeiten für MongoDB, JSON, XML, etc. -->
    </dependencies>
    <build>
        <!-- Maven Build-Konfiguration -->
    </build>
    
    ```
