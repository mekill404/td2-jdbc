package com.mekill404.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private Integer id;
    private String name;
    private ContinentEnum continent;
    private List<Player> players;

    public Team(int id, String name, ContinentEnum continent)
    {
        this.id = id;
        this.name = name;
        this.continent = continent;
        this.players = new ArrayList<>();
    }
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    public void setName(String name) 
    {
        this.name = name;
    }
    public ContinentEnum getContinent()
    {
        return continent;
    }
    public void setContinent(ContinentEnum continent)
    {
        this.continent = continent;
    }
    
    
    public List<Player> getPlayers()
    {
        return players;
    }
    
    public void setPlayers(List<Player> players)
    {
        this.players = players;
    }

    public void addPlayer(Player player)
    {
        if (player != null) 
        {
            this.players.add(player);
            
        }
    }
    public int getPlayersCount()
    {
        return (players != null) ? players.size() : 0;
    }
}