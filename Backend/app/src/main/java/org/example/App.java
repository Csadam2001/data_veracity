package org.example;

import org.example.config.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import java.util.Date;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    static List<String[]> savedpaths = new ArrayList<>();
    static List<String[]> savedexpectations = new ArrayList<>();

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    public static void CreateVLA(List<String[]> paths, List<String[]> expectations, List<String[]> values,
            List<String[]> typepath, List<String[]> typevalue) {
        Meta meta = createMeta();

        Map<String, Object> unifiedSchema = new HashMap<>();
        List<Objective> objectives = new ArrayList<>();
        List<Objective> valueObj = new ArrayList<>();
        for (int i = 0; i < typepath.size(); i++) {
            String[] typepathArray = typepath.get(i);
            String[] typevalueArray = typevalue.get(i);
            mergeProperties(unifiedSchema, typepathArray, typevalueArray);
        }

        for (int i = 0; i < paths.size(); i++) {
            String[] pathArray = paths.get(i);
            String[] valueArray = values.get(i);
            String[] expArray = expectations.get(i);

            Objective objective = createObjective(expArray[0], pathArray, valueArray);
            valueObj.add(objective);

        }

        Map<String, Object> schemaRoot = new HashMap<>();
        schemaRoot.put("type", "object");
        schemaRoot.put("properties", unifiedSchema);

        Objective syntaxObjective = new Objective(null, null);
        syntaxObjective.setDescription("xAPI schema validation");
        syntaxObjective.setAspect("syntax");

        Evaluation evaluation = new Evaluation();
        evaluation.setSchema(schemaRoot);
        syntaxObjective.setEvaluation(evaluation);

        Map<String, Object> jsonStructure = new HashMap<>();
        jsonStructure.put("meta", meta);
        objectives.add(syntaxObjective);
        objectives.addAll(valueObj);
        jsonStructure.put("objectives", objectives);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("VLA.json"), jsonStructure);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Meta createMeta() {
        Meta meta = new Meta(null, null, null, null, null, null);
        meta.setDescription("VLA example for xAPI traces");
        meta.setProvider("providerID");
        meta.setConsumer("consumerID");
        meta.setDataType("JSON");
        meta.setStatus("signed");
        meta.setTimestamp(new Date().toInstant().toString());
        return meta;
    }

    private static void mergeProperties(Map<String, Object> root, String[] path, String[] value) {
        Map<String, Object> current = root;
    
        for (String fullPath : path) {
            String[] parts = fullPath.split("\\.");
    
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                boolean isLast = (i == parts.length - 1);
    
                if (!current.containsKey(part)) {
                    Map<String, Object> newNode = new HashMap<>();
                    if (isLast) {
                        newNode.put("type", value[0].toLowerCase());
                    } else {
                        newNode.put("type", "object");
                        newNode.put("properties", new HashMap<>());
                    }
                    current.put(part, newNode);
                }
    
                if (!isLast) {
                    current = (Map<String, Object>) ((Map<String, Object>) current.get(part)).get("properties");
                }
            }
        }
    }

    private static Objective createObjective(String aspect, String[] pathArray, String[] valueArray) {
        Objective objective = new Objective(null, null);
        objective.setDescription(getDescriptionForAspect(aspect));
        objective.setAspect(aspect);

        Evaluation evaluation = new Evaluation();

        switch (aspect) {
            case "Timestamp_Within_Range":
                evaluation.setTarget("statement.timestamp");
                if (valueArray.length > 0) {
                    try {
                        evaluation.setMeasurement(Integer.parseInt(valueArray[0]));
                        evaluation.setUnit(pathArray[0]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid value for timestamp range: " + valueArray[0]);
                    }
                }
                break;

            case "Record_Count":
                if (valueArray.length > 0) {
                    if ("Exact".equalsIgnoreCase(pathArray[0])) {
                        try {
                            evaluation.setExact(Integer.parseInt(valueArray[0]));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid value for exact: " + valueArray[0]);
                        }
                    } else if("Max".equalsIgnoreCase(pathArray[0])){
                        try {
                            evaluation.setMax(Integer.parseInt(valueArray[0]));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid value for max: " + valueArray[0]);
                        }
                    }else if("Min_Max".equalsIgnoreCase(pathArray[0])){
                        try {
                            evaluation.setMin(Integer.parseInt(valueArray[0]));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid value for min: " + valueArray[0]);
                        }
                        try {
                            evaluation.setMax(Integer.parseInt(valueArray[1]));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid value for max: " + valueArray[1]);
                        }
                    }
                    else{
                        try {
                            evaluation.setMin(Integer.parseInt(valueArray[0]));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid value for min: " + valueArray[0]);
                        }
                    }
                }
                break;
            case "Sequiental":
                if (valueArray.length > 0) {
                    try {
                        evaluation.setTarget("statement.timestamp");
                        evaluation.setType(valueArray[0]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid value for timestamp range: " + valueArray[0]);
                    }
                }
                break;
            case "String_value":
                if (valueArray.length > 0) {
                    try {
                        StringBuilder sb = new StringBuilder("");
                        for (int i = 0; i < pathArray.length; i++) {
                            if (pathArray.length == 1) {
                                sb.append(pathArray[i]);
                            } else {
                                sb.append(pathArray[i]);
                                if (i != pathArray.length - 1) {
                                    sb.append(".");
                                }
                            }

                        }
                        evaluation.setTarget(sb.toString());
                        evaluation.setUnit(valueArray[0]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid value for timestamp range: " + valueArray[0]);
                    }
                }
                break;
            default:
                break;
        }

        objective.setEvaluation(pruneEvaluation(evaluation));
        return objective;
    }

    private static Evaluation pruneEvaluation(Evaluation evaluation) {
        Evaluation prunedEvaluation = new Evaluation();

        if (evaluation.getTarget() != null) {
            prunedEvaluation.setTarget(evaluation.getTarget());
        }
        if (evaluation.getType() != null) {
            prunedEvaluation.setType(evaluation.getType());
        }
        if (evaluation.getMin() != null) {
            prunedEvaluation.setMin(evaluation.getMin());
        }
        if (evaluation.getMax() != null) {
            prunedEvaluation.setMax(evaluation.getMax());
        }
        if (evaluation.getMeasurement() != null) {
            prunedEvaluation.setMeasurement(evaluation.getMeasurement());
        }
        if (evaluation.getUnit() != null) {
            prunedEvaluation.setUnit(evaluation.getUnit());
        }
        if (evaluation.getSchema() != null) {
            prunedEvaluation.setSchema(evaluation.getSchema());
        }
        if (evaluation.getExact() != null) {
            prunedEvaluation.setExact(evaluation.getExact());
        }

        return prunedEvaluation;
    }

    private static String getDescriptionForAspect(String aspect) {
        switch (aspect) {
            case "Timestamp_Within_Range":
                return "Check if records within expected time range";
            case "Record_Count":
                return "Check number of xAPI records";
            case "Sequiental":
                return "Trace records are sequential";
            case "String_value":
                return "Check if the String is equal to the value";
            default:
                return "xAPI schema validation";
        }
    }

    public static Map<String, List<Map<String, Object>>> resolveConflicts(
            List<List<String>> paths, List<String> exp, List<List<Object>> values) {

        Map<List<String>, List<String>> pathToExps = new HashMap<>();
        Map<List<String>, List<List<Object>>> pathToValues = new HashMap<>();

        for (int i = 0; i < paths.size(); i++) {
            List<String> path = paths.get(i);
            String expKey = exp.get(i);
            List<Object> value = values.get(i);

            pathToExps.computeIfAbsent(path, k -> new ArrayList<>()).add(expKey);
            pathToValues.computeIfAbsent(path, k -> new ArrayList<>()).add(value);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> result2 = new ArrayList<>();

        for (Map.Entry<List<String>, List<String>> entry : pathToExps.entrySet()) {
            List<String> path = entry.getKey();
            List<String> exps = entry.getValue();
            List<List<Object>> associatedValues = pathToValues.get(path);

            if (exps.size() > 1) {
                boolean hasConflict = false;
                boolean hasRequired = exps.contains("Required");

                for (int i = 0; i < exps.size(); i++) {
                    for (int j = i + 1; j < exps.size(); j++) {
                        String type1 = exps.get(i);
                        String type2 = exps.get(j);
                        if (!isValidCombination(type1, type2)) {
                            hasConflict = true;
                            break;
                        }
                    }
                    if (hasConflict)
                        break;
                }

                if (hasConflict) {
                    for (int i = 0; i < exps.size(); i++) {
                        String expType = exps.get(i);
                        List<Object> value = associatedValues.get(i);
                        Map<String, Object> conflictEntry = new HashMap<>();
                        conflictEntry.put("path", new ArrayList<>(path));
                        conflictEntry.put("type", Collections.singletonList(expType));
                        conflictEntry.put("value", value);

                        result.add(conflictEntry);
                    }

                    if (hasRequired) {
                        int requiredIndex = exps.indexOf("Required");
                        Map<String, Object> nonConflictEntry = new HashMap<>();
                        nonConflictEntry.put("path", new ArrayList<>(path));
                        nonConflictEntry.put("type", Collections.singletonList("Required"));
                        nonConflictEntry.put("value", associatedValues.get(requiredIndex));

                        result2.add(nonConflictEntry);
                    }
                } else {
                    for (int i = 0; i < exps.size(); i++) {
                        Map<String, Object> nonConflictEntry = new HashMap<>();
                        nonConflictEntry.put("path", new ArrayList<>(path));
                        nonConflictEntry.put("type", Collections.singletonList(exps.get(i)));
                        nonConflictEntry.put("value", associatedValues.get(i));

                        result2.add(nonConflictEntry);
                    }
                }
            } else {
                for (int i = 0; i < exps.size(); i++) {
                    Map<String, Object> nonConflictEntry = new HashMap<>();
                    nonConflictEntry.put("path", new ArrayList<>(path));
                    nonConflictEntry.put("type", Collections.singletonList(exps.get(i)));
                    nonConflictEntry.put("value", associatedValues.get(i));
                    ;
                    result2.add(nonConflictEntry);
                }
            }
        }

        Map<String, List<Map<String, Object>>> combinedResults = new HashMap<>();
        combinedResults.put("conflict", result);
        combinedResults.put("nonConflicts", result2);
        return combinedResults;
    }

    private static boolean isValidCombination(String type1, String type2) {
        if ((type1.equals("Required") && type2.equals("String")) ||
                (type1.equals("String") && type2.equals("Required")) ||
                (type1.equals("Required") && type2.equals("Integer")) ||
                (type1.equals("Integer") && type2.equals("Required")) ||
                (type1.equals("Required") && type2.equals("Boolean")) ||
                (type1.equals("Boolean") && type2.equals("Required")) ||
                (type1.equals("Required") && type2.equals("Required"))) {
            return true;
        }
        return false;
    }

    public static List<Map<String, String>> parseJson(String jsonString) {
        List<Map<String, String>> result = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);

            result.addAll(parseJsonRecursive(rootNode, new ArrayList<>()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static List<Map<String, String>> parseJsonRecursive(JsonNode value, List<String> path) {
        List<Map<String, String>> result = new ArrayList<>();

        if (value.isObject()) {
            value.fieldNames().forEachRemaining(fieldName -> {
                List<String> newPath = new ArrayList<>(path);
                newPath.add(fieldName);
                result.addAll(parseJsonRecursive(value.get(fieldName), newPath));
            });
        } else if (value.isArray()) {
            result.add(createResult(path, "Array"));
        } else if (value.isTextual()) {
            result.add(createResult(path, "String"));
        } else if (value.isNumber()) {
            result.add(createResult(path, "Integer"));
        } else if (value.isBoolean()) {
            result.add(createResult(path, "Boolean"));
        } else {
            result.add(createResult(path, "Unknown"));
        }

        return result;
    }

    private static Map<String, String> createResult(List<String> path, String type) {
        String pathString = String.join(".", path); 
        Map<String, String> entry = new HashMap<>();
        entry.put("path", pathString);
        entry.put("type", type);
        return entry;
    }

}
