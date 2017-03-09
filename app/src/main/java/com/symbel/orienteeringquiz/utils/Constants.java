package com.symbel.orienteeringquiz.utils;

public class Constants {

    //APP INFO
    public static final String APPID = "FMjiPZdtHctkhHbLvE4h8Har1HOxStI5sm6CWcRP";
    public static final String CLIENTKEY = "pK9BrhuXQhYUFEzcThpwPVd1Vje43LlVWiFN7XoC";
    public static final String SERVER = "https://parseapi.back4app.com/";

    //PARSE TABLES
    public static final String IMAGENPERFIL = "ImagenPerfil";
    public static final String SIMBOLOS = "Simbolos";
    public static final String CLASIFICACION = "Clasificacion";

    //SYMBOL TYPES
    public static final String MAPA = "mapa";
    public static final String DESCRIPCION = "descripcion";


    // EMAILS
    public static final String EMAIL_TO = "";

    // URLS
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    public static final String URL_REGEX_SPECIAL = "^[0-9]+([\\.|,][0-9]+)+[a-z0-9]+?$";

    // DATE FORMATS
    public static String FORMAT_DATE_SHOW = "EEE dd MMM";
    public static String FORMAT_DATE_READ = "yyyy-MM-dd HH:mm:ss";
    public static String FORMAT_DATE_HOUR = "HH:mm";
    public static String FORMAT_DATE_SUBMIT = "yyyy-MM-dd";

    // CONNECTIONS
    //    public static String DOMINIO = "desarrollo.miotek.es";
    public static String DOMINIO = "pre.sportreview.es";
    public static String URL_MAIN = "https://" + DOMINIO + "/api/";
    public static String URL_TIMESTAMP = URL_MAIN + "info";
    public static String SUCCESS = "success";

    // TEXTVIEW LENGHTS
    public static int TVMAXLENGHT = 2000;
    public static int TVMINLENGHT = 150;
    public static int MAX_CHARACTERS = 150;

    // BitManageSinSSL
    public static String URL_DOWNLOAD = "https://" + DOMINIO + "/";
}