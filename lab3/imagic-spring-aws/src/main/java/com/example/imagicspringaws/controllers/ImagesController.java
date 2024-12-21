package com.example.imagicspringaws.controllers;

import com.example.imagicspringaws.dto.Detected;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@CrossOrigin
@Controller
@RequiredArgsConstructor
public class ImagesController {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final BasicStroke stroke = new BasicStroke(5);
    @Value("${external.api.url}")
    private String apiUrl;

    @Value("${external.api.key}")
    private String apiKey;

    @Value("${cv.meta-info}")
    private String meta;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/process")
    public byte[] processImage(@RequestParam("file") ByteArrayResource file, Model model) throws IOException {
//        if (file.isEmpty()) {
//            model.addAttribute("error", "Please upload a valid image file.");
//            return "upload";
//        }
        var extension = "jpg";
        var label = this.detectOnImage(file);
        var result = this.drawDetect(file, label.coordination(), extension);

        return result;

//        // Draw bounding boxes on the image
//        BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
//        drawBoundingBoxes(image, objectCoordinates);
//
//        // Convert modified image to byte array
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ImageIO.write(image, "png", outputStream);
//        byte[] processedImageBytes = outputStream.toByteArray();
//
//        // Pass processed image to view
//        String base64Image = java.util.Base64.getEncoder().encodeToString(processedImageBytes);
//        model.addAttribute("image", base64Image);
//
//        return "result";
    }

    private Detected detectOnImage(ByteArrayResource file) throws JsonProcessingException {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        var map = new LinkedMultiValueMap<String, Object>();
        map.add("file", file);
        map.add("meta", this.meta);

        var request = new HttpEntity<>(map, headers);
        var response = restTemplate.postForEntity(
                apiUrl + "/api/v1/persons/recognize?oauth_token=" + apiKey+"&oauth_provider=mcs",
                request,
                String.class
        );

        var node = objectMapper.readTree(response.getBody());
        var detectedData = node
                .get("body")
                .get("object_labels")
                .get(0)
                .get("labels")
                .get(0);

        return objectMapper.treeToValue(detectedData, Detected.class);
    }

    private void drawBoundingBoxes(BufferedImage image, List<List<Integer>> objectCoordinates) {
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(2));

        for (List<Integer> coordinates : objectCoordinates) {
            int x = coordinates.get(0);
            int y = coordinates.get(1);
            int width = coordinates.get(2);
            int height = coordinates.get(3);
            graphics.drawRect(x, y, width, height);
        }

        graphics.dispose();
    }
    private byte[] drawDetect(ByteArrayResource file, List<Integer> measures, String extension) throws IOException {
        var image = ImageIO.read(file.getInputStream());
        var graphics = image.createGraphics();

        graphics.setColor(Color.RED);
        graphics.setStroke(stroke);
        graphics.drawRect(
                measures.get(0),
                measures.get(1),
                measures.get(2),
                measures.get(3)
        );
        graphics.dispose();

        var outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, extension, outputStream);

        return outputStream.toByteArray();
    }
}