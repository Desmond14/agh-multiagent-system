package pl.edu.agh.agents.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConfigurationLoader {
    private static final String CONFIG_FILENAME = "config.properties";
    private static final String VALUES_SEPARATOR = ",";

    private List<AgentConfiguration> agentConfigurations;
    private Properties properties;

    public void load() throws IOException {
        agentConfigurations = new ArrayList<>();
        properties = loadPropertiesFile(CONFIG_FILENAME);
        loadAgentConfigurations();
    }

    private void loadAgentConfigurations() throws IOException {
        String agentFilenames = properties.getProperty(ConfigurationKey.AGENT_FILES);
        for (String filename : agentFilenames.split(VALUES_SEPARATOR)) {
            agentConfigurations.add(loadAgentConfiguration(filename));
        }
    }

    private AgentConfiguration loadAgentConfiguration(String filename) throws IOException {
        Properties properties = loadPropertiesFile(filename);
        AgentConfiguration agentConfiguration = new AgentConfiguration();
        agentConfiguration.setName(loadString(properties, ConfigurationKey.AGENT_NAME));
        agentConfiguration.setAcceleration(loadInteger(properties, ConfigurationKey.ACCELERATION));
        agentConfiguration.setCarLength(loadDouble(properties, ConfigurationKey.CAR_LENGTH));
        agentConfiguration.setCarWidth(loadDouble(properties, ConfigurationKey.CAR_WIDTH));
        agentConfiguration.setInitialPosition(loadDouble(properties, ConfigurationKey.INITIAL_POSITION));
        agentConfiguration.setInitialVelocity(loadInteger(properties, ConfigurationKey.INITIAL_VELOCITY));
        agentConfiguration.setMaxVelocity(loadInteger(properties, ConfigurationKey.MAX_VELOCITY));
        agentConfiguration.setStreetNumber(loadInteger(properties, ConfigurationKey.STREET_NUMBER));
        return agentConfiguration;
    }

    private Properties loadPropertiesFile(String filename) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream configInputStream = classloader.getResourceAsStream(filename);
        Properties properties;
        try {
            properties = new Properties();
            properties.load(configInputStream);
        } finally {
            configInputStream.close();
        }
        return properties;
    }

    private Integer loadInteger(Properties properties, String propertyKey) {
        return Integer.valueOf(properties.getProperty(propertyKey));
    }

    private Double loadDouble(Properties properties, String propertyKey) {
        return Double.valueOf(properties.getProperty(propertyKey));
    }

    private String loadString(Properties properties, String propertyKey) {
        return properties.getProperty(propertyKey);
    }

    public List<AgentConfiguration> getAgentConfigurations(){
        return agentConfigurations;
    }
}
