package adhi.avi.animaldetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.squti.androidwaverecorder.WaveRecorder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Record_animal_sound extends AppCompatActivity implements View.OnClickListener {
    Button b1,b2,b3;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    SharedPreferences sh;
    String fileName="",url,loginId;
    byte[] fileBytes=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_animal_sound);
        b1=(Button)findViewById(R.id.st);
        b2=(Button)findViewById(R.id.sto);
        b3=(Button)findViewById(R.id.up);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);


        b2.setEnabled(false);

        random = new Random();
        sh= PreferenceManager.getDefaultSharedPreferences(this);
        url = sh.getString("url","")+"api_sound_upload";
        loginId = sh.getString("loginId","");



    }

    WaveRecorder wrd;
    @Override
    public void onClick(View view) {
        if (view==b1)
        {
            if(checkPermission()) {

                AudioSavePathInDevice =
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "AudioRecording.wav";

                wrd  = new WaveRecorder(AudioSavePathInDevice);
                wrd.startRecording();

//                MediaRecorderReady();
//
//                try {
//                    mediaRecorder.prepare();
//                    mediaRecorder.start();
//
//                } catch (IllegalStateException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }




                b1.setEnabled(false);
                b2.setEnabled(true);

                Toast.makeText(Record_animal_sound.this, "Recording started",
                        Toast.LENGTH_SHORT).show();
            } else {
                requestPermission();
            }

        }
        if (view==b2)
        {
           wrd.stopRecording();
            b2.setEnabled(false);
            b1.setEnabled(true);

            Toast.makeText(Record_animal_sound.this, "Recording Completed",
                    Toast.LENGTH_SHORT).show();

//            Intent returnIntent = new Intent();
//            returnIntent.putExtra("result",AudioSavePathInDevice);
//            setResult(Activity.RESULT_OK,returnIntent);
//            finish();


            if (AudioSavePathInDevice != null) {

                try {
                    File fl = new File(AudioSavePathInDevice);
                    int ln = (int) fl.length();
                    fileName = fl.getName();
                    InputStream inputStream = new FileInputStream(fl);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] b = new byte[ln];
                    int bytesRead = 0;
                    while ((bytesRead = inputStream.read(b)) != -1)
                    {
                        bos.write(b, 0, bytesRead);
                    }
                    inputStream.close();
                    fileBytes = bos.toByteArray();
                    Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (view==b3)
        {
            if(fileBytes==null){
                Toast.makeText(Record_animal_sound.this, "No file selected! please select any file to continue.", Toast.LENGTH_SHORT).show();
            }
            else {
                uploadFile();
            }
        }
        }


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),

                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    public void MediaRecorderReady(){
//        mediaRecorder=new MediaRecorder();
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
//        mediaRecorder.setOutputFile(AudioSavePathInDevice);


        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mediaRecorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);
        mediaRecorder.setAudioEncodingBitRate(128000);
        mediaRecorder.setAudioSamplingRate(48000);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }
    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(Record_animal_sound.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(Record_animal_sound.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Record_animal_sound.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    private void uploadFile() {

        final ProgressDialog pd=new ProgressDialog(Record_animal_sound.this);
        pd.setMessage("Uploading....");
        pd.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            pd.dismiss();
                            JSONObject obj = new JSONObject(new String(response.data));
                            String status=obj.getString("status");
                            if(status.equalsIgnoreCase("success")){
                                JSONObject jb=obj.getJSONObject("data");
                                String name_=jb.getString("name");
                                String photo_=jb.getString("photo");
                                String description_=jb.getString("description");

                                SharedPreferences.Editor edt=sh.edit();
                                edt.putString("name",name_);
                                edt.putString("photo",photo_);
                                edt.putString("description",description_);
                                edt.commit();
                                startActivity(new Intent(getApplicationContext(),AnimalViewActivity.class));
                            }
                            else{
                                Toast.makeText(Record_animal_sound.this, "not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Record_animal_sound.this, e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("lid", sh.getString("lid",""));
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("file", new DataPart(AudioSavePathInDevice, fileBytes));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
}
