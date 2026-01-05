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

                for (int i = 0; i <= 100; i++) {
                    System.out.print(i % 2 == 0 ? '*' : '=');
                    if (i == 50) {
                        System.out.println("\n             DÉBUT DES TESTS JDBC            ");
                    }
                }
                System.out.println();

                testFindTeamById_1(dr);
                testFindTeamById_5(dr);
                testFindPlayers_1_2(dr);
                testFindPlayers_3_5(dr);
                testFindTeamsByPlayerName(dr);
                testFindPlayersByCriteria(dr);
                testCreatePlayersAlreadyExists(dr);
                testCreatePlayersSuccess(dr);
                testSaveTeamAddPlayer(dr);
                testSaveTeamRemovePlayers(dr);
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

    // a)
    static void testFindTeamById_1(DataRetriever dr) {
        System.out.println("\n--- a) findTeamById(1) ---");
        System.out.println(dr.findTeamById(1));
    }

    // b)
    static void testFindTeamById_5(DataRetriever dr) {
        System.out.println("\n--- b) findTeamById(5) ---");
        System.out.println(dr.findTeamById(5));
    }

    // c)
    static void testFindPlayers_1_2(DataRetriever dr) {
        System.out.println("\n--- c) findPlayers(page=1, size=2) ---");
        dr.findPlayers(1, 2).forEach(System.out::println);
    }

    // d)
    static void testFindPlayers_3_5(DataRetriever dr) {
        System.out.println("\n--- d) findPlayers(page=3, size=5) ---");
        List<Player> players = dr.findPlayers(3, 5);
        System.out.println(players.isEmpty() ? "Liste vide ✔" : players);
    }

    // e)
    static void testFindTeamsByPlayerName(DataRetriever dr) {
        System.out.println("\n--- e) findTeamsByPlayerName(\"an\") ---");
        dr.findTeamsByPlayerName("an").forEach(System.out::println);
    }

    // f)
    static void testFindPlayersByCriteria(DataRetriever dr) {
        System.out.println("\n--- f) findPlayersByCriteria ---");
        dr.findPlayersByCriteria(
                "ud",
                PlayerPostionEnum.MIDF,
                "Madrid",
                ContinentEnum.EUROPA,
                1,
                10
        ).forEach(System.out::println);
    }

    // g)
    static void testCreatePlayersAlreadyExists(DataRetriever dr) {
        System.out.println("\n--- g) createPlayers (joueur existant) ---");
        try {
            List<Player> players = List.of(
                    new Player(6, "Jude Bellingham", 23, PlayerPostionEnum.STR, null),
                    new Player(7, "Pedri", 24, PlayerPostionEnum.MIDF, null)
            );
            dr.createPlayers(players);
        } catch (RuntimeException e) {
            System.out.println("Exception attendue ✔ : " + e.getMessage());
        }
    }

    // h)
    static void testCreatePlayersSuccess(DataRetriever dr) {
        System.out.println("\n--- h) createPlayers (succès) ---");
        List<Player> players = List.of(
            new Player(6, "Vini", 25, PlayerPostionEnum.STR, null),
            new Player(7, "Pedri", 24, PlayerPostionEnum.MIDF, null)
        );
        dr.createPlayers(players).forEach(System.out::println);
    }

    // i)
    static void testSaveTeamAddPlayer(DataRetriever dr) {
        System.out.println("\n--- i) saveTeam (ajout joueur à Real Madrid) ---");
        Team team = dr.findTeamById(1);
        team.getPlayers().add(
                new Player(6, "Vini", 25, PlayerPostionEnum.STR, null)
        );
        dr.saveTeam(team);
        System.out.println(dr.findTeamById(1));
    }

    // j)
    static void testSaveTeamRemovePlayers(DataRetriever dr) {
        System.out.println("\n--- j) saveTeam (suppression joueurs FC Barcelone) ---");
        Team team = new Team(2, "FC Barcelona", ContinentEnum.EUROPA);
        team.setPlayers(new ArrayList<>());
        dr.saveTeam(team);
        System.out.println(dr.findTeamById(2));
    }
}
