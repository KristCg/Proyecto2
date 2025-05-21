package workspacep;

import java.util.LinkedList;
import java.util.Scanner;

public class App 
{
    public static void main(String[] args) {
		
		//String username = "neo4j";
		//String password = "densities-feelings-tubes";
		//String boltURL = "bolt://44.192.99.82:7687";

        String username = "javaapp";
		String password = "Test2025!";
		String boltURL = "bolt://localhost:7687";

        System.out.println('1.Iniciar Sesion');
        System.out.println('2. Registrarse');
        System.out.println('Opcion: ');
        Scanner inicio = new Scanner(System.in);







        System.out.println('¿Que deseas hacer?');
        System.out.println('1. Ver recomendaciones');
        System.out.println('2. Ver libros guardados');
        System.out.println('3. Ver libros leidos');
        System.out.println('4. Agregar libro');
        System.out.println('Opcion: ');
        int option = Integer.parseInt(in.nextLine());
        
        
		try ( EmbeddedNeo4j db = new EmbeddedNeo4j( boltURL, username, password ) )
        {
            switch(option){
                catch 1:		 	
                System.out.println("***Recomendaciones***");

                catch 2: 
                System.out.println("***Libros guardados***");

                Usuario guardados = Usuario.getGuardados();

                for(String libro: guardados){
                    System.out.println(libro);
                }

                catch 3: 
                System.out.println("***Libros leidos***");

                Usuario leidos = Usuario.getLeidos();

                for(String libro: leidos){
                    System.out.println(libro);
                }



            }

        	
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
