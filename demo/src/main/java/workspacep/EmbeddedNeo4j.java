package workspacep;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

import static org.neo4j.driver.Values.parameters;

import java.util.ArrayList;
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

    public Driver getDriver() {
        return driver;
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public boolean validarUsuario(String name) {
            try (Session session = driver.session()) {
                return session.executeRead(tx -> {
                    Result result = tx.run(
                        "MATCH (u:Usuario {name: $nombre}) RETURN count(u) > 0",
                        parameters("nombre", name));
                    return result.single().get(0).asBoolean();
                });
            }
        }

    public void registrarUsuario(String nombreUsuario, String password) {
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                tx.run("CREATE (u:Usuario {name: $usuario, password: $password})",
                        parameters("usuario", nombreUsuario, "password", password));
                return null;
            });
        }
    }

    public void agregarInteres(String name, String genero) {
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (u:Usuario {name: $usuario}) " +
                    "MERGE (g:Genero {genero: $genero}) " +
                    "MERGE (u)-[:Interes_en]->(g)",
                    parameters("usuario", name, "genero", genero));
                return null;
            });
        }
    }

    public List<String> getGeneros() {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                Result result = tx.run("MATCH (g:Genero) RETURN g.genero ORDER BY g.genero");
                List<String> generos = new ArrayList<>();
                while (result.hasNext()) {
                    generos.add(result.next().get(0).asString());
                }
                return generos;
            });
        }
    }

    public void agregarLibro(String usuario, String titulo, String autor, String genero, int anio) {
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                tx.run("""
                    MERGE (a:Autor {nombre: $autor})
                    MERGE (g:Genero {genero: $genero})
                    MERGE (l:Libro {titulo: $titulo, publicacion: $anio})
                    MERGE (a)-[:Autor_de]->(l)  
                    MERGE (l)-[:genero]->(g)    
                    WITH l
                    MATCH (u:Usuario {nombre: $usuario})
                    MERGE (u)-[:Leido]->(l)
                    MERGE (u)-[:Interes_en]->(g)
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

    public List<String> obtenerRecomendaciones(String usuario) {
    List<String> recomendaciones = new ArrayList<>();
    try (Session session = driver.session()) {
        try (Transaction tx = session.beginTransaction()) {
            Result result = tx.run("""
                MATCH (l:Libro)-[:genero]->(g:Genero)<-[:genero]-(l2:Libro)<-[:Leido]-(u:Usuario)
                WITH g, COUNT(*) AS popularidad
                ORDER BY popularidad DESC
                LIMIT 2
                MATCH (g)<-[:genero]-(libro:Libro)
                WHERE NOT EXISTS((:Usuario {nombre: $usuario})-[:Leido]->(libro))
                RETURN DISTINCT libro.titulo AS recomendacion
                LIMIT 5
                """,
                parameters("usuario", usuario));
            while (result.hasNext()) {
                recomendaciones.add(result.next().get("recomendacion").asString());
            }
            tx.commit();
        }
    }
    return recomendaciones;
}

    public List<String> obtenerLibrosLeidos(String usuario) {
    List<String> leidos = new LinkedList<>();
    try (Session session = driver.session()) {
        try (Transaction tx = session.beginTransaction()) {
            Result result = tx.run("""
                MATCH (u:Usuario {nombre: $usuario})-[:Leido]->(l:Libro)
                RETURN l.titulo AS titulo
                """,
                parameters("usuario", usuario));
            while (result.hasNext()) {
                leidos.add(result.next().get("titulo").asString());
            }
            tx.commit();
        }
    }
    return leidos;
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

    public List<String> obtenerAmigos(String usuario){
        List<String> amigos = new LinkedList<>();
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                Result result = tx.run("""
                    MATCH (u:Usuario {nombre: $usuario})-[:Amigos]->(a:Usuario)
                    RETURN a.name AS amigo
                    """,
                    parameters("usuario", usuario));
                while (result.hasNext()) {
                    amigos.add(result.next().get("amigo").asString());
                }
                tx.commit();
            }
        }
        return amigos;
    }

    public void agregarAmigo(String usuario, String a_usuario){
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                tx.run("""
                    MATCH (u:Usuario {name: $usuario})
                    MATCH (a:Usuario {name: $ausuario})
                    MERGE (u)-[:Amigo]->(a)
                    """,
                    parameters("usuario", usuario, "ausuario", a_usuario));
                tx.commit();
            }
        }
    }

    //recomendaciones
    public LinkedList<String> getRecomendacionesAmigos(String usuario) {
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                return new SistemaDeRecomendaciones(usuario).execute(tx);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new LinkedList<>();
        }
    }
}
