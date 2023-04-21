package adhi.avi.animaldetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PredictionHistory extends AppCompatActivity {

    ListView lv;
    SharedPreferences sh;
    String url="";
    ArrayList<String> date,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_history);

        lv = (ListView)findViewById(R.id.list_view);
        sh= PreferenceManager.getDefaultSharedPreferences(this);
        url=sh.getString("url","")+"api_history";

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
                                name=new ArrayList<>();
                                date=new ArrayList<>();

                                JSONArray ja=jsonObject.getJSONArray("data");
                                for (int i=0;i<ja.length();i++){
                                    JSONObject jo=ja.getJSONObject(i);
                                    name.add(jo.getString("name"));
                                    date.add(jo.getString("date"));


                                }
                                lv.setAdapter(new listitem(getApplicationContext(),name,date));

                                }
                            else{
                                Toast.makeText(PredictionHistory.this, "Failed to fetch prediction history!", Toast.LENGTH_SHORT).show();
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
}
