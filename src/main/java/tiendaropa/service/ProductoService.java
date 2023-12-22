package tiendaropa.service;

import tiendaropa.model.Producto;
import tiendaropa.model.ProductoData;
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

}
