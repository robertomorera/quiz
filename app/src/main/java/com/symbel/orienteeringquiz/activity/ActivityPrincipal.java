package com.symbel.orienteeringquiz.activity;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.model.ImagenPerfil;
import com.symbel.orienteeringquiz.model.Usuario;
import com.symbel.orienteeringquiz.utils.BitManage;
import com.symbel.orienteeringquiz.utils.DialogManager;
import com.symbel.orienteeringquiz.utils.SharedPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityPrincipal extends AppCompatActivity {

    private Activity activity;
    private  Toolbar toolbar;
    private Usuario usuario;
    private ImagenPerfil imagenPerfil;
    private static Drawer result;
    private CardView cvAprende, cvMapa, cvDesc, cvMixto, cvRanking;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // REMOVE TITLE BAR
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // SET LAYOUT
            setContentView(R.layout.activity_principal);

            // SET MAIN VARIABLES
            activity = this;
            SharedPreference.setMyContext(this);

            // SET BACK BUTTON
            toolbar = (Toolbar) findViewById(R.id.toolbar);

            setCardViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DialogManager.dialogExit(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                rebuildDrawer();
            }
            checkPermissions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCardViews() {
        cvAprende = (CardView) findViewById(R.id.cvAprende);
        cvMapa = (CardView) findViewById(R.id.cvMapa);
        cvDesc = (CardView) findViewById(R.id.cvDesc);
        cvMixto = (CardView) findViewById(R.id.cvMixto);
        cvRanking = (CardView) findViewById(R.id.cvRanking);

        cvAprende.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ActivityAprende.class));
            }
        });

        cvMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityQuizSimbolos.class);
                intent.putExtra("isMapasQuiz", true);
                startActivity(intent);
            }
        });

        cvDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityQuizSimbolos.class);
                intent.putExtra("isMapasQuiz", false);
                startActivity(intent);
            }
        });

        cvMixto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPreference.hasUsuario()) {
                    startActivity(new Intent(getApplicationContext(), ActivityQuizMixto.class));
                } else {
                    DialogManager.dialogLogIn(activity);
                }
            }
        });

        cvRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ActivityClasif.class));
            }
        });
    }

    public void rebuildDrawer() {
        if (result != null) {
            result.closeDrawer();
        }
        buildDrawer();
    }

    public void buildDrawer() {

        setDrawerImageLoader();

        AccountHeader headerResult = setAccountHeader();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem itemLogInOut;
        String puntuacion;
        String club;
        if (SharedPreference.hasUsuario()) {
            club = getString(R.string.club, SharedPreference.loadUsuario().getClub());
            puntuacion = getString(R.string.punt_max, String.valueOf(SharedPreference.loadImagenPerfil().getPuntuacion()));
            itemLogInOut = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.cerrar_sesion);
        } else {
            club = getString(R.string.iniciar_sesion_club);
            puntuacion = getString(R.string.iniciar_sesion_punt);
            itemLogInOut = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.iniciar_sesion);
        }
        PrimaryDrawerItem itemClub = new PrimaryDrawerItem().withIdentifier(0).withName(club);
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(puntuacion);

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .addDrawerItems(
                        itemClub,
                        item1,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withIdentifier(4).withName(R.string.aprende),
                        new SecondaryDrawerItem().withIdentifier(6).withName(R.string.btnSimbolosMapa),
                        new SecondaryDrawerItem().withIdentifier(7).withName(R.string.btnSimbolosDescripcion),
                        new SecondaryDrawerItem().withIdentifier(8).withName(R.string.btnMixQuiz),
                        new SecondaryDrawerItem().withIdentifier(5).withName(R.string.btnClasificacion)
                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem.getIdentifier() == 3) {
                            if (SharedPreference.hasUsuario()) {
                                DialogManager.dialogLogOut(activity);
                            } else {
                                startActivity(new Intent(getApplicationContext(), ActivityLogin.class));
                            }

                        } else if (drawerItem.getIdentifier() == 4) {
                            startActivity(new Intent(getApplicationContext(), ActivityAprende.class));

                        } else if (drawerItem.getIdentifier() == 5) {
                            startActivity(new Intent(getApplicationContext(), ActivityClasif.class));

                        } else if (drawerItem.getIdentifier() == 6) {
                            Intent intent = new Intent(getApplicationContext(), ActivityQuizSimbolos.class);
                            intent.putExtra("isMapasQuiz", true);
                            startActivity(intent);

                        } else if (drawerItem.getIdentifier() == 7) {
                            Intent intent = new Intent(getApplicationContext(), ActivityQuizSimbolos.class);
                            intent.putExtra("isMapasQuiz", false);
                            startActivity(intent);

                        } else if (drawerItem.getIdentifier() == 8) {
                            if (SharedPreference.hasUsuario()) {
                                startActivity(new Intent(getApplicationContext(), ActivityQuizMixto.class));
                            } else {
                                DialogManager.dialogLogIn(activity);
                            }
                        }
                        return true;
                    }
                })
                .build();

        result.addStickyFooterItem(itemLogInOut);
    }

    private AccountHeader setAccountHeader() {
        AccountHeader headerResult;
        // Create the AccountHeader
        if (SharedPreference.hasUsuario()) {
            usuario = SharedPreference.loadUsuario();
            imagenPerfil = SharedPreference.loadImagenPerfil();

            headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.background)
                    .addProfiles(
                            new ProfileDrawerItem().withName(usuario.getUsername()).withEmail(usuario.getEmail()).withIcon(imagenPerfil.getUrl())
                    )
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                            return false;
                        }
                    })
                    .build();
        } else {
            headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.background)
                    .addProfiles(
                            new ProfileDrawerItem().withName("").withEmail("").withIcon(R.drawable.no_image_user)
                    )
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                            return false;
                        }
                    })
                    .build();
        }

        return headerResult;
    }

    private void setDrawerImageLoader() {
        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                BitManage.loadUri(uri, imageView, activity);
            }

            @Override
            public void cancel(ImageView imageView) {
            }
        });
    }

    public static void closeDrawer() {
        if (result != null) {
            result.closeDrawer();
        }
    }

    // METODOS PARA COMPROBAR LOS PERMISOS

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private void checkPermissions() {
        try {
            List<String> permissionsNeeded = new ArrayList<>();

            final List<String> permissionsList = new ArrayList<>();
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
                if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
                    permissionsNeeded.add("GPS");
            if (!addPermission(permissionsList, Manifest.permission.CAMERA))
                permissionsNeeded.add("Cámara");
            if (!addPermission(permissionsList, Manifest.permission.GET_ACCOUNTS))
                permissionsNeeded.add("Cuentas");
            if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
                permissionsNeeded.add("Audio");
            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
                    permissionsNeeded.add("Almacenamiento");

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = getString(R.string.app_name) + getString(R.string.msg_permisos_requeridos);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message += "\n" + permissionsNeeded.get(i);
                    showOKMessage(message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            });
                } else {
                    ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOKMessage(String message, DialogInterface.OnClickListener okListener) {
        try {
            new AlertDialog.Builder(activity)
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.dialog_aceptar), okListener)
                    .create()
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        try {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                try {
                    Map<String, Integer> perms = new HashMap<>();
                    // Initial
                    perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                    }
                    // Fill with results
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for PERMISSIONS
                    if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                            && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED
                            && perms.get(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_DENIED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        // Permission Denied
                        showOKMessage(getString(R.string.msg_permisos_denegados), null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
