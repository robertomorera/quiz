package com.symbel.orienteeringquiz.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.ImagePickerSheetView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.symbel.orienteeringquiz.R;
import com.symbel.orienteeringquiz.activity.ActivityRegistro;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CambiarImagen extends Activity {

    // MAIN VARIABLES
    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    private static final int REQUEST_LOAD_VIDEO = REQUEST_LOAD_IMAGE + 1;
    private static final int REQUEST_VIDEO_CAPTURE = REQUEST_LOAD_VIDEO + 1;
    protected BottomSheetLayout bottomSheetLayout;
    private Uri cameraImageUri = null, selectedImageUri;
    private ImageView selectedImage;
    private CircularImageView selectedImageCircular;
    private Fragment fragment;
    private Activity activity;
    private boolean isRegistroCircular, resultado, isPostNow;
    private String action;
    private Intent picImageIntent;
    private boolean isVideo;

    // MAIN PUBLIC METHODS: IMAGES

    public CambiarImagen(Activity activity, BottomSheetLayout bottomSheetLayout, ImageView selectedImage, Boolean isPostNow, String action) {
        this.activity = activity;
        this.fragment = null;
        this.bottomSheetLayout = bottomSheetLayout;
        this.selectedImage = selectedImage;
        this.isRegistroCircular = false;
        this.action = action;
        this.isPostNow = isPostNow;
        this.isVideo = false;

        if (checkNeedsPermission()) {
            requestStoragePermission();
        } else {
            showSheetView();
        }
    }

    public CambiarImagen(Activity activity, BottomSheetLayout bottomSheetLayout, CircularImageView selectedImage, String action) {
        this.activity = activity;
        this.fragment = null;
        this.bottomSheetLayout = bottomSheetLayout;
        this.selectedImageCircular = selectedImage;
        this.isRegistroCircular = true;
        this.action = action;
        this.isPostNow = false;
        this.isVideo = false;

        if (checkNeedsPermission()) {
            requestStoragePermission();
        } else {
            showSheetView();
        }

    }

    // METHODS TO CHECK PERMISSIONS

    private boolean checkNeedsPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        } else {
            // Eh, prompt anyway
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSheetView();
            } else {
                // Permission denied
                Toast.makeText(this, "Sheet is useless without access to external storage :/", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // MAIN BOTTOM SHEET METHODS: IMAGES

    /**
     * Show an {@link ImagePickerSheetView}
     */
    private void showSheetView() {
        String choose_image = activity.getString(R.string.dialog_choose_image);
        ImagePickerSheetView sheetView = new ImagePickerSheetView.Builder(activity)
                .setMaxItems(30)
                .setShowCameraOption(createCameraIntent() != null)
                .setShowPickerOption(createPickIntent() != null)
                .setImageProvider(new ImagePickerSheetView.ImageProvider() {
                    @Override
                    public void onProvideImage(ImageView imageView, Uri imageUri, int size) {
                        Glide.with(activity)
                                .load(imageUri)
                                .centerCrop()
                                .crossFade()
                                .into(imageView);
                    }
                })
                .setOnTileSelectedListener(new ImagePickerSheetView.OnTileSelectedListener() {
                    @Override
                    public void onTileSelected(ImagePickerSheetView.ImagePickerTile selectedTile) {
                        bottomSheetLayout.dismissSheet();
                        if (selectedTile.isCameraTile()) {
                            dispatchTakePictureIntent();
                        } else if (selectedTile.isPickerTile()) {
                            if (fragment != null) {
                                fragment.startActivityForResult(getPickerIntent(), REQUEST_LOAD_IMAGE);
                            } else {
                                activity.startActivityForResult(getPickerIntent(), REQUEST_LOAD_IMAGE);
                            }
                        } else if (selectedTile.isImageTile()) {
                            showSelectedImage(selectedTile.getImageUri());
                            if (action != null) {
                                if (action.equalsIgnoreCase(activity.getString(R.string.activity_registro))) {
                                    ActivityRegistro.mostrarNuevaImagen(selectedImageUri);
                                }
//                                else if (action.equalsIgnoreCase(activity.getString(R.string.activity_editar_perfil))) {
//                                    ActivityEditarPerfil.mostrarNuevaImagen(selectedImageUri);
//                                }
                            }
                        } else {
                            genericError();
                        }
                    }
                })
                .setTitle(choose_image)
                .create();

        bottomSheetLayout.showWithSheetView(sheetView);
    }

    /**
     * For images captured from the camera, we need to create a File first to tell the camera
     * where to store the image.
     *
     * @return the File created for the image to be store under.
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        cameraImageUri = Uri.fromFile(imageFile);
        return imageFile;
    }

    /**
     * This checks to see if there is a suitable activity to handle the `ACTION_PICK` intent
     * and returns it if found. {@link Intent#ACTION_PICK} is for picking an image from an external app.
     *
     * @return A prepared intent if found.
     */
    @Nullable
    private Intent createPickIntent() {
        Intent picImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        setPickerIntent(picImageIntent);
        return picImageIntent;
    }

    private Intent getPickerIntent() {
        return picImageIntent;
    }

    private void setPickerIntent(Intent picImageIntent) {
        this.picImageIntent = picImageIntent;
    }

    /**
     * This checks to see if there is a suitable activity to handle the {@link MediaStore#ACTION_IMAGE_CAPTURE}
     * intent and returns it if found. {@link MediaStore#ACTION_IMAGE_CAPTURE} is for letting another app take
     * a picture from the camera and store it in a file that we specify.
     *
     * @return A prepared intent if found.
     */
    @Nullable
    private Intent createCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            return takePictureIntent;
        } else {
            return null;
        }
    }

    /**
     * This utility function combines the camera intent creation and image file creation, and
     * ultimately fires the intent.
     *
     * @see {@link #createCameraIntent()}
     * @see {@link #createImageFile()}
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = createCameraIntent();
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent != null) {
            // Create the File where the photo should go
            try {
                File imageFile = createImageFile();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                if (fragment != null) {
                    fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException e) {
                // Error occurred while creating the File
                genericError("Could not create imageFile for camera");
            }
        }
    }

    public Uri myOnActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        onActivityResult(requestCode, resultCode, data);
        return selectedImageUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = null;
            if (requestCode == REQUEST_LOAD_IMAGE && data != null) {
                selectedImage = data.getData();
                if (selectedImage == null) {
                    genericError();
                } else {
                    selectedImage = readPickedMedia(selectedImage);
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Do something with imagePath
                selectedImage = cameraImageUri;
            } else if (requestCode == REQUEST_LOAD_VIDEO && data != null) {
                selectedImage = data.getData();
                if (selectedImage == null) {
                    genericError();
                } else {
                    selectedImage = readPickedMedia(selectedImage);
                }
            } else if (requestCode == REQUEST_VIDEO_CAPTURE) {
                // Do something with imagePath
                selectedImage = cameraImageUri;
            }
            if (selectedImage != null) {
                showSelectedImage(selectedImage);
            } else {
                genericError();
            }
        }
    }

    private void showSelectedImage(Uri selectedImageUri) {
        try {
            if (isRegistroCircular) {
                selectedImageCircular.setImageDrawable(null);
                Glide.with(activity)
                        .load(selectedImageUri)
                        .crossFade()
                        .centerCrop()
                        .into(selectedImageCircular);
            } else {
                selectedImage.setImageDrawable(null);
                Glide.with(activity)
                        .load(selectedImageUri)
                        .crossFade()
                        .centerCrop()
                        .into(selectedImage);
            }

            this.selectedImageUri = selectedImageUri;

            // Si isPostNow la imagen debe ser subida inmediatamente. Si !isPostNow, se debe devolver la uri.
            if (isPostNow) {
//                if (action.equalsIgnoreCase("imagen") || action.equalsIgnoreCase("imagenFondo")) {
//                    archivos = new ArrayList<>();
//                    archivos.add(new Archivo(action, new File(selectedImageUri.getPath())));
//                    new ImagenPostThread().execute();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GENERIC CLASS METHODS

    private Uri readPickedMedia(Uri pickedMedia) {
        // Let's read picked image path using content resolver
        String mediaPath = "";
        String[] filePath = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.getContentResolver().query(pickedMedia, filePath, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            mediaPath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            cursor.close();
        }
        return Uri.fromFile(new File(mediaPath));
    }

    private void genericError() {
        genericError(activity.getString(R.string.msg_error_post));
    }

    private void genericError(String message) {
        Utilidades.showSnackbar(activity.getCurrentFocus(), message);
    }
}