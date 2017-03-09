package com.symbel.orienteeringquiz.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.model.Simbolo;
import com.symbel.orienteeringquiz.utils.BitManage;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdapterActivityAprende extends RecyclerView.Adapter {

    private ArrayList<Simbolo> simbolos;
    private Activity activity;
    private boolean isShowing;
    private boolean[] posicionesLogicas;

    public AdapterActivityAprende(ArrayList<Simbolo> simbolos, Activity activity, boolean[] posicionesLogicas) {
        this.simbolos = simbolos;
        this.activity = activity;
        this.posicionesLogicas = posicionesLogicas;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterActivityAprende.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemLayoutView;
        itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_simbolo_aprende, parent, false);

        // create ViewHolder
        return new AdapterActivityAprende.ViewHolder(itemLayoutView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // Get data from your itemsData at this position
        try {
            final AdapterActivityAprende.ViewHolder viewHolder = (AdapterActivityAprende.ViewHolder) holder;
            final Simbolo simbolo = simbolos.get(position);
            isShowing = false;

            // Replace the contents of the view with that itemsData
            BitManage.loadBitmap(simbolo.getUrl(), viewHolder.ivImagen, activity);

            viewHolder.tvDesCorta.setText(simbolo.getDescripcionCorta());
            viewHolder.tvTipo.setText(simbolo.getTipo());

            if(posicionesLogicas[position]){
                isShowing = true;
                viewHolder.tvDesLarga.setVisibility(View.VISIBLE);
                viewHolder.tvDesLarga.setText(simbolo.getDescripcionLarga());
            }else{
                isShowing = false;
                viewHolder.tvDesLarga.setVisibility(View.GONE);
            }

            viewHolder.cvRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isShowing) {
                        isShowing = true;
                        posicionesLogicas[position] = true;
                        viewHolder.tvDesLarga.setVisibility(View.VISIBLE);
                        viewHolder.tvDesLarga.setText(simbolo.getDescripcionLarga());
                    } else {
                        posicionesLogicas[position] = false;
                        isShowing = false;
                        viewHolder.tvDesLarga.setVisibility(View.GONE);
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
        return simbolos.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivImagen;
        public TextView tvDesCorta, tvDesLarga, tvTipo;
        public CardView cvRow;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            try {
                ivImagen = (ImageView) itemLayoutView.findViewById(R.id.ivImagen);
                tvDesCorta = (TextView) itemLayoutView.findViewById(R.id.tvDesCorta);
                tvDesLarga = (TextView) itemLayoutView.findViewById(R.id.tvDesLarga);
                tvTipo = (TextView) itemLayoutView.findViewById(R.id.tvTipo);
                cvRow = (CardView) itemLayoutView.findViewById(R.id.cvRow);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
