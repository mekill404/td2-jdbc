package com.mekill404.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mekill404.configuration.DBConnection;
import com.mekill404.model.ContinentEnum;
import com.mekill404.model.Player;
import com.mekill404.model.PlayerPostionEnum;
import com.mekill404.model.Team;

public class DataRetriever 
{
    private final DBConnection dbConnection = new DBConnection();

    public Team findTeamById(Integer id)
    {
        String sqlTeam = "SELECT * FROM Team WHERE id = ?";
        String sqlPlayers = "SELECT * FROM Player WHERE id_team = ?";
        try 
        (
            Connection connection = dbConnection.getDBConnection();
            PreparedStatement psTeam = connection.prepareStatement(sqlTeam)
        )
        {
            psTeam.setInt(1, id);
            try (ResultSet rsTeam = psTeam.executeQuery()) 
            {
                if (rsTeam.next()) 
                {
                    Team team = new Team
                    (
                        rsTeam.getInt("id"),
                        rsTeam.getString("name"),
                        ContinentEnum.valueOf(rsTeam.getString("continent"))
                    );
                    try (PreparedStatement psPlayers = connection.prepareStatement(sqlPlayers)) 
                    {
                        psPlayers.setInt(1, id);
                        try (ResultSet rsPlayers = psPlayers.executeQuery())
                        {
                            while (rsPlayers.next())
                            {
                                Player player = new Player
                                (
                                    rsPlayers.getInt("id"),
                                    rsPlayers.getString("name"),
                                    PlayerPostionEnum.valueOf(rsPlayers.getString("position")),
                                    team
                                );
                                team.addPlayer(player);
                            }
                        }
                    }
                return team;       
                }
                
                
            }
            
        } catch (SQLException e)
        {
            throw new RuntimeException("Erreur lors de la récupération de l'équipe"+ id, e);        
        }
        return null;
    }
}
