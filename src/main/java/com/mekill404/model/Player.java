package com.mekill404.model;

public class Player {
    private int id;
    private String name;
    private PlayerPostionEnum position = PlayerPostionEnum.GK;
    private Team team;

    
    public Player(int id, String name, PlayerPostionEnum position, Team team)
    {
        this.id = id;
        this.name = name;
        this.position = position;
        this.team = team;
    }
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public PlayerPostionEnum getPosition()
    {
        return position;
    }
    public void setPosition(PlayerPostionEnum position)
    {
        this.position = position;
    }
    public Team getTeam() 
    {
        return team;
    }
    public void setTeam(Team team) 
    {
        this.team = team;
    }
    
    public String getTeamName()
    {
        return (team != null) ? team.getName() : "Sans club";    
    }
}
