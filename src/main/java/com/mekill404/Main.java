package com.mekill404;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mekill404.configuration.DBConnection;
import com.mekill404.model.Player;
import com.mekill404.model.Team;
import com.mekill404.model.ContinentEnum;
import com.mekill404.model.PlayerPostionEnum;
import com.mekill404.repository.DataRetriever;

public class Main {

    public static void main(String[] args) {
        DBConnection db = new DBConnection();
        DataRetriever dr = new DataRetriever(db);
        Connection conn = null;

        try {
            conn = db.getDBConnection();
            if (conn != null) {
                System.out.println("**************************************************");
                System.out.println("             DÉBUT DES TESTS JDBC            ");
                System.out.println("**************************************************");

                testFindTeamById_1(dr);
                testFindPlayers_1_2(dr);
                testFindTeamsByPlayerName(dr);
                testFindPlayersByCriteria(dr);
                testCreatePlayersAlreadyExists(dr);
                testCreatePlayersSuccess(dr);
                testExamenGoalsCalculation(dr);
                testSaveTeamUpdateAndCheckGoals(dr);

            }
        } catch (SQLException e) {
            System.err.println("Erreur DB : " + e.getMessage());
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("\nConnexion fermée");
                }
            } catch (SQLException e) {
                System.err.println("Erreur fermeture : " + e.getMessage());
            }
        }
    }

    static void testExamenGoalsCalculation(DataRetriever dr) {
        System.out.println("\n--- EXAMEN 4.1 : Test findTeamById + getPlayersGoals ---");
        try {
            Team team = dr.findTeamById(1);
            if (team != null) {
                System.out.println("Équipe récupérée : " + team.getName());
                System.out.println("Nombre total de buts de l'équipe : " + team.getPlayersGoals());
            }
        } catch (RuntimeException e) {
            System.out.println("Comportement attendu (Exception) : " + e.getMessage());
        }
    }

    static void testSaveTeamUpdateAndCheckGoals(DataRetriever dr) {
        System.out.println("\n--- EXAMEN 4.2 : Test saveTeam + vérification buts ---");
        try {
            Team team = dr.findTeamById(1);
            for (Player p : team.getPlayers()) {
                if (p.getGoalNb() == null) p.setGoalNb(0);
            }
            
            Team savedTeam = dr.saveTeam(team);
            System.out.println("Sauvegarde réussie pour " + savedTeam.getName());
            System.out.println("Somme des buts après mise à jour : " + savedTeam.getPlayersGoals());
        } catch (RuntimeException e) {
            System.out.println("Erreur lors du test saveTeam : " + e.getMessage());
        }
    }


    static void testFindTeamById_1(DataRetriever dr) {
        System.out.println("\n--- a) findTeamById(1) ---");
        System.out.println(dr.findTeamById(1));
    }

    static void testFindPlayers_1_2(DataRetriever dr) {
        System.out.println("\n--- c) findPlayers(page=1, size=2) ---");
        dr.findPlayers(1, 2).forEach(System.out::println);
    }

    static void testFindTeamsByPlayerName(DataRetriever dr) {
        System.out.println("\n--- e) findTeamsByPlayerName(\"an\") ---");
        dr.findTeamsByPlayerName("an").forEach(System.out::println);
    }

    static void testFindPlayersByCriteria(DataRetriever dr) {
        System.out.println("\n--- f) findPlayersByCriteria ---");
        dr.findPlayersByCriteria("ud", PlayerPostionEnum.MIDF, "Madrid", ContinentEnum.EUROPA, 1, 10)
          .forEach(System.out::println);
    }

    static void testCreatePlayersAlreadyExists(DataRetriever dr) {
        System.out.println("\n--- g) createPlayers (joueur existant) ---");
        try {
            List<Player> players = new ArrayList<>();
            players.add(new Player(6, "Jude Bellingham", 23, PlayerPostionEnum.STR, null));
            players.add(new Player(7, "Pedri", 24, PlayerPostionEnum.MIDF, null));
            dr.createPlayers(players);
        } catch (RuntimeException e) {
            System.out.println("Exception attendue ✔ : " + e.getMessage());
        }
    }

    static void testCreatePlayersSuccess(DataRetriever dr) {
        System.out.println("\n--- h) createPlayers (succès) ---");
        try {
            List<Player> players = new ArrayList<>();
            players.add(new Player(88, "Vini Jr", 24, PlayerPostionEnum.STR, null));
            players.add(new Player(99, "Gavi", 20, PlayerPostionEnum.MIDF, null));
            dr.createPlayers(players).forEach(System.out::println);
        } catch (RuntimeException e) {
            System.out.println("Erreur h) : " + e.getMessage());
        }
    }
}