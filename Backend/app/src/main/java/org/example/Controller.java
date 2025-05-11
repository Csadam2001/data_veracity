package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.database.ValidateDB;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class Controller {


    @PostMapping("/create-expectation")
    public ResponseEntity<String> createExpectation(@RequestBody Map<String, List<String[]>> data) {
        List<String[]> paths = data.get("paths");
        List<String[]> expectations = data.get("expectations");
        List<String[]> values = data.get("values");
        List<String[]> typepath = data.get("typepath");
        List<String[]> typevalue = data.get("typevalue");
        try {
            App.CreateVLA(paths, expectations,values,typepath,typevalue);
            return ResponseEntity.ok("Expectation created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create expectation");
        }
    }

    @PostMapping("/json_types")
    public ResponseEntity<List<Map<String, String>>> getJsonTypes(
            @RequestBody String data) {
        try {
            List<Map<String, String>> json = App.parseJson(data);
            return ResponseEntity.ok(json);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/get-conflict")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> receiveVLA(
            @RequestBody List<Map<String, Object>> data) {
        try {
            List<List<String>> paths = new ArrayList<>();
            List<String> exp = new ArrayList<>();
            List<List<Object>> values = new ArrayList<>();

            for (Map<String, Object> validation : data) {
                paths.add((List<String>) validation.get("path"));
                exp.add((String) validation.get("type"));

                Object value = validation.get("value");
                if (value instanceof List) {
                    values.add((List<Object>) value);
                } else {
                    values.add(Collections.singletonList(value));
                }
            }

            Map<String, List<Map<String, Object>>> resolvedConflicts = App.resolveConflicts(paths, exp, values);

            return ResponseEntity.ok(resolvedConflicts);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

   @GetMapping("/validate")
public Map<String, Object> validate() {
    ValidateDB.ValidationResult validationResult = ValidateDB.Validate();

    Map<String, Object> result = new HashMap<>();
    result.put("syntaxValidation", validationResult.isSyntaxValidation());
    result.put("valueValidation", validationResult.isValueValidation());
    result.put("syntaxJson", "syntax_validation_results.json");
    result.put("valueJson", "value_validation_results.json");

    return result;
}
@GetMapping("/json")
public ResponseEntity<String> getJsonContents() {
    try {
        Path filePath = Paths.get("./VLA.json").toAbsolutePath().normalize();
        Path filePath2 = Paths.get("../../VLA.json").toAbsolutePath().normalize();
        String jsonContent = java.nio.file.Files.readString(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonContent);

    } catch (IOException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Could not read file\"}");
    }
}



}
