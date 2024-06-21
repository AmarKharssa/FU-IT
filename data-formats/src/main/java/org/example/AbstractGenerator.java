package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public abstract class AbstractGenerator {
    // Verbindungseinstellungen für die MongoDB
    protected String host = "localhost";
    protected int port = 27017;
    protected String databaseName = "Clients";
    protected String username = "root";
    protected String password = "root";

    // Methode zum Abrufen einer MongoDB-Sammlung
    protected MongoCollection<Document> getMongoCollection(String collectionName) {
        String connectionString = "mongodb://" + username + ":" + password + "@" + host + ":" + port + "/?authSource=admin";
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        System.out.println("Connection was successful");
        return database.getCollection(collectionName);
    }
    // Abstrakte Methode zur Generierung und Validierung, die von Unterklassen implementiert wird
    public abstract void generateAndValidate() throws Exception;
}