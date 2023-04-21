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

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_feedback;

    Button btn_feedback;
    SharedPreferences sh;
    String url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        et_feedback=(EditText)findViewById(R.id.et_feedback);

        btn_feedback=(Button)findViewById(R.id.btn_feedback);

        btn_feedback.setOnClickListener(this);
        sh= PreferenceManager.getDefaultSharedPreferences(this);
        url=sh.getString("url","")+"api_feedbackFn";

    }
    @Override
    public void onClick(View view) {
        if(view==btn_feedback){
            final String feedback = et_feedback.getText().toString();
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
                                    Toast.makeText( FeedbackActivity.this, "Feedback Added", Toast.LENGTH_SHORT).show();
                                    Intent ij=new Intent(getApplicationContext(),Sam.class);
                                    startActivity(ij);
                                }
                                else{
                                    Toast.makeText(FeedbackActivity.this, "Failed to add Feedback, Please try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                Toast.makeText(FeedbackActivity.this, "Error "+e, Toast.LENGTH_SHORT).show();
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
                    params.put("feedback",feedback);
                    params.put("lid",sh.getString("lid",""));

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
