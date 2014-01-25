package com.dryerzinia.pokemon.test.util;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.StringStream;

public class JSONObjectTest {

	@Test
	public void testJSONToArray() {
		fail("Not yet implemented");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testJSONToObject() {

		HashMap<String, Object> result;
		StringStream jsonStream;
		String jsonText, text;
		int number, numbertwo;

		/*
		 * No Whitespace test
		 */
		
		jsonText = "{\"text\":\"DrYerzinia\",\"number\":123,\"numbertwo\":321}";
		jsonStream = new StringStream(jsonText);

		result = (HashMap<String, Object>) JSONObject.JSONToObject(jsonStream);

		text = (String) result.get("text");
		System.out.println("Text is: " + text);
		assertTrue("no whitespace text fail", text.equals("DrYerzinia"));

		number = ((Float)result.get("number")).intValue();
		System.out.println("Number is: " + number);
		assertTrue("no whitespace number fail", number == 123);

		numbertwo = ((Float)result.get("numbertwo")).intValue();
		System.out.println("Numbertwo is: " + numbertwo);
		assertTrue("no whitespace numbertwo fail", numbertwo == 321);

		/*
		 * Whitespace Test
		 */
		
		jsonText = "{\n\t\"text\" : \"DrYerzinia\",\n\t\"number\" : 123,\n\t\"numbertwo\":321\n}";
		jsonStream = new StringStream(jsonText);

		result = (HashMap<String, Object>) JSONObject.JSONToObject(jsonStream);

		text = (String) result.get("text");
		System.out.println("Text is: " + text);
		assertTrue("no whitespace text fail", text.equals("DrYerzinia"));

		((Float)result.get("number")).intValue();
		System.out.println("Number is: " + number);
		assertTrue("no whitespace number fail", number == 123);

		numbertwo = ((Float)result.get("numbertwo")).intValue();
		System.out.println("Numbertwo is: " + numbertwo);
		assertTrue("no whitespace numbertwo fail", numbertwo == 321);

		/*
		 * Numerical array test
		 */

	}

}
