package workspacep;
import java.util.LinkedList;
import java.util.List;

import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

public class Usuario {
    private String usuario;
    private String password;
    private LinkedList<String> leidos;
    private LinkedList<String> guardados:


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.leidos = new LinkedList<>();
        this.guardados = new LinkedList<>();
    }

    // Getters y setters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LinkedList<String> getLeidos() {
        return leidos;
    }

    public LinkedList<String> getGuardados() {
        return guardados;
    }

    public boolean IniciarSesion(String username, String password) {
    
        try (Session session = driver.session()) {
            Boolean autenticado = session.readTransaction(tx -> {
                Result result = tx.run(
                    "MATCH (u:Usuario {name: $username}) RETURN u.password AS password",
                    parameters("username", username));
                
                if (!result.hasNext()) {
                    throw new SecurityException("Usuario no encontrado");
                }
                
                Record record = result.next();
                
                String dbPassword = record.get("password").asString();
                
                if (!dbPassword.equals(password)) {
                    throw new SecurityException("Contraseña incorrecta");
                }
                
                return true;
            });
            
            return autenticado;
            
        } catch (SecurityException e) {
            throw e;
        } 
    }

    public static void registrarUsuario(EmbeddedNeo4j db, String name, String password, List<String> generosInteres) {
        if (db.validar(name)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        db.crearUsuario(name, password);

        // Registrar géneros de interés
        /* for (String genero : generosInteres) {
            String generoTrimmed = genero.trim();
            if (!generoTrimmed.isEmpty()) {
                db.agregarInteresUsuario(nombreUsuario, generoTrimmed);
            }
        } */
    }


    public void addLeidos(String titulo) {
        if (!leidos.contains(titulo)) {
            if(guardados.contains(titulo)){
                guardados.remove(titulo);
            }

            leidos.add(titulo);

        }
    }

    public void addGuardados(String titulo){
        if (!guardados.contains(titulo)){
            if(!leidos.contains(titulo)){
                guardados.add(titulo);
            }
        }
    }
}
