package adhi.avi.animaldetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText ed1,ed2;
    TextView forgot,register,guest;
    Button btn;
    SharedPreferences sh;
    String url="";
    ImageView im;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        im=(ImageView)findViewById(R.id.imageView2);

        Picasso.with(getApplicationContext()).load(R.drawable.logo).transform(new CircleTransform()).into(im);
        ed1=(EditText)findViewById(R.id.etEmail);
        ed2=(EditText)findViewById(R.id.etpassword);
        forgot=(TextView)findViewById(R.id.tv_forgot);
        register=(TextView)findViewById(R.id.tv_back);
        guest=(TextView)findViewById(R.id.textView2);
        btn=(Button)findViewById(R.id.btn_reset);



        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ins =new Intent(getApplicationContext(),Record_animal_sound_copy.class);
                startActivity(ins);
            }
        });



        btn.setOnClickListener(this);
        forgot.setOnClickListener(this);
        register.setOnClickListener(this);
        sh= PreferenceManager.getDefaultSharedPreferences(this);
        url=sh.getString("url","")+"api_login_check";
ed1.setText(sh.getString("email",""));
ed2.setText(sh.getString("pass",""));
    }

    @Override
    public void onClick(View view) {
        if(view==btn){
            final String username=ed1.getText().toString();
            final String password=ed2.getText().toString();
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
                                 String lid=jsonObject.getString("loginId");
                                 SharedPreferences.Editor edt=sh.edit();
                                 edt.putString("lid",lid);
                                   edt.putString("email",username);
                                   edt.putString("pass",password);
                                 edt.commit();
                                 startActivity(new Intent(getApplicationContext(),Sam.class));
                               }
                               else{
                                   Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                               }
                           }catch (Exception e){}

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
                    params.put("email",username);

                    params.put("password",password);

                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            requestQueue.add(postRequest);

        }
        else if(view==forgot){
            startActivity(new Intent(getApplicationContext(),ForgotPassword.class));
        }
        else{
            startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }
}
