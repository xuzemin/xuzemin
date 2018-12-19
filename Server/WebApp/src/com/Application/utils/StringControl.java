package com.Application.utils;

import java.util.Arrays;

public class StringControl {
	public static String replaceOne(String string,int number,char item) {
		char[] status = string.toCharArray();
		status[number] = item;
		string = Arrays.toString(status).replaceAll("[\\[\\]\\s,]", "");
		return string;
	}
}
