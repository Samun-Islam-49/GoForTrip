package com.samun.gofortrip.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samun.gofortrip.models.Package;
import com.samun.gofortrip.models.UserInfo;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PrefManager {

    public static final String SHARED_PREFS = "profile", PROFILE_LISTS = "userInfo", LOG = "logs" , EMAIL = "email" , PACKAGE = "package" , RET_PIE_COLOR = "ret_pie_color" , RET_PIE_ENTRIES = "ret_pie_entries";


    public static void saveUserInfo(Context context, UserInfo userInfo) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor mDataEditor = sharedPreferences.edit();

        Gson gson = new Gson();
        String jsonString = gson.toJson(userInfo);

        mDataEditor.putString(PROFILE_LISTS, jsonString);
        mDataEditor.apply();
    }

    public static UserInfo getUserInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String encList = sharedPreferences.getString(PROFILE_LISTS, "");

        Gson gson = new Gson();
        Type type = new TypeToken<UserInfo>() {
        }.getType();

        return gson.fromJson(encList, type);
    }

    public static void deleteUserInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor mDataEditor = sharedPreferences.edit();

        mDataEditor.remove(PROFILE_LISTS);
        mDataEditor.apply();
    }

    public static void saveLog(Context context, String log) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor mDataEditor = sharedPreferences.edit();

        String encLog = Base64.encodeToString(log.getBytes(), 0);

        mDataEditor.putString(LOG, encLog);
        mDataEditor.apply();
    }


    public static String getLog(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String encLog = sharedPreferences.getString(LOG, "");

        return new String(Base64.decode(encLog, 0));
    }

    public static void deleteLog(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor mDataEditor = sharedPreferences.edit();

        mDataEditor.remove(LOG);
        mDataEditor.apply();
    }

    public static void savePkgInfo(Context context, Package pkg) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor mDataEditor = sharedPreferences.edit();

        Gson gson = new Gson();
        String jsonString = gson.toJson(pkg);

        mDataEditor.putString(PACKAGE, jsonString);
        mDataEditor.apply();
    }

    public static Package getPkgInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String encList = sharedPreferences.getString(PACKAGE, "");

        Gson gson = new Gson();
        Type type = new TypeToken<Package>() {
        }.getType();

        return gson.fromJson(encList, type);
    }

    public static void savePieEntryList(Context context , List<PieEntry> pieEntries) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor mDataEditor = sharedPreferences.edit();

        Gson gson = new Gson();
        String jsonString = gson.toJson(pieEntries);

        mDataEditor.putString(RET_PIE_ENTRIES, jsonString);
        mDataEditor.apply();
    }

    public static List<PieEntry> getPieEntryList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String encList = sharedPreferences.getString(RET_PIE_ENTRIES, "");

        Gson gson = new Gson();
        Type type = new TypeToken<List<PieEntry>>() {
        }.getType();

        return gson.fromJson(encList, type);
    }

    public static void savePieColorList(Context context , List<Integer> colors) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor mDataEditor = sharedPreferences.edit();

        Gson gson = new Gson();
        String jsonString = gson.toJson(colors);

        mDataEditor.putString(RET_PIE_COLOR, jsonString);
        mDataEditor.apply();
    }

    public static List<Integer> getPieColorList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String encList = sharedPreferences.getString(RET_PIE_COLOR, "");

        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>() {
        }.getType();

        return gson.fromJson(encList, type);
    }
}
