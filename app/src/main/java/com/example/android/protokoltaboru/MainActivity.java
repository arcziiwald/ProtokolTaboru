package com.example.android.protokoltaboru;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.provider.MediaStore.Files.FileColumns;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MainActivity extends AppCompatActivity {
    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;
    private TextView twNazwaProtokolanta, twCzynnosc, twRodzaj, twZewnatrz, twZdjecie, twPrzestrzen, twZdjecie2, twWyposazenie;
    private EditText etPassword, etNrRej, etOpisUszk, etOpisUszk2, etOpisBrak;
    private RadioButton rbName1, rbName2, rbTabor, rbPrzekazanie, rbCiagnik, rbNaczepa, rbTak1, rbNie1, rbTak2, rbNie2,
    rbTak3, rbNie3, rbTak4, rbNie4, rbTak5, rbNie5;
    private Button buttonZdjecie, buttonZdjecie2, buttonZakoncz;
    private int imagePosition = 0;
    private Bitmap bitmap1, bitmap2, tmp;
    private ImageView iv;
    private String name, password, czynnosc, nrRej, rodzaj, zdjecie1 = "null", zdjecie2="null", stanNaczepy = "Czy naczepa na zewnatrz ok? : ", stanPrzestrzeni = "Przestrzen ladunkowa ok? : ",
            wyposazenie = "Czy wyposazenie naczepy jest kompletne? : ", opis;
    private boolean firstPicture = false, secondPicture = false;
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.imageView);
        InitializeView();
        checkRadios();

/**
 * Capture image button click event
 */
        buttonZdjecie.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                imagePosition = 1;
                captureImage();
            }
        });
        buttonZdjecie2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePosition = 2;
                captureImage();
            }
        });

        /**
         * Record video button click event
         */

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

        buttonZakoncz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Przechwycenie uzytkownika
                if (rbName1.isChecked()) {
                    name = rbName1.getText().toString();
                }
                if (rbName2.isChecked()) {
                    name = rbName2.getText().toString();
                }
                password = etPassword.getText().toString();

                    if(getSharedPreferences("myPrefs", Context.MODE_PRIVATE).getString("PHOTO11", null)!=null) {
                        zdjecie1 = getSharedPreferences("myPrefs", Context.MODE_PRIVATE).getString("PHOTO11", null);
                    }
                    if(getSharedPreferences("myPrefs", Context.MODE_PRIVATE).getString("PHOTO12", null)!=null) {
                        zdjecie2 = getSharedPreferences("myPrefs", Context.MODE_PRIVATE).getString("PHOTO12", null);
                    }
                //Wybor czynnosci
                if (rbTabor.isChecked()) {
                    czynnosc = rbTabor.getText().toString();
                }
                if (rbPrzekazanie.isChecked()) {
                    czynnosc = rbPrzekazanie.getText().toString();
                }

                // Wybor rodzaju
                if (rbCiagnik.isChecked()) {
                    rodzaj = rbCiagnik.getText().toString();
                }
                if (rbNaczepa.isChecked()) {
                    rodzaj = rbNaczepa.getText().toString();
                }
                nrRej = etNrRej.getText().toString();

                if (rbTak1.isChecked()) {
                    stanNaczepy = stanNaczepy + rbTak1.getText().toString();
                }
                if (rbNie1.isChecked()) {
                    stanNaczepy = stanNaczepy + rbNie1.getText().toString() + " Opis: " + etOpisUszk.getText().toString();
                }
                if (rbTak2.isChecked()) {
                    // zdjecie
                }
                if (rbTak3.isChecked()) {
                    stanPrzestrzeni = stanPrzestrzeni + rbTak3.getText().toString();
                }
                if (rbNie3.isChecked()) {
                    stanPrzestrzeni = stanPrzestrzeni + rbNie3.getText().toString() + " Opis: " + etOpisUszk2.getText().toString();
                }
                if (rbTak4.isChecked()) {
                    // zdjecie
                }
                if (rbTak5.isChecked()) {
                    wyposazenie = wyposazenie + rbTak5.getText().toString();
                }
                if (rbNie5.isChecked()) {
                    wyposazenie = wyposazenie + rbNie5.getText().toString() + " Opis: " + etOpisBrak.getText().toString();
                }

                Log.d("Name", name);
                Log.d("Haslo", password);
                Log.d("Czynnosc", czynnosc);
                Log.d("Rodzaj", rodzaj);
                Log.d("Nr rejestracyjny", nrRej);
                Log.d("Stan Naczepy", stanNaczepy);
                Log.d("Stan przestrzeni", stanPrzestrzeni);
                Log.d("Wyposazenie", wyposazenie);
                opis = "Protokolant: "+name + "<br>" +
                        "Nazwa czynnosci: "+ czynnosc + "<br>" + "Rodzaj czynnosci: " + rodzaj + "<br>" +
                        "Nr rejestracyjny: " + nrRej + "<br>" + "Stan naczepy: " + stanNaczepy + "<br>" +
                        "Stan przestrzeni: " + stanPrzestrzeni + "<br>" + "Wyposazenie: " + wyposazenie + "<br>";

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {   // Jako parametr response, zostanie przeslana odpowiedz o powodzeniu z php
                        try {
                            JSONObject jsonResponse = new JSONObject(response); // Konwertujemy to na plik JSON bo wphp mamy encode json
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                SharedPreferences prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.clear().commit();
                                Toast.makeText(getApplicationContext(), "Pomyślnie wysłano",Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(getIntent());
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Upewnij się, że haslo i nr rejestracyjny sa poprawne.",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                FormRequest formRequest = new FormRequest(name, password, opis, zdjecie1, zdjecie2, nrRej, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(formRequest);
            }



        });



    }

    private void checkRadios() {
        rbTak1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbTak1.isChecked()){
                    //textview czy zrobic zdjecie widoczny
                    buttonZdjecie.setVisibility(View.GONE);
                    rbTak2.setVisibility(View.VISIBLE);
                    rbNie2.setVisibility(View.VISIBLE);
                    etOpisUszk.setVisibility(View.GONE);
                    twZdjecie.setVisibility(View.VISIBLE);
                }

                if(rbNie1.isChecked()){
                    buttonZdjecie.setVisibility(View.VISIBLE);
                    rbTak2.setVisibility(View.GONE);
                    rbNie2.setVisibility(View.GONE);
                    etOpisUszk.setVisibility(View.VISIBLE);
                    twZdjecie.setVisibility(View.GONE);
                }
            }
        });

        rbNie1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbTak1.isChecked()){
                    //textview czy zrobic zdjecie widoczny
                    buttonZdjecie.setVisibility(View.GONE);
                    rbTak2.setVisibility(View.VISIBLE);
                    rbNie2.setVisibility(View.VISIBLE);
                    etOpisUszk.setVisibility(View.GONE);
                }

                if(rbNie1.isChecked()){
                    buttonZdjecie.setVisibility(View.VISIBLE);
                    rbTak2.setVisibility(View.GONE);
                    rbNie2.setVisibility(View.GONE);
                    etOpisUszk.setVisibility(View.VISIBLE);
                }
            }
        });

        rbTak2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbTak2.isChecked()){
                    //textview czy zrobic zdjecie widoczny
                    buttonZdjecie.setVisibility(View.VISIBLE);
                }

                if(rbNie2.isChecked()){
                    buttonZdjecie.setVisibility(View.GONE);
                }
            }
        });
        rbNie2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!rbTak2.isChecked()){
                    //textview czy zrobic zdjecie widoczny
                    buttonZdjecie.setVisibility(View.VISIBLE);
                }

                if(rbNie2.isChecked()){
                    buttonZdjecie.setVisibility(View.GONE);
                    Log.d("NEJE", "HEHEHE");
                }
            }
        });


        rbTak3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbTak3.isChecked()){
                    //textview czy zrobic zdjecie widoczny
                    rbTak4.setVisibility(View.VISIBLE);
                    rbNie4.setVisibility(View.VISIBLE);
                    etOpisUszk2.setVisibility(View.GONE);
                    buttonZdjecie2.setVisibility(View.GONE);
                    twZdjecie2.setVisibility(View.VISIBLE);
                }

                if(rbNie3.isChecked()){
                    buttonZdjecie2.setVisibility(View.VISIBLE);
                    rbTak4.setVisibility(View.GONE);
                    rbNie4.setVisibility(View.GONE);
                    etOpisUszk2.setVisibility(View.VISIBLE);
                    twZdjecie2.setVisibility(View.GONE);
                }
            }
        });

        rbNie3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbTak3.isChecked()){
                    //textview czy zrobic zdjecie widoczny
                    rbTak4.setVisibility(View.VISIBLE);
                    rbNie4.setVisibility(View.VISIBLE);
                    etOpisUszk2.setVisibility(View.GONE);
                }

                if(rbNie3.isChecked()){
                    buttonZdjecie2.setVisibility(View.VISIBLE);
                    rbTak4.setVisibility(View.GONE);
                    rbNie4.setVisibility(View.GONE);
                    etOpisUszk2.setVisibility(View.VISIBLE);
                }
            }
        });

        rbTak4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbTak4.isChecked()){
                    //textview czy zrobic zdjecie widoczny
                    buttonZdjecie2.setVisibility(View.VISIBLE);
                }

                if(rbNie4.isChecked()){
                    buttonZdjecie2.setVisibility(View.GONE);
                }
            }
        });
        rbNie4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbTak4.isChecked()){
                    //textview czy zrobic zdjecie widoczny
                    buttonZdjecie2.setVisibility(View.VISIBLE);
                }

                if(rbNie4.isChecked()){
                    buttonZdjecie2.setVisibility(View.GONE);
                }
            }
        });

        rbTak5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbTak5.isChecked()){
                    etOpisBrak.setVisibility(View.GONE );
                }
            }
        });
        rbNie5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbNie5.isChecked()){
                    etOpisBrak.setVisibility(View.VISIBLE );
                }

            }
        });
    }

    // Inicjalizacja widoku
    private void InitializeView() {

        // Imie i nazwisko protokolanta
        rbName1 = (RadioButton) findViewById(R.id.radioButton);
        rbName2 = (RadioButton) findViewById(R.id.radioButton2);
        // Wpisz haslo
        etPassword = (EditText) findViewById(R.id.editText2);

        // Wybierz czynnosc
        rbTabor = (RadioButton) findViewById(R.id.radioButton3);
        rbPrzekazanie = (RadioButton) findViewById(R.id.radioButton4);

        // Wybierz rodzaj
        rbCiagnik = (RadioButton) findViewById(R.id.radioButton5);
        rbNaczepa = (RadioButton) findViewById(R.id.radioButton6);

        // Wpisz numer rejestracyjny
        etNrRej = (EditText) findViewById(R.id.editText3);

        // Czy naczepa na zewnatrz jest ok?
        rbTak1 = (RadioButton) findViewById(R.id.radioButton7);
        rbNie1 = (RadioButton) findViewById(R.id.radioButton8);
        // Jesli nie to opisujemy uszkodzenie i obligatoryjnie robimy zdjecie
        etOpisUszk = (EditText) findViewById(R.id.editText4);
        //etOpisUszk.setVisibility(View.GONE);
        // Jesli tak to pytamy czy zrobic zdjecie
        twZdjecie = (TextView) findViewById(R.id.textView6);
        rbTak2 = (RadioButton) findViewById(R.id.radioButton9);
        //rbTak2.setVisibility(View.GONE);
        rbNie2 = (RadioButton) findViewById(R.id.radioButton10);
        //rbNie2.setVisibility(View.GONE);
        // Dodajemy button dodaj zdjecie
        buttonZdjecie = (Button) findViewById(R.id.button);
        //buttonZdjecie.setVisibility(View.GONE);
        // Czy przestrzen ladunkowa naczepy ok
        rbTak3 = (RadioButton) findViewById(R.id.radioButton11);
        rbNie3 = (RadioButton) findViewById(R.id.radioButton12);
        // Jesli nie to opisujemy uszkodzenie i obligatoryjnie robimy zdjecie
        etOpisUszk2= (EditText) findViewById(R.id.editText6);
        // Dodajemy button dodaj zdjecie
        buttonZdjecie2 = (Button) findViewById(R.id.button2);
        twZdjecie2 = (TextView) findViewById(R.id.textView9);
        // Jesli tak to pytamy czy zrobic zdjecie
        rbTak4 = (RadioButton) findViewById(R.id.radioButton13);
        rbNie4 = (RadioButton) findViewById(R.id.radioButton14);
        // Jesli tak to dodajemy button zrob zdjecie

        // Czy wyposazenie naczepy jest kompletne?
        rbTak5 = (RadioButton) findViewById(R.id.radioButton15);
        rbNie5 = (RadioButton) findViewById(R.id.radioButton16);

        // Jesli nie opisujemy czego brak
        etOpisBrak = (EditText) findViewById(R.id.editText7);
        buttonZakoncz = (Button) findViewById(R.id.button3);
    }
    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Launching camera app to capture image
     */
    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }
    private void captureImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

    }

    /**
     * Launching camera app to record video
     */

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }



    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity
                launchUploadActivity(false);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(MainActivity.this, UploadActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        i.putExtra("image",imagePosition);
        startActivity(i);
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
