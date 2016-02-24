package com.teespring.challenge.models;

import java.util.LinkedList;
import java.util.List;

public class Answer {
	private List<String> inks;
	
	public Answer() {
		inks = new LinkedList<String>();
	}

	public List<String> getInks() {
		return inks;
	}

	public void setInks(List<String> inks) {
		this.inks = inks;
	}
}
