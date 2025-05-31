package workspacep;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {

        String username = "javaapp";
        String password = "Test2025!";
        String boltURL = "bolt://localhost:7687";

        try (EmbeddedNeo4j db = new EmbeddedNeo4j(boltURL, username, password);
             Scanner scanner = new Scanner(System.in)) {
            
            while (true) {
                System.out.println("\n=== SISTEMA DE RECOMENDACIÓN DE LIBROS ===");
                System.out.println("1. Iniciar Sesión");
                System.out.println("2. Registrarse");
                System.out.print("Opción: ");
                
                int opcion1;
                try {
                    opcion1 = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Por favor ingrese un número válido.");
                    continue;
                }

                switch (opcion1) {
                    case 1:
                        EmbeddedNeo4j db = new EmbeddedNeo4j( boltURL, username, password);
                        System.out.println("\n=== INICIAR SESIÓN ===");
                        System.out.print("Nombre de usuario: ");
                        String nombreUsuario = scanner.nextLine();
                        
                        System.out.print("Contraseña: ");
                        String contraseña = scanner.nextLine();
                
                        try {
                            if (db.iniciarSesion(nombreUsuario, contraseña)) {
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
                        EmbeddedNeo4j db = new EmbeddedNeo4j( boltURL, username, password);
                        System.out.println("\n=== REGISTRO ===");
                        System.out.print("Nombre de usuario: ");
                        String nombreUsuario = scanner.nextLine();
                        
                        System.out.print("Contraseña: ");
                        String contraseña = scanner.nextLine();

                        System.out.println("\nGéneros disponibles:");
                        List<String> generos = db.getGeneros();
                        generos.forEach(System.out::println);
                        
                        System.out.print("Ingrese sus géneros de interés (separados por comas): ");
                        String generosInput = scanner.nextLine();
                        List<String> generosSeleccionados = Arrays.asList(generosInput.split(","));

                        try {
                            Usuario.registrarUsuario(db, nombreUsuario, contraseña, generosSeleccionados);
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
        while (true) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Ver recomendaciones");
            System.out.println("2. Ver libros guardados");
            System.out.println("3. Ver libros leídos");
            System.out.println("4. Agregar libro");
            System.out.println("5. Cerrar sesión");
            System.out.print("Opción: ");
            
            int opcion;
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    System.out.println("Cerrando sesión...");
                    return;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }


}
