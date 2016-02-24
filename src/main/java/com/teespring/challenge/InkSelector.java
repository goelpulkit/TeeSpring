package com.teespring.challenge;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.teespring.challenge.models.Answer;
import com.teespring.challenge.models.AvailableInksResponse;
import com.teespring.challenge.models.Ink;
import com.teespring.challenge.models.Layer;
import com.teespring.challenge.models.Question;
import com.teespring.challenge.models.Scenario;
import com.teespring.challenge.models.ScenarioResponse;

public class InkSelector {
	public static final String BASE_URL = "http://challenge.teespring.com";
	public static final String AVAILABLE_INKS_URL = "/v1/inks";
	public static final String PRACTICE_QUESTION_URL = "/v1/question/practice";
	public static final String EVALUATE_QUESTION_URL = "/v1/question/evaluate";
	
	public static final String DEBUG_ANSWER_URL = "/v1/answer/practice";
	public static final String ANSWERS_URL = "/v1/answer/evaluate";
	
	public static final String AUTH_TOKEN = "0d960d44-7afe-46f9-acc2-f9fde45c7401";
	
	// Created this set to keep track of invalid inks. Some Ink ids are duplicate which may cause issue
	// during printing. We can ignore such inks and report it back to system.
	private HashSet<String> invalidInkSet = new HashSet<String>();
	
	public static void main(String[] args) {	
		InkSelector inkSelector = new InkSelector();
//		inkSelector.testPractice();
		inkSelector.testEvaluate();
	}
	
	public void testPractice(){
		Scenario scenario = getScenario(BASE_URL+PRACTICE_QUESTION_URL);
		List<Ink> availableInks = getAvailableInks(BASE_URL+AVAILABLE_INKS_URL);
		invalidInkSet = verifyAvailableInks(availableInks);
		
		ScenarioResponse response = getScenarioResponse(scenario, availableInks);
		Gson gson = new Gson();
		String jsonResponse = gson.toJson(response);

		String result = verifyAnswers(BASE_URL+ANSWERS_URL, jsonResponse);
		System.out.println(result);
	}
	
	public void testEvaluate(){
		Scenario scenario = getScenario(BASE_URL+EVALUATE_QUESTION_URL);
		List<Ink> availableInks = getAvailableInks(BASE_URL+AVAILABLE_INKS_URL);
		invalidInkSet = verifyAvailableInks(availableInks);
		
		ScenarioResponse response = getScenarioResponse(scenario, availableInks);
		Gson gson = new Gson();
		String jsonResponse = gson.toJson(response);

		String result = verifyAnswers(BASE_URL+ANSWERS_URL, jsonResponse);
		System.out.println(result);
	}
	
	protected HashSet<String> verifyAvailableInks(List<Ink> availableInks) {
		HashSet<String> inkSet = new HashSet<String>();
		HashSet<String> invalidInkSet = new HashSet<String>();
		for(Ink ink : availableInks){
			if(inkSet.contains(ink.getId())) {
				invalidInkSet.add(ink.getId());
			} else {
				inkSet.add(ink.getId());
			}
		}
		
		System.err.println("Number of invalid inks: "+invalidInkSet.size());
		
		return invalidInkSet;
	}
	
	public ScenarioResponse getScenarioResponse(Scenario scenario, List<Ink> availableInks) {
		List<Answer> answers = new LinkedList<Answer>();
		
		for(Question question : scenario.getQuestions()) {
			Answer answer = new Answer();
			for(Layer layer : question.getLayers()){
				// assuming printing cost can't be negative
				double minPrintingCost = -1.0;
				String inkId = null;
				for(Ink ink : availableInks) {
					if(invalidInkSet.contains(ink.getId())) {
						System.err.println("Invalid ink: "+ink.getId()+":"+ink.getColor()+":"+ink.getCost());
						continue;
					}
					double colorDifference = calculateColorDifference(ink.getColor(), layer.getColor());
					double totalPrintingCost = ink.getCost() * layer.getVolume();
					if(colorDifference <= 20.0d && (totalPrintingCost < minPrintingCost || minPrintingCost < 0) ) {
						minPrintingCost = totalPrintingCost;
						inkId = ink.getId();
					}
				}
				if(inkId != null) {
					answer.getInks().add(inkId);
				}
			}
			answers.add(answer);
		}
		ScenarioResponse scenarioResponse = new ScenarioResponse();
		scenarioResponse.setScenarioId(scenario.getScenarioId());
		scenarioResponse.setAnswers(answers);
		
		return scenarioResponse;
	}

	
	public Scenario getScenario(String url) {

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
        	HttpGet request = new HttpGet(url);
            request.addHeader("content-type", "application/json");
            request.addHeader("Auth-Token", AUTH_TOKEN);
            
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");

            com.google.gson.Gson gson = new com.google.gson.Gson();
            Scenario response = gson.fromJson(json, Scenario.class);

            System.out.println("scenarioId: "+response.getScenarioId());
            System.out.println("questions size: "+response.getQuestions().size());
            return response;
        } catch (IOException ex) {
        	System.err.println("Scenario GET call failed: " + ex.getMessage() );
        }
        return null;
    }
	
	public List<Ink> getAvailableInks(String url) {

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
        	HttpGet request = new HttpGet(url);
            request.addHeader("content-type", "application/json");
            request.addHeader("Auth-Token", AUTH_TOKEN);
            
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");

            com.google.gson.Gson gson = new com.google.gson.Gson();
            AvailableInksResponse response = gson.fromJson(json, AvailableInksResponse.class);

            System.out.println(response.getInks().size());
            return response.getInks();
        } catch (IOException ex) {
        	System.err.println("Available Inks GET call failed: " + ex.getMessage() );
        }
        return new LinkedList<Ink>();
    }
	
	public String verifyAnswers(String url, String body) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(body);
            request.addHeader("content-type", "application/json");
            request.addHeader("Auth-Token", AUTH_TOKEN);
            request.setEntity(params);
            HttpResponse result = httpClient.execute(request);
            String response = EntityUtils.toString(result.getEntity(), "UTF-8");
            return response;
        } catch (IOException ex) {
        	System.err.println("Answer evaluation call failed: " + ex.getMessage() );
        }
        return null;
    }
	
	// Calculating color difference by using Euclidean distance formula
	protected double calculateColorDifference(String inkColor, String teeColor){
		int inkColorRed = Integer.parseInt(inkColor.substring(1, 3), 16);
		int inkColorGreen = Integer.parseInt(inkColor.substring(3, 5), 16);
		int inkColorBlue = Integer.parseInt(inkColor.substring(5, 7), 16);
		
		int teeColorRed = Integer.parseInt(teeColor.substring(1, 3), 16);
		int teeColorGreen = Integer.parseInt(teeColor.substring(3, 5), 16);
		int teeColorBlue = Integer.parseInt(teeColor.substring(5, 7), 16);
		
		return Math.sqrt(Math.pow((inkColorRed - teeColorRed), 2) + Math.pow((inkColorGreen - teeColorGreen), 2) + Math.pow((inkColorBlue - teeColorBlue), 2));
	}
}
