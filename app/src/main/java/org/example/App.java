package org.example;

import org.example.database.GetDatabase;
import org.example.database.ValidateDB;
import org.checkerframework.checker.units.qual.m;
import org.example.config.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.List;
import java.io.IOException;
import java.util.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    
        public static void main(String[] args) {
            SpringApplication.run(App.class, args);
            /*ValidationConfig config = new ValidationConfig();
            config.setMeta(SetUpMeta("1111", "2222", "signed"));
            List<Objective> objectives = new ArrayList<>();

        
         objectives.add(createCombinedValidationForPaths(
            "Syntax Validation",
            List.of(
                new PathTypePair(new String[] {"verb", "id"}, "string"),
                new PathTypePair(new String[] {"statement", "context", "contextActivities", "category", "definition", "type"}, "string")
            )
        ));
            
            config.setObjectives(objectives);
            writeToJsonFile(config);*/
        }

        public static void VueTest(List<String[]> paths, List<String[]> expectations){
            ValidationConfig config = new ValidationConfig();
            config.setMeta(SetUpMeta("1111", "2222", "signed"));
            List<Objective> objectives = new ArrayList<>();
            List<PathTypePair> path = new ArrayList<>();
            for(int i = 0; i < paths.size(); i++){
                PathTypePair newpath = new PathTypePair(paths.get(i), expectations.get(i)[0]);
                path.add(newpath);
            }

            objectives.add(createCombinedValidationForPaths(
                "Syntax Validation",
                path
            ));
                
                config.setObjectives(objectives);
                writeToJsonFile(config);
        }

        public static Meta SetUpMeta(String ProviderID, String ConsumerID, String signed){
            Meta meta = new Meta("Description", ProviderID, ConsumerID, "Json",signed,GetTimestamp());
            return meta;
        }
        
        public static Objective createObjective(String description, String aspect, Evaluation evaluation) {
            Objective objective = new Objective(description, aspect);
            objective.setEvaluation(evaluation);
            return objective;
        }
    
        public static Objective createCombinedValidationForPaths(String description, List<PathTypePair> pathTypePairs) {
            Objective objective = new Objective(description, "syntax");
    
            Property rootSchema = new Property("object", new HashMap<>());
            
            for (PathTypePair pathTypePair : pathTypePairs) {
                addPathToSchema(rootSchema, pathTypePair.path, pathTypePair.type);
            }
    
            Evaluation evaluation = new Evaluation();
            evaluation.setSchema(rootSchema);
    
            objective.setEvaluation(evaluation);
            return objective;
        }
    
        public static void addPathToSchema(Property rootSchema, String[] path, String type) {
            Property current = rootSchema;
            for (int i = 0; i < path.length; i++) {
                if (current.getProperties() == null) {
                    current.setProperties(new HashMap<>());
                }
                Map<String, Property> properties = current.getProperties();
                properties.putIfAbsent(path[i], new Property(i == path.length - 1 ? type : "object"));
                current = properties.get(path[i]);
            }
        }
        
        public static class PathTypePair {
            String[] path;
            String type;
    
            public PathTypePair(String[] path, String type) {
                this.path = path;
                this.type = type;
            }
        }
        public static Evaluation createEvaluationWithArgs(String target, String type, Args args) {
            Evaluation evaluation = new Evaluation(target, type, args);
            return evaluation;
        }
    
        public static Evaluation createEvaluationWithRange(double min, double max) {
            Evaluation evaluation = new Evaluation();
            evaluation.setMin(min);
            evaluation.setMax(max);
            return evaluation;
        }
    
        public static Evaluation createEvaluationWithMin(double min) {
            Evaluation evaluation = new Evaluation();
            evaluation.setMin(min);
            return evaluation;
        }
    
        public static Evaluation createEvaluation(String target, String type) {
            return new Evaluation(target, type, null);
        }

        public static void writeToJsonFile(ValidationConfig config) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
             mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); 
            try {
                mapper.writeValue(new File("expectations.json"), config);
                System.out.println("Expectations have been written to expectations.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public static String GetTimestamp(){
            Date date = new Date();
            String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
            TimeZone utc = TimeZone.getTimeZone("UTC");
            sdf.setTimeZone(utc);
            return sdf.format(date);
        }
}
