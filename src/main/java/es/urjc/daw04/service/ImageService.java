package es.urjc.daw04.service;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw04.model.Image;
import es.urjc.daw04.repositories.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public Image createImage(MultipartFile imageFile) throws IOException {
        Image image = new Image();
        try {
            image.setImageFile(new SerialBlob(imageFile.getBytes()));
        } catch (Exception e) {
            throw new IOException("Error al guardar la imagen", e);
        }
        // No guardamos aquí: el CascadeType.ALL del Product persiste la imagen
        // al hacer productService.save(product)
        return image;
    }

    public Image createImageFromResource(Resource resource) throws IOException {
        Image image = new Image();
        try {
            image.setImageFile(new SerialBlob(resource.getInputStream().readAllBytes()));
        } catch (Exception e) {
            throw new IOException("Error al cargar la imagen desde recurso: " + resource.getFilename(), e);
        }
        // No guardamos aquí: el CascadeType.ALL del Product persiste la imagen
        return image;
    }

    public Resource getImageFile(long id) throws SQLException {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada: " + id));
        if (image.getImageFile() != null) {
            return new InputStreamResource(image.getImageFile().getBinaryStream());
        }
        throw new RuntimeException("Datos de imagen vacíos para id: " + id);
    }
}
