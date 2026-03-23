package es.urjc.daw04.controllers.web;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.urjc.daw04.service.ImageService;

@Controller
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/images/{id:[\\d]+}")
    public ResponseEntity<Resource> getImage(@PathVariable long id) throws SQLException, IOException {
        Resource imageFile = imageService.getImageFile(id);
        MediaType mediaType = MediaTypeFactory.getMediaType(imageFile).orElse(MediaType.IMAGE_JPEG);
        return ResponseEntity.ok().contentType(mediaType).body(imageFile);
    }
}
