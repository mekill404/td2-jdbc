package com.mekill404.models;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private int id;
    private String name;
    private ContinentEnum continent;
    private List<Player> players = new ArrayList<>();

    public Team(int id, String name, ContinentEnum continent)
    {
        this.id = id;
        this.name = name;
        this.continent = continent;
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
    
    public void addPlayer(Player player)
    {
        this.players.add(player);
    }
    
    public List<Player> getPlayers()
    {
        return players;
    }
    
    public void setPlayers(List<Player> players)
    {
        this.players = players;
    }

    public int getPlayersCount()
    {
        return this.players.size();
    }
}