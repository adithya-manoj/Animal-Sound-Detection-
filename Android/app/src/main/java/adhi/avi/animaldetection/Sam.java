package adhi.avi.animaldetection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Sam extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    Button btn_browse,btn_upload;
    SharedPreferences sh;
    String fileName="",url,loginId;
    byte[] fileBytes=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sam);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_browse=(Button)findViewById(R.id.btn_browse);
        btn_upload=(Button)findViewById(R.id.btn_upload);
        btn_browse.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        sh= PreferenceManager.getDefaultSharedPreferences(this);
        url = sh.getString("url","")+"api_sound_upload";
        loginId = sh.getString("loginId","");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sam, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            // Handle the camera action
            Intent in=new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(in);
        } else if (id == R.id.history) {
            Intent in=new Intent(getApplicationContext(), PredictionHistory.class);
            startActivity(in);
        } else if (id == R.id.feedback) {
            Intent in=new Intent(getApplicationContext(), FeedbackActivity.class);
            startActivity(in);
        }
        else if (id == R.id.logout) {
            Intent in=new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(in);
        }else if (id == R.id.sound) {
            Intent in=new Intent(getApplicationContext(), Record_animal_sound.class);
            startActivity(in);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view==btn_browse){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            try{
                startActivityForResult(intent, 100);
            } catch (ActivityNotFoundException e){
                Toast.makeText(Sam.this, "There are no file explorer clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(view==btn_upload) {

            if(fileBytes==null){
                Toast.makeText(Sam.this, "No file selected! please select any file to continue.", Toast.LENGTH_SHORT).show();
            }
            else {
                uploadFile();
            }
            }
    }



    private void uploadFile() {

        final ProgressDialog pd=new ProgressDialog(Sam.this);
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
                                Toast.makeText(Sam.this, "not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Sam.this, e.toString(), Toast.LENGTH_SHORT).show();
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

}
