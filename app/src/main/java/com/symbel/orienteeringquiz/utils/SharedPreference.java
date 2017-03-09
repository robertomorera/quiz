package com.symbel.orienteeringquiz.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.symbel.orienteeringquiz.model.ImagenPerfil;
import com.symbel.orienteeringquiz.model.Simbolo;
import com.symbel.orienteeringquiz.model.Usuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreference {

    private static final String PREFS_NAME = "NKDROID_APP";
    private static final String USUARIO = "Usuario";
    private static final String IMAGENPERFIL = "ImagenPerfil";
    private static final String SIMBOLOS = "Simbolos";
    private static Context context;

    public SharedPreference() {
        super();
    }

    // HILO POST PREFERENCIAS USUARIO

    public static Context getMyContext() {
        return context;
    }

    // MÉTODOS PRINCIPALES DE SHARED PREFERENCE

    public static void setMyContext(Context context) {
        SharedPreference.context = context;
    }

    public static SharedPreferences getMyPrefs() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // USUARIO

    public static void saveUsuario(Usuario usuario) {
        SharedPreferences.Editor editor;
        editor = getMyPrefs().edit();
        String json = new Gson().toJson(usuario);
        editor.putString(USUARIO, json);
        editor.apply();
    }

    public static Usuario loadUsuario() {
        SharedPreferences settings = getMyPrefs();
        Usuario usuario;
        if (settings.contains(USUARIO)) {
            String json = settings.getString(USUARIO, null);
            usuario = new Gson().fromJson(json, Usuario.class);
        } else {
            usuario = new Usuario();
        }
        return usuario;
    }

    public static void removeUsuario() {
        if (hasUsuario()) {
            SharedPreferences.Editor editor;
            editor = getMyPrefs().edit().remove(USUARIO);
            editor.apply();
        }
    }

    public static boolean hasUsuario() {
        return getMyPrefs().contains(USUARIO);
    }

    // IMAGEN PERFIL

    public static void saveImagenPerfil(ImagenPerfil imagenPerfil) {
        SharedPreferences.Editor editor;
        editor = getMyPrefs().edit();
        String json = new Gson().toJson(imagenPerfil);
        editor.putString(IMAGENPERFIL, json);
        editor.apply();
    }

    public static ImagenPerfil loadImagenPerfil() {
        SharedPreferences settings = getMyPrefs();
        ImagenPerfil imagenPerfil;
        if (settings.contains(IMAGENPERFIL)) {
            String json = settings.getString(IMAGENPERFIL, null);
            imagenPerfil = new Gson().fromJson(json, ImagenPerfil.class);
        } else {
            imagenPerfil = new ImagenPerfil();
        }
        return imagenPerfil;
    }

    public static void removeImagenPerfil() {
        if (hasImagenPerfil()) {
            SharedPreferences.Editor editor;
            editor = getMyPrefs().edit().remove(IMAGENPERFIL);
            editor.apply();
        }
    }

    public static boolean hasImagenPerfil() {
        return getMyPrefs().contains(IMAGENPERFIL);
    }

    // TODOS LOS SIMBOLOS

    public static ArrayList<Simbolo> loadTodosSimbolos() {
        // used for retrieving arraylist from json formatted string
        SharedPreferences settings = getMyPrefs();
        List<Simbolo> simbolos = new ArrayList<>();
        if (settings.contains(SIMBOLOS)) {
            String json = settings.getString(SIMBOLOS, "");
            Simbolo[] simboloItems = new Gson().fromJson(json, Simbolo[].class);
            simbolos = new ArrayList<>(Arrays.asList(simboloItems));
        }
        return (ArrayList<Simbolo>) simbolos;
    }

    public static void removeTodosSimbolos() {
        SharedPreferences.Editor editor;
        editor = getMyPrefs().edit().remove(SIMBOLOS);
        editor.apply();
    }

    public static boolean hasTodosLosSimbolos() {
        return getMyPrefs().contains(SIMBOLOS);
    }

    public static void storeMySimbolos(List simbolos) {
        if (SharedPreference.hasTodosLosSimbolos()) {
            SharedPreference.removeTodosSimbolos();
        }
        SharedPreferences.Editor editor = getMyPrefs().edit();
        String json = new Gson().toJson(simbolos);
        editor.putString(SIMBOLOS, json);
        editor.apply();
    }
}
