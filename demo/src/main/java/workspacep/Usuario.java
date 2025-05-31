package workspacep;
import java.util.LinkedList;
import java.util.List;

import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;

import static org.neo4j.driver.Values.parameters;

public class Usuario {
    private String usuario;
    private String password;
    private LinkedList<String> leidos;
    private LinkedList<String> guardados;


    public Usuario(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
        this.leidos = new LinkedList<>();
        this.guardados = new LinkedList<>();
    }

    // Getters y setters
    public String getUsername() {
        return usuario;
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

    public static boolean iniciarSesion(EmbeddedNeo4j db, String username, String password) {
        try (Session session = db.getDriver().session()) {
            Boolean autenticado = session.executeRead(tx -> {
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void registrarUsuario(EmbeddedNeo4j db, String name, String password, List<String> generosInteres) {
        if (db.validarUsuario(name)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        db.registarUsuario(name, password);

        for (String genero : generosInteres) {
            genero = genero.trim();
            if (!genero.isEmpty()) {
                db.agregarInteres(name, genero);
            }
        } 
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
