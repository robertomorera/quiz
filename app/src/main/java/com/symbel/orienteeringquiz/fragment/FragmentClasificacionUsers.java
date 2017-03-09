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
import java.util.List;

import dmax.dialog.SpotsDialog;

public class FragmentClasificacionUsers extends Fragment {

    private Activity activity;
    private View viewroot;
    private RecyclerView rvClasifUsu;
    private SpotsDialog dialog;
    private ArrayList<Clasificacion> listaClasifUser;
    private AdapterClasificacion adapter;

    public FragmentClasificacionUsers() {

    }

    private class GetClasifUserThread extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                getClasifUsers();
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
        viewroot = inflater.inflate(R.layout.fragment_clasificacion_users, container, false);
        return viewroot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            // 1. get a reference to recyclerView
            rvClasifUsu = (RecyclerView) viewroot.findViewById(R.id.rvClasifUsu);
            // 2. set layoutManger
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            rvClasifUsu.setLayoutManager(linearLayoutManager);
            // 3. set item animator to DefaultAnimator
            rvClasifUsu.setItemAnimator(new DefaultItemAnimator());

            // LOAD DATA
            reload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getClasifUsers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.CLASIFICACION);
        query.orderByDescending("puntuacion");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                try {
                    if (e == null) {
                        addImagesPerfil(objects);
                    } else {
                        Utilidades.showSnackbar(activity.getCurrentFocus(), e.getMessage());
                    }
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
    }

    private void addImagesPerfil(List<ParseObject> objects) {
        listaClasifUser = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            ParseObject object = objects.get(i);

            //CREO NUEVO OBJETO CLASIF Y DESPUES DESCARGO IMAGEN
            final Clasificacion clasifUser = new Clasificacion();
            clasifUser.setNombreUsuario(object.getString("nombreUsuario"));
            clasifUser.setClub(object.getString("club"));
            clasifUser.setPuntuacion(object.getInt("puntuacion"));
            listaClasifUser.add(clasifUser);
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.IMAGENPERFIL);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                try {
                    if (e == null) {
                        //GUARDO IMAGEN EN OBJETO CLASIF
                        for (ParseObject object : objects) {
                            for (Clasificacion clasifUser : listaClasifUser) {
                                if (clasifUser.getNombreUsuario().equalsIgnoreCase(object.getString("nombreUsuario"))) {
                                    clasifUser.setUrl(object.getParseFile("ficheroImagen").getUrl());
                                }
                            }
                        }
                        pintar();
                    } else {
                        Utilidades.showSnackbar(activity.getCurrentFocus(), e.getMessage());
                    }
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
    }

    private void pintar() {
        // Si hay adapter, recargamos
        if (adapter != null) {
            adapter.notifyItemRangeInserted(adapter.getItemCount(), listaClasifUser.size() - 1);
            adapter.notifyDataSetChanged();
        } else {
            // Si la lista no está vacía, la pintamos
            if (listaClasifUser != null) {
                if (listaClasifUser.size() > 0) {
                    // 4. create and set adapter
                    adapter = new AdapterClasificacion(listaClasifUser, activity);
                    adapter.notifyDataSetChanged();
                    rvClasifUsu.setAdapter(adapter);
                }
            }
        }

        // Cerramos dialogo y refresh
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void reload() {
        try {
            listaClasifUser = null;
            adapter = null;
            cargarClasifUSer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarClasifUSer() {
        try {
            dialog = new SpotsDialog(getActivity(), R.style.Custom);
            dialog.show();
            new GetClasifUserThread().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
