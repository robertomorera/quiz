package com.symbel.orienteeringquiz.adapter;


import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.activity.ActivityQuizMixto;
import com.symbel.orienteeringquiz.model.ImagenPerfil;
import com.symbel.orienteeringquiz.model.Simbolo;
import com.symbel.orienteeringquiz.model.Usuario;
import com.symbel.orienteeringquiz.utils.BitManage;
import com.symbel.orienteeringquiz.utils.Constants;
import com.symbel.orienteeringquiz.utils.DialogManager;
import com.symbel.orienteeringquiz.utils.SharedPreference;
import com.symbel.orienteeringquiz.utils.Utilidades;

import java.util.ArrayList;
import java.util.List;

public class AdapterQuizMixto extends RecyclerView.Adapter {

    private ArrayList<Simbolo> simbolosQuiz;
    private ActivityQuizMixto activity;
    private String textoPregunta;
    private int ronda, aciertos, currentPunt;

    public AdapterQuizMixto(ArrayList<Simbolo> simbolosQuiz, ActivityQuizMixto activity, String textoPregunta) {
        this.simbolosQuiz = simbolosQuiz;
        this.activity = activity;
        this.textoPregunta = textoPregunta;
        ronda = activity.getRonda();
        aciertos = activity.getAciertos();
        currentPunt = activity.getCurrentPunt();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterQuizMixto.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemLayoutView;
        itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_quiz, parent, false);

        // create ViewHolder
        return new AdapterQuizMixto.ViewHolder(itemLayoutView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // Get data from your itemsData at this position
        try {
            final AdapterQuizMixto.ViewHolder viewHolder = (AdapterQuizMixto.ViewHolder) holder;
            final Simbolo simbolo = simbolosQuiz.get(position);

            // Replace the contents of the view with that itemsData
            if (simbolo.getUrl() != null) {
                BitManage.loadBitmap(simbolo.getUrl(), viewHolder.ivSimbolo, activity);
            } else {
                viewHolder.ivSimbolo.setImageResource(R.drawable.baliza);
            }

            viewHolder.cvSimbolo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (simbolo.getDescripcionCorta().equalsIgnoreCase(textoPregunta)) {
                        //ACIERTO
                        viewHolder.ivSimbolo.setImageResource(R.drawable.check_ok);
                        setDialogAcierto(simbolo.getDescripcionCorta());
                    } else {
                        //ERROR
                        viewHolder.ivSimbolo.setImageResource(R.drawable.check_ko);
                        setDialogFallo(simbolo.getDescripcionCorta());
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return simbolosQuiz.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivSimbolo;
        public CardView cvSimbolo;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            try {
                ivSimbolo = (ImageView) itemLayoutView.findViewById(R.id.ivSimbolo);
                cvSimbolo = (CardView) itemLayoutView.findViewById(R.id.cvSimbolo);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void setDialogAcierto(String descripcionCorta) {
        //ACTUALIZAMOS EL JUEGO
        updateJuego();

        DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //CADA 2 RONDAS, UN SIMBOLO MAS
                if (esPar(aciertos)) {
                    int cantidad = activity.getSYMBOLSTOGET();
                    cantidad++;
                    activity.setSYMBOLSTOGET(cantidad);
                }
                activity.nuevoJuego();
                DialogManager.dismiss();
            }
        };
        String positiveMessage = activity.getString(R.string.continuar);
        String title = activity.getString(R.string.felicidades);
        String message = activity.getString(R.string.has_acertado, descripcionCorta);

        DialogManager.dialogAccept(activity, title, message, positiveClick, positiveMessage, R.drawable.check_ok);
    }

    private void updateJuego() {
        aciertos++;
        currentPunt = currentPunt + ronda;
        ronda++;

        activity.setAciertos(aciertos);
        activity.setRonda(ronda);
        activity.setCurrentPunt(currentPunt);
    }

    private void setDialogFallo(String descripcionCorta) {
        setFallo();

        DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogManager.dismiss();
                activity.onBackPressed();
            }
        };
        String positiveMessage = activity.getString(R.string.dialog_aceptar);
        String title = activity.getString(R.string.fin_juego);
        String message = activity.getString(R.string.has_fallado, descripcionCorta);

        DialogManager.dialogAccept(activity, title, message, positiveClick, positiveMessage, R.drawable.check_ko);
    }

    private void setFallo() {
        //AL FALLAR ACTUALIZAMOS SU CLASIFICACION O LA CREAMOS
        Usuario user = SharedPreference.loadUsuario();
        final String username = user.getUsername();
        final String club = user.getClub();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.CLASIFICACION);
        query.whereEqualTo("nombreUsuario", username);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                try {
                    if (e == null) {
                        if (objects.size() > 0) {
                            //SI TIENE CLASIFICACION Y SU PUNTUACION ES MAYOR A LA ACTUAL, LA ACTUALIZAMOS
                            ParseObject object = objects.get(0);
                            if (object.getInt("puntuacion") < currentPunt) {
                                object.put("puntuacion", currentPunt);
                                object.saveInBackground();
                                updateSharedPreference();
                            }
                        }else {
                            //SI NO TIENE, LA CREAMOS
                            createClasificacion(username, club);
                        }
                    }else {
                        Utilidades.showSnackbar(activity.getCurrentFocus(), e.getMessage());
                    }
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
    }

    private void createClasificacion(String username, String club) {

        ParseObject clasificacion = new ParseObject(Constants.CLASIFICACION);
        clasificacion.put("nombreUsuario", username);
        clasificacion.put("club", club);
        clasificacion.put("puntuacion", currentPunt);
        clasificacion.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //GUARDAMOS EN LOCAL LA NUEVA CLASIFICAION
                ImagenPerfil imagenPerfil = SharedPreference.loadImagenPerfil();
                SharedPreference.removeImagenPerfil();
                imagenPerfil.setPuntuacion(currentPunt);
                SharedPreference.saveImagenPerfil(imagenPerfil);
            }
        });
    }

    private void updateSharedPreference() {
        try {
            ImagenPerfil imagenPerfil = SharedPreference.loadImagenPerfil();
            SharedPreference.removeImagenPerfil();
            imagenPerfil.setPuntuacion(currentPunt);
            SharedPreference.saveImagenPerfil(imagenPerfil);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean esPar(int numero) {
        if (numero % 2 == 0) {
            return true;
        } else {
            return false;
        }
    }
}
