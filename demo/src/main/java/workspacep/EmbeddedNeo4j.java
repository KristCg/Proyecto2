package workspacep;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.summary.ResultSummary;

import static org.neo4j.driver.Values.parameters;

import java.util.LinkedList;
import java.util.List;
/**
 * @author Administrator
 *
 */
public class EmbeddedNeo4j implements AutoCloseable{

    private final Driver driver;
    
    public EmbeddedNeo4j( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

public void agregarLibro(String usuario, String titulo, String autor, String genero, int anio) {
    try (Session session = driver.session()) {
        try (Transaction tx = session.beginTransaction()) {
            tx.run("""
                MERGE (a:Autor {nombre: $autor})
                MERGE (g:Genero {genero: $genero})
                MERGE (l:Libro {titulo: $titulo, publicacion: $anio})
                MERGE (a)-[:Autor]->(l)
                MERGE (l)-[:Genero_A]->(g)
                WITH l
                MATCH (u:Usuario {nombre: $usuario})
                MERGE (u)-[:Leido]->(l)
                """,
                parameters("usuario", usuario, "titulo", titulo, "autor", autor, "genero", genero, "anio", anio));
            tx.commit();
        }
    }
}

public void guardarLibro(String usuario, String titulo) {
    try (Session session = driver.session()) {
        try (Transaction tx = session.beginTransaction()) {
            tx.run("""
                MATCH (u:Usuario {nombre: $usuario})
                MATCH (l:Libro {titulo: $titulo})
                MERGE (u)-[:Guardado]->(l)
                """,
                parameters("usuario", usuario, "titulo", titulo));
            tx.commit();
        }
    }
}

public List<String> obtenerLibrosGuardados(String usuario) {
    List<String> guardados = new LinkedList<>();
    try (Session session = driver.session()) {
        try (Transaction tx = session.beginTransaction()) {
            Result result = tx.run("""
                MATCH (u:Usuario {nombre: $usuario})-[:Guardado]->(l:Libro)
                RETURN l.titulo AS titulo
                """,
                parameters("usuario", usuario));
            while (result.hasNext()) {
                guardados.add(result.next().get("titulo").asString());
            }
            tx.commit();
        }
    }
    return guardados;
}



    


}
