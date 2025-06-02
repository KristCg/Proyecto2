package workspacep;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpSession;

@Controller
public class Controlador {

    private EmbeddedNeo4j db;

    public Controlador() {
        String username = "neo4j";
        String password = "aSK9BvZ8fAxmFaLZSYaMYk-vSho-AgFkIs4LR6ouFIY"; 
        String boltURL = "neo4j+s://c2dfb3cb.databases.neo4j.io"; 
        this.db = new EmbeddedNeo4j(boltURL, username, password);
    }

    @GetMapping("/")
    public RedirectView redireccionarInicio(HttpSession session) {
        String usuario = (String) session.getAttribute("usuario");
        return (usuario != null) ? new RedirectView("/index") : new RedirectView("/login");
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    @PostMapping("/login")
    public RedirectView procesarLogin(
            @RequestParam("usuario") String usuario,
            @RequestParam("contrasena") String contrasena,
            HttpSession session,
            RedirectAttributes attributes) {

        try {
            if (db.iniciarSesion(usuario, contrasena)) {
                session.setAttribute("usuario", usuario);
                return new RedirectView("/index");
            } else {
                attributes.addFlashAttribute("error", "Usuario o contraseña incorrectos.");
                return new RedirectView("/login");
            }
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error en la conexión con la base de datos.");
            return new RedirectView("/login");
        }
    }

    @GetMapping("/index")
        public String mostrarInicio(HttpSession session, Model model) {
            String usuario = (String) session.getAttribute("usuario");
            if (usuario == null) {
                return "redirect:/login";
            }

            model.addAttribute("usuario", usuario);

            List<Libro> libros = db.obtenerLibrosAleatorios(10);
            model.addAttribute("libros", libros);

            return "index";
        }

    @GetMapping("/register")
    public String mostrarFormularioRegistro() {
        return "register"; 
    }
    @PostMapping("/register")
    public String procesarRegistro(
            @RequestParam("name") String name,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        if (db.validarUsuario(name)) {
            model.addAttribute("error", "El nombre de usuario ya existe.");
            return "register";
        }

        session.setAttribute("nameTemp", name);
        session.setAttribute("passwordTemp", password);

        List<String> generos = db.getGeneros();
        model.addAttribute("generos", generos);

        return "generos"; 
    }

    @PostMapping("/register/generos")
    public String procesarGeneros(
            @RequestParam("generos") List<String> generosSeleccionados,
            HttpSession session,
            Model model) {

        String nombre = (String) session.getAttribute("nameTemp");
        String password = (String) session.getAttribute("passwordTemp");

        if (nombre == null || password == null) {
            model.addAttribute("error", "Sesión expirada. Por favor, regístrese nuevamente.");
            return "register";
        }

        try {
            Usuario.registrarUsuarioGeneros(db, nombre, password, generosSeleccionados);
            session.removeAttribute("nombreTemp");
            session.removeAttribute("passwordTemp");
            session.setAttribute("usuario", nombre);

            return "redirect:/index";
        } catch (Exception e) {
            model.addAttribute("error", "Error en el registro: " + e.getMessage());
            model.addAttribute("generos", db.getGeneros());
            return "generos";
        }
    }

    @GetMapping("/biblioteca")
    public String mostrarBiblioteca(HttpSession session, Model model) {
        String usuario = (String) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        List<Libro> libros = db.obtenerLibrosDeUsuario(usuario);
        model.addAttribute("libros", libros);
        return "biblioteca";
    }

    @PostMapping("/guardarLibroEnBiblioteca")
    public String guardarLibroEnBibliotecaDesdeInicio(
            @RequestParam("titulo") String titulo,
            HttpSession session,
    RedirectAttributes redirectAttributes) {

    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/login";

    try {
        db.agregarLibroBiblioteca(usuario, titulo);
        redirectAttributes.addFlashAttribute("mensaje", "Libro guardado en tu biblioteca.");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "No se pudo guardar el libro: " + e.getMessage());
    }

    return "redirect:/index";
}

    @GetMapping("/agregarLibro")
    public String mostrarFormularioAgregarLibro(HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        return "agregarLibro";
    }

    @PostMapping("/agregarLibro")
    public String agregarLibro(
        @RequestParam String titulo,
        @RequestParam String autor,
        @RequestParam int aniopublicacion,
        @RequestParam String genero,
        @RequestParam(required = false) String imagen,
        HttpSession session,
        Model model) 
    {
        String usuario = (String) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        try {
            db.agregarLibro(usuario, titulo, autor, genero, aniopublicacion, imagen);
            return "redirect:/biblioteca";
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo agregar el libro: " + e.getMessage());
            return "agregarLibro";
        }
    }

    @GetMapping("/amigos")
    public String mostrarAmigos(HttpSession session, Model model) {
        String usuario = (String) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        try {
            List<String> amigos = db.obtenerAmigos(usuario);
            model.addAttribute("amigos", amigos);
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar amigos: " + e.getMessage());
        }

        return "amigos";
    }

    @PostMapping("/amigos")
    public String agregarAmigo(
            @RequestParam("nuevoAmigo") String nuevoAmigo,
            HttpSession session,
            Model model) {

        String usuario = (String) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        try {
            if (db.usuarioExiste(nuevoAmigo)) {
                db.agregarAmigo(usuario, nuevoAmigo);
                model.addAttribute("mensaje", "Amigo agregado con éxito.");
            } else {
                model.addAttribute("error", "El usuario ingresado no existe.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al agregar amigo: " + e.getMessage());
        }

        List<String> amigos = db.obtenerAmigos(usuario);
        model.addAttribute("amigos", amigos);
        return "amigos";
    }
}



