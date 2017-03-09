package com.symbel.orienteeringquiz.adapter;


import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.activity.ActivityQuizSimbolos;
import com.symbel.orienteeringquiz.model.Simbolo;
import com.symbel.orienteeringquiz.utils.BitManage;
import com.symbel.orienteeringquiz.utils.DialogManager;

import java.util.ArrayList;

public class AdapterQuiz extends RecyclerView.Adapter {

    private ArrayList<Simbolo> simbolosQuiz;
    private Activity activity;
    private String textoPregunta;

    public AdapterQuiz(ArrayList<Simbolo> simbolosQuiz, Activity activity, String textoPregunta) {
        this.simbolosQuiz = simbolosQuiz;
        this.activity = activity;
        this.textoPregunta = textoPregunta;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterQuiz.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemLayoutView;
        itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_quiz, parent, false);

        // create ViewHolder
        return new AdapterQuiz.ViewHolder(itemLayoutView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // Get data from your itemsData at this position
        try {
            final AdapterQuiz.ViewHolder viewHolder = (AdapterQuiz.ViewHolder) holder;
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
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDialogAcierto(String descripcionCorta) {
        DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((ActivityQuizSimbolos) activity).nuevoJuego();
                DialogManager.dismiss();
            }
        };
        String positiveMessage = activity.getString(R.string.jugar);
        String title = activity.getString(R.string.felicidades);
        String message = activity.getString(R.string.has_acertado, descripcionCorta);

        DialogManager.dialogAccept(activity, title, message, positiveClick, positiveMessage, R.drawable.check_ok);
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
}
