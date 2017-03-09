package com.symbel.orienteeringquiz.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;

import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.fragment.FragmentClasificacionUsers;
import com.symbel.orienteeringquiz.fragment.FragmentClasificacionClubes;
import com.symbel.orienteeringquiz.utils.SharedPreference;
import com.symbel.orienteeringquiz.utils.Utilidades;

import java.util.ArrayList;
import java.util.List;


public class ActivityClasif extends AppCompatActivity {

    private Activity activity;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            SharedPreference.setMyContext(this);
            // Comprobar si el usuario ya ha iniciado sesion (cookie)

            // REMOVE TITLE BAR
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // SET LAYOUT
            setContentView(R.layout.activity_clasif);

            // SET BACK BUTTON
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // SET MAIN VARIABLES
            activity = this;
            // GET MAIN LAYOUT CONTROLS
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            tabLayout = (TabLayout) findViewById(R.id.tabs);

            // GET CONTROLS AND SET LISTENER ON BUTTONS
            setupViewPager(viewPager);
            if (tabLayout != null) {
                tabLayout.setupWithViewPager(viewPager);
            }

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

    private void setupViewPager(ViewPager viewPager) {
        try {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

            FragmentClasificacionUsers fragClasif = new FragmentClasificacionUsers();
//            fragClasif.setParametros(parametrosNoticias, llamada, fromWhere, true);
            adapter.addFragment(fragClasif, getString(R.string.usuarios));

            FragmentClasificacionClubes fragClasifClubes = new FragmentClasificacionClubes();
//            fragClasifClubes.setParametros(parametrosEventos, llamada, fromWhere, false);
            adapter.addFragment(fragClasifClubes, getString(R.string.clubes));

            viewPager.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
