package workspacep;

import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import static org.neo4j.driver.Values.parameters;

public class SistemaDeRecomendaciones implements TransactionWork<LinkedList<String>> {
    private String usuario; 

    public SistemaDeRecomendaciones(String usuario) {  
        this.usuario = usuario;
    }

    public String getUsuario() {
        return usuario;
    }

    @Override
    public LinkedList<String> execute(Transaction tx) {
        String query = "MATCH (yo:Usuario {name: $usuario})-[:Amigo]->(amigo:Usuario)-[:leido]->(libro:Libro)\n" +
                    "WHERE NOT (yo)-[:leido]->(libro)\n" +
                    "AND NOT (yo)-[:guardado]->(libro)\n" +
                    "RETURN libro.titulo AS titulo";
        
        Result result = tx.run(query, parameters("usuario", this.usuario));
        
        LinkedList<String> recomendacionAmigos = new LinkedList<>();
        List<Record> registros = result.list();
        
        for (Record registro : registros) {  
            recomendacionAmigos.add(registro.get("titulo").asString());  
        }
        
        return recomendacionAmigos;
    }
}