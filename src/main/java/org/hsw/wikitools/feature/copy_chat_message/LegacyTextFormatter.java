package org.hsw.wikitools.feature.copy_chat_message;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

// format component into legacy formatting codes with '&' ('§' in the text)
public class LegacyTextFormatter {
    private static final char FORMAT_TAG_SYMBOL = '&';
    private static final char SECTION_SIGN = '§';
    private static final String NEWLINE_SYMBOL = "/";
    private static final String ESCAPE_SYMBOL = "\\";

    private static final Set<ChatFormatting> COLORS = EnumSet.range(ChatFormatting.BLACK, ChatFormatting.WHITE);

    private static final LegacyStyle PLAIN_STYLE = new LegacyStyle(null, EnumSet.noneOf(ChatFormatting.class));

    public LegacyText formatComponent(Component component) {
        StringBuilder sb = new StringBuilder();
        Set<String> unknownColors = new LinkedHashSet<>();
        LegacyStyle[] currentStyle = {PLAIN_STYLE};

        component.visit((style, text) -> {
            if (text.isEmpty()) {
                return Optional.empty();
            }

            LegacyStyle segmentStyle = LegacyStyle.of(style, unknownColors);
            if (!startsWithColorCode(text)) {
                sb.append(formatStyleChange(currentStyle[0], segmentStyle));
            }
            sb.append(escapeText(text).replace(SECTION_SIGN, FORMAT_TAG_SYMBOL));

            currentStyle[0] = applyFormattingCodes(segmentStyle, text);
            return Optional.empty();
        }, Style.EMPTY);

        return new LegacyText(sb.toString(), unknownColors);
    }

    // escape text before legacy conversion: color codes are still using '§' here
    private static String escapeText(String text) {
        return text
                .replace(String.valueOf(FORMAT_TAG_SYMBOL), ESCAPE_SYMBOL + FORMAT_TAG_SYMBOL)
                .replace(NEWLINE_SYMBOL, ESCAPE_SYMBOL + NEWLINE_SYMBOL);
    }

    private static boolean startsWithColorCode(String text) {
        return text.length() >= 2 && text.charAt(0) == SECTION_SIGN
                && COLORS.contains(ChatFormatting.getByCode(text.charAt(1)));
    }

    private static LegacyStyle applyFormattingCodes(LegacyStyle segmentStyle, String text) {
        LegacyStyle style = segmentStyle;
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) != SECTION_SIGN) {
                continue;
            }

            ChatFormatting formatting = ChatFormatting.getByCode(text.charAt(++i));
            if (formatting == null) {
                continue;
            }

            if (formatting == ChatFormatting.RESET) {
                style = segmentStyle;
            }
            else if (COLORS.contains(formatting)) {
                style = new LegacyStyle(formatting, EnumSet.noneOf(ChatFormatting.class));
            }
            else {
                Set<ChatFormatting> decorations = EnumSet.of(formatting);
                decorations.addAll(style.decorations());
                style = new LegacyStyle(style.color(), decorations);
            }
        }
        return style;
    }

    private static String formatStyleChange(LegacyStyle currentStyle, LegacyStyle nextStyle) {
        if (nextStyle.equals(currentStyle)) {
            return "";
        }

        boolean onlyAddsDecorations = currentStyle.color() == nextStyle.color()
                && nextStyle.decorations().containsAll(currentStyle.decorations());
        if (onlyAddsDecorations) {
            Set<ChatFormatting> addedDecorations = EnumSet.copyOf(nextStyle.decorations());
            addedDecorations.removeAll(currentStyle.decorations());
            return formatCodes(addedDecorations);
        }

        // a new code resets all styles
        ChatFormatting resetCode = nextStyle.color() != null ? nextStyle.color() : ChatFormatting.RESET;
        return toLegacyCode(resetCode) + formatCodes(nextStyle.decorations());
    }

    private static String formatCodes(Set<ChatFormatting> formattings) {
        StringBuilder sb = new StringBuilder();
        for (ChatFormatting formatting : formattings) {
            sb.append(toLegacyCode(formatting));
        }
        return sb.toString();
    }

    private static String toLegacyCode(ChatFormatting formatting) {
        return formatting.toString().replace(SECTION_SIGN, FORMAT_TAG_SYMBOL);
    }

    @Nullable
    private static ChatFormatting toLegacyColor(@Nullable TextColor color, Set<String> unknownColors) {
        if (color == null) {
            return null;
        }

        for (ChatFormatting legacyColor : COLORS) {
            if (TextColor.fromLegacyFormat(legacyColor).getValue() == color.getValue()) {
                return legacyColor;
            }
        }

        unknownColors.add(String.format("#%06X", color.getValue()));
        return null;
    }

    private record LegacyStyle(@Nullable ChatFormatting color, Set<ChatFormatting> decorations) {
        static LegacyStyle of(Style style, Set<String> unknownColors) {
            Set<ChatFormatting> decorations = EnumSet.noneOf(ChatFormatting.class);
            if (style.isObfuscated()) decorations.add(ChatFormatting.OBFUSCATED);
            if (style.isBold()) decorations.add(ChatFormatting.BOLD);
            if (style.isStrikethrough()) decorations.add(ChatFormatting.STRIKETHROUGH);
            if (style.isUnderlined()) decorations.add(ChatFormatting.UNDERLINE);
            if (style.isItalic()) decorations.add(ChatFormatting.ITALIC);
            return new LegacyStyle(toLegacyColor(style.getColor(), unknownColors), decorations);
        }
    }
}
