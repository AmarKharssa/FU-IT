package org.example.xml;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.example.AbstractGenerator;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class XMLGenerator extends AbstractGenerator {
    private static final Path XML_FILE_PATH = Paths.get("C:", "Users", "AmarKharssa", "IdeaProjects", "FU-IT", "data-formats", "src", "main", "resources", "xmlFile.xml");
    private static final String SCHEMA_FILE_PATH = "C:\\Users\\AmarKharssa\\IdeaProjects\\FU-IT\\data-formats\\src\\main\\java\\org\\example\\xml\\schema.xsd";

    @Override
    public void generateAndValidate() {
        // Hauptmethode zur Generierung und Validierung der XML-Datei
        try {
            System.out.println("Starting XML generation and validation...");

            // Sammlungen aus der MongoDB abrufen
            MongoCollection<Document> clientsCollection = getMongoCollection("clients");
            MongoCollection<Document> openOrdersCollection = getMongoCollection("openOrders");
            MongoCollection<Document> closedOrdersCollection = getMongoCollection("closedOrders");

            // XML-Datei generieren
            generateXMLFromMongoDB(clientsCollection, openOrdersCollection, closedOrdersCollection, String.valueOf(XML_FILE_PATH));
            System.out.println(STR."Generated XML file: \{XML_FILE_PATH}");

            // XML-Datei gegen Schema validieren
            validateXMLAgainstSchema(String.valueOf(XML_FILE_PATH), SCHEMA_FILE_PATH);
            System.out.println(STR."Validated XML file against schema: \{SCHEMA_FILE_PATH}");

            System.out.println("XML generation and validation completed successfully.");

        } catch (Exception e) {
            System.err.println(STR."An error occurred: \{e.getMessage()}");
            e.printStackTrace();
        }
    }

    private void generateXMLFromMongoDB(MongoCollection<Document> clientsCollection, MongoCollection<Document> openOrdersCollection,
                                        MongoCollection<Document> closedOrdersCollection, String xmlFilePath) throws ParserConfigurationException, IOException, TransformerException {
        System.out.println("Generating XML from MongoDB data...");

        // Generiert XML-Dateien aus MongoDB-Daten
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.newDocument();

        // Root element
        Element rootElement = doc.createElement("database");
        doc.appendChild(rootElement);

        // Clients
        Element clientsElement = doc.createElement("clients");
        rootElement.appendChild(clientsElement);

        MongoCursor<Document> cursor = clientsCollection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document clientDoc = cursor.next();
                Element clientElement = doc.createElement("client");

                appendChildElement(doc, clientElement, "firstname", clientDoc.getString("firstname"));
                appendChildElement(doc, clientElement, "lastname", clientDoc.getString("lastname"));
                appendChildElement(doc, clientElement, "birthday", clientDoc.getDate("birthday").toString());

                Element addressElement = doc.createElement("address");
                Document addressDoc = (Document) clientDoc.get("address");
                appendChildElement(doc, addressElement, "street", addressDoc.getString("street"));
                appendChildElement(doc, addressElement, "city", addressDoc.getString("city"));
                appendChildElement(doc, addressElement, "postalCode", addressDoc.getString("postalCode"));
                appendChildElement(doc, addressElement, "country", addressDoc.getString("country"));

                clientElement.appendChild(addressElement);
                clientsElement.appendChild(clientElement);
            }
        } finally {
            cursor.close();
        }

        // Open Orders
        Element openOrdersElement = doc.createElement("openOrders");
        rootElement.appendChild(openOrdersElement);

        cursor = openOrdersCollection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document orderDoc = cursor.next();
                Element orderElement = doc.createElement("order");

                appendChildElement(doc, orderElement, "orderId", orderDoc.getInteger("orderId").toString());
                appendChildElement(doc, orderElement, "clientId", orderDoc.getInteger("clientId").toString());
                appendChildElement(doc, orderElement, "product", orderDoc.getString("product"));
                appendChildElement(doc, orderElement, "quantity", orderDoc.getInteger("quantity").toString());
                appendChildElement(doc, orderElement, "price", orderDoc.get("price").toString());
                appendChildElement(doc, orderElement, "status", orderDoc.getString("status"));
                appendChildElement(doc, orderElement, "orderDate", orderDoc.getDate("orderDate").toString());

                openOrdersElement.appendChild(orderElement);
            }
        } finally {
            cursor.close();
        }

        // Closed Orders
        Element closedOrdersElement = doc.createElement("closedOrders");
        rootElement.appendChild(closedOrdersElement);

        cursor = closedOrdersCollection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document orderDoc = cursor.next();
                Element orderElement = doc.createElement("order");

                appendChildElement(doc, orderElement, "orderId", orderDoc.getInteger("orderId").toString());
                appendChildElement(doc, orderElement, "clientId", orderDoc.getInteger("clientId").toString());
                appendChildElement(doc, orderElement, "product", orderDoc.getString("product"));
                appendChildElement(doc, orderElement, "quantity", orderDoc.getInteger("quantity").toString());
                appendChildElement(doc, orderElement, "price", orderDoc.get("price").toString());
                appendChildElement(doc, orderElement, "status", orderDoc.getString("status"));
                appendChildElement(doc, orderElement, "orderDate", orderDoc.getDate("orderDate").toString());
                appendChildElement(doc, orderElement, "deliveryDate", orderDoc.getDate("deliveryDate").toString());

                closedOrdersElement.appendChild(orderElement);
            }
        } finally {
            cursor.close();
        }

        // Write the content into XML file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlFilePath));

        transformer.transform(source, result);
        System.out.println(STR."XML generated successfully: \{xmlFilePath}");
    }

    private void appendChildElement(org.w3c.dom.Document doc, Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        if (tagName.equals("birthday") || tagName.equals("orderDate") || tagName.equals("deliveryDate")) {
            // Parse the date string into a java.util.Date
            Date date = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                date = sdf.parse(textContent);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Convert java.util.Date to OffsetDateTime
            Instant instant = Instant.ofEpochMilli(date.getTime());
            OffsetDateTime offsetDateTime = instant.atOffset(ZoneOffset.UTC); // Adjust ZoneOffset as necessary

            // Format the OffsetDateTime according to XML Schema dateTime format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
            textContent = offsetDateTime.format(formatter);
        }
        element.setTextContent(textContent);
        parent.appendChild(element);
    }

    private void validateXMLAgainstSchema(String xmlFilePath, String schemaFilePath) throws Exception {
        System.out.println("Validating XML against schema...");

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new File(schemaFilePath));

        Validator validator = schema.newValidator();
        Source source = new StreamSource(new File(xmlFilePath));
        validator.validate(source);
        System.out.println("XML validated successfully against schema.");
    }
}