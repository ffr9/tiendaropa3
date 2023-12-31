package tiendaropa.service;

import tiendaropa.model.Producto;
import tiendaropa.dto.ProductoData;
import tiendaropa.model.Usuario;
import tiendaropa.repository.ProductoRepository;
import tiendaropa.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductoService {

    Logger logger = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<ProductoData> allProductos() {
        logger.debug("Devolviendo todos los productos");
        //Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);

        List<Producto> productos = productoRepository.findAll();
        List<ProductoData> productosDTO = productos.stream().map(producto -> modelMapper.map(producto, ProductoData.class)).collect(Collectors.toList());

        return productosDTO;
    }

    @Transactional(readOnly = true)
    public List<ProductoData> buscarProductos(String busqueda) {
        logger.debug("Devolviendo todos los productos con busqueda");

        List<ProductoData> productos = allProductos();
        List<ProductoData> filtrados;
        filtrados = productos.stream().filter(producto -> producto.getNombre().contains(busqueda)).collect(Collectors.toList());

        return filtrados;
    }

    @Transactional(readOnly = true)
    public ProductoData findById(Long productoId) {
        logger.debug("Buscando producto " + productoId);
        Producto tarea = productoRepository.findById(productoId).orElse(null);
        if (tarea == null) return null;
        else return modelMapper.map(tarea, ProductoData.class);
    }

    @Transactional
    public void borrarProducto(Long idProducto) {
        logger.debug("Borrando producto " + idProducto);
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null) {
            //throw new TareaServiceException("No existe tarea con id " + idTarea);
        }else{
            productoRepository.delete(producto);
        }
        //descomentar esta linea cuando ponga excepcion en if, ademas quitar else
        //productoRepository.delete(producto);

    }

    @Transactional
    public ProductoData modificarProducto(Long idProducto, String nombre, float precio, Integer stock, String numref, boolean destacado, Integer categoriaid){
        logger.debug("Modificando producto " + idProducto + " - " + nombre);
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null) {
            //throw new TareaServiceException("No existe tarea con id " + idTarea);
        }

        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setNumref(numref);
        producto.setDestacado(destacado);
        producto.setCategoriaid(categoriaid);

        producto = productoRepository.save(producto);
        return modelMapper.map(producto, ProductoData.class);
    }

    @Transactional
    public ProductoData crearProducto(String nombre, float precio, Integer stock, String numref, boolean destacado, Integer categoriaid){
        logger.debug("Creando producto " + nombre);
        Producto producto = new Producto(nombre, precio, stock, numref, destacado, categoriaid);
        productoRepository.save(producto);
        return modelMapper.map(producto, ProductoData.class);
    }

    @Transactional
    public List<Producto> obtenerProductosDestacados() {
        return productoRepository.findByDestacadoIsTrue(); // Suponiendo que tienes un campo 'destacado' en tu entidad Producto
    }

}
