package com.symbel.orienteeringquiz.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;

import com.parse.Parse;
import com.symbel.orienteeringquiz.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Conexion {


    public static void initializeParse(Context myContext) {
        Parse.initialize(new Parse.Configuration.Builder(myContext)
                .applicationId(Constants.APPID)
                .clientKey(Constants.CLIENTKEY)
                .server(Constants.SERVER).build()
        );
    }

    /**
     * Metodo para hacer una conexión
     *
     * @param txtUrl Requerido. La url a la que conectar.
     * @return Devuelve los datos en modo String.
     */
    private static String connect(String txtUrl) {
        StringBuilder result = new StringBuilder();
        HttpsURLConnection urlConnection = null;
        try {
            isNetworkAvailable();
            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = new URL(txtUrl);
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result.toString();
    }

    // CHECK CONNECTION STATUS

    /**
     * Show custom message as snackbar if not connected.
     */
    private static void isNetworkAvailable() {
        try {
            Context context = SharedPreference.getMyContext();
            if (context != null) {
                Activity activity = ((Activity) context);
                if (!checkNetworkAvailable(context)) {
                    View viewroot = activity.getWindow().getDecorView();
                    if (viewroot != null) {
                        Utilidades.showSnackbar(viewroot, context.getString(R.string.msg_no_internet));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean checkNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
