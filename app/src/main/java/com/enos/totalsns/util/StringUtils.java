package com.enos.totalsns.util;

import java.util.HashMap;

public class StringUtils {

    public static boolean isStringValid(String string) {
        return string != null && string.length() > 0;
    }

    public static int getActualSize(String[] strings) {
        int size = 0;
        if (strings == null) return size;

        for (String str : strings) {
            if (isStringValid(str)) size++;
        }

        return size;
    }

    public static String getExpandedUrlFromMap(HashMap<String, String> urlMap, String matchedText) {
        if (urlMap == null) return matchedText;
        String normalizedString;
        if (urlMap.containsKey(matchedText)) {
            normalizedString = urlMap.get(matchedText);
        } else {
            normalizedString = matchedText;
        }

        normalizedString = checkHttpSchemeAndInsertIfNotExist(normalizedString);
        return normalizedString;
    }

    public static String checkHttpSchemeAndInsertIfNotExist(String matchedText) {
        String normalizedString = matchedText;
        if (!matchedText.contains("http://") && !matchedText.contains("https://")) {
            normalizedString = "https://" + normalizedString;
        } else if (matchedText.contains("http://")) {
            normalizedString = normalizedString.replace("http://", "https://");
        }
        return normalizedString;
    }

    public static String removeUnnecessaryString(String matchedText) {
        if (matchedText == null) return null;
        String result = matchedText.replaceAll(" ", "").replaceAll("\n", "");
        return result;
    }
}
