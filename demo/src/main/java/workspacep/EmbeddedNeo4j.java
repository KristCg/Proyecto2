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

    public boolean iniciarSesion(String usuario, String password) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                Result result = tx.run("""
                    MATCH (u:Usuario {name: $usuario, password: $password})
                    RETURN count(u) > 0
                    """,
                    parameters("usuario", usuario, "password", password));
                return result.single().get(0).asBoolean();
            });
        }
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

    public boolean usuarioExiste(String usuario) {
        String query = "MATCH (u:Usuario {name: $usuario}) RETURN u LIMIT 1";
        try (var session = driver.session()) {
            var result = session.run(query, java.util.Map.of("usuario", usuario));
            return result.hasNext();
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
    public void agregarLibroBiblioteca(String usuario, String titulo) {
    try (Session session = driver.session()) {
        session.executeWrite(tx -> {
            tx.run(
                "MATCH (u:Usuario {name: $usuario}), (l:Libro {titulo: $titulo}) " +
                "MERGE (u)-[:GUARDO]->(l)",
                parameters("usuario", usuario, "titulo", titulo)
            );
            return null;
        });
    }
}


    public void agregarLibro(String usuario, String titulo, String autor, String genero, int anio, String descripcion, String imagen) {
        if (titulo == null || titulo.isEmpty() || autor == null || autor.isEmpty() || genero == null || genero.isEmpty() || descripcion == null || descripcion.isEmpty() || imagen == null || imagen.isEmpty()) {   
            throw new IllegalArgumentException("Llena todos los campos");
        }

        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                tx.run("""
                    MERGE (a:Autor {name: $autor})
                    MERGE (g:Genero {genero: $genero})
                    MERGE (l:Libro {titulo: $titulo})
                    SET l.publicacion = $anio,
                        l.autor = $autor,
                        l.genero = $genero,
                        l.descripcion = $descripcion,
                        l.imagen = $imagen
                    MERGE (a)-[:Autor_de]->(l)
                    MERGE (l)-[:genero]->(g)
                    WITH l
                    MATCH (u:Usuario {name: $usuario})
                    MERGE (u)-[:Leido]->(l)
                    MERGE (u)-[:Interes_en]->(g)
                    """,
                    parameters(
                        "usuario", usuario,
                        "titulo", titulo,
                        "autor", autor,
                        "genero", genero,
                        "anio", anio,
                        "descripcion", descripcion,
                        "imagen", imagen
                    ));
                tx.commit();
            }
        }
    }

    public void guardarLibro(String usuario, String titulo) {
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                tx.run("""
                    MATCH (u:Usuario {name: $usuario})
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
                    WHERE NOT EXISTS((:Usuario {name: $usuario})-[:Leido]->(libro))
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

    public List<Libro> obtenerLibrosAleatorios(int cantidad) {
        List<Libro> libros = new ArrayList<>();
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            Result result = tx.run("""
                            MATCH (l:Libro)
                            RETURN l.titulo AS titulo, l.autor AS autor, l.aniopublicacion AS anio, l.genero AS genero, l.imagen AS imagen, l.descripcion AS descripcion
                            ORDER BY rand()
                            LIMIT $cantidad
                            """, parameters("cantidad", cantidad));
                            while (result.hasNext()) {
                                var record = result.next();
                                libros.add(new Libro(
                                    record.get("titulo").asString(),
                                    record.get("autor").asString(),
                                    record.get("anio").isNull() ? 0 : record.get("anio").asInt(),
                                    record.get("genero").asString(),
                                    record.get("imagen").isNull() ? "" : record.get("imagen").asString(),
                                    record.get("descripcion").isNull() ? "" : record.get("descripcion").asString()
                                ));
                        }
                tx.commit();
            }
        }
        return libros;
    }

    public List<Libro> obtenerLibrosDeUsuario(String usuario) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                List<Libro> libros = new ArrayList<>();
                Result result = tx.run(
                    "MATCH (u:Usuario {name: $usuario})-[:GUARDO]->(l:Libro) RETURN l",
                    parameters("usuario", usuario)
                );
                while (result.hasNext()) {
                    org.neo4j.driver.Record r = result.next();
                    libros.add(convertirANodoLibro(r.get("l").asNode()));
                }
                return libros;
            });
        }
    }


    public List<String> obtenerLibrosLeidos(String usuario) {
    List<String> leidos = new LinkedList<>();
    try (Session session = driver.session()) {
        try (Transaction tx = session.beginTransaction()) {
            Result result = tx.run("""
                MATCH (u:Usuario {name: $usuario})-[:Leido]->(l:Libro)
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
                    MATCH (u:Usuario {name: $usuario})-[:Guardado]->(l:Libro)
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
                    MATCH (u:Usuario {name: $usuario})-[:Amigos]->(a:Usuario)
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

    private Libro convertirANodoLibro(org.neo4j.driver.types.Node nodo) {
        String titulo = nodo.get("titulo").asString();
        String autor = nodo.get("autor").asString();
        int anio = nodo.get("anio").isNull() ? 0 : nodo.get("anio").asInt(); 
        String genero = nodo.get("genero").asString();
        String imagen = nodo.get("imagen").isNull() ? "" : nodo.get("imagen").asString(); 
        String descripcion = nodo.get("descripcion").isNull() ? "" : nodo.get("descripcion").asString();

        return new Libro(titulo, autor, anio, genero, imagen, descripcion);
    }

    public Libro buscarLibroPorTitulo(String titulo) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                Result result = tx.run("""
                MATCH (l:Libro)
                WHERE toLower(trim(l.titulo)) = toLower(trim($titulo))
                RETURN l
            """, parameters("titulo", titulo));
                if (result.hasNext()) {
                    return convertirANodoLibro(result.next().get("l").asNode());
                } else {
                    return null;
                }
            });
        }
    }

}
