package com.symbel.orienteeringquiz.adapter;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.model.Clasificacion;
import com.symbel.orienteeringquiz.utils.BitManage;

import java.util.ArrayList;

public class AdapterClasificacion extends RecyclerView.Adapter{

    private ArrayList<Clasificacion> listaClasif;
    private Activity activity;

    public AdapterClasificacion(ArrayList<Clasificacion> listaClasif, Activity activity) {
        this.listaClasif = listaClasif;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterClasificacion.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemLayoutView;
        itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_clasif, parent, false);

        // create ViewHolder
        return new AdapterClasificacion.ViewHolder(itemLayoutView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // Get data from your itemsData at this position
        try {
            final AdapterClasificacion.ViewHolder viewHolder = (AdapterClasificacion.ViewHolder) holder;
            final Clasificacion clasificacion = listaClasif.get(position);

            // Replace the contents of the view with that itemsData
            if (clasificacion.getUrl() != null) {
                BitManage.loadBitmap(clasificacion.getUrl(), viewHolder.ivPerfil, activity);
            }else {
                viewHolder.ivPerfil.setImageResource(R.drawable.baliza);
            }

            if (clasificacion.getNombreUsuario().isEmpty()) {
                viewHolder.tvNombre.setVisibility(View.GONE);
            }else {
                viewHolder.tvNombre.setVisibility(View.VISIBLE);
                viewHolder.tvNombre.setText(clasificacion.getNombreUsuario());
            }
            viewHolder.tvClub.setText(clasificacion.getClub());
            String punt = String.valueOf(clasificacion.getPuntuacion());
            viewHolder.tvPunt.setText(punt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listaClasif.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView ivPerfil;
        public TextView tvNombre, tvClub, tvPunt;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            try {
                ivPerfil = (CircularImageView) itemLayoutView.findViewById(R.id.ivPerfil);
                tvNombre = (TextView) itemLayoutView.findViewById(R.id.tvNombre);
                tvClub = (TextView) itemLayoutView.findViewById(R.id.tvClub);
                tvPunt = (TextView) itemLayoutView.findViewById(R.id.tvPunt);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
