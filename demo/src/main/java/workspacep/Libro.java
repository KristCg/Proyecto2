package workspacep;

public class Libro {
    private String titulo;
    private String autor;
    private int anio;
    private String genero;
    private String imagen;
    private String descripcion;

    public Libro(String titulo, String autor, int anio, String genero, String imagen, String descripcion) {
        this.titulo = titulo;
        this.autor = autor;
        this.anio = anio;
        this.genero = genero;
        this.imagen = imagen;
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getAnio() {
        return anio;
    }
    public String getGenero() {
        return genero;
    }  
    public String getImagen() {
        return imagen;
    }
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString(){
        return "Titulo: " + titulo + ", Autor: " + autor + ", Año de Publicación: " + anio + ", Género: " + genero+ ", Imagen: " + imagen + ", Descripción: " + descripcion;

    }

}