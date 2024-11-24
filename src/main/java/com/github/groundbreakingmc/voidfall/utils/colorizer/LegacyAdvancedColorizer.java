package com.github.groundbreakingmc.voidfall.utils.colorizer;

public final class LegacyAdvancedColorizer implements IColorizer {

    private static final char COLOR_CHAR = '§';

    @Override
    public String colorize(final String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        final StringBuilder builder = new StringBuilder();
        final char[] messageChars = message.toCharArray();

        boolean isColor = false, isHashtag = false, isDoubleTag = false;

        for (int index = 0; index < messageChars.length; ) {
            final char currentChar = messageChars[index];
            if (isDoubleTag) {
                isDoubleTag = false;
                if (this.processDoubleTag(builder, messageChars, index)) {
                    index += 3;
                    continue;
                }

                builder.append("&##");

            } else if (isHashtag) {
                isHashtag = false;
                if (currentChar == '#') {
                    isDoubleTag = true;
                    index++;
                    continue;
                }

                if (this.processSingleTag(builder, messageChars, index)) {
                    index += 6;
                    continue;
                }

                builder.append("&#");

            } else if (isColor) {
                isColor = false;
                if (currentChar == '#') {
                    isHashtag = true;
                    index++;
                    continue;
                }

                if (this.isValidColorCharacter(currentChar)) {
                    builder.append(COLOR_CHAR).append(currentChar);
                    index++;
                    continue;
                }

                builder.append('&');

            } else if (currentChar == '&') {
                isColor = true;
                index++;
            } else {
                builder.append(currentChar);
                index++;
            }
        }

        this.appendRemainingColorTags(builder, isColor, isHashtag, isDoubleTag);

        return builder.toString();
    }

    private boolean processDoubleTag(final StringBuilder builder, final char[] messageChars, final int index) {
        if (index + 3 <= messageChars.length && this.isValidHexCode(messageChars, index, 3)) {
            builder.append(COLOR_CHAR).append('x');
            for (int i = index; i < index + 3; i++) {
                builder.append(COLOR_CHAR).append(messageChars[i]).append(COLOR_CHAR).append(messageChars[i]);
            }

            return true;
        }

        return false;
    }

    private boolean processSingleTag(final StringBuilder builder, final char[] messageChars, final int index) {
        if (index + 6 <= messageChars.length && this.isValidHexCode(messageChars, index, 6)) {
            builder.append(COLOR_CHAR).append('x');
            for (int i = index; i < index + 6; i++) {
                builder.append(COLOR_CHAR).append(messageChars[i]);
            }

            return true;
        }

        return false;
    }

    private boolean isValidHexCode(final char[] chars, final int start, final int length) {
        for (int i = start; i < start + length; i++) {
            char tmp = chars[i];
            if (!((tmp >= '0' && tmp <= '9') || (tmp >= 'a' && tmp <= 'f') || (tmp >= 'A' && tmp <= 'F'))) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidColorCharacter(final char c) {
        final boolean isColorNumb = c >= '0' && c <= '9';
        final boolean isColorChar = c >= 'a' && c <= 'f';
        final boolean isUpperCaseColorChar = c >= 'A' && c <= 'F';
        final boolean isResetChar = c == 'r';
        final boolean isUpperCaseResetChar = c == 'R';
        final boolean isStyleChar = c >= 'k' && c <= 'o';
        final boolean isUpperCaseStyleChar = c >= 'K' && c <= 'O';

        return isColorNumb || isColorChar || isUpperCaseColorChar || isResetChar || isUpperCaseResetChar || isStyleChar || isUpperCaseStyleChar;
    }

    private void appendRemainingColorTags(final StringBuilder builder, final boolean isColor, final boolean isHashtag, final boolean isDoubleTag) {
        if (isColor) {
            builder.append('&');
        } else if (isHashtag) {
            builder.append("&#");
        } else if (isDoubleTag) {
            builder.append("&##");
        }
    }
}