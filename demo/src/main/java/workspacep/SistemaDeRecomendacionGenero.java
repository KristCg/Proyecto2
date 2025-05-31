package workspacep;

import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import static org.neo4j.driver.Values.parameters;

public class SistemaDeRecomendacionGenero implements TransactionWork<LinkedList<String>> {
    private String username;

    public SistemaDeRecomendacionGenero(String username) {
        this.username = username;
    }

    @Override
    public LinkedList<String> execute(Transaction tx) {
        String query =
            "MATCH (u:Usuario {name: $username})-[:Interes_en]->(g:Genero)<-[:genero]-(l:Libro) " +
            "WHERE NOT (u)-[:leido]->(l) " +
            "AND NOT (u)-[:guardado]->(l) " +
            "RETURN DISTINCT l.titulo AS titulo";

        Result result = tx.run(query, parameters("username", this.username));
        
        LinkedList<String> recomendacionGenero = new LinkedList<>();
        List<Record> registros = result.list();

        for (Record registro : registros) {
            recomendacionGenero.add(registro.get("titulo").asString());
        }

        return recomendacionGenero;
    }
}
