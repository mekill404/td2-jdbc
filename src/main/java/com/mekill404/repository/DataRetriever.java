package com.mekill404.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.mekill404.configuration.DBConnection;
import com.mekill404.model.ContinentEnum;
import com.mekill404.model.Player;
import com.mekill404.model.PlayerPostionEnum;
import com.mekill404.model.Team;

public class DataRetriever {
    private final DBConnection dbConnection;

    public DataRetriever(DBConnection dbConnection)
    {
        this.dbConnection = dbConnection;
    }

    public Team findTeamById(Integer id)
    {
        String sql = "SELECT t.id AS team_id, t.name AS team_name, t.continent, " +
                    "p.id AS player_id, p.name AS player_name, p.position, p.goal_nb " +
                    "FROM team t " +
                    "LEFT JOIN player p ON p.id_team = t.id " +
                    "WHERE t.id = ?";

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try
        {
            connection = dbConnection.getDBConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            
            rs = ps.executeQuery();
            Team team = null;

            while (rs.next())
            {
                
                if (team == null) {
                    int teamId = rs.getInt("team_id");
                    String teamName = rs.getString("team_name");
                    String continentStr = rs.getString("continent");
                    
                    ContinentEnum continent = ContinentEnum.valueOf(continentStr);
                    team = new Team(teamId, teamName, continent);
                }
                int playerId = rs.getInt("player_id");
                if (playerId != 0)
                {
                    String playerName = rs.getString("player_name");
                    String posStr = rs.getString("position");
                    PlayerPostionEnum position = PlayerPostionEnum.valueOf(posStr);
                    Player player = new Player(playerId, playerName, position, team);
                    int goals = rs.getInt("goal_nb");
                    
                    if (rs.wasNull())
                    {
                        player.setGoalNb(null);
                    } else
                    {
                        player.setGoalNb(goals);
                    }

                    team.addPlayer(player);
                }
            }
            return team;

        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        } finally
        {
            try
            {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    public List<Player> findPlayers(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        List<Player> players = new ArrayList<>();
        String sql = "SELECT id, name, position FROM player ORDER BY id LIMIT ? OFFSET ?";

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    players.add(new Player(
                        rs.getInt("id"),
                        rs.getString("name"),
                        PlayerPostionEnum.valueOf(rs.getString("position")),
                        null
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération paginée", e);
        }
        return players;
    }

    public List<Player> createPlayers(List<Player> newPlayers) {
        String insertSql = "INSERT INTO player(name, age, \"position\", id_team) VALUES (?, ?, ?::position_enum, ?)";

        try (Connection connection = dbConnection.getDBConnection()) {
            connection.setAutoCommit(false); 

            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                for (Player player : newPlayers) {
                    ps.setString(1, player.getName());
                    ps.setInt(2, player.getAge());
                    
                    ps.setString(3, player.getPosition().name());
                    
                    if (player.getTeam() != null && player.getTeam().getId() != 0) {
                        ps.setInt(4, player.getTeam().getId());
                    } else {
                        ps.setNull(4, java.sql.Types.INTEGER);
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
                connection.commit();
                return newPlayers;
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("Transaction annulée", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Team saveTeam(Team team)
    {
        String insertSql = "INSERT INTO team(name, continent) VALUES (?, ?)";
        String updateSql = "UPDATE team SET name = ?, continent = ? WHERE id = ?";
        String updatePlayerSql = "UPDATE player SET id_team = ?, goal_nb = ? WHERE id = ?";

        try (Connection connection = dbConnection.getDBConnection()) {
            connection.setAutoCommit(false);
            try {
                Integer teamId = team.getId();
                if (teamId == null || teamId == 0) {
                    try (PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, team.getName());
                        ps.setObject(2, team.getContinent().name(), Types.OTHER);
                        ps.executeUpdate();
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) team.setId(rs.getInt(1));
                        }
                    }
                } else {
                    try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                        ps.setString(1, team.getName());
                        ps.setObject(2, team.getContinent().name(), Types.OTHER);
                        ps.setInt(3, team.getId());
                        ps.executeUpdate();
                    }
                }
                if (team.getPlayers() != null) {
                    try (PreparedStatement psP = connection.prepareStatement(updatePlayerSql)) {
                        for (Player p : team.getPlayers()) {
                            psP.setInt(1, team.getId());
                            if (p.getGoalNb() == null) psP.setNull(2, Types.INTEGER);
                            else psP.setInt(2, p.getGoalNb());
                            psP.setInt(3, p.getId());
                            psP.executeUpdate();
                        }
                    }
                }
                connection.commit();
                return team;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    
    public List<Team> findTeamsByPlayerName(String playerName) {
        List<Team> teams = new ArrayList<>();
        String sql = """
            SELECT DISTINCT t.id, t.name, t.continent
            FROM team t
            JOIN player p ON p.id_team = t.id
            WHERE p.name ILIKE ?
        """;
        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + playerName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    teams.add(new Team(
                        rs.getInt("id"), rs.getString("name"),
                        ContinentEnum.valueOf(rs.getString("continent"))
                    ));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return teams;
    }


    public List<Player> findPlayersByCriteria(
        String playerName, PlayerPostionEnum position,
        String teamName, ContinentEnum continent,
        int page, int size
    )
    {
        List<Player> players = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT p.* FROM player p 
            JOIN team t ON p.id_team = t.id 
            WHERE 1=1 """);
        List<Object> params = new ArrayList<>();

        if (playerName != null) {
            sql.append(" AND p.name ILIKE ?");
            params.add("%" + playerName + "%");
        }
        if (position != null) {
            sql.append(" AND p.position = ?::position_enum");
            params.add(position.name());
        }
        if (teamName != null) {
            sql.append(" AND t.name ILIKE ?");
            params.add("%" + teamName + "%");
        }
        if (continent != null) {
            sql.append(" AND t.continent = ?::continent_enum");
            params.add(continent.name());
        }

        sql.append(" ORDER BY p.id LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page - 1) * size);

        try (Connection conn = dbConnection.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    players.add(new Player(rs.getInt("id"), rs.getString("name"), 
                        PlayerPostionEnum.valueOf(rs.getString("position")), null));
                }
            }
        } catch (SQLException e)
        {
            throw new RuntimeException(e);

        }
        return players;
    }
}