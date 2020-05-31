package net.sunken.common.util;

import lombok.NonNull;

public class StringUtil {

    public static String center(@NonNull String message, int center) {
        int messagePxSize = 0;

        for (char c : message.toCharArray()) {
            messagePxSize += DefaultFontInfo.getDefaultFontInfo(c).getLength();
            messagePxSize++;
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = center - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;

        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return (sb.toString() + message);
    }

}
