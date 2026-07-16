package org.hsw.wikitools.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MctextTemplateFormatter {
    private static final String formatTagSymbol = "&";
    private static final String newlineSymbol = "/";

    private final Map<String, String> extraEscapes;

    public MctextTemplateFormatter(Map<String, String> extraEscapes) {
        this.extraEscapes = extraEscapes;
    }

    public String formatName(String name) {
        boolean removeFormattingCode = true;
        return formatTextForTemplate(name, removeFormattingCode);
    }

    public String formatTitle(String name) {
        boolean removeFormattingCode = false;
        return formatTextForTemplate(name, removeFormattingCode);
    }

    public String formatLore(List<String> lore) {
        List<String> loreList = new ArrayList<>();
        String templateDelimiter = "/";
        boolean removeFormattingCode = false;

        for (String line : lore) {
            String formattedLine = formatTextForTemplate(line, removeFormattingCode);
            loreList.add(formattedLine);
        }

        return String.join(templateDelimiter, loreList);
    }

    private String formatTextForTemplate(String text, boolean removeFormattingCode) {
        text = handleAllEscapes(text);

        text = formatFormattingCode(text, removeFormattingCode);

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
        for (Map.Entry<String, String> entry : extraEscapes.entrySet()) {
            text = text.replaceAll(entry.getKey(), entry.getValue());
        }
        return text;
    }

    private static String formatFormattingCode(String text, boolean removeFormattingCode) {
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
        Map<String, String> templateRelatedReplacements = new HashMap<String, String>() {{
            put("\\|", "{{!}}"); // Replace | with {{!}}
            put("=", "{{=}}"); // Replace = with {{=}}
        }};
        for (Map.Entry<String, String> entry : templateRelatedReplacements.entrySet()) {
            text = text.replaceAll(entry.getKey(), entry.getValue());
        }
        return text;
    }

}
