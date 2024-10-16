package com.ppproperti.simarco.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.ppproperti.simarco.entities.Apartment;
import com.ppproperti.simarco.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomPreferences {
    public final static String DATA = "data";
    private Context context;
    private String code;

    public CustomPreferences(Context context) {
        this.context = context;
    }

    public CustomPreferences(Context context, String code) {
        this.context = context;
        this.code = code;
    }

    public void savePreferences(String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getPreference(String key, String def){
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, def);
    }

    public User getUser(){
        CustomPreferences customPreferences = new CustomPreferences(context, "user");
        String result = customPreferences.getPreference("user", null);
        return new Gson().fromJson(result, User.class);
    }

    public String getParcelableJson(String key){
        CustomPreferences customPreferences = new CustomPreferences(context, DATA);
        return customPreferences.getPreference(key, null);
    }

    public Class<?> getParcelableJson(String key, Class<?> cls){
        CustomPreferences customPreferences = new CustomPreferences(context, DATA);
        String json = customPreferences.getPreference(key, null);
        return (Class<?>) new Gson().fromJson(json, cls);
    }

    public void saveParcelableJson(String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public List<Apartment> getListApartment(){
        List<Apartment> list = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("apartments", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        Log.d(CustomPreferences.class.getSimpleName(), "allEntries:"+allEntries.entrySet().size());
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String result = sharedPreferences.getString(entry.getKey(), null);
            Log.d(CustomPreferences.class.getSimpleName(), "getListApartment:"+result);
            Apartment apartment = new Gson().fromJson(result, Apartment.class);
            if (apartment != null){
                list.add(apartment);
            }
        }

        return list;
    }

    public boolean setListApartment(List<Apartment> list){
        SharedPreferences sharedPreferences = context.getSharedPreferences("apartments", Context.MODE_PRIVATE);
        if (list != null && list.size() > 0){
            clear("apartments");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (Apartment apartment: list) {
                editor.putString(String.valueOf(apartment.getId()), new Gson().toJson(apartment));
            }
            editor.apply();
        }

        return true;
    }

    public boolean setApartment(Apartment apartment){
        CustomPreferences customPreferences = new CustomPreferences(context, DATA);
        customPreferences.savePreferences("apartment", new Gson().toJson(apartment));
        return true;
    }

    public Apartment getApartment(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return new Gson().fromJson(sharedPreferences.getString("apartment", null), Apartment.class);
    }

    public Map<String, ?> getAll(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getAll();
    }

    public void clear(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
