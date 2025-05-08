package org.example.config;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta {
    private String description;
    private String provider;
    private String consumer;
    private String dataType;
    private String status;
    private String timestamp;
    private String id;

    public Meta(String description, String provider, String consumer, String dataType, String status, String timestamp, String id) {
        this.description = description;
        this.provider = provider;
        this.consumer = consumer;
        this.dataType = dataType;
        this.status = status;
        this.timestamp = timestamp;
        this.id = id;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    
}
