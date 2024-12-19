package com.example.imagicspringaws.controllers;

import com.example.imagicspringaws.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@Controller
@RequiredArgsConstructor
public class ImagesController {
    private final ImageService imageService;

    @GetMapping("/")
    public String welcomePage(Model model) {
        List<String> imagesList = this.imageService.getListFiles();
        model.addAttribute("images", imagesList);
        return "index";
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> getListFiles() {
        return ResponseEntity.ok(this.imageService.getListFiles());
    }

    @GetMapping("{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable("fileName") String fileName) throws IOException {
        var byteArray = this.imageService.getFile(fileName);

        var headers = new HttpHeaders();
        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                String.format("attachment; filename=\"%s\"", fileName)
        );

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(byteArray.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArray);
    }

    @GetMapping("/delete/{fileName}")
    public String deleteFile(@PathVariable("fileName") String fileName) {
        this.imageService.deleteFile(fileName);

        return "redirect:/";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("image") MultipartFile file) throws IOException {
        this.imageService.uploadFile(file);

        return "redirect:/";
    }
}