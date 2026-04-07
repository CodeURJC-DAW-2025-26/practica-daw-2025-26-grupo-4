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
            throw new IOException("Error saving the image", e);
        }
        // Not saved here: the CascadeType.ALL on Product will persist the image
        // when calling productService.save(product)
        return image;
    }

    public Image createImageFromResource(Resource resource) throws IOException {
        Image image = new Image();
        try {
            image.setImageFile(new SerialBlob(resource.getInputStream().readAllBytes()));
        } catch (Exception e) {
            throw new IOException("Error loading image from resource: " + resource.getFilename(), e);
        }
        // Not saved here: the CascadeType.ALL on Product will persist the image
        return image;
    }

    public Image getImage(long id) {
        return imageRepository.findById(id).orElseThrow();
    }

    public Resource getImageFile(long id) throws SQLException {
        Image image = getImage(id);
        if (image.getImageFile() != null) {
            return new InputStreamResource(image.getImageFile().getBinaryStream());
        }
        throw new RuntimeException("Empty image data for id: " + id);
    }

    public void replaceImageFile(long id, java.io.InputStream inputStream) throws IOException {
        Image image = getImage(id);
        try {
            image.setImageFile(new SerialBlob(inputStream.readAllBytes()));
            imageRepository.save(image);
        } catch (SQLException e) {
            throw new IOException("Error replacing image file", e);
        }
    }

    public void deleteImage(long id) {
        imageRepository.deleteById(id);
    }
}
