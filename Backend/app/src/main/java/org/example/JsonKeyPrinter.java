package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class JsonKeyPrinter {

    public Node getJsonStructure(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(filePath));

            Node rootNode = buildJsonTree(root, null);
            return rootNode;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Node buildJsonTree(JsonNode node, Node parent) {
        Node currentNode = new Node(node.asText(), parent);

        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                Node childNode = buildJsonTree(field.getValue(), currentNode);
                childNode.setKey(field.getKey());
                currentNode.addChild(childNode);
            }
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                Node childNode = buildJsonTree(arrayElement, currentNode);
                currentNode.addChild(childNode);
            }
        }

        return currentNode;
    }

    public static class Node {
        private String key;
        private String value;
        private List<Node> children;
        private Node parent;

        public Node(String value, Node parent) {
            this.value = value;
            this.children = new ArrayList<>();
            this.parent = parent;
        }

        public void addChild(Node child) {
            this.children.add(child);
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public List<Node> getChildren() {
            return children;
        }

        public Node getParent() {
            return parent;
        }
    }
}
