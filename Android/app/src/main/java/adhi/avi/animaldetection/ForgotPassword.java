package adhi.avi.animaldetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    EditText et_email;
    TextView back;
    Button b;
    SharedPreferences sh;
    String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        et_email=(EditText)findViewById(R.id.etEmail);
        b=(Button)findViewById(R.id.btn_reset);
        back=(TextView)findViewById(R.id.tv_back);
        b.setOnClickListener(this);
        back.setOnClickListener(this);
        sh= PreferenceManager.getDefaultSharedPreferences(this);
        url=sh.getString("url","")+"api_forgotpasswordFn";
    }

    @Override
    public void onClick(View view) {
        if(view==b){
            final String email=et_email.getText().toString();
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                String status=jsonObject.getString("status");
                                if(status.equalsIgnoreCase("ok"))
                                {
                                    Toast.makeText(ForgotPassword.this, "If the email is registered you will recieve a password reset link shortly.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(ForgotPassword.this, "Invalid Mail", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                Toast.makeText(ForgotPassword.this, ""+e, Toast.LENGTH_SHORT).show();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Toast.makeText(getApplicationContext(), "Server Error" + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email",email);
                    params.put("ip",sh.getString("ip",""));

                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            requestQueue.add(postRequest);


        }
        else{

            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
    }
}
