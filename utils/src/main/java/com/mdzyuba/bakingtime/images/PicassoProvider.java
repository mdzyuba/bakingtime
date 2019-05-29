package com.mdzyuba.bakingtime.images;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import com.mdzyuba.bakingtime.http.HttpClientProvider;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Provides a singleton instance of the picasso library.
 *
 * Applying Jake's suggestion to keep it as a singleton:
 * https://github.com/square/picasso/issues/1100
 *
 * This class is inspired by this example:
 * https://github.com/square/picasso/blob/master/picasso-sample/src/main/java/com/example/picasso/provider/PicassoProvider.java
 */
public class PicassoProvider {
    @SuppressLint("StaticFieldLeak")
    private static volatile Picasso instance;

    private PicassoProvider() {
    }

    @NonNull
    public static Picasso getPicasso(@NonNull Context context) {
        if (instance == null) {
            synchronized (PicassoProvider.class) {
                Picasso.Builder picassoBuilder =
                        new Picasso.Builder(context.getApplicationContext())
                                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Timber.e(exception, "Error loading an image: %s",  uri);
                    }
                });
                OkHttpClient client = HttpClientProvider.getClient(context);
                picassoBuilder.downloader(new OkHttp3Downloader(client));
                instance = picassoBuilder.build();
            }
        }
        return instance;
    }
}
