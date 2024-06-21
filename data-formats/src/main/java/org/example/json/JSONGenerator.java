package org.example.json;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.example.AbstractGenerator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONGenerator extends AbstractGenerator {
    private static final Path JSON_FILE_PATH = Paths.get("C:", "Users", "AmarKharssa", "IdeaProjects", "FU-IT", "data-formats", "src", "main", "resources", "JSONFILE.json");
    private static final String SCHEMA_FILE_PATH = "C:\\Users\\AmarKharssa\\IdeaProjects\\FU-IT\\data-formats\\src\\main\\java\\org\\example\\json\\Schema.json";

    @Override
    public void generateAndValidate() throws IOException {
        // Hauptmethode zur Generierung und Validierung der JSON-Datei
        try {
            System.out.println("Starting JSON generation and validation...");
            // Sammlungen aus der MongoDB abrufen
            MongoCollection<Document> clientsCollection = getMongoCollection("clients");
            MongoCollection<Document> openOrdersCollection = getMongoCollection("openOrders");
            MongoCollection<Document> closedOrdersCollection = getMongoCollection("closedOrders");

            // JSON-Datei generieren
            generateJSONFromMongoDB(clientsCollection, openOrdersCollection, closedOrdersCollection, JSON_FILE_PATH.toString());
            System.out.println(STR."Generated JSON file: \{JSON_FILE_PATH}");

            // JSON-Datei gegen Schema validieren
            validateJSONAgainstSchema(JSON_FILE_PATH.toString(), SCHEMA_FILE_PATH.toString());
            System.out.println(STR."Validated JSON file against schema: \{SCHEMA_FILE_PATH}");

            System.out.println("JSON generation and validation completed successfully.");

        } catch (Exception e) {
            System.err.println(STR."An error occurred: \{e.getMessage()}");
        }
    }

    private void generateJSONFromMongoDB(MongoCollection<Document> clientsCollection,
                                         MongoCollection<Document> openOrdersCollection,
                                         MongoCollection<Document> closedOrdersCollection,
                                         String jsonFilePath) throws IOException {
        System.out.println("Generating JSON from MongoDB data...");

        JSONObject jsonObject = new JSONObject();
        // Sammlungen in JSON-Arrays umwandeln
        JSONArray clientsArray = new JSONArray();
        JSONArray openOrdersArray = new JSONArray();
        JSONArray closedOrdersArray = new JSONArray();

        // Clients-Daten abrufen und in JSON umwandeln
        try (MongoCursor<Document> cursor = clientsCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                JSONObject clientObject = new JSONObject();
                clientObject.put("firstname", document.getString("firstname"));
                clientObject.put("lastname", document.getString("lastname"));
                clientObject.put("birthday", document.getDate("birthday").toInstant().toString());
                JSONObject addressObject = new JSONObject();
                Document address = document.get("address", Document.class);
                addressObject.put("street", address.getString("street"));
                addressObject.put("city", address.getString("city"));
                addressObject.put("postalCode", address.getString("postalCode"));
                addressObject.put("country", address.getString("country"));
                clientObject.put("address", addressObject);
                clientsArray.put(clientObject);
            }
        }

        try (MongoCursor<Document> cursor = openOrdersCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                JSONObject openOrderObject = new JSONObject();
                openOrderObject.put("orderId", document.getInteger("orderId"));
                openOrderObject.put("clientId", document.getInteger("clientId"));
                openOrderObject.put("product", document.getString("product"));
                openOrderObject.put("quantity", document.getInteger("quantity"));
                openOrderObject.put("price", castToDouble(document.get("price")));
                openOrderObject.put("status", document.getString("status"));
                openOrderObject.put("orderDate", document.getDate("orderDate").toInstant().toString());
                openOrdersArray.put(openOrderObject);
            }
        }

        try (MongoCursor<Document> cursor = closedOrdersCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                JSONObject closedOrderObject = new JSONObject();
                closedOrderObject.put("orderId", document.getInteger("orderId"));
                closedOrderObject.put("clientId", document.getInteger("clientId"));
                closedOrderObject.put("product", document.getString("product"));
                closedOrderObject.put("quantity", document.getInteger("quantity"));
                closedOrderObject.put("price", castToDouble(document.get("price")));
                closedOrderObject.put("status", document.getString("status"));
                closedOrderObject.put("orderDate", document.getDate("orderDate").toInstant().toString());
                closedOrderObject.put("deliveryDate", document.getDate("deliveryDate").toInstant().toString());
                closedOrdersArray.put(closedOrderObject);
            }
        }
        // JSON-Objekt zusammenstellen und in Datei schreiben
        jsonObject.put("clients", clientsArray);
        jsonObject.put("openOrders", openOrdersArray);
        jsonObject.put("closedOrders", closedOrdersArray);

        try (FileWriter fileWriter = new FileWriter(jsonFilePath)) {
            fileWriter.write(jsonObject.toString(4));
        }
        System.out.println(STR."JSON generated successfully: \{jsonFilePath}");
    }

    private Double castToDouble(Object value) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else {
            throw new IllegalArgumentException(STR."Unsupported data type for price: \{value.getClass().getName()}");
        }
    }

    private void validateJSONAgainstSchema(String jsonFilePath, String schemaFilePath) throws IOException {
        System.out.println("Validating JSON against schema...");

        JSONObject rawSchema = new JSONObject(new JSONTokener(new FileInputStream(schemaFilePath)));
        Schema schema = SchemaLoader.load(rawSchema);

        JSONObject json = new JSONObject(new JSONTokener(new FileInputStream(jsonFilePath)));
        schema.validate(json);
        System.out.println("JSON validated successfully against schema.");
    }
}