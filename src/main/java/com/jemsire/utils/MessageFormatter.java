package com.jemsire.utils;

import com.hypixel.hytale.server.core.Message;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for formatting messages with colors and converting them to Hytale Message objects.
 * Supports color tags like <red>, <#FF0000>, and legacy & color codes.
 * Uses ColorUtils to parse colors and apply them to Hytale Messages.
 * 
 * Supported formats:
 * - <red>text</red> - Named colors
 * - <#FF0000>text</#FF0000> - Hex colors
 * - &atext - Legacy color codes
 */
public class MessageFormatter {
    // Pattern for color tags: <tag> or <tag:arg>
    private static final Pattern TAG_PATTERN = Pattern.compile("<([^>]+)>");
    
    /**
     * Formats a message string and creates a Hytale Message object.
     * Extracts color information from tags/codes and applies it to the message.
     * 
     * @param message The message string with formatting
     * @return A Message object with formatting applied, or plain text if parsing fails
     */
    public static Message format(String message) {
        if (message == null || message.isEmpty()) {
            return Message.raw("");
        }
        
        // First, check if it contains legacy & color codes
        if (message.contains("&") && hasLegacyColorCodes(message)) {
            return parseLegacyColorCodes(message);
        }
        
        // Check if it contains color tags
        if (!containsColorTags(message)) {
            return Message.raw(message);
        }
        
        try {
            // Extract text and color from tags
            String plainText = stripTags(message);
            Color color = extractColorFromTags(message);
            
            Message formattedMessage = Message.raw(plainText);
            
            // Apply color if found
            if (color != null) {
                try {
                    formattedMessage = formattedMessage.color(color);
                } catch (Exception e) {
                    Logger.warning("Failed to apply color to message: " + e.getMessage());
                }
            }
            
            return formattedMessage;
            
        } catch (Exception e) {
            Logger.warning("Failed to format message, falling back to plain text: " + e.getMessage());
            // Fallback: strip tags and return plain text
            return Message.raw(stripTags(message));
        }
    }
    
    /**
     * Extracts color information from color tags.
     * 
     * @param text The text with color tags
     * @return Color object if found, null otherwise
     */
    private static Color extractColorFromTags(String text) {
        Matcher matcher = TAG_PATTERN.matcher(text);
        
        while (matcher.find()) {
            String tagContent = matcher.group(1);
            
            // Handle hex colors: <#FF0000>
            if (tagContent.startsWith("#")) {
                String hex = tagContent.substring(1);
                Color color = ColorUtils.parseHexColor(hex);
                if (color != null) {
                    return color;
                }
            }
            
            // Handle named colors: <red>, <blue>, etc.
            // Check if it's a color name (not a closing tag or other tag)
            if (!tagContent.startsWith("/") && !tagContent.contains(":")) {
                Color color = ColorUtils.getNamedColor(tagContent);
                if (color != null) {
                    return color;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Strips all color tags from text, leaving only the content.
     * 
     * @param text The text with tags
     * @return Plain text without tags
     */
    private static String stripTags(String text) {
        // Remove all tags: <tag> and </tag>
        return text.replaceAll("<[^>]+>", "");
    }
    
    /**
     * Parses legacy & color codes (e.g., &a, &c, &l).
     * 
     * @param text The text with legacy color codes
     * @return Message with color applied
     */
    private static Message parseLegacyColorCodes(String text) {
        // Extract plain text
        String plainText = ColorUtils.stripColorCodes(text);
        
        // Find the first color code
        Color color = null;
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && ColorUtils.isColorCode(chars[i + 1])) {
                color = ColorUtils.getColorForCode(chars[i + 1]);
                break; // Use first color found
            }
        }
        
        Message message = Message.raw(plainText);
        if (color != null) {
            try {
                message = message.color(color);
            } catch (Exception e) {
                Logger.warning("Failed to apply legacy color: " + e.getMessage());
            }
        }
        
        return message;
    }
    
    /**
     * Checks if text contains legacy & color codes.
     * 
     * @param text The text to check
     * @return true if legacy color codes are found
     */
    private static boolean hasLegacyColorCodes(String text) {
        if (text == null || !text.contains("&")) {
            return false;
        }
        
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && ColorUtils.isColorCode(chars[i + 1])) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if a string contains color formatting tags.
     * 
     * @param text The text to check
     * @return true if the text contains color tags
     */
    public static boolean containsColorTags(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return text.matches(".*<[^>]+>.*");
    }
}
