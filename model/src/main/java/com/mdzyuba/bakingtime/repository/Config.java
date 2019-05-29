package com.mdzyuba.bakingtime.repository;

import com.mdzyuba.model.BuildConfig;

public class Config {
    private static String recipeUrl = BuildConfig.BAKER_URL;

    public static String getRecipeUrl() {
        return recipeUrl;
    }

    /**
     * This is used for testing only.
     * @param url a recipeUrl.
     */
    public static void setRecipeUrl(String url) {
        recipeUrl = url;
    }
}
