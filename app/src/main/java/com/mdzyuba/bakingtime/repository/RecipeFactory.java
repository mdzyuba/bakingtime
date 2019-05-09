package com.mdzyuba.bakingtime.repository;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mdzyuba.bakingtime.model.Recipe;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RecipeFactory {

    private static final String RECIPES_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static final String DELIMITER = "\\A";

    public Collection<Recipe> loadRecipes(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Recipe>>(){}.getType();
        return gson.fromJson(json, collectionType);
    }

    public Collection<Recipe> loadRecipes(Context context) throws IOException {
        URL url = getUrl();
        return loadRecipes(getResponseFromHttpUrl(context, url));
    }

    private URL getUrl() throws MalformedURLException {
        Uri uri = Uri.parse(RECIPES_URL).buildUpon().build();
        URL url = new URL(uri.toString());
        return url;
    }

    public String getResponseFromHttpUrl(Context context, URL url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = HttpClientProvider.getClient(context);
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            try (InputStream in = body.byteStream())  {
                Scanner scanner = new Scanner(in);
                scanner.useDelimiter(DELIMITER);

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            }
        }
    }
}
