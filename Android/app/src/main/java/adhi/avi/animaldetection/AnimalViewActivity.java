package adhi.avi.animaldetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class AnimalViewActivity extends AppCompatActivity {

    TextView tv_name,tv_desc;
    ImageView img_profile;
    SharedPreferences sh;
    String name,description,photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_view);

        tv_name =(TextView)findViewById(R.id.tv_name);
        tv_desc =(TextView)findViewById(R.id.tv_desc);
        img_profile =(ImageView) findViewById(R.id.img_profile);

        sh= PreferenceManager.getDefaultSharedPreferences(this);
        String imageURL=sh.getString("url","")+"static/uploads/animal_img/"+sh.getString("photo","");

        name = sh.getString("name","");
        description = sh.getString("description","");


        tv_name.setText(name);
        tv_desc.setText(description);
        Picasso.with(AnimalViewActivity.this).load(Uri.parse(imageURL)).into(img_profile);






    }
}
