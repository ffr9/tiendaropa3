package tiendaropa.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tiendaropa.authentication.ManagerUserSession;
import tiendaropa.controller.exception.ProductoNotFoundException;
import tiendaropa.controller.exception.UsuarioNoLogeadoException;
import tiendaropa.dto.BusquedaData;
import tiendaropa.dto.UsuarioData;
import tiendaropa.model.Producto;
import tiendaropa.dto.ProductoData;
import tiendaropa.service.ProductoService;
import tiendaropa.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ProductoController {

    @Autowired
    ManagerUserSession managerUserSession;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ProductoService productoService;

    private boolean comprobarUsuarioLogeado() {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null)
            return false;

        return true;
    }

    private boolean comprobarAdmin() {
        UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        return usuario.isAdmin();
    }

    @GetMapping("/tiendaropa/catalogo")
    public String mostrarCatalogo(Model model) {

        if(comprobarUsuarioLogeado()) {
            UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
            model.addAttribute("usuario", usuario);
        }else{
            model.addAttribute("usuario", null);
        }
        List<ProductoData> productos = productoService.allProductos();
        model.addAttribute("productos", productos);

        return "catalogo";
    }

    @GetMapping("/tiendaropa/catalogo/busqueda")
    public String buscarProducto(@ModelAttribute BusquedaData busquedaData, Model model){
        if(comprobarUsuarioLogeado()) {
            UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
            model.addAttribute("usuario", usuario);
        }else{
            model.addAttribute("usuario", null);
        }
        List<ProductoData> productos = productoService.buscarProductos(busquedaData.getBusqueda());
        model.addAttribute("busquedaData", new BusquedaData());
        model.addAttribute("productos", productos);

        return "catalogo";
    }

    @GetMapping("/admin/tiendaropa/catalogo")
    public String mostrarCatalogoAdmin(Model model) {
        if(comprobarUsuarioLogeado()) {
            UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
            model.addAttribute("usuario", usuario);
        }else{
            model.addAttribute("usuario", null);
        }
        List<ProductoData> productos = productoService.allProductos();
        model.addAttribute("productos", productos);

        //if(comprobarAdmin()) {
            return "catalogoAdmin";
        //}
        //return "catalogo";
    }

    @DeleteMapping("/tiendaropa/productos/{id}")
    @ResponseBody
    // La anotación @ResponseBody sirve para que la cadena devuelta sea la resupuesta
    // de la petición HTTP, en lugar de una plantilla thymeleaf
    public String borrarTarea(@PathVariable(value="id") Long idProducto, RedirectAttributes flash, HttpSession session) {
        ProductoData producto = productoService.findById(idProducto);
        if (producto == null) {
            throw new ProductoNotFoundException();
        }
        if(comprobarUsuarioLogeado()){
            productoService.borrarProducto(idProducto);
        }
        return "";
    }

    @GetMapping("/admin/tiendaropa/productos/{id}/editar")
    public String formEditarProducto(@PathVariable(value="id") Long idProducto, @ModelAttribute ProductoData productoData,
                                 Model model, HttpSession session) {

        ProductoData producto = productoService.findById(idProducto);
        if (producto == null) {
            throw new ProductoNotFoundException();
        }

        if(!comprobarUsuarioLogeado()) {
            throw new UsuarioNoLogeadoException();
        }
        UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        model.addAttribute("usuario", usuario);
        model.addAttribute("producto", producto);

        productoData.setNombre(producto.getNombre());
        productoData.setPrecio(producto.getPrecio());
        productoData.setStock(producto.getStock());
        productoData.setNumref(producto.getNumref());
        productoData.setDestacado(producto.getDestacado());
        productoData.setCategoriaid(producto.getCategoriaid());
        return "formEditarProducto";
    }

    @PostMapping("/admin/tiendaropa/productos/{id}/editar")
    public String grabaProductoModificado(@PathVariable(value = "id") Long idProducto, @ModelAttribute ProductoData productoData,
                                       Model model, RedirectAttributes flash, HttpSession session) {

        if(!comprobarUsuarioLogeado()) {
            throw new UsuarioNoLogeadoException();
        }
        UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        model.addAttribute("usuario", usuario);
        productoService.modificarProducto(idProducto, productoData.getNombre(), productoData.getPrecio(), productoData.getStock(), productoData.getNumref(), productoData.getDestacado(), productoData.getCategoriaid());
        flash.addFlashAttribute("mensaje", "Producto modificado correctamente");
        return "redirect:/admin/tiendaropa/catalogo";
    }

    @GetMapping("/admin/tiendaropa/productos/nuevo")
    public String formNuevoProducto(@ModelAttribute ProductoData productoData, Model model,HttpSession session){
        if(!comprobarUsuarioLogeado()){
            throw new UsuarioNoLogeadoException();
        }
        UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        model.addAttribute("usuario", usuario);
        return "formNuevoProducto";
    }

    @PostMapping("/admin/tiendaropa/productos/nuevo")
    public String nuevoProducto(@ModelAttribute ProductoData productoData,Model model, RedirectAttributes flash,HttpSession session){
        if(!comprobarUsuarioLogeado()){
            throw new UsuarioNoLogeadoException();
        }
        UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        model.addAttribute("usuario", usuario);
        productoService.crearProducto(productoData.getNombre(), productoData.getPrecio(), productoData.getStock(), productoData.getNumref(), productoData.getDestacado(), productoData.getCategoriaid());
        flash.addFlashAttribute("mensaje", "Producto creado correctamente");
        return "redirect:/admin/tiendaropa/catalogo";
    }

}
