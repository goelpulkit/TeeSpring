package com.teespring.challenge;

import com.teespring.challenge.models.Ink;

public class TestModels {

	public static Ink createInk(String id, String color, double cost){
		Ink ink = new Ink();
		ink.setId(id);
		ink.setColor(color);
		ink.setCost(cost);
		return ink;
	}
}
