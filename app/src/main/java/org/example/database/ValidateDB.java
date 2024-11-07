package org.example.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ValidateDB {

    public static boolean Validate() {
        String pythonScriptPath = "C:\\Egyetem\\szakdoga\\szakdogaa\\app\\src\\main\\java\\org\\example\\validate_from_json.py"; 
        String jsonFilePath = "C:\\Egyetem\\szakdoga\\szakdogaa\\app\\expectations.json"; 

        try {
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, jsonFilePath);
            
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Validation completed successfully.");
                return true;

            } else {
                System.out.println("Validation failed with exit code " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
