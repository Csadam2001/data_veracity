package org.example.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ValidateDB {

    public static void Validate() {
        String pythonScriptPath = "C:\\Egyetem\\szakdoga\\szakdoga\\szakdoga\\app\\src\\main\\java\\org\\example\\validate_from_json.py"; // Path to your Python script
        String jsonFilePath = "C:\\Egyetem\\szakdoga\\szakdoga\\szakdoga\\app\\expectations.json"; // Path to the JSON file

        try {
            // Create a process builder to execute the Python script
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, jsonFilePath);
            
            // Redirect error stream to the standard output (useful for debugging)
            pb.redirectErrorStream(true);
            
            // Start the process
            Process process = pb.start();
            
            // Capture the output from the Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print the output (or handle as needed)
            }
            
            // Wait for the process to finish and get the exit code
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Validation completed successfully.");
                // Optionally, read the validation results from a file (like validation_results.json)
                // and process them in Java.
            } else {
                System.out.println("Validation failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
    }
}
