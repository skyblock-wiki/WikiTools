package org.hsw.wikitools.feature.copy_item_tooltip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MctextModuleFormatter {
    private static final String formatTagSymbol = "&";
    private static final String newlineSymbol = "/";

    public String formatName(String name) {
        boolean removeFormattingCode = true;
        return formatTextForModule(name, removeFormattingCode);
    }

    public String formatTitle(String name) {
        boolean removeFormattingCode = false;
        return formatTextForModule(name, removeFormattingCode);
    }

    public String formatLore(List<String> lore) {
        List<String> loreList = new ArrayList<>();
        String moduleDelimiter = "/";
        boolean removeFormattingCode = false;

        for (String line : lore) {
            String formattedLine = formatTextForModule(line, removeFormattingCode);
            loreList.add(formattedLine);
        }

        return String.join(moduleDelimiter, loreList);
    }

    private String formatTextForModule(String text, boolean removeFormattingCode) {
        // Common handlers

        text = handleAllEscapes(text);

        text = formatFormattingCode(text, removeFormattingCode);

        // Extra handlers when used in module context, e.g. tooltip string

        text = handleAllReplacements(text);

        return text;
    }

    private String handleAllEscapes(String text) {
        Map<String, String> mctextRelatedReplacements = new HashMap<String, String>() {{
            put("\\\\", "\\\\\\\\"); // Replace \ with \\
            put(formatTagSymbol, "\\\\" + formatTagSymbol); // Escape format tag symbol
            put(newlineSymbol, "\\\\" + newlineSymbol); // Escape newline symbol
        }};
        for (Map.Entry<String, String> entry : mctextRelatedReplacements.entrySet()) {
            text = text.replaceAll(entry.getKey(), entry.getValue());
        }
        return text;
    }

    private String formatFormattingCode(String text, boolean removeFormattingCode) {
        if (removeFormattingCode) {
            // Remove color formatting
            text = text.replaceAll("§.", "");
        }
        else {
            // Replace all color codes (§) with &
            text = text.replaceAll("§", formatTagSymbol);
        }
        return text;
    }

    private String handleAllReplacements(String text) {
        Map<String, String> luaSingleQuotedStringRelatedReplacements = new HashMap<String, String>() {{
            // Backslash handler: The backslash is an escape character in lua
            // So we need to escape all backslashes again
            put("\\\\", "\\\\\\\\"); // Replace \ with \\
            put("'", "\\\\'"); // Replace ' with \'
        }};
        for (Map.Entry<String, String> entry : luaSingleQuotedStringRelatedReplacements.entrySet()) {
            text = text.replaceAll(entry.getKey(), entry.getValue());
        }
        return text;
    }

}
