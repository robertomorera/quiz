package com.symbel.orienteeringquiz.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;

import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.adapter.AdapterActivityAprende;
import com.symbel.orienteeringquiz.model.Simbolo;
import com.symbel.orienteeringquiz.utils.SharedPreference;
import com.symbel.orienteeringquiz.utils.Utilidades;

import java.util.ArrayList;


public class ActivityAprende extends AppCompatActivity {

    private Activity activity;
    private RecyclerView rvSimbolosAprende;
    private ArrayList<Simbolo> simbolos;
    private boolean[] posicionesLogicas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            SharedPreference.setMyContext(this);
            // Comprobar si el usuario ya ha iniciado sesion (cookie)

            // REMOVE TITLE BAR
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // SET LAYOUT
            setContentView(R.layout.activity_aprende);

            // SET BACK BUTTON
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // SET MAIN VARIABLES
            activity = this;

            simbolos = new ArrayList<>();
            if (SharedPreference.hasTodosLosSimbolos()) {
                simbolos = SharedPreference.loadTodosSimbolos();
            }
            posicionesLogicas = new boolean[simbolos.size()];

            // GET CONTROLS AND SET LISTENER ON BUTTONS
            prepareRecyclerView();

            Utilidades.closeDrawer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void prepareRecyclerView() {
        try{
            // Get a reference to recyclerView
            rvSimbolosAprende = (RecyclerView) findViewById(R.id.rvSimbolosAprende);
            // Set layoutManger
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            rvSimbolosAprende.setLayoutManager(linearLayoutManager);
            // Set item animator to DefaultAnimator
            rvSimbolosAprende.setItemAnimator(new DefaultItemAnimator());
            rvSimbolosAprende.setAdapter(new AdapterActivityAprende(simbolos, activity, posicionesLogicas));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
