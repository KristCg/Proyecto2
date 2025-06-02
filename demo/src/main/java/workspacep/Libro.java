package workspacep;

public class Libro {
    private String titulo;
    private String autor;
    private int aniopublicacion;
    private String genero;
    private String imagen;

    public Libro(String titulo, String autor, int aniopublicacion, String genero, String imagen) {
        this.titulo = titulo;
        this.autor = autor;
        this.aniopublicacion = aniopublicacion;
        this.genero = genero;
        this.imagen = imagen;
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
    public String getImagen() {
        return imagen;
    }
    
    @Override
    public String toString(){
        return "Titulo: " + titulo + ", Autor: " + autor + ", Año de Publicación: " + aniopublicacion + ", Género: " + genero+ ", Imagen: " + imagen;

    }

}


