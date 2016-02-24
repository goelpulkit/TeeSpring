package com.teespring.challenge.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ScenarioResponse {
	@SerializedName("scenario_id")
	private String scenarioId;
	private List<Answer> answers;
	
	public String getScenarioId() {
		return scenarioId;
	}
	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}
	public List<Answer> getAnswers() {
		return answers;
	}
	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
}
