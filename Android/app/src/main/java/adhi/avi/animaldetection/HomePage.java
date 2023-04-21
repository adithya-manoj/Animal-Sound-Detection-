package adhi.avi.animaldetection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    TextView tv_feedback;
    Button btn_profile,btn_history,btn_browse,btn_upload;
    SharedPreferences sh;
    String loginId;
    String fileName="";
    byte[] fileBytes=null;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

//        tv_feedback =(TextView)findViewById(R.id.tv_feedback);
//        btn_profile=(Button)findViewById(R.id.btn_profile);
//        btn_history=(Button)findViewById(R.id.btn_history);
        btn_browse=(Button)findViewById(R.id.btn_browse);
        btn_upload=(Button)findViewById(R.id.btn_upload);

        btn_profile.setOnClickListener(this);
        btn_history.setOnClickListener(this);
        btn_browse.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        tv_feedback.setOnClickListener(this);

        sh= PreferenceManager.getDefaultSharedPreferences(this);
        url = sh.getString("url","")+"api_sound_upload";
        loginId = sh.getString("loginId","");

    }
    @Override
    public void onClick(View view){

        if(view==btn_profile){

            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));

        }else if(view==btn_history){

            startActivity(new Intent(getApplicationContext(),PredictionHistory.class));

        }
        else if(view==btn_browse){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            try{
                startActivityForResult(intent, 100);
            } catch (ActivityNotFoundException e){
                Toast.makeText(HomePage.this, "There are no file explorer clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(view==btn_upload){

                uploadFile();



        }
        else if(view==tv_feedback){


            startActivity(new Intent(getApplicationContext(),FeedbackActivity.class));

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode == Activity.RESULT_OK) {


                String filePath = getImageFilePath(data);
                  Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
                if (filePath != null) {

                    try {
                        File fl = new File(filePath);
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

    }
    private String getImageFromFilePath(Intent data) {

        return getPathFromURI(data.getData());

    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }
    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void uploadFile() {

        final ProgressDialog pd=new ProgressDialog(HomePage.this);
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
                                Toast.makeText(HomePage.this, "not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(HomePage.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("file", new DataPart(fileName, fileBytes));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
}
