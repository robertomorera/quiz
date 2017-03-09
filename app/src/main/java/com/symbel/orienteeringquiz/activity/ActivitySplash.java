package com.symbel.orienteeringquiz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.model.Simbolo;
import com.symbel.orienteeringquiz.utils.Conexion;
import com.symbel.orienteeringquiz.utils.Constants;
import com.symbel.orienteeringquiz.utils.SharedPreference;
import com.symbel.orienteeringquiz.utils.Utilidades;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class ActivitySplash extends AppCompatActivity {
    private static final long DELAY = 500;
    private Activity activity;
    private TextView tvSplashLoading;
    private String idioma;

    private class GetDataSimbolosThread extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Integer integer) {
            super.onPostExecute(integer);
            try {
                new Timer().schedule(getTask(), DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // SET SCREEN ORIENTATION
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            SharedPreference.setMyContext(this);
            // HIDE TITLE
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            // SET FULLSCREEN
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            // SET LAYOUT
            setContentView(R.layout.activity_splash);

            // MAIN VARIABLES
            activity = this;

            tvSplashLoading = (TextView) findViewById(R.id.tvSplashLoading);

            checkIdioma();
            Conexion.initializeParse(this);
            getSimbolos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TimerTask getTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    //ABRIR PRINCIPAL
                    activity.startActivity(new Intent(activity, ActivityPrincipal.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void checkIdioma() {
        idioma = Locale.getDefault().getLanguage();
        if (idioma != null && !idioma.equalsIgnoreCase("es")) {
            idioma = "en";
        }
    }

    private void getSimbolos() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.SIMBOLOS);
        query.whereEqualTo("idioma", idioma.toLowerCase());
//        query.whereEqualTo("idioma", "es");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                try {
                    if (e == null) {
                        saveSimbolos(objects);
                    } else {
                        Utilidades.showSnackbar(activity.getCurrentFocus(), e.getMessage());
                    }
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
    }

    private void saveSimbolos(List<ParseObject> objects) {
        ArrayList<Simbolo> simbolos = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            ParseObject parseObject = objects.get(i);
            Simbolo simbolo = new Simbolo();
            simbolo.setDescripcionCorta(parseObject.getString("descripcionCorta"));
            simbolo.setDescripcionLarga(parseObject.getString("descripcionLarga"));
            simbolo.setTipo(parseObject.getString("tipo"));
            simbolo.setUrl(parseObject.getParseFile("imagen").getUrl());
            simbolos.add(simbolo);
        }
        //GUARDAMOS LA LISTA
        SharedPreference.storeMySimbolos(simbolos);
        //NOS VAMOS A HOME
        new Timer().schedule(getTask(), DELAY);
    }

    private void hideLoader() {
        try {
            if (tvSplashLoading != null) {
                tvSplashLoading.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoader() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (tvSplashLoading != null) {
                        tvSplashLoading.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
