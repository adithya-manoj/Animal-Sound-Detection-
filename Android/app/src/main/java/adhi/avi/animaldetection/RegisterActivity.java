package adhi.avi.animaldetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_username,et_email,et_phone,et_location,et_password,et_confirmpass;
    TextView tv_login;
    Button register;
    SharedPreferences sh;
    ImageView img;
    String fileName="",url,loginId;
    byte[] fileBytes=null;
    String name="",email="",phone="",place="",password="",conpass="";
    Bitmap bitmap=null;
    ProgressDialog pd=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username=(EditText)findViewById(R.id.et_username);
        et_email=(EditText)findViewById(R.id.et_email);
        et_phone=(EditText) findViewById(R.id.et_phone);
        et_location=(EditText) findViewById(R.id.et_location);
        et_password=(EditText) findViewById(R.id.et_password);
        et_confirmpass=(EditText) findViewById(R.id.et_confirmpass);
        tv_login=(TextView)findViewById(R.id.tv_login);
        img=(ImageView)findViewById(R.id.imageView3);
        register=(Button)findViewById(R.id.btn_register);

        sh= PreferenceManager.getDefaultSharedPreferences(this);
        url=sh.getString("url","")+"api_registerFn";

        register.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        img.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);
            return;
        }

    }

    @Override
    public void onClick(View view) {
        if(view==register){
            name=et_username.getText().toString();
            email=et_email.getText().toString();
            phone=et_phone.getText().toString();
            place=et_location.getText().toString();
            password=et_password.getText().toString();
            conpass=et_confirmpass.getText().toString();
            int f=0;

            if (name.equalsIgnoreCase(""))
            {
                f=1;
                et_username.setError("Enter data");
            }
            if (email.equalsIgnoreCase(""))
            {
                f=1;
                et_email.setError("Enter data");
            }
             if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                f=1;
                et_email.setError("Invalid email");
            }
            if (phone.equalsIgnoreCase(""))
            {
                f=1;
                et_phone.setError("Enter data");
            }
            if (phone.length()!=10)
            {
                f=1;
                et_phone.setError("Invalid phone");
            }
            if (place.equalsIgnoreCase(""))
            {
                f=1;
                et_location.setError("Enter data");
            }
            if (password.equalsIgnoreCase(""))
            {
                f=1;
                et_password.setError("Enter data");
            }
            if (conpass.equalsIgnoreCase(""))
            {
                f=1;
                et_confirmpass.setError("Enter data");
            }
            if (!password.equalsIgnoreCase(conpass))
            {
                f=1;
                Toast.makeText(this, "Password mismatch....", Toast.LENGTH_SHORT).show();
            }
            if (f==0){

                uploadBitmap(bitmap);
            }
        }
        else if(view==tv_login){

            startActivity(new Intent(getApplicationContext(),LoginActivity.class));


        }
        else if (view==img)
        {

            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 100);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                img.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap) {

        pd=new ProgressDialog(RegisterActivity.this);
        pd.setMessage("Uploading....");
        pd.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            pd.dismiss();

                            JSONObject obj = new JSONObject(new String(response.data));

                            if(obj.getString("status").equalsIgnoreCase("success"))
                            {
                                Toast.makeText(getApplicationContext(),"Successfully registered", Toast.LENGTH_SHORT).show();
                                Intent ins= new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(ins);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Failed to register", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
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
                params.put("username", name);
                params.put("email", email);
                params.put("phone", phone);
                params.put("location", place);
                params.put("password", password);
                return params;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
}