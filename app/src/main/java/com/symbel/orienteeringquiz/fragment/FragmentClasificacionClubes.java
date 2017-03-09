package com.symbel.orienteeringquiz.fragment;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.adapter.AdapterClasificacion;
import com.symbel.orienteeringquiz.model.Clasificacion;
import com.symbel.orienteeringquiz.utils.Constants;
import com.symbel.orienteeringquiz.utils.Utilidades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class FragmentClasificacionClubes extends Fragment {

    private Activity activity;
    private View viewroot;
    private RecyclerView rvClasifClub;
    private SpotsDialog dialog;
    private ArrayList<Clasificacion> listaClasifClubes, listaClasifUsers;
    private AdapterClasificacion adapter;

    public FragmentClasificacionClubes() {

    }

    private class GetClasifClubThread extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                getClasifClubes();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Integer integer) {
            super.onPostExecute(integer);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewroot = inflater.inflate(R.layout.fragment_clasificacion_clubes, container, false);
        return viewroot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            // 1. get a reference to recyclerView
            rvClasifClub = (RecyclerView) viewroot.findViewById(R.id.rvClasifClub);
            // 2. set layoutManger
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            rvClasifClub.setLayoutManager(linearLayoutManager);
            // 3. set item animator to DefaultAnimator
            rvClasifClub.setItemAnimator(new DefaultItemAnimator());

            // LOAD DATA
            reload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getClasifClubes() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.CLASIFICACION);
        query.orderByAscending("club");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                try {
                    if (e == null) {
                        //CLASIF ORDENADA POR CLUBES
                        orderByClub(objects);
                    } else {
                        Utilidades.showSnackbar(activity.getCurrentFocus(), e.getMessage());
                    }
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
    }

    private void orderByClub(List<ParseObject> objects) {
        listaClasifUsers = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            ParseObject object = objects.get(i);

            //CREO NUEVO OBJETO CLASIF Y DESPUES DESCARGO IMAGEN
            final Clasificacion clasifUser = new Clasificacion();
            clasifUser.setNombreUsuario(object.getString("nombreUsuario"));
            clasifUser.setClub(object.getString("club"));
            clasifUser.setPuntuacion(object.getInt("puntuacion"));
            listaClasifUsers.add(clasifUser);
        }

        // SORT THIS LIST BY CLUB
        Collections.sort(listaClasifUsers, new Comparator<Clasificacion>() {
            public int compare(Clasificacion s1, Clasificacion s2) {
                return s1.getClub().compareToIgnoreCase(s2.getClub());
            }
        });

        String currentClub = "";
        int currentClubPunt = 0;
        listaClasifClubes = new ArrayList<>();

        //AGRUPAR LISTA DE USUARIOS POR CLUB, SUMANDO SUS PUNTUACIONES
        for (int i = 0; i < listaClasifUsers.size(); i++) {
            Clasificacion clasifUser = listaClasifUsers.get(i);

            if (!clasifUser.getClub().equalsIgnoreCase(currentClub)) {
                Clasificacion clasifClub = new Clasificacion();
                if (!currentClub.isEmpty()) {
                    clasifClub.setClub(currentClub);
                    clasifClub.setPuntuacion(currentClubPunt);
                    listaClasifClubes.add(clasifClub);
                }
                currentClub = clasifUser.getClub();
                currentClubPunt = clasifUser.getPuntuacion();
            } else {
                currentClubPunt = currentClubPunt + clasifUser.getPuntuacion();
            }

            if (i == listaClasifUsers.size() - 1) {
                Clasificacion clasifClub = new Clasificacion();
                clasifClub.setClub(currentClub);
                clasifClub.setPuntuacion(currentClubPunt);
                listaClasifClubes.add(clasifClub);
            }
        }

        // SORT THIS LIST BY PUNT
        Collections.sort(listaClasifClubes, new Comparator<Clasificacion>() {
            public int compare(Clasificacion s1, Clasificacion s2) {
                return s2.getPuntuacion() - s1.getPuntuacion();
            }
        });
        //PINTAMOS
        pintar();
    }

    private void pintar() {
        // Si hay adapter, recargamos
        if (adapter != null) {
            adapter.notifyItemRangeInserted(adapter.getItemCount(), listaClasifClubes.size() - 1);
            adapter.notifyDataSetChanged();
        } else {
            // Si la lista no está vacía, la pintamos
            if (listaClasifClubes != null) {
                if (listaClasifClubes.size() > 0) {
                    // 4. create and set adapter
                    adapter = new AdapterClasificacion(listaClasifClubes, activity);
                    adapter.notifyDataSetChanged();
                    rvClasifClub.setAdapter(adapter);
                }
            }
        }
    }

    private void reload() {
        try {
            listaClasifClubes = null;
            adapter = null;
            cargarClasifClub();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarClasifClub() {
        try {
            new GetClasifClubThread().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
