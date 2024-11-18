package org.example.Database;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Config {
    @JsonProperty("database")
    private DatabaseConfig databaseConfig;

    @JsonProperty("queries")
    private Map<String, String> queries;

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public static Config loadConfig(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new com.fasterxml.jackson.dataformat.yaml.YAMLFactory());
        return mapper.readValue(new File(filePath), Config.class);
    }

    public static class DatabaseConfig {
        private String url;
        private String username;
        private String password;

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
