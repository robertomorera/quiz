package com.symbel.orienteeringquiz.utils;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.activity.ActivityLogin;
import com.symbel.orienteeringquiz.activity.ActivityPrincipal;

public class DialogManager {
    private static AlertDialog dialog;

    /**
     * Method to close the app (hide in background)
     */
    public static void dialogExit(final Activity activity) {
        // PREPARAMOS EL CLICK POSITIVO
        DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.moveTaskToBack(true);
                DialogManager.dismiss();
                //android.os.Process.killProcess(android.os.Process.myPid());
                //System.exit(1);
                activity.finish();
            }
        };

        // PREPARAMOS EL CLICK NEGATIVO
        DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        };

        // PREPARAMOS TEXTOS
        String title = activity.getString(R.string.exit_title);
        String message = activity.getString(R.string.exit_message);
        String positiveMessage = activity.getString(R.string.exit_salir);
        String negativeMessage = activity.getString(R.string.exit_cancelar);

        // MOSTRAMOS EL DIALOGO
        setAlertDialog(activity, title, message, positiveClick, positiveMessage, negativeClick, negativeMessage);
    }

    /**
     * Method to log in any user
     */
    public static void dialogLogIn(final Activity activity) {
        if (!activity.isFinishing()) {
            // PREPARAMOS EL CLICK POSITIVO
            DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    activity.startActivity(new Intent(activity, ActivityLogin.class));
                }
            };

            // PREPARAMOS EL CLICK NEGATIVO
            DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                }
            };

            // PREPARAMOS TEXTOS
            String title = activity.getString(R.string.dialog_title_no_log_vip);
            String message = activity.getString(R.string.dialog_login);
            String positiveMessage = activity.getString(R.string.dialog_aceptar);
            String negativeMessage = activity.getString(R.string.dialog_cancelar);

            // MOSTRAMOS EL DIALOGO
            setAlertDialog(activity, title, message, positiveClick, positiveMessage, negativeClick, negativeMessage);
        }
    }
    /**
     * Method to log out any user
     */
    public static void dialogLogOut(final Activity activity) {
        // PREPARAMOS EL CLICK POSITIVO
        DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreference.removeUsuario();
                SharedPreference.removeImagenPerfil();
                ((ActivityPrincipal)activity).rebuildDrawer();
                dialog.dismiss();
            }
        };

        // PREPARAMOS EL CLICK NEGATIVO
        DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        };

        // PREPARAMOS TEXTOS
        String title = activity.getString(R.string.cerrar_sesion);
        String message = activity.getString(R.string.dialog_message_logout);;
        String positiveMessage = activity.getString(R.string.dialog_yes);
        String negativeMessage = activity.getString(R.string.dialog_cancelar);

        // MOSTRAMOS EL DIALOGO
        setAlertDialog(activity, title, message, positiveClick, positiveMessage, negativeClick, negativeMessage);
    }

    public static void dialogAccept(final Activity activity, String title, String message, DialogInterface.OnClickListener positiveClick, String positiveMessage, int icon) {
        if (!activity.isFinishing()) {
            // MOSTRAMOS EL DIALOGO
            setAlertDialogAccept(activity, title, message, positiveClick, positiveMessage, icon);
        }
    }


    public static void dismiss() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    private static void setAlertDialog(Activity activity, String title, String message, DialogInterface.OnClickListener positiveClick, String positiveMessage, DialogInterface.OnClickListener negativeClick, String negativeMessage) {
        dialog = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveMessage, positiveClick)
                .setNegativeButton(negativeMessage, negativeClick)
                .setIcon(R.drawable.baliza)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private static void setAlertDialogAccept(Activity activity, String title, String message, DialogInterface.OnClickListener positiveClick, String positiveMessage, int icon) {
        dialog = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveMessage, positiveClick)
                .setIcon(icon)
                .setCancelable(false)
                .show();
    }
}
