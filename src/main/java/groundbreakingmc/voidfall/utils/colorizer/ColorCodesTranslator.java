package groundbreakingmc.voidfall.utils.colorizer;

import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;

public final class ColorCodesTranslator {

    private static final char COLOR_CHAR = '§';
    private static final CharSet CODES = new CharOpenHashSet(new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f',
            'A', 'B', 'C', 'D', 'E', 'F',
            'k', 'l', 'm', 'n', 'o', 'r', 'x',
            'K', 'L', 'M', 'N', 'O', 'R', 'X'
    });

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        final char[] charArray = textToTranslate.toCharArray();
        int i = 0;
        while (i < charArray.length - 1) {
            if (charArray[i] == altColorChar) {
                final char nextChar = charArray[i + 1];
                if (CODES.contains(nextChar)) {
                    charArray[i] = COLOR_CHAR;
                    charArray[++i] = (char) (nextChar | 0x20);
                }
            }
            i++;
        }

        return new String(charArray);
    }
}
