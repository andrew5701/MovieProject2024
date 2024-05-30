// Andrew Krasuski

package org.example;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import openllet.jena.PelletReasonerFactory;

import static org.apache.jena.assembler.JA.baseModel;

public class MovieQueries {

    private static final String TURTLE_FILE = "src/main/resources/MovieProject2024.ttl";

    public static void main(String[] args) {
        Model model = FileManager.get().loadModel(TURTLE_FILE, null, "TTL");

        // OntModel ontModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, baseModel.getModel());
        // ontModel.prepare();

        System.out.println("Query 1: Find the actors who directed the movies they were also actors in\n");
        System.out.println("------------------------------------------------------------");
        executeSPARQLQuery(model, query1());

        System.out.println("\nQuery 2: Return the actors who have awards\n");
        System.out.println("------------------------------------------------------------");
        executeSPARQLQuery(model, query2());

        System.out.println("\nQuery 3: Find movies released in 1990\n");
        System.out.println("------------------------------------------------------------");
        executeSPARQLQuery(model, query3());

        System.out.println("\nQuery 4: Construct a graph of movies and their directors\n");
        System.out.println("------------------------------------------------------------");
        executeConstructQuery(model, query4());

        System.out.println("\nQuery 5: Return true or false whether a movie with a duration over 120 minutes exists\n");
        System.out.println("------------------------------------------------------------");
        executeAskQuery(model, query5());

        System.out.println("\nQuery 6: Find movies produced by WaltDisneyPictures\n");
        executeSPARQLQuery(model, query6());

        System.out.println("\nQuery 7: Describe the movie 'The Lion King'\n");
        executeDescribeQuery(model, query7());
    }

    private static Model loadModel(String inputFileName) {
        Model model = ModelFactory.createDefaultModel();
        FileManager.get().readModel(model, inputFileName);
        return model;
    }

    private static void executeSPARQLQuery(Model model, String queryString) {
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out(System.out, results, query);
        } catch (Exception e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executeConstructQuery(Model model, String queryString) {
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            Model resultModel = qexec.execConstruct();
            System.out.println("Constructed Model:");
            resultModel.write(System.out, "TTL");
        } catch (Exception e) {
            System.err.println("Error executing CONSTRUCT query: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executeAskQuery(Model model, String queryString) {
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            boolean result = qexec.execAsk();
            System.out.println("Ask Query Result: " + result);
        } catch (Exception e) {
            System.err.println("Error executing ASK query: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executeDescribeQuery(Model model, String queryString) {
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            Model resultModel = qexec.execDescribe();
            System.out.println("Described Model:");
            resultModel.write(System.out, "TTL");
        } catch (Exception e) {
            System.err.println("Error executing DESCRIBE query: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String query1() {
        return """
            PREFIX : <http://www.semanticweb.org/andrewkrasuski/ontologies/MovieProject2024.owl/>
            SELECT ?actor ?movie WHERE {
                ?actor :playsIn ?movie ;
                        :directed ?movie .
            }
            """;
    }

    private static String query2() {
        return """
            PREFIX : <http://www.semanticweb.org/andrewkrasuski/ontologies/MovieProject2024.owl/>
            SELECT ?actor ?award WHERE {
                ?actor :playsIn ?movie ;
                        :hasAward ?award .
            }
            """;
    }

    private static String query3() {
        return """
            PREFIX : <http://www.semanticweb.org/andrewkrasuski/ontologies/MovieProject2024.owl/>
            SELECT ?movie ?title WHERE {
                ?movie a :Movie ;
                        :releaseDate ?date ;
                        :movieTitle ?title .
                FILTER (year(?date) = 1990)
            }
            """;
    }

    private static String query4() {
        return """
            PREFIX : <http://www.semanticweb.org/andrewkrasuski/ontologies/MovieProject2024.owl/>
            CONSTRUCT {
                ?movie :movieTitle ?title ;
                        :directedBy ?director .
                ?director :firstName ?firstName ;
                          :lastName ?lastName .
            }
            WHERE {
                ?movie a :Movie ;
                        :movieTitle ?title .
                ?director :directed ?movie ;
                          :firstName ?firstName ;
                          :lastName ?lastName .
            }
            """;
    }

    private static String query5() {
        return """
        PREFIX : <http://www.semanticweb.org/andrewkrasuski/ontologies/MovieProject2024.owl/>
        ASK {
            ?movie a :Movie ;
                    :hasGenre :Action ;
                    :duration ?duration .
            FILTER (?duration > 120)
        }
        """;
    }

    private static String query6() {
        return """
            PREFIX : <http://www.semanticweb.org/andrewkrasuski/ontologies/MovieProject2024.owl/>
            SELECT ?movie ?title WHERE {
                ?movie a :Movie ;
                        :producedBy :WaltDisneyPictures ;
                        :movieTitle ?title .
            }
            """;
    }

    private static String query7() {
        return """
            PREFIX : <http://www.semanticweb.org/andrewkrasuski/ontologies/MovieProject2024.owl/>
            DESCRIBE :TheLionKing
            """;
    }
}
