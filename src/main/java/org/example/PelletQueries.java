package org.example;

import openllet.jena.PelletReasonerFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.query.*;

import java.io.InputStream;

public class PelletQueries {

    public static void main(String[] args) {
        OntModelSpec ontModelSpec = PelletReasonerFactory.THE_SPEC;
        OntModel model = ModelFactory.createOntologyModel(ontModelSpec);

        model.setStrictMode(false);

        try {
            InputStream in = FileManager.get().open("src/main/resources/MovieProject2024.ttl");
            if (in == null) {
                throw new IllegalArgumentException("File not found");
            }
            model.read(in, null, "TTL");
            System.out.println("Ontology loaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        model.prepare();
        System.out.println("Model prepared with Pellet reasoning.");

        performQueries(model);
    }

    private static void performQueries(OntModel model) {
        // Query 1: Find all inferred actors
        String queryActors =
                "PREFIX : <http://www.semanticweb.org/andrewkrasuski/ontologies/MovieProject2024.owl/> " +
                        "SELECT ?actor WHERE { " +
                        "  ?actor a :Actor . " +
                        "}";
        System.out.println("Actors:");
        executeSelectQuery(model, queryActors);

        // Query 2: Find all movies and their directors
        String queryDirectedBy =
                "PREFIX : <http://www.semanticweb.org/andrewkrasuski/ontologies/MovieProject2024.owl/> " +
                        "SELECT ?movie ?director WHERE { " +
                        "  ?movie a :Movie ; " +
                        "         :directedBy ?director . " +
                        "}";
        System.out.println("Movies and Directors:");
        executeSelectQuery(model, queryDirectedBy);

        // Query 3: Find all movies and who produces them
        String queryProduces =
                "PREFIX : <http://www.semanticweb.org/andrewkrasuski/ontologies/MovieProject2024.owl/> " +
                        "SELECT ?producer ?movie WHERE { " +
                        "  ?producer a ?type ; " +
                        "            :produces ?movie . " +
                        "  FILTER(?type IN (:Producer, :Production_Company)) " +
                        "}";
        System.out.println("Movies and Producers:");
        executeSelectQuery(model, queryProduces);
    }

    private static void executeSelectQuery(OntModel model, String queryString) {
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            if (!results.hasNext()) {
                System.out.println("No results found for this query.");
            }
            ResultSetFormatter.out(System.out, results, query);
        } catch (Exception e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
