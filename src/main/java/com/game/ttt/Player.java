package com.game.ttt;

public class Player {

	public int ID;
	
	private char placeHolder;
	
	private String name;
	
    public Player(int id, char ch, String s)
    {
    	ID = id;
    	placeHolder = ch;
    	name = s;
    }
    
    public char getPlaceHolder()
    {
    	return placeHolder;
    }
    
    public String getName()
    {
    	return name;
    }
}
