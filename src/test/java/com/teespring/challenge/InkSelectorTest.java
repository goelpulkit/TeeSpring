package com.teespring.challenge;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.teespring.challenge.models.Ink;


public class InkSelectorTest {

	@Test()
	public void testVerifyAvailableInks() {

		InkSelector inkSelector = new InkSelector();
		
		List<Ink> availableInks = new LinkedList<Ink>();
		availableInks.add(TestModels.createInk("ABCD", "#121212", 1.2));
		availableInks.add(TestModels.createInk("AB12", "#131612", 1.8));
		availableInks.add(TestModels.createInk("ABCD", "#121212", 1.2));
		
		HashSet<String> invalidInkSet = inkSelector.verifyAvailableInks(availableInks);
		
		Assert.assertNotNull(invalidInkSet);
		Assert.assertEquals(invalidInkSet.size(), 1);
		Assert.assertNotNull(invalidInkSet.remove("ABCD"));
	}

}