package workspacep;

public class Libro {
    private String titulo;
    private String autor;
    private int anopublicacion;
    private String genero;

    public Libro(String titulo, String autor, int anopublicacion, String genero) {
        this.titulo = titulo;
        this.autor = autor;
        this.anopublicacion = anopublicacion;
        this.genero = genero;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getAnopublicacion() {
        return anopublicacion;
    }
    public String getGenero() {
        return genero;
    }  
    
    @Override
    public String toString(){
        return "Titulo: " + titulo + ", Autor: " + autor + ", Año de Publicación: " + anopublicacion + ", Género: " + genero;

    }

}


