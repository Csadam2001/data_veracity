package org.example;

import org.example.database.GetDatabase;
import org.example.database.ValidateDB;
import org.example.Expectation;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Scanner;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
public class App {
    
    static String tableName = "houses";
    static List<String> columns = new ArrayList<>();
    static List<String> columntype = new ArrayList<>();
    static List<Expectation> expectations = new ArrayList<>();
    static int level = 0;
    public static void main(String[] args) {
        columns = GetDatabase.getTableColumnNames(tableName);
        columntype = GetDatabase.getTableColumnTypes(tableName);
        Consumerside(level);

        ValidationConfig config = new ValidationConfig(
            "test",  // datasource
            "postgresql+psycopg2://postgres:asd@localhost:5432/postgres",  // connection_string
            tableName,  // table_name
            expectations  // list of expectations
        );
        writeToJsonFile(config);
        ValidateDB.Validate();
    }
    public static void Consumerside(int a){
        int level = a;
        int columnnum = 0;
        String type = "";
        String name = "";
         String value = "";
        Scanner in = new Scanner(System.in);
        while(level != -1){
            switch (level) {
                case 0:
                    System.out.println("Consumer side: "+'\n'+"Choose from the following by pressing the number:"+'\n'+"1: Add an expectation"+'\n'+"2: End the contract");
                    level = in.nextInt();
                    break;
                case 1: 
                    System.err.println("Choose from the following columns:");
                    for(int i = 0; i < columns.size(); i++){
                        System.out.println(i + ": " + columns.get(i));
                    }
                    columnnum = in.nextInt();
                    type = columntype.get(columnnum);
                    name = columns.get(columnnum);
                    switch (type) {
                        case "int4":
                            System.out.println("The column you choose has integers in it, choose from the following: "
                                                + '\n' + "1: Add interval expectation"
                                                + '\n' + "2: Add Minimum expectation"
                                                + '\n' + "3: Add Maximum expectation");
                            columnnum = in.nextInt();
                            switch (columnnum) {
                                case 1:
                                    System.out.println("Minimum:");
                                    int min = in.nextInt();
                                    System.out.println("Maximum:");
                                    int max = in.nextInt();
                                    value = String.valueOf(min) + " " + String.valueOf(max);
                                    expectations.add(new Expectation(name, type, value));
                                    level = 0;
                                    break;
                                case 2:
                                    System.out.println("Minimum:");
                                    min = in.nextInt();
                                    value = String.valueOf(min);
                                    expectations.add(new Expectation(name, type, value));
                                    level = 0;
                                    break;
                                case 3:
                                    System.out.println("Maximum:");
                                    max = in.nextInt();
                                    value = String.valueOf(max);
                                    expectations.add(new Expectation(name, type, value));
                                    level = 0;
                                    break;
                            
                                default:
                                    System.out.println("Wrong number!");
                                    level = 0;
                                    break;
                            }
                            break;
                        case "varchar":
                            System.out.println("The column you choose has String in it, choose from the following: "
                                                + '\n' + "1: Has a specific String"
                                                + '\n' + "2: The String can be empty"
                                                + '\n' + "3: The String cannot be empty");
                            columnnum = in.nextInt();
                            switch (columnnum) {
                                case 1:
                                    System.out.println("Type the word that need to be in the row:");
                                    value = in.next();
                                    expectations.add(new Expectation(name, type, value));
                                    level = 0;
                                    break;
                                case 2:
                                    System.out.println(name+"Can be empty");
                                    value = "Can be empty";
                                    expectations.add(new Expectation(name, type, value));
                                    level = 0;
                                    break;
                                case 3:
                                    System.out.println(name+"Cannot be empty");
                                    value = "Cannot be empty";
                                    expectations.add(new Expectation(name, type, value));
                                    level = 0;
                                    break;
                            
                                default:
                                    System.out.println("Wrong number!");
                                    level = 0;
                                    break;
                            }
                            break;
                        case "bool":
                            System.out.println("The column you choose has Bool in it, choose from the following: "
                                                + '\n' + "1: Cannot be True"
                                                + '\n' + "2: Cannot be False");
                            columnnum = in.nextInt();
                            switch (columnnum) {
                                case 1:
                                    System.out.println(name +" cannot be true!");
                                    value = "Cannot be true";
                                    expectations.add(new Expectation(name, type, value));
                                    level = 0;
                                    break;
                                case 2:
                                    System.out.println(name + " cannot be false!");
                                    value = "Cannot be false";
                                    expectations.add(new Expectation(name, type, value));
                                    level = 0;
                                    break;
                                default:
                                    System.out.println("Wrong number!");
                                    level = 0;
                                    break;
                            }
                            break;
                        default:
                            level = 0;
                            break;
                    }
                    break;
                case 2:
                    level = -1;
                    break;
                default:
                    System.out.println("Wrong number!");
                    level = 0;
            }
        }
    }
    public static void writeToJsonFile(ValidationConfig config) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Write the list of expectations to a JSON file
            mapper.writeValue(new File("expectations.json"), config);
            System.out.println("Expectations have been written to expectations.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
