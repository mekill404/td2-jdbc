package com.mekill404.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private final String JDBC_URL;
    private final String USERNAME;
    private final String PASSWORD;
    private Properties properties = new Properties();

    public DBConnection() {
        try (FileInputStream fis = new FileInputStream("conf.properties"))
        {
            properties.load(fis);
        } catch (IOException e)
        {
            throw new RuntimeException("Erreur lors du chargement de conf.properties: Fichier non trouvé ou illisible.", e);
        }
        JDBC_URL = properties.getProperty("jdbc.url");
        USERNAME = properties.getProperty("jdbc.username");
        PASSWORD = properties.getProperty("jdbc.password");
        try 
        {
            Class.forName(properties.getProperty("jdbc.driver.class"));
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Driver JDBC introuvable", e);
        }
        this.properties = null; 
    }
    public Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}