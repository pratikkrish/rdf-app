package com.tnob;

import com.hp.hpl.jena.rdf.model.*;
import com.tnob.mapper.RDFConstants;
import org.neo4j.graphdb.GraphDatabaseService;

import java.util.*;

/**
 * Created by tahmid on 2/24/15.
 */
public class RDFNodeMapper extends RDFModelIterator{


    public static final String rdfPrefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String rdfsPrefix = "http://www.w3.org/2000/01/rdf-schema#";

    public static final String typePrefix = rdfPrefix + "type";
    public static final String labelPrefix = rdfsPrefix + "label";
    private GraphDatabaseService db;
    private Map<String, Map<String, String>> nodeAttributeMap;

    public RDFNodeMapper(Model model) {
        super(model);
        nodeAttributeMap = new HashMap<>();
    }

    @Override
    protected void doInIterator(Statement stmt) {
        Resource subject = stmt.getSubject();
        Property predicate = stmt.getPredicate();
        RDFNode object = stmt.getObject();

        String resourceURI = subject.toString();
        String attributeName = predicate.toString();
        String attributeValue = object.toString();

        Map attributesMap;
        if(nodeAttributeMap.containsKey(resourceURI)) {
            attributesMap = nodeAttributeMap.get(resourceURI);
        } else {
            attributesMap = new HashMap<>();
            nodeAttributeMap.put(resourceURI, attributesMap);
        }
        addAttributes(attributesMap, attributeName, attributeValue);

    }

    private void addAttributes(Map attributesMap, String attributeName, String attributeValue) {
        if (attributeName.equals(labelPrefix)) {
            attributesMap.put(RDFConstants.LABEL, attributeValue);
        }
        else if (attributeName.equals(typePrefix)) {
            String[] splitAttributeValue = attributeValue.split("/");
            attributeValue = splitAttributeValue[splitAttributeValue.length - 1];
            attributesMap.put(RDFConstants.TYPE, attributeValue);
        } else {
            if (!attributeValue.contains("http://www.wikipedia.org")) {
                attributesMap.put(attributeName, attributeValue);
            }
        }
    }

    public void printNode() {
        for (String resourceURI : nodeAttributeMap.keySet()) {
            System.out.println("URI: " + resourceURI);
            Map<String, String> attributesMap = nodeAttributeMap.get(resourceURI);
            for (String attributeName : attributesMap.keySet()) {
                System.out.println("\t\t" + attributeName + " : " + attributesMap.get(attributeName));
                /*if (attributesMap.get(attributeName).contains("Wiki")) {
                    System.out.println(resourceURI + "\t\t" + attributeName + " : " + attributesMap.get(attributeName));
                }*/
            }
        }
    }


    public Map<String, Map<String, String>> getNodeAttributeMap() {
        return nodeAttributeMap;
    }
}
