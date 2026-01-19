package com.jemsire.utils;

import com.hypixel.hytale.server.core.Message;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Deque;
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
 * 
 * Uses Message.empty() and Message.insert() to support multiple colors in a single message.
 */
public class MessageFormatter {
    // Matches <tag>, <tag:arg>, </tag>
    private static final Pattern TAG_PATTERN = Pattern.compile("<(/?)([a-zA-Z0-9_#]+)(?::([^>]+))?>");
    
    /**
     * Simple state record to track color styling.
     */
    private static class ColorState {
        final Color color;
        
        ColorState() {
            this(null);
        }
        
        ColorState(Color color) {
            this.color = color;
        }
        
        ColorState withColor(Color color) {
            return new ColorState(color);
        }
        
        ColorState copy() {
            return new ColorState(color);
        }
    }
    
    /**
     * Formats a message string and creates a Hytale Message object.
     * Supports multiple colors by creating separate message segments and inserting them into a container.
     * 
     * @param message The message string with formatting
     * @return A Message object with formatting applied, or plain text if parsing fails
     */
    public static Message format(String message) {
        if (message == null || message.isEmpty()) {
            return Message.raw("");
        }
        
        // If it already contains legacy & codes, convert them to tags first
        if (message.contains("&") && hasLegacyColorCodes(message)) {
            message = convertLegacyCodesToTags(message);
        }
        
        // Check if it contains color tags
        if (!message.contains("<")) {
            return Message.raw(message);
        }
        
        try {
            return parse(message);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = e.getClass().getSimpleName();
            }
            Logger.warning("Failed to format message, falling back to plain text: " + errorMsg);
            // Fallback: strip tags and return plain text
            return Message.raw(stripTags(message));
        }
    }
    
    /**
     * Parses a string containing color formatting tags and converts it into a Hytale Message.
     * 
     * @param text The string to parse, containing color formatting tags
     * @return A formatted Message object ready to be sent to players
     */
    public static Message parse(String text) {
        if (!text.contains("<")) {
            return Message.raw(text);
        }
        
        Message root = Message.empty();
        
        // Stack keeps track of nested styles.
        // Example: Stack = [Base, Red, Red+Bold]
        Deque<ColorState> stateStack = new ArrayDeque<>();
        stateStack.push(new ColorState()); // Start with default empty state
        
        Matcher matcher = TAG_PATTERN.matcher(text);
        int lastIndex = 0;
        
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            
            // Handle text BEFORE this tag (using the state at the top of the stack)
            if (start > lastIndex) {
                String content = text.substring(lastIndex, start);
                Message segmentMsg = createStyledMessage(content, stateStack.peek());
                root.insert(segmentMsg);
            }
            
            // Process the tag to update the Stack
            boolean isClosing = "/".equals(matcher.group(1));
            String tagName = matcher.group(2).toLowerCase();
            String tagArg = matcher.group(3);
            
            if (isClosing) {
                if (stateStack.size() > 1) {
                    stateStack.pop();
                }
            } else {
                // Start with the current state, and modify it
                ColorState currentState = stateStack.peek();
                ColorState newState = currentState.copy();
                
                // Check named colors directly
                Color namedColor = ColorUtils.getNamedColor(tagName);
                if (namedColor != null) {
                    newState = newState.withColor(namedColor);
                } else {
                    // Handle color:arg format: <color:#FF0000> or <color:red>
                    if ("color".equals(tagName) || "c".equals(tagName) || "colour".equals(tagName)) {
                        if (tagArg != null) {
                            Color c = parseColorArg(tagArg);
                            if (c != null) {
                                newState = newState.withColor(c);
                            }
                        }
                    } else if (tagName.startsWith("#")) {
                        // Handle hex colors: <#FF0000>
                        Color c = ColorUtils.parseHexColor(tagName.substring(1));
                        if (c != null) {
                            newState = newState.withColor(c);
                        }
                    } else if ("reset".equals(tagName) || "r".equals(tagName)) {
                        stateStack.clear();
                        newState = new ColorState();
                    }
                }
                stateStack.push(newState);
            }
            
            lastIndex = end;
        }
        
        // Handle remaining text after last tag
        if (lastIndex < text.length()) {
            String content = text.substring(lastIndex);
            Message segmentMsg = createStyledMessage(content, stateStack.peek());
            root.insert(segmentMsg);
        }
        
        return root;
    }
    
    /**
     * Creates a Message with the current style state applied.
     * 
     * @param content The text content
     * @param state The color state to apply
     * @return A Message with the style applied
     */
    private static Message createStyledMessage(String content, ColorState state) {
        if (content == null || content.isEmpty()) {
            content = "";
        }
        
        Message msg = Message.raw(content);
        
        if (state != null && state.color != null) {
            msg = msg.color(state.color);
        }
        
        return msg;
    }
    
    /**
     * Parses a color from an argument string (named color or hex).
     * 
     * @param arg The color argument (e.g., "red", "#FF0000", "FF0000")
     * @return The Color object, or null if invalid
     */
    private static Color parseColorArg(String arg) {
        if (arg == null) {
            return null;
        }
        
        // Try named color first
        Color namedColor = ColorUtils.getNamedColor(arg);
        if (namedColor != null) {
            return namedColor;
        }
        
        // Try hex color
        return ColorUtils.parseHexColor(arg);
    }
    
    /**
     * Converts legacy & color codes to color tags for parsing.
     * 
     * @param text The text with legacy color codes
     * @return Text with color tags
     */
    private static String convertLegacyCodesToTags(String text) {
        StringBuilder result = new StringBuilder();
        char[] chars = text.toCharArray();
        
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '&' && i + 1 < chars.length) {
                char code = Character.toLowerCase(chars[i + 1]);
                
                if (ColorUtils.isColorCode(code)) {
                    Color color = ColorUtils.getColorForCode(code);
                    if (color != null) {
                        // Find the color name
                        String colorName = findColorName(color);
                        if (colorName != null) {
                            result.append("<").append(colorName).append(">");
                        }
                    }
                    i++; // Skip the code character
                } else if (code == 'r') {
                    // Reset code
                    result.append("<reset>");
                    i++; // Skip the code character
                } else {
                    // Unknown code, keep as-is
                    result.append(chars[i]);
                }
            } else {
                result.append(chars[i]);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Finds the name of a color by matching it to known colors.
     * 
     * @param color The color to find
     * @return The color name, or null if not found
     */
    private static String findColorName(Color color) {
        if (color == null) {
            return null;
        }
        
        // Check all named colors
        String[] colorNames = {"black", "dark_blue", "dark_green", "dark_aqua", "dark_red", 
                              "dark_purple", "gold", "gray", "dark_gray", "blue", "green", 
                              "aqua", "red", "light_purple", "yellow", "white"};
        
        for (String name : colorNames) {
            Color namedColor = ColorUtils.getNamedColor(name);
            if (namedColor != null && colorsMatch(color, namedColor)) {
                return name;
            }
        }
        
        return null;
    }
    
    /**
     * Checks if two colors match exactly.
     */
    private static boolean colorsMatch(Color c1, Color c2) {
        if (c1 == null || c2 == null) {
            return false;
        }
        return c1.getRed() == c2.getRed() && 
               c1.getGreen() == c2.getGreen() && 
               c1.getBlue() == c2.getBlue();
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
