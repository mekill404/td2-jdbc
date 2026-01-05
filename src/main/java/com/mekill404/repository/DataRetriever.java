package com.mekill404.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mekill404.configuration.DBConnection;
import com.mekill404.model.ContinentEnum;
import com.mekill404.model.Player;
import com.mekill404.model.PlayerPostionEnum;
import com.mekill404.model.Team;

public class DataRetriever 
{
    private final DBConnection dbConnection;
    public DataRetriever(DBConnection dbConnection)
    {
        this.dbConnection = dbConnection;
    }
    public Team findTeamById(Integer id)
    {
        String sql = """
            SELECT 
                t.id AS team_id,
                t.name AS team_name,
                t.continent,
                p.id AS player_id,
                p.name AS player_name,
                p.position
            FROM team t
            LEFT JOIN player p ON p.id_team = t.id
            WHERE t.id = ?
        """;

        try (
            Connection connection = dbConnection.getDBConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                Team team = null;

                while (rs.next()) {

                    if (team == null) {
                        team = new Team(
                            rs.getInt("team_id"),
                            rs.getString("team_name"),
                            ContinentEnum.valueOf(rs.getString("continent"))
                        );
                    }

                    if (rs.getInt("player_id") != 0) {
                        Player player = new Player(
                            rs.getInt("player_id"),
                            rs.getString("player_name"),
                            PlayerPostionEnum.valueOf(rs.getString("position")),
                            team
                        );
                        team.addPlayer(player);
                    }
                }

                return team;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de l'équipe " + id, e);
        }
    }


    public List<Player> findPlayers(int page, int size) 
    {

        if (page < 1) page = 1;
        if (size < 1) size = 10;

        List<Player> players = new ArrayList<>();

        String sql = """
            SELECT id, name, position
            FROM player
            ORDER BY id
            LIMIT ? OFFSET ?
        """;

        int offset = (page - 1) * size;

        try (
            Connection connection = dbConnection.getDBConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, size);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    players.add(
                        new Player(
                            rs.getInt("id"),
                            rs.getString("name"),
                            PlayerPostionEnum.valueOf(rs.getString("position")),
                            null
                        )
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération paginée des joueurs", e);
        }

        return players;
    }

    public List<Player> createPlayers(List<Player> newPlayers) 
    {
        String checkSql = "SELECT COUNT(*) FROM player WHERE name = ?";
        String insertSql = "INSERT INTO player(name, age, \"position\", id_team) VALUES (?, ?, ?::position, ?)";

        try (Connection connection = dbConnection.getDBConnection()) {
            connection.setAutoCommit(false);

            try (
                PreparedStatement psCheck = connection.prepareStatement(checkSql);
                PreparedStatement psInsert = connection.prepareStatement(insertSql)
            ) {
                for (Player player : newPlayers) {

                    // validations
                    if (player.getName() == null || player.getName().isEmpty())
                        throw new RuntimeException("Impossible de créer un joueur sans nom");
                    if (player.getAge() < 15 || player.getAge() > 45)
                        throw new RuntimeException("Âge du joueur invalide: " + player.getAge());
                    if (player.getPosition() == null)
                        throw new RuntimeException("Position du joueur non définie");

                    // vérification doublons
                    psCheck.setString(1, player.getName());
                    try (ResultSet rs = psCheck.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) > 0) {
                            throw new RuntimeException("Joueur déjà existant : " + player.getName());
                        }
                    }

                    // insertion
                    psInsert.setString(1, player.getName());
                    psInsert.setInt(2, player.getAge());
                    psInsert.setObject(3, player.getPosition().name(), java.sql.Types.OTHER);
                    psInsert.setObject(4, player.getTeam() != null ? player.getTeam().getId() : null);
                    psInsert.executeUpdate();
                }

                connection.commit();
                return newPlayers;

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur création joueurs (transaction annulée)", e);
        }
    }

    public Team saveTeam(Team team) 
    {
        String checkSql = "SELECT COUNT(*) FROM team WHERE id = ?";
        String insertSql = "INSERT INTO team(name, continent) VALUES (?, ?)";
        String updateSql = "UPDATE team SET name = ?, continent = ? WHERE id = ?";
        String updatePlayerSql = "UPDATE player SET id_team = ? WHERE id = ?";

        try (Connection connection = dbConnection.getDBConnection())
        {
            connection.setAutoCommit(false);
            boolean exists;

            try (PreparedStatement psCheck = connection.prepareStatement(checkSql))
            {
                psCheck.setInt(1, team.getId());
                ResultSet rs = psCheck.executeQuery();
                rs.next();
                exists = rs.getInt(1) > 0;
            }

            if (exists) 
            {
                try (PreparedStatement psUpdate = connection.prepareStatement(updateSql))
                {
                    psUpdate.setString(1, team.getName());
                    psUpdate.setString(2, team.getContinent().name());
                    psUpdate.setInt(3, team.getId());
                    psUpdate.executeUpdate();
                }
            } 
            else 
            {
                try (PreparedStatement psInsert = connection.prepareStatement(insertSql))
                {
                    psInsert.setString(1, team.getName());
                    psInsert.setString(2, team.getContinent().name());
                    psInsert.executeUpdate();
                }
            }
            if (team.getPlayers() != null) 
            {
                try (PreparedStatement psPlayer = connection.prepareStatement(updatePlayerSql))
                {
                    for (Player player : team.getPlayers())
                    {
                        psPlayer.setInt(1, team.getId());
                        psPlayer.setInt(2, player.getId());
                        psPlayer.executeUpdate();
                    }
                }
            }

            connection.commit();
            return team;
        } catch (SQLException e)
        {
            throw new RuntimeException("Erreur sauvegarde équipe", e);
        }
    }
    public List<Team> findTeamsByPlayerName(String playerName) 
    {

        List<Team> teams = new ArrayList<>();

        String sql = """
            SELECT DISTINCT t.id, t.name, t.continent
            FROM team t
            JOIN player p ON p.id_team = t.id
            WHERE LOWER(p.name) LIKE LOWER(?)
        """;

        try (
            Connection connection = dbConnection.getDBConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        )
        {
            ps.setString(1, "%" + playerName + "%");

            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    teams.add(new Team(
                        rs.getInt("id"),
                        rs.getString("name"),
                        ContinentEnum.valueOf(rs.getString("continent"))
                    ));
                }
            }

        } catch (SQLException e)
        {
            throw new RuntimeException("Erreur recherche équipes", e);
        }
        return teams;
    }
    public List<Player> findPlayersByCriteria(
        String playerName,PlayerPostionEnum position,
        String teamName, ContinentEnum continent,
        int page, int size
    ) 
    {

        List<Player> players = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT p.id, p.name, p.position
            FROM player p
            JOIN team t ON p.id_team = t.id
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();
        if (playerName != null)
        {
            sql.append(" AND LOWER(p.name) LIKE LOWER(?)");
            params.add("%" + playerName + "%");
        }
        if (position != null)
        {
            sql.append(" AND p.position = ?::position_enum");
            params.add(position.name());
        }
        if (teamName != null)
        {
            sql.append(" AND LOWER(t.name) LIKE LOWER(?)");
            params.add("%" + teamName + "%");
        }
        if (continent != null)
        {
            sql.append(" AND t.continent = ?::continent_enum"); // <-- cast ici
            params.add(continent.name());
        }

        sql.append(" ORDER BY p.id LIMIT ? OFFSET ?");
        int offset = (page - 1) * size;
        params.add(size);
        params.add(offset);

        try (
            Connection connection = dbConnection.getDBConnection();
            PreparedStatement ps = connection.prepareStatement(sql.toString())
        ) 
        {
            for (int i = 0; i < params.size(); i++)
            {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next()) {
                    players.add(new Player(
                        rs.getInt("id"),
                        rs.getString("name"),
                        PlayerPostionEnum.valueOf(rs.getString("position")),
                        null
                    ));
                }
            }

        } catch (SQLException e)
        {
            throw new RuntimeException("Erreur recherche joueurs avancée", e);
        }

        return players;
    }

}
