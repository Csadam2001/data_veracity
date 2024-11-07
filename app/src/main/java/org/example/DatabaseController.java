package org.example;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class DatabaseController {

    private final ObjectMapper objectMapper = new ObjectMapper();


   @GetMapping("/json-structure")
    public JsonNode getJsonStructure() {
        try {
            File file = new File("C:\\Egyetem\\szakdoga\\szakdogaa\\singlexAPI.json");
            if (file.exists()) {
                return objectMapper.readTree(file);
            } else {
                return objectMapper.createObjectNode(); 
            }
        } catch (IOException e) {
            e.printStackTrace();
            return objectMapper.createObjectNode();
        }
    }

    @PostMapping("/create-expectation")
    public ResponseEntity<String> createExpectation(@RequestBody Map<String, List<String[]>> data) {
    List<String[]> paths = data.get("paths");
    List<String[]> expectations = data.get("expectations");
    System.out.println("------------------PATHS------------------" + paths);
    System.out.println("------------------EXPECTATIONS------------------" + expectations);
    try {
        App.VueTest(paths, expectations);
        return ResponseEntity.ok("Expectation created successfully!");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create expectation");
    }
}
}
