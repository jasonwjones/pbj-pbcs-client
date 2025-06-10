package com.jasonwjones.pbcs.client.impl.export;

import com.jasonwjones.pbcs.util.NumberUtil;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;

class ExportStringUtils {

    private ExportStringUtils() {}

    public static String leftPad(String text, int width) {
        if (text == null) text = "";
        return String.format("%" + width + "s", text);
    }

    public static String formatCurrency(String input, String format) {
        if (!StringUtils.hasText(input)) return null;
        if (NumberUtil.isNumeric(input)) {
            Double d = Double.parseDouble(input);
            DecimalFormat df = new DecimalFormat(format);
            return df.format(d);
        } else {
            return input;
        }
    }

    public static String repeat(String text, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(text);
        }
        return builder.toString();
    }
}