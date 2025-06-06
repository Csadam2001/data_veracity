package org.example.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.io.IOException;

public class ValidateDB {

    public static ValidationResult Validate() {
        
        String pythonSyntaxScriptPath = Paths.get("../../validate_from_json.py").toAbsolutePath().normalize().toString();
        String pythonValueScriptPath = Paths.get("../../validate_value_exp.py").toAbsolutePath().normalize().toString();
        String pythonInterpreterPath = Paths.get("../../validation/my_env/Scripts/python.exe").toAbsolutePath().normalize().toString();
        boolean syntaxValidation = false;
        boolean valueValidation = false;
        try {
            String syntaxResult = runPythonScript(pythonInterpreterPath, pythonSyntaxScriptPath);
            syntaxValidation = "valid".equals(parseValidationResult(syntaxResult));

            String valueResult = runPythonScript(pythonInterpreterPath, pythonValueScriptPath);
            valueValidation = "valid".equals(parseValidationResult(valueResult));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return new ValidationResult(syntaxValidation, valueValidation);
    }

    private static String runPythonScript(String pythonInterpreterPath, String scriptPath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(pythonInterpreterPath, scriptPath);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
            return output.toString().trim();
        }
    }

    private static String parseValidationResult(String result) {
        String lowerCaseResult = result.trim().toLowerCase();
        System.out.println(lowerCaseResult);
        if (lowerCaseResult.equals("valid")) {
            return "valid";
        } else if (lowerCaseResult.equals("invalid")) {
            return "invalid";
        }
        return "unknown";
    }
    

    public static class ValidationResult {
        private final boolean syntaxValidation;
        private final boolean valueValidation;

        public ValidationResult(boolean syntaxValidation, boolean valueValidation) {
            this.syntaxValidation = syntaxValidation;
            this.valueValidation = valueValidation;
        }

        public boolean isSyntaxValidation() {
            return syntaxValidation;
        }

        public boolean isValueValidation() {
            return valueValidation;
        }

        public boolean isValid() {
            return syntaxValidation && valueValidation;
        }
    }
}
