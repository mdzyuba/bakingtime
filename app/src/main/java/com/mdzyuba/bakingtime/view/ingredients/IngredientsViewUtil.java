package com.mdzyuba.bakingtime.view.ingredients;

import android.content.Context;
import android.text.TextUtils;

import com.mdzyuba.bakingtime.R;

public class IngredientsViewUtil {

    public static String formatQuantity(Context context, float f) {
        if (f % 1.0 != 0) {
            return String.format("%s", f);
        }
        return context.getString(R.string.quantity_number_format, f);
    }

    public static String formatMeasure(String measure) {
        if (TextUtils.isEmpty(measure)) {
            return "";
        }
        return measure.toLowerCase();
    }
}
