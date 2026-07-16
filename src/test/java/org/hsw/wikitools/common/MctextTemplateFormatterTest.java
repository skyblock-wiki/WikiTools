package org.hsw.wikitools.common;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MctextTemplateFormatterTest {
    @Nested
    class TestEscapeAndReplaceRules {
        private String formatTitle(String input) {
            MctextTemplateFormatter formatter = new MctextTemplateFormatter(new HashMap<>());
            return formatter.formatTitle(input);
        }

        @Test
        void should_escapeBackslash_withBackslash() {
            String input = "Line with \\ backslash";

            String result = formatTitle(input);

            String expected = "Line with \\\\ backslash";

            assertEquals(expected, result);
        }

        @Test
        void should_escapeAmpersand_withBackslash() {
            String input = "Line with & ampersand";

            String result = formatTitle(input);

            String expected = "Line with \\& ampersand";

            assertEquals(expected, result);
        }

        @Test
        void should_escapeForwardSlash_withBackslash() {
            String input = "Line with / forward slash";

            String result = formatTitle(input);

            String expected = "Line with \\/ forward slash";

            assertEquals(expected, result);
        }

        @Test
        void should_escapePipe_asTemplateLiteral() {
            String input = "Line with | pipe";

            String result = formatTitle(input);

            String expected = "Line with {{!}} pipe";

            assertEquals(expected, result);
        }

        @Test
        void should_escapeEqualSign_asTemplateLiteral() {
            String input = "Line with = equal sign";

            String result = formatTitle(input);

            String expected = "Line with {{=}} equal sign";

            assertEquals(expected, result);
        }
    }

    @Nested
    class TestSpecialEscapeRules {
        private String formatNameForUi(String input) {
            MctextTemplateFormatter formatter = new MctextTemplateFormatter(new HashMap<String, String>() {{
                put(",", "\\\\,");  // Replace , with \,
            }});
            return formatter.formatName(input);
        }

        @Test
        void should_escapeComma_withBackslash_whenUsingUiFormatter() {
            String input = "Line with , comma";

            String result = formatNameForUi(input);

            String expected = "Line with \\, comma";

            assertEquals(expected, result);
        }
    }

    @Nested
    class TestsForFormatName {
        private String formatName(String input) {
            MctextTemplateFormatter formatter = new MctextTemplateFormatter(new HashMap<>());
            return formatter.formatName(input);
        }

        @ParameterizedTest
        @CsvSource({
                "§aGreen text,Green text",
                "§a§lBold green text,Bold green text",
                "§aGreen text then §bBlue text,Green text then Blue text"
        })
        void shouldRemoveFormattingCodeAndItsPrefix(String input, String expected) {
            String result = formatName(input);

            assertEquals(expected, result);
        }
    }

    @Nested
    class TestsForFormatTitle {
        private String formatTitle(String input) {
            MctextTemplateFormatter formatter = new MctextTemplateFormatter(new HashMap<>());
            return formatter.formatTitle(input);
        }

        @ParameterizedTest
        @CsvSource({
                "§aGreen text,&aGreen text",
                "§a§lBold green text,&a&lBold green text",
                "§aGreen text then §bBlue text,&aGreen text then &bBlue text"
        })
        void shouldReplaceFormattingCodePrefixWithAmpersand(String input, String expected) {
            String result = formatTitle(input);

            assertEquals(expected, result);
        }
    }

    @Nested
    class TestsForFormatLore {
        private String formatLore(List<String> input) {
            MctextTemplateFormatter formatter = new MctextTemplateFormatter(new HashMap<>());
            return formatter.formatLore(input);
        }

        @Test
        void emptyLoreShouldResultInEmptyString() {
            String result = formatLore(Collections.emptyList());

            assertEquals("", result);
        }

        @Test
        void shouldUseCorrectLineSeparator() {
            List<String> input = Arrays.asList("Line 1", "Line 2", "Line 3");

            String result = formatLore(input);

            String expected = "Line 1/Line 2/Line 3";

            assertEquals(expected, result);
        }

        @ParameterizedTest
        @CsvSource({
                "§aGreen text,&aGreen text",
                "§a§lBold green text,&a&lBold green text",
                "§aGreen text then §bBlue text,&aGreen text then &bBlue text"
        })
        void shouldReplaceFormattingCodePrefixWithAmpersand(String input, String expected) {
            String result = formatLore(Collections.singletonList(input));

            assertEquals(expected, result);
        }
    }

}
