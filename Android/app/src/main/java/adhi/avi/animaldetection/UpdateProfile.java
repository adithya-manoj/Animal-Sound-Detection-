package adhi.avi.animaldetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity  implements View.OnClickListener{
    ImageView img_select;
    EditText et_name,et_place,et_email,et_phone;
    Button btn_update;
    SharedPreferences sh;

    String url="";
    String path, atype, fname, attach ="yes", attatch1;
    byte[] byteArray = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        et_name=(EditText)findViewById(R.id.et_name);
        et_place=(EditText)findViewById(R.id.et_place);
        et_phone=(EditText)findViewById(R.id.et_phone);
        et_email=(EditText)findViewById(R.id.et_email);
        btn_update =(Button)findViewById(R.id.et_update);
        img_select =(ImageView)findViewById(R.id.img_select);
        img_select.setOnClickListener(this);
        sh= PreferenceManager.getDefaultSharedPreferences(this);

        btn_update.setOnClickListener(this);
        et_name.setText(sh.getString("name",""));
        et_place.setText(sh.getString("place",""));
        et_phone.setText(sh.getString("phone",""));
        et_email.setText(sh.getString("email",""));
        Picasso.with(getApplicationContext()).load(Uri.parse(sh.getString("photo",""))).networkPolicy(NetworkPolicy.NO_CACHE).transform(new CircleTransform()).memoryPolicy(MemoryPolicy.NO_CACHE).into(img_select);
        sh= PreferenceManager.getDefaultSharedPreferences(this);
        url=sh.getString("url","")+"api_updateProfile";

    }

    @Override
    public void onClick(View view) {
        if(view==btn_update){
            final String username=et_name.getText().toString();
            final String email=et_email.getText().toString();
            final String phone=et_phone.getText().toString();
            final String location=et_place.getText().toString();





            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                String status=jsonObject.getString("status");
                                if(status.equalsIgnoreCase("success"))
                                {
                                    Toast.makeText(UpdateProfile.this, "Update Successful", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                                }
                                else{
                                    Toast.makeText(UpdateProfile.this, "Failed to Register, Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){}

                            Toast.makeText(UpdateProfile.this, "Server Error, Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Toast.makeText(getApplicationContext(), "eeeee" + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username",username);
                    params.put("email",email);
                    params.put("phone",phone);
                    params.put("location",location);
                    params.put("photo",attach);
                    params.put("lid",sh.getString("lid",""));

                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            requestQueue.add(postRequest);

        }
        if(view==img_select)
        {
            showfilechooser(1);
        }
    }






    void showfilechooser(int string) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //getting all types of files

        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), string);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getApplicationContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                ////
                Uri uri = data.getData();

                try {
                    path = FileUtils.getPath(this, uri);

                    File fil = new File(path);
                    float fln = (float) (fil.length() / 1024);
                    atype = path.substring(path.lastIndexOf(".") + 1);


                    fname = path.substring(path.lastIndexOf("/") + 1);

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                try {

                    File imgFile = new File(path);

                    if (imgFile.exists()) {

                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        img_select.setImageBitmap(myBitmap);

                    }


                    File file = new File(path);
                    byte[] b = new byte[8192];
                    Log.d("bytes read", "bytes read");

                    InputStream inputStream = new FileInputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    int bytesRead = 0;

                    while ((bytesRead = inputStream.read(b)) != -1) {
                        bos.write(b, 0, bytesRead);
                    }
                    byteArray = bos.toByteArray();

                    String str = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                    attach = str;


                } catch (Exception e) {
                    Toast.makeText(this, "String :" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }

                ///

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent ij=new Intent(getApplicationContext(),ProfileActivity.class);
        startActivity(ij);
    }
}
