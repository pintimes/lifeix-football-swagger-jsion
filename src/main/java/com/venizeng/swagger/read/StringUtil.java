package com.venizeng.swagger.read;

import java.util.regex.Matcher;

public class StringUtil {

	public static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

	/**
	 * 移除反斜杠
	 * @param s
	 * @return
	 */
	public static String removeSlash(String s) {
		String qu = Matcher.quoteReplacement("\\");
		String result = s.replaceAll(qu, "/");
		return result;
	}
}
