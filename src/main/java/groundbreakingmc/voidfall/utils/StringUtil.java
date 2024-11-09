package groundbreakingmc.voidfall.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StringUtil {

    private StringUtil() {

    }

    public static String replaceEach(@Nullable final String text, @NotNull final String[] searchList, @NotNull final String[] replacementList) {
        if (text == null || text.isEmpty() || searchList.length == 0 || replacementList.length == 0) {
            return text;
        }

        if (searchList.length != replacementList.length) {
            throw new IllegalArgumentException("Search and replacement arrays must have the same length.");
        }

        final StringBuilder result = new StringBuilder(text);

        for (int i = 0; i < searchList.length; ++i) {
            final String search = searchList[i];
            final String replacement = replacementList[i];

            for (int start = 0; (start = result.indexOf(search, start)) != -1; start += replacement.length()) {
                result.replace(start, start + search.length(), replacement);
            }
        }

        return result.toString();
    }
}
