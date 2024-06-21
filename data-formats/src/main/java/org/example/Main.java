package org.example;

import org.example.json.JSONGenerator;
import org.example.xml.XMLGenerator;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        //generate and validate XML
        XMLGenerator xmlGenerator = new XMLGenerator();
        xmlGenerator.generateAndValidate();

        //generate and validate JSON
        JSONGenerator jsonGenerator = new JSONGenerator();
        jsonGenerator.generateAndValidate();
    }
}
