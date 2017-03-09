package com.symbel.orienteeringquiz.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.adapter.AdapterQuizMixto;
import com.symbel.orienteeringquiz.model.Simbolo;
import com.symbel.orienteeringquiz.utils.SharedPreference;
import com.symbel.orienteeringquiz.utils.Utilidades;

import java.util.ArrayList;
import java.util.Random;

public class ActivityQuizMixto extends AppCompatActivity {
    private int SYMBOLSTOGET = 2;

    private Activity activity;
    private RecyclerView rvSimbolosQuiz;
    private ArrayList<Simbolo> simbolos;
    private TextView tvTitulo, tvPregunta, tvCurrentPunt, tvRonda, tvAciertos;
    private AdapterQuizMixto adapter;
    private int ronda, aciertos, currentPunt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            SharedPreference.setMyContext(this);

            // REMOVE TITLE BAR
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // SET LAYOUT
            setContentView(R.layout.activity_quiz_mixto);

            tvTitulo = (TextView) findViewById(R.id.tvTitulo);
            tvTitulo.setText(getString(R.string.activity_quiz_mixto));

            tvPregunta = (TextView) findViewById(R.id.tvPregunta);
            tvCurrentPunt = (TextView) findViewById(R.id.tvCurrentPunt);
            tvRonda = (TextView) findViewById(R.id.tvRonda);
            tvAciertos = (TextView) findViewById(R.id.tvAciertos);

            // SET BACK BUTTON
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // SET MAIN VARIABLES
            activity = this;
            // GET CONTROLS AND SET LISTENER ON BUTTONS
            prepareRecyclerView();

            Utilidades.closeDrawer();

            //LOAD DATA
            reload();

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
        try {
            // Get a reference to recyclerView
            rvSimbolosQuiz = (RecyclerView) findViewById(R.id.rvSimbolosQuiz);
            // Set layoutManger
            LinearLayoutManager linearLayoutManager = new GridLayoutManager(this, 4);
            rvSimbolosQuiz.setLayoutManager(linearLayoutManager);
            // Set item animator to DefaultAnimator
            rvSimbolosQuiz.setItemAnimator(new DefaultItemAnimator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reload() {
        try {
            simbolos = null;
            adapter = null;
            aciertos = currentPunt = 0;
            ronda = 1;
            filtrarListaSimbolos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filtrarListaSimbolos() {
        try {
            simbolos = new ArrayList<>();

            simbolos = new ArrayList<>();
            if (SharedPreference.hasTodosLosSimbolos()) {
                simbolos = SharedPreference.loadTodosSimbolos();
                nuevoJuego();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nuevoJuego() {
        try {
            //OBTENEMOS LOS SYMBOLSTOGET SIMBOLOS DE MANERA ALEATORIA, SIN QUE SE REPITAN
            ArrayList<Simbolo> simbolsToPlay = new ArrayList<>();
            Random r = new Random();
            int symbolsFound = 0;
            while (symbolsFound < SYMBOLSTOGET) {
                int i1 = r.nextInt(simbolos.size());
                if (!simbolsToPlay.contains(simbolos.get(i1))) {
                    simbolsToPlay.add(simbolos.get(i1));
                    symbolsFound++;
                }
            }

            //OBTENEMOS EL TEXTO DEL SIMBOLO A ADIVINAR
            String textoPregunta = getTextoPregunta(simbolsToPlay);

            //AHORA SI, PINTAMOS
            pintar(simbolsToPlay, textoPregunta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTextoPregunta(ArrayList<Simbolo> simbolsToPlay) {
        String text = "";
        try {
            Random ran = new Random();
            int i2 = ran.nextInt(simbolsToPlay.size());
            text = simbolsToPlay.get(i2).getDescripcionCorta();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    private void pintar(ArrayList<Simbolo> simbolsToPlay, String textoPregunta) {
        try {
            adapter = null;
            //TEXTO A ADIVINAR
            tvPregunta.setText(textoPregunta);
            //INFO DEL JUEGO
            tvAciertos.setText(String.valueOf(aciertos));
            tvRonda.setText(String.valueOf(ronda));
            tvCurrentPunt.setText(String.valueOf(currentPunt));

            // Si la lista no está vacía, la pintamos
            if (simbolsToPlay != null) {
                if (simbolsToPlay.size() > 0) {
                    // 4. create and set adapter
                    adapter = new AdapterQuizMixto(simbolsToPlay,(ActivityQuizMixto) activity, textoPregunta);
                    adapter.notifyDataSetChanged();
                    rvSimbolosQuiz.setAdapter(adapter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSYMBOLSTOGET(int num) {
        this.SYMBOLSTOGET = num;
    }

    public int getSYMBOLSTOGET() {
        return SYMBOLSTOGET;
    }

    public int getAciertos() {
        return aciertos;
    }

    public void setAciertos(int aciertos) {
        this.aciertos = aciertos;
    }

    public int getRonda() {
        return ronda;
    }

    public void setRonda(int ronda) {
        this.ronda = ronda;
    }

    public int getCurrentPunt() {
        return currentPunt;
    }

    public void setCurrentPunt(int currentPunt) {
        this.currentPunt = currentPunt;
    }
}
