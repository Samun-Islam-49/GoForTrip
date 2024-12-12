package com.samun.gofortrip.helpers;

import static com.samun.gofortrip.activities.SplashActivity.email;

public class UserVerification {

    public static boolean isUser(String email) {
        return !email.equals(email());
    }

    public static String getFirebasePath(String string) {
        return string.replaceAll("[.#$]","").replaceAll("\\[", "").replaceAll("]","");
    }
}
