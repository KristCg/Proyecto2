package workspacep;

public class Libro {
    private String titulo;
    private String autor;
    private int aniopublicacion;
    private String genero;

    public Libro(String titulo, String autor, int aniopublicacion, String genero) {
        this.titulo = titulo;
        this.autor = autor;
        this.aniopublicacion = aniopublicacion;
        this.genero = genero;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getAniopublicacion() {
        return aniopublicacion;
    }
    public String getGenero() {
        return genero;
    }  
    
    @Override
    public String toString(){
        return "Titulo: " + titulo + ", Autor: " + autor + ", Año de Publicación: " + aniopublicacion + ", Género: " + genero;

    }

}


