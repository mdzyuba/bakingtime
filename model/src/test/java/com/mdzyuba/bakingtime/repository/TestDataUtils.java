package com.mdzyuba.bakingtime.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import androidx.annotation.Nullable;

public class TestDataUtils {
    private static final String DELIMITER = "\\A";

    @Nullable
    public static String getJsonString(String fileName) throws IOException {
        ClassLoader classLoader = TestDataUtils.class.getClassLoader();
        if (classLoader == null) {
            return null;
        }
        try (InputStream inputStream =
                     classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                return null;
            }
            Scanner scanner = new Scanner(inputStream);
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
