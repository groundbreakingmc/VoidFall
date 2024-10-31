package noslowdwn.voidfall.utils.colorizer;

import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;

public final class ColorCodesTranslator {

    private static final CharSet CODES = new CharOpenHashSet(new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f',
            'A', 'B', 'C', 'D', 'E', 'F',
            'k', 'l', 'm', 'n', 'o', 'r', 'x',
            'K', 'L', 'M', 'N', 'O', 'R', 'X'
    });

    public static String translateAlternateColorCodes(final char altColorChar, final String textToTranslate) {
        char[] b = textToTranslate.toCharArray();

        for (int i = 0, length = b.length - 1; i < length; ++i) {
            if (b[i] == altColorChar && CODES.contains(b[i + 1])) {
                b[i++] = '§';
                b[i] = Character.toLowerCase(b[i]);
            }
        }

        return new String(b);
    }
}
