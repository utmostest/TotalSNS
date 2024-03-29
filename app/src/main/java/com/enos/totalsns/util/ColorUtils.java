package com.enos.totalsns.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.palette.graphics.Palette;

import java.util.List;

/**
 * Utilities for performing common color-related tasks.
 *
 * @author Ryan Ware
 */
public class ColorUtils {

    public static int getComplimentColor(Paint paint) {
        return getComplimentColor(paint.getColor());
    }

    /**
     * Returns the complimentary (opposite) color.
     *
     * @param color int RGB color to return the compliment of
     * @return int RGB of compliment color
     */
    public static int getComplimentColor(int color) {
        // get existing colors
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);

        // find compliments
        red = (~red) & 0xff;
        blue = (~blue) & 0xff;
        green = (~green) & 0xff;

        return Color.argb(alpha, red, green, blue);
    }

    public static String getComplimentColor(String hexColor) {
        return getHexStringForARGB(getComplimentColor(Color.parseColor(hexColor)));
    }

    /**
     * Converts an int RGB color representation into a hexadecimal {@link String}.
     *
     * @param argbColor int RGB color
     * @return {@link String} hexadecimal color representation
     */
    public static String getHexStringForARGB(int argbColor) {
        String hexString = "#";
        hexString += ARGBToHex(Color.alpha(argbColor));
        hexString += ARGBToHex(Color.red(argbColor));
        hexString += ARGBToHex(Color.green(argbColor));
        hexString += ARGBToHex(Color.blue(argbColor));

        return hexString;
    }

    /**
     * Converts an int R, G, or B value into a hexadecimal {@link String}.
     *
     * @param rgbVal int R, G, or B value
     * @return {@link String} hexadecimal value
     */
    private static String ARGBToHex(int rgbVal) {
        String hexReference = "0123456789ABCDEF";

        rgbVal = Math.max(0, rgbVal);
        rgbVal = Math.min(rgbVal, 255);
        rgbVal = Math.round(rgbVal);

        return String.valueOf(hexReference.charAt((rgbVal - rgbVal % 16) / 16) + "" + hexReference.charAt(rgbVal % 16));
    }

    public static int getBodyTextColorFromPalette(Bitmap resource) {
        if (resource != null) {
            Palette p = Palette.from(resource).generate();
            List<Palette.Swatch> swatches = p.getSwatches();
            if (swatches != null && swatches.size() > 0) {
                Palette.Swatch s = swatches.get(0);
                return s.getBodyTextColor();
            }
        }
        return Color.BLACK;
    }

    /**
     * https://stackoverflow.com/questions/33072365/how-to-darken-a-given-color-int
     * @param color color provided
     * @param factor factor to make color darker
     * @return int as darker color
     */
    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }
}