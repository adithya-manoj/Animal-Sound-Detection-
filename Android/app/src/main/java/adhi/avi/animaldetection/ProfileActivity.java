package adhi.avi.animaldetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tv_name,tv_place,tv_email,tv_phone;
    Button btn_update;
    SharedPreferences sh;
    ImageView img_profile;
    String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tv_name=(TextView)findViewById(R.id.tv_name);
        tv_place=(TextView)findViewById(R.id.tv_place);
        tv_phone=(TextView)findViewById(R.id.tv_phone);
        tv_email=(TextView)findViewById(R.id.tv_email);
        btn_update = (Button)findViewById(R.id.btn_update);
        img_profile = (ImageView)findViewById(R.id.img_profile);

        sh= PreferenceManager.getDefaultSharedPreferences(this);
        url=sh.getString("url","")+"api_viewProfile";


        btn_update.setOnClickListener(this);

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
                                tv_name.setText(jsonObject.getString("name"));
                                tv_place.setText(jsonObject.getString("place"));
                                tv_phone.setText(jsonObject.getString("phone"));
                                tv_email.setText(jsonObject.getString("email"));
                                Log.d("===============",sh.getString("url","")+"static/uploads/user_image/"+jsonObject.getString("photo"));
                                Picasso.with(getApplicationContext()).load(Uri.parse(sh.getString("url","")+"static/uploads/user_image/"+jsonObject.getString("photo"))).networkPolicy(NetworkPolicy.NO_CACHE).transform(new CircleTransform()).memoryPolicy(MemoryPolicy.NO_CACHE).into(img_profile);
                                SharedPreferences.Editor edt=sh.edit();
                                edt.putString("name",jsonObject.getString("name"));
                                edt.putString("email",jsonObject.getString("email"));
                                edt.putString("phone",jsonObject.getString("phone"));
                                edt.putString("place",jsonObject.getString("place"));
                                edt.putString("photo",sh.getString("url","")+"static/uploads/user_image/"+jsonObject.getString("photo"));
                                edt.commit();



                            }
                            else{
                                Toast.makeText(ProfileActivity.this, "Profile not found!", Toast.LENGTH_SHORT).show();
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
                params.put("lid",sh.getString("lid",""));

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        requestQueue.add(postRequest);

    }

    @Override
    public void onClick(View view) {
        if(view==btn_update){

            startActivity(new Intent(getApplicationContext(),UpdateProfile.class));
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent ij=new Intent(getApplicationContext(),Sam.class);
        startActivity(ij);
    }
}
