package org.example;

import org.example.config.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
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
        Meta meta = new Meta(null, null, null, null, null, null,null);
        meta.setDescription("VLA example for xAPI traces");
        meta.setProvider("providerID");
        meta.setConsumer("consumerID");
        meta.setDataType("JSON");
        meta.setStatus("signed");
        meta.setTimestamp(new Date().toInstant().toString());
        String uniqueID = UUID.randomUUID().toString();
        meta.setId(uniqueID);
        return meta;
    }

    private static void mergeProperties(Map<String, Object> root, String[] path, String[] value) {
        Map<String, Object> current = root;
        List<String> urlBuffer = new ArrayList<>();
        Boolean url = false;

        for (String fullPath : path) {
            String[] parts = fullPath.split("\\.");
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (part.contains("http://") || part.startsWith("https://") || url) {
                    urlBuffer.add(part);
                    url = true;
                    if (i < parts.length - 1) {
                        continue;
                    }
                    url = false;
                    part = String.join(".", urlBuffer);
                    urlBuffer.clear();
                }
                if (!url) {
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

                        Object next = current.get(part);
                        if (next instanceof Map) {
                            Map<String, Object> nextMap = (Map<String, Object>) next;

                            if (!nextMap.containsKey("properties")) {
                                nextMap.put("properties", new HashMap<>());
                            }

                            current = (Map<String, Object>) nextMap.get("properties");
                        } else {
                            throw new IllegalStateException(
                                    "Expected a map at " + part + " but found: "
                                            + (next != null ? next.getClass() : "null"));
                        }
                    }
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
                    } else if ("Max".equalsIgnoreCase(pathArray[0])) {
                        try {
                            evaluation.setMax(Integer.parseInt(valueArray[0]));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid value for max: " + valueArray[0]);
                        }
                    } else if ("Min_Max".equalsIgnoreCase(pathArray[0])) {
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
                    } else {
                        try {
                            evaluation.setMin(Integer.parseInt(valueArray[0]));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid value for min: " + valueArray[0]);
                        }
                    }
                }
                break;
            case "Sequential":
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

        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> result2 = new ArrayList<>();
        List<Integer> conflictnums = new ArrayList<>();
        boolean hasconflict = false;
        for (int i = 0; i < paths.size(); i++) {
            for (int j = i + 1; j < paths.size(); j++) {
                String expKey = exp.get(i);
                String expKey2 = exp.get(j);
                if (expKey.equals(expKey2)) {
                    if (hasConflict(expKey, expKey2, paths.get(i), paths.get(j), values.get(i), values.get(j))) {
                        Map<String, Object> conflictEntry = new HashMap<>();
                        conflictEntry.put("path", new ArrayList<>(paths.get(i)));
                        conflictEntry.put("type", Collections.singletonList(exp.get(i)));
                        conflictEntry.put("value", values.get(i));

                        result.add(conflictEntry);
                        Map<String, Object> conflictEntry2 = new HashMap<>();
                        conflictEntry2.put("path", new ArrayList<>(paths.get(j)));
                        conflictEntry2.put("type", Collections.singletonList(exp.get(j)));
                        conflictEntry2.put("value", values.get(j));

                        result.add(conflictEntry2);
                        hasconflict = true;
                        conflictnums.add(i);
                        conflictnums.add(j);
                    }
                }
            }
            if (!hasconflict && !conflictnums.contains(i)) {
                Map<String, Object> nonConflictEntry = new HashMap<>();
                nonConflictEntry.put("path", new ArrayList<>(paths.get(i)));
                nonConflictEntry.put("type", Collections.singletonList(exp.get(i)));
                nonConflictEntry.put("value", values.get(i));
                result2.add(nonConflictEntry);

            }
            hasconflict = false;
        }

        Map<String, List<Map<String, Object>>> combinedResults = new HashMap<>();
        combinedResults.put("conflict", result);
        combinedResults.put("nonConflicts", result2);
        return combinedResults;
    }

    private static boolean hasConflict(String expkey, String expkey2, List<String> path1, List<String> path2,
            List<Object> value1, List<Object> value2) {
        if (expkey.equals("Record_Count") && expkey2.equals("Record_Count")) {
            if (path1.get(0).equals("Exact") || path2.get(0).equals("Exact")) {
                return true;
            }

            if (path1.get(0).equals("Min_Max") || path2.get(0).equals("Min_Max")) {
                return true;
            }

            if (path1.get(0).equals("Min") && path2.get(0).equals("Max")) {
                int min = Integer.parseInt((String) value1.get(0));
                int max = Integer.parseInt((String) value2.get(0));
                if (min >= max) {
                    return true;
                }
            }
            if (path1.get(0).equals("Max") && path2.get(0).equals("Min")) {
                int max = Integer.parseInt((String) value1.get(0));
                int min = Integer.parseInt((String) value2.get(0));
                if (max <= min) {
                    return true;
                }
            }
        }
        if (expkey.equals("Sequiental") && expkey2.equals("Sequiental")) {
            return true;
        }
        if (expkey.equals("Timestamp_Within_Range") && expkey2.equals("Timestamp_Within_Range")) {
            return true;
        }
        if (expkey.equals("String_value") && expkey2.equals("String_value")) {
            if (path1.get(0).equals(path2.get(0))) {
                return true;
            }
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

    private static List<Map<String, String>> parseJsonRecursive(JsonNode node, List<String> path) {
        List<Map<String, String>> result = new ArrayList<>();

        if (node.isObject()) {
            node.fieldNames().forEachRemaining(fieldName -> {
                JsonNode childNode = node.get(fieldName);
                if (fieldName.contains("http")) {
                    result.add(createResult(path, "array"));

                }
                else{

                if (fieldName.equals("type")) {
                    if (!childNode.isTextual()) {
                        List<String> newPath = new ArrayList<>(path);
                        newPath.add(fieldName);
                        result.addAll(parseJsonRecursive(childNode, newPath));
                    } else {
                        String type = childNode.asText();
                        result.add(createResult(path, type));
                    }
                } else if (fieldName.equals("items")) {
                    List<String> newPath = new ArrayList<>(path);
                    newPath.add("items");
                    result.addAll(parseJsonRecursive(childNode, newPath));
                } else if (!fieldName.equals("properties")) {
                    List<String> newPath = new ArrayList<>(path);
                    newPath.add(fieldName);
                    result.addAll(parseJsonRecursive(childNode, newPath));
                } else {
                    result.addAll(parseJsonRecursive(childNode, path));
                }
            }
            });
        } else if (node.isArray()) {
            for (JsonNode arrayItem : node) {
                result.addAll(parseJsonRecursive(arrayItem, path));
            }
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
