package com.symbel.orienteeringquiz.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.symbel.orienteeringquiz.activity.ActivityPrincipal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Utilidades {
    private static Activity activity;

    // METODOS PRINCIPALES DE UTILIDADES

    public Utilidades(Activity activity) {
        Utilidades.activity = activity;
    }

    public static void closeDrawer(){
        ActivityPrincipal.closeDrawer();
    }

    /**
     * Show the snackbar at the bottom of your current layout.
     *
     * @param view    REQUIRED. The current view activity where you are. Use: activity.getCurrentFocus();
     * @param message REQUIRED. The message you want to show as String.
     */
    public static void showSnackbar(View view, String message) {
        try {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getByteArray(File file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static byte[] getByteArray(Drawable drawable) throws IOException {
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static void hideKeyboard(Activity activity) {
        try {
            // Hide the keyboard
            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void abrirCamara(ImageView imageView) {
        //new AbrirCamara(activity, imageView);
    }

    /**
     * Método cambiar imagen para ActivityNuevoPost. Dispone de opcion de subida de imagen.
     */
    public CambiarImagen cambiarImagen(Activity activity, BottomSheetLayout bottomSheetLayout, ImageView selectedImage, Boolean isPostNow, String action) {
        return new CambiarImagen(activity, bottomSheetLayout, selectedImage, isPostNow, action);
    }

    /**
     * Método cambiar imagen para una activity sobre un CircularImageView. NO dispone de opcion de subida de imagen.
     */
    public CambiarImagen cambiarImagen(Activity activity, BottomSheetLayout bottomSheetLayout, CircularImageView selectedImage, String action) {
        return new CambiarImagen(activity, bottomSheetLayout, selectedImage, action);
    }
}