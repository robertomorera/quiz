package com.symbel.orienteeringquiz.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.model.ImagenPerfil;
import com.symbel.orienteeringquiz.model.Usuario;
import com.symbel.orienteeringquiz.utils.Constants;
import com.symbel.orienteeringquiz.utils.EmailValidator;
import com.symbel.orienteeringquiz.utils.SharedPreference;
import com.symbel.orienteeringquiz.utils.Utilidades;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class ActivityLogin extends AppCompatActivity {

    // MAIN VARIABLES

    private ActivityLogin activity;
    private SpotsDialog dialog;
    private EditText etRegistroUsername, etRegistroPass;
    private String resultado;
    private boolean isKeyboardShowing;

    // MAIN THREAD METHODS

    private void loginUser(final String username, String password) {
        // SHOW THE DIALOG
        dialog = new SpotsDialog(activity, R.style.Custom);
        dialog.show();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                try {
                    if (user != null) {
                        getImagenPerfil(user);
                    } else {
                        Utilidades.showSnackbar(activity.getCurrentFocus(), e.getMessage());
                        dialog.dismiss();
                    }
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
    }

    private void getImagenPerfil(final ParseUser user) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.IMAGENPERFIL);
        query.whereEqualTo("nombreUsuario", user.getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                try {
                    if (e == null) {
                        saveUsuario(user);
                        ImagenPerfil imagenPerfil = new ImagenPerfil();
                        imagenPerfil.setNombreUsuario(objects.get(0).getString("nombreUsuario"));
                        imagenPerfil.setUrl(objects.get(0).getParseFile("ficheroImagen").getUrl());
                        SharedPreference.saveImagenPerfil(imagenPerfil);
                        getUserPunt(user.getUsername());
                    } else {
                        Utilidades.showSnackbar(activity.getCurrentFocus(), e.getMessage());
                    }
                    dialog.dismiss();
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
    }

    private void saveUsuario(ParseUser user) {
        try {


            Usuario usuario = new Usuario();
            usuario.setUsername(user.getUsername());
            usuario.setClub(user.getString("club"));
            usuario.setEmail(user.getString("email"));
            SharedPreference.saveUsuario(usuario);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getUserPunt(String username) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.CLASIFICACION);
        query.whereEqualTo("nombreUsuario", username);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                try {
                    if (e == null) {
                        //SI TIENE CLASIFICACION, TERMINAMOS DE RELLENAR
                        ImagenPerfil imagenPerfil = SharedPreference.loadImagenPerfil();
                        SharedPreference.removeImagenPerfil();

                        imagenPerfil.setPuntuacion(objects.get(0).getInt("puntuacion"));
                        imagenPerfil.setClub(objects.get(0).getString("club"));

                        SharedPreference.saveImagenPerfil(imagenPerfil);
                        goTo(true);
                    } else {
                        //SI NO TIENE, NOS DA IGUAL, YA TENDRÁ
//                        Utilidades.showSnackbar(activity.getCurrentFocus(), e.getMessage());
                    }
                    goTo(true);
                    dialog.dismiss();
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
    }

    // MAIN ACTIVITY METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            SharedPreference.setMyContext(this);
            // Comprobar si el usuario ya ha iniciado sesion (cookie)

            // REMOVE TITLE BAR
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // SET LAYOUT
            setContentView(R.layout.activity_login);

            // SET BACK BUTTON
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // SET MAIN VARIABLES
            activity = this;

            // GET CONTROLS AND SET LISTENER ON BUTTONS
            getControlsAndSetListener();

            Utilidades.closeDrawer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // HIDE KEYBOARD
            hideKeyboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (isKeyboardShowing) {
                isKeyboardShowing = false;
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getControlsAndSetListener() {
        try {

            // SET LISTENER ON BACKGROUND TO HIDE KEYBOARD
            LinearLayout fondo = (LinearLayout) findViewById(R.id.myFondo);
            if (fondo != null) {
                fondo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideKeyboard();
                    }
                });
            }

            // GET CONTROLS AND SET LISTENER ON BUTTONS
            setEditTextListeners();

            // SET BUTTONS CLICK LISTENERS
            Button btnLogin = (Button) findViewById(R.id.btnLogin);
            if (btnLogin != null) {
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideKeyboard();
                        login();
                    }
                });
            }
            Button btnRegistrarse = (Button) findViewById(R.id.btnRegistrarse);
            if (btnRegistrarse != null) {
                btnRegistrarse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notRegistered();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEditTextListeners() {
        etRegistroUsername = (EditText) findViewById(R.id.etRegistroUsername);
        etRegistroUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isKeyboardShowing = true;
            }
        });
        etRegistroPass = (EditText) findViewById(R.id.etRegistroPass);
        etRegistroPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isKeyboardShowing = true;
            }
        });
    }

    private void login() {
        try {
            String username = etRegistroUsername.getText().toString();
            String pass = etRegistroPass.getText().toString();
            if (EmailValidator.isNotNull(username)) {
                Utilidades.showSnackbar(activity.getCurrentFocus(), getString(R.string.msg_campo_requerido, getString(R.string.registro_email)));
                etRegistroUsername.requestFocus();
            } else if (EmailValidator.isNotNull(pass)) {
                Utilidades.showSnackbar(activity.getCurrentFocus(), getString(R.string.msg_campo_requerido, getString(R.string.registro_password)));
                etRegistroPass.requestFocus();
            } else {
                // Login
                loginUser(username, pass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goTo(Boolean finish) {
        try {
            startActivity(new Intent(getApplicationContext(), ActivityPrincipal.class));
            if (finish) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notRegistered() {
        try {
            startActivity(new Intent(activity, ActivityRegistro.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideKeyboard() {
        try {
            Utilidades.hideKeyboard(activity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
