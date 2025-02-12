package com.example.imagicspringaws.controllers;

import com.example.imagicspringaws.dto.Detected;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.*;
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
    public String processImage(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please upload a valid image file.");
            return "upload";
        }
        detectedObjects objects = this.detectOnImage(file);
        var image = this.drawDetect(file, objects.getObjects());
        String base64Image = java.util.Base64.getEncoder().encodeToString(image);
        model.addAttribute("image", base64Image);
        model.addAttribute("count", objects.getCount());
        return "result";
    }

    private detectedObjects detectOnImage(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("file", fileAsResource);
        body.add("meta", meta);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        var response = restTemplate.postForEntity(
                apiUrl + "/api/v1/objects/detect?oauth_token=" + apiKey+"&oauth_provider=mcs",
                requestEntity,
                String.class
        );
        System.out.println(response.getBody());
        var node = objectMapper.readTree(response.getBody());
        var detectedData = node
                .get("body")
                .get("pedestrian_labels")
                .get(0)
                .get("labels");
        var countData = node
                .get("body")
                .get("pedestrian_labels")
                .get(0)
                .get("count_by_density");

        var data = objectMapper.treeToValue(detectedData, Detected[].class);
        var count = objectMapper.treeToValue(countData, Integer.class);

        return new detectedObjects(data, count);
    }

    private byte[] drawDetect(MultipartFile file, Detected[] objectsArr) throws IOException {
        var image = ImageIO.read(file.getInputStream());
        var graphics = image.createGraphics();
        graphics.setStroke(stroke);
        for (int i = 0; i < objectsArr.length; i++) {
            graphics.setColor(randomColor());
            List<Integer> measures = objectsArr[i].coordination();
            graphics.drawRect(
                    measures.get(0),
                    measures.get(1),
                    measures.get(2)-measures.get(0),
                    measures.get(3)-measures.get(1)
            );
            //String name = objectsArr[i].name();
            //graphics.drawString(name, (measures.get(2) + measures.get(0)) / 2, (measures.get(3) + measures.get(1)) / 2);
        }
        graphics.dispose();

        var outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);

        return outputStream.toByteArray();
    }

    public Color randomColor() {
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        float[] hsb = new float[3];
        Color.RGBtoHSB(r, g, b, hsb);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    final class detectedObjects {
        private final Detected[] objects;
        private final int count;

        public detectedObjects(Detected[] objects, int count) {
            this.objects = objects;
            this.count = count;
        }

        public Detected[] getObjects() {
            return objects;
        }

        public int getCount() {
            return count;
        }
    }
}