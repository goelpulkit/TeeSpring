package com.teespring.challenge.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Scenario {

	@SerializedName("scenario_id")
	private String scenarioId;
	private List<Question> questions;
	public String getScenarioId() {
		return scenarioId;
	}
	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}
	public List<Question> getQuestions() {
		return questions;
	}
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
	
}
