package com.symbel.orienteeringquiz.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.model.ImagenPerfil;
import com.symbel.orienteeringquiz.model.Usuario;
import com.symbel.orienteeringquiz.utils.CambiarImagen;
import com.symbel.orienteeringquiz.utils.Constants;
import com.symbel.orienteeringquiz.utils.EmailValidator;
import com.symbel.orienteeringquiz.utils.SharedPreference;
import com.symbel.orienteeringquiz.utils.Utilidades;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.IOException;

import dmax.dialog.SpotsDialog;

public class ActivityRegistro extends AppCompatActivity {

    // VARIABLES
    private ActivityRegistro activity;
    private Utilidades utilities;
    private SpotsDialog dialog;
    private String nombre, club, email, clave;
    private boolean isKeyboardShowing;
    private CambiarImagen ci;
    private static Uri uri;

    // CONTROLS
    private EditText etRegistroNombre, etRegistroClub, etRegistroEmail, etRegistroPass, etRegistroPassRepeat;
    private CircularImageView ivRegistroAvatar;
    private BottomSheetLayout bottomSheetLayout;

    // PUBLIC METHODS

    /**
     * Method called from CambiarImagen
     *
     * @param selectedImageUri The uri of the selected image
     */
    public static void mostrarNuevaImagen(Uri selectedImageUri) {
        uri = selectedImageUri;
    }

    // MAIN THREAD METHODS

    private void registerUser(final String username, String password, final String email, final String club, final Uri ficheroImangen) {
        try {
            // SHOW THE DIALOG
            dialog = new SpotsDialog(activity, R.style.Custom);
            dialog.show();

            final ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.put("club", club);

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    String resultado;
                    if (e == null) {
                        Usuario usuario = new Usuario();
                        usuario.setUsername(username);
                        usuario.setClub(club);
                        usuario.setEmail(email);
                        registerFoto(username, ficheroImangen, usuario);

                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                        resultado = e.getMessage();
                        Utilidades.showSnackbar(activity.getCurrentFocus(), resultado);
                    }
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerFoto(final String username, Uri ficheroImangen, final Usuario user) {
        final ParseFile file;
        try {
            if (ficheroImangen != null) {
                file = new ParseFile(username + "_image.jpg", Utilidades.getByteArray(new File(ficheroImangen.getPath())));
            } else {
                file = new ParseFile("noimage.jpg", Utilidades.getByteArray(ContextCompat.getDrawable(activity, R.drawable.no_image_user)));
            }

            final ParseObject imagenPerfil = new ParseObject(Constants.IMAGENPERFIL);
            imagenPerfil.put("nombreUsuario", username);
            imagenPerfil.put("ficheroImagen", file);
            imagenPerfil.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    String resultado;
                    if (e == null) {
                        SharedPreference.saveUsuario(user);
                        ImagenPerfil imagenPerfil = new ImagenPerfil();
                        imagenPerfil.setNombreUsuario(username);
                        imagenPerfil.setUrl(file.getUrl());
                        SharedPreference.saveImagenPerfil(imagenPerfil);
                        goTo();
                    } else {
                        resultado = e.getMessage();
                        Utilidades.showSnackbar(activity.getCurrentFocus(), resultado);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // MAIN ACTIVITY METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // REMOVE TITLE BAR
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // SET LAYOUT
            setContentView(R.layout.activity_registro);

            // SET BACK BUTTON
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // PREPARE SOME VARIABLES
            setVariables();

            // GET AND SET CONTROLS
            get_and_set_controls();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            uri = ci.myOnActivityResult(requestCode, resultCode, data);
            mostrarNuevaImagen(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setVariables() {
        try {
            activity = this;
            utilities = new Utilidades(this);
            isKeyboardShowing = false;
            SharedPreference.setMyContext(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void get_and_set_controls() {
        try {
            // AVATAR DEL USUARIO
            LinearLayout btnRegistroAvatar = (LinearLayout) findViewById(R.id.btnRegistroAvatar);
            if (btnRegistroAvatar != null) {
                btnRegistroAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cambiarImagen();
                    }
                });
            }
            ivRegistroAvatar = (CircularImageView) findViewById(R.id.ivRegistroAvatar);

            // NOMBRE, APELLIDOS Y EMAIL DEL USUARIO
            etRegistroNombre = (EditText) findViewById(R.id.etRegistroNombre);
            etRegistroNombre.addTextChangedListener(textWatcher());
            etRegistroClub = (EditText) findViewById(R.id.etRegistroClub);
            etRegistroClub.addTextChangedListener(textWatcher());
            etRegistroEmail = (EditText) findViewById(R.id.etRegistroEmail);
            etRegistroEmail.addTextChangedListener(textWatcher());

            // CONTRASEÑA DEL USUARIO
            etRegistroPass = (EditText) findViewById(R.id.etRegistroPass);
            etRegistroPass.addTextChangedListener(textWatcher());
            etRegistroPassRepeat = (EditText) findViewById(R.id.etRegistroPassRepeat);
            etRegistroPassRepeat.addTextChangedListener(textWatcher());
            etRegistroPassRepeat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!etRegistroPassRepeat.getText().toString().isEmpty()) {
                        Utilidades.hideKeyboard(activity);
                        isKeyboardShowing = false;
                    }
                }
            });

            // Boton registro
            Button btnRegistrarse = (Button) findViewById(R.id.btnRegistrarse);
            if (btnRegistrarse != null) {
                btnRegistrarse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registrarse();
                    }
                });
            }

            // Preparamos el bottomSheetLayout (para cambiar la imagen)
            bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
            if (bottomSheetLayout != null) {
                bottomSheetLayout.setPeekOnDismiss(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cambiarImagen() {
        try {
            if (isKeyboardShowing) {
                Utilidades.hideKeyboard(activity);
                isKeyboardShowing = false;
            } else {
                ci = utilities.cambiarImagen(this, bottomSheetLayout, ivRegistroAvatar, getString(R.string.activity_registro));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registrarse() {
        try {
            nombre = etRegistroNombre.getText().toString();
            club = etRegistroClub.getText().toString();
            email = etRegistroEmail.getText().toString();
            clave = etRegistroPass.getText().toString();
            String claveRepeat = etRegistroPassRepeat.getText().toString();

            if (EmailValidator.isNotNull(nombre)) {
                Utilidades.showSnackbar(activity.getCurrentFocus(), getString(R.string.msg_campo_requerido, getString(R.string.registro_nombre)));
                etRegistroNombre.requestFocus();
            } else if (EmailValidator.isNotNull(email)) {
                Utilidades.showSnackbar(activity.getCurrentFocus(), getString(R.string.msg_campo_requerido, getString(R.string.registro_email)));
                etRegistroEmail.requestFocus();
            } else if (!EmailValidator.validate(email)) {
                Utilidades.showSnackbar(activity.getCurrentFocus(), getString(R.string.msg_email_error));
                etRegistroEmail.requestFocus();
            } else if (EmailValidator.isNotNull(clave)) {
                Utilidades.showSnackbar(activity.getCurrentFocus(), getString(R.string.msg_campo_requerido, getString(R.string.registro_password)));
                etRegistroPass.requestFocus();
            } else if (EmailValidator.isNotNull(claveRepeat)) {
                Utilidades.showSnackbar(activity.getCurrentFocus(), getString(R.string.msg_campo_requerido, getString(R.string.registro_password)));
                etRegistroPassRepeat.requestFocus();
            } else if (!claveRepeat.equals(clave)) {
                Utilidades.showSnackbar(activity.getCurrentFocus(), getString(R.string.msg_pass_no_coinciden));
                etRegistroPassRepeat.setText("");
                etRegistroPassRepeat.requestFocus();
            } else {
                // Registro. Guardar cookie, enviar a servidor los datos y crear registro, luego ir atrás.
                nombre = StringEscapeUtils.escapeJava(nombre);
                email = email.toLowerCase();
                registerUser(nombre, clave, email, club, uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goTo() {
        try {
            startActivity(new Intent(getApplicationContext(), ActivityPrincipal.class));
            overridePendingTransition(0, 0);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextWatcher textWatcher() {
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isKeyboardShowing = true;
            }
        };
    }
}
