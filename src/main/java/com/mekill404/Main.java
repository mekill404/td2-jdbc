package com.mekill404;

import java.sql.Connection;
import java.sql.SQLException;

import com.mekill404.configuration.DBConnection;

public class Main {
    public static void main(String[] args) {
        DBConnection db = new DBConnection();
        Connection conn = null;

        try{
            System.out.println("Tentative de connexion...");
            conn = db.getDBConnection();
            if (conn != null) {
                System.out.println("Succées ! Connecté via conf.properties");
                System.out.println("Schema actuel: " + conn.getSchema());

            }
        }catch(SQLException e)
        {
            System.err.println("Erreur: " + e.getErrorCode());
        }finally{
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("Connexion fermée");                    
                }
            } catch (SQLException e) {
                System.err.println("erreur: " + e.getErrorCode());
            }

        }
    }
}
