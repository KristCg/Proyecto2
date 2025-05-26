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

    @Override
    public LinkedList<String> execute(Transaction tx) {
         //Result result = tx.run( "MATCH (people:Person) RETURN people.name");
         Result result = tx.run( "MATCH (u:Usuario) RETURN u.name");
         LinkedList<String> usuarios = new LinkedList<String>();
         List<Record> registros = result.list();
         for (int i = 0; i < registros.size(); i++) {
             //myactors.add(registros.get(i).toString());
             usuarios.add(registros.get(i).get("u.name").asString());
         }
         
         return usuarios;
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
