package com.pilot.data.api;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Bezbedan storage za JWT tokene.
 */
public class TokenManager {

    private static final String PREFS_FILE = "pilot_secure_prefs";
    private static final String KEY_ACCESS_TOKEN  = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID       = "user_id";
    private static final String KEY_USER_EMAIL    = "user_email";
    private static final String KEY_USER_NAME     = "user_full_name";
    private static final String KEY_PROFILE_DONE  = "profile_complete";

    private static TokenManager instance;
    private final SharedPreferences prefs;

    private TokenManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREFS_FILE,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Greška pri inicijalizaciji bezbednog storage-a.", e);
        }
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
        return instance;
    }

    // ---- Čuvanje tokena posle logina/registracije ----

    public void saveTokens(String accessToken, String refreshToken) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    public void saveUserInfo(long userId, String email, String fullName, boolean profileComplete) {
        prefs.edit()
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_NAME, fullName)
                .putBoolean(KEY_PROFILE_DONE, profileComplete)
                .apply();
    }

    // ---- Getteri ----

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    public String getUserFullName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    public boolean isProfileComplete() {
        return prefs.getBoolean(KEY_PROFILE_DONE, false);
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    // ---- Čišćenje (logout) ----

    public void clearAll() {
        prefs.edit().clear().apply();
    }

    public void markProfileComplete() {
        prefs.edit().putBoolean(KEY_PROFILE_DONE, true).apply();
    }
}
