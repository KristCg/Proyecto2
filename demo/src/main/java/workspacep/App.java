package workspacep;

//import java.util.Scanner;
//import java.util.Arrays;
//import java.util.List;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

/* public class App {
    public static void main(String[] args) {

        String username = "neo4j";
        String password = "aSK9BvZ8fAxmFaLZSYaMYk-vSho-AgFkIs4LR6ouFIY";
        String boltURL = "neo4j+s://c2dfb3cb.databases.neo4j.io";

        try (EmbeddedNeo4j db = new EmbeddedNeo4j(boltURL, username, password);
            Scanner scanner = new Scanner(System.in)) {
            
            while (true) {
                System.out.println("\n=== SISTEMA DE RECOMENDACIÓN DE LIBROS ===");
                System.out.println("1. Iniciar Sesión");
                System.out.println("2. Registrarse");
                System.out.print("Opción: ");
                
                int op1;
                try {
                    op1 = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Por favor ingrese un número válido.");
                    continue;
                }

                switch (op1) {
                    case 1:
                        System.out.println("\n=== INICIAR SESIÓN ===");
                        System.out.print("Nombre de usuario: ");
                        String nombreUsuario = scanner.nextLine();
                        
                        System.out.print("Contraseña: ");
                        String contraseña = scanner.nextLine();
                
                        try {
                            if (Usuario.iniciarSesion(db, nombreUsuario, contraseña)) {
                                System.out.println("\n¡Inicio de sesión exitoso!");
                                menuPrincipal(db, scanner, nombreUsuario);
                            } else {
                                System.out.println("Usuario o contraseña incorrectos.");
                            }
                        } catch (Exception e) {
                            System.err.println("Error al iniciar sesión: " + e.getMessage());
                        }
                        break;
                    case 2:
                        System.out.println("\n=== REGISTRO ===");
                        System.out.print("Nombre de usuario: ");
                        String nuevoUsuario = scanner.nextLine();
                        
                        System.out.print("Contraseña: ");
                        String nuevacontraseña = scanner.nextLine();

                        System.out.println("\nGéneros disponibles:");
                        List<String> generos = db.getGeneros();
                        generos.forEach(System.out::println);
                        
                        System.out.print("Ingrese sus géneros de interés (separados por comas): ");
                        String generosInput = scanner.nextLine();
                        List<String> generosSeleccionados = Arrays.asList(generosInput.split(","));

                        try {
                            Usuario.registrarUsuario(db, nuevoUsuario, nuevacontraseña, generosSeleccionados);
                            System.out.println("\n¡Usuario registrado!");
                        } catch (Exception e) {
                            System.err.println("Error en el registro: " + e.getMessage());
                        }
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error en la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void menuPrincipal(EmbeddedNeo4j db, Scanner scanner, String nombreUsuario) {
        int op2;
        op2 = 0; // Inicializar la opción del menú principal
        System.out.println("\n¡Bienvenido " + nombreUsuario + "!");
        while (op2 != 5) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Pagina principal");
            System.out.println("2. Biblioteca");
            System.out.println("3. Agregar libro");
            System.out.println("4. Agregar amigos");
            System.out.println("5. Cerrar sesión");
            System.out.print("Opción: ");
            
            try {
                op2 = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido.");
                continue;
            }

            switch (op2) {
                case 1:
                    System.out.println("\n=== PÁGINA PRINCIPAL ===");
                    System.out.println("Esta son nuestras recomendaciones de libros basadas en tus géneros de interés.");
                    List<String> recomendaciones = db.obtenerRecomendaciones(nombreUsuario);
                    if (recomendaciones.isEmpty()) {
                        System.out.println("No hay recomendaciones disponibles.");
                    } else {
                        recomendaciones.forEach(System.out::println);
                    }
                    break;
                case 2:
                    System.out.println("\n=== BIBLIOTECA ===");
                    List<String> leidos = db.obtenerLibrosLeidos(nombreUsuario);
                    if (leidos.isEmpty()) {
                        System.out.println("No has leído ningún libro.");
                    } else {
                        System.out.println("Libros leídos:");
                        leidos.forEach(System.out::println);
                    }
                    List<String> guardados = db.obtenerLibrosGuardados(nombreUsuario);
                    if (guardados.isEmpty()) {
                        System.out.println("No tienes libros guardados.");
                    } else {
                        System.out.println("Libros guardados:");
                        guardados.forEach(System.out::println);
                    }
                    break;

                case 3:
                    System.out.println("\n=== AGREGAR LIBRO ===");
                    System.out.print("Título del libro: ");
                    String titulo = scanner.nextLine();
                    System.out.print("Autor del libro: ");
                    String autor = scanner.nextLine();
                    System.out.print("Género del libro: ");
                    String genero = scanner.nextLine();
                    System.out.print("Año de publicación: ");
                    int anio;
                    try {
                        anio = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Por favor ingrese un año válido.");
                        continue;
                    }
                    try {
                        db.agregarLibro(nombreUsuario, titulo, autor, genero, anio);
                        System.out.println("Libro agregado exitosamente.");
                    } catch (Exception e) {
                        System.err.println("Error al agregar el libro: " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.println("\n=== AGREGAR AMIGO ===");
                    System.out.println("\n-- TUS AMIGOS --");
                    List<String> amigos = db.obtenerAmigos(nombreUsuario);
                    if (amigos.isEmpty()) {
                        System.out.println("Todavia no tienes amigos.");
                    } else {
                        amigos.forEach(System.out::println);
                    }
                    System.out.println("\n-- AGREGA AMIGOS --");
                    System.out.print("Nombre de usuario: ");
                    String a_usuario = scanner.nextLine();

                    if (db.validarUsuario(a_usuario)) {
                        try {
                            db.agregarAmigo(nombreUsuario, a_usuario);
                            System.out.println("Amigo agregado exitosamente.");
                        } catch (Exception e) {
                            System.err.println("Error al agregar amigo: " + e.getMessage());
                        }
                        break;
    
                    }

                    
                case 5:
                    System.out.println("Cerrando sesión...");
                    return;

                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }


}
 */