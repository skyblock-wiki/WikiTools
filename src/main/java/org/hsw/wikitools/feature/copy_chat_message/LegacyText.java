package org.hsw.wikitools.feature.copy_chat_message;

import java.util.Set;

/**
 * @param unknownColors hex codes of colors that have no legacy formatting code and were ignored
 */
public record LegacyText(String text, Set<String> unknownColors) {
}
