package de.tud.kom.challenge.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberTool {
	public static List<String> extractGroupOfNumbersFromString(String line) {
		ArrayList<String> numbers = new ArrayList<String>();

		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(line);

		while (m.find()) {
			numbers.add(m.group());
		}

		return numbers;
	}
}
