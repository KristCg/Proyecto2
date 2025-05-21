package workspacep;
import java.util.LinkedList;

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
