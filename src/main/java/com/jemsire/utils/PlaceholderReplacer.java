package com.jemsire.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for replacing placeholders in strings.
 * Supports placeholders in the format {placeholderName}
 */
public class PlaceholderReplacer {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)\\}");

    /**
     * Replaces placeholders in a string with values from the provided map.
     * @param template The string template with placeholders like {player}, {position}, etc.
     * @param placeholders Map of placeholder names to their replacement values
     * @return The string with all placeholders replaced
     */
    public static String replacePlaceholders(String template, Map<String, String> placeholders) {
        if (template == null || template.isEmpty()) {
            return template;
        }

        if (placeholders == null || placeholders.isEmpty()) {
            return template;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholderName = matcher.group(1); // Get the name inside the braces
            String replacement = placeholders.get(placeholderName);
            
            if (replacement != null) {
                // Escape any special regex characters in the replacement
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            } else {
                // If placeholder not found, keep the original placeholder
                matcher.appendReplacement(result, matcher.group(0));
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
