package io.thatsimple.authservice.utils;

import java.util.UUID;

public class Rando {
    public static String getRandomString() {
        return UUID.randomUUID().toString().split("-")[0];
    }
}
