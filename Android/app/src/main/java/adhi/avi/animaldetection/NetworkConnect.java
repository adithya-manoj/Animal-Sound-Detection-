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

import com.squareup.picasso.Picasso;

public class NetworkConnect extends AppCompatActivity implements View.OnClickListener {

    EditText et_ip;
    SharedPreferences sh;
    ImageView im;
    Button connect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_connect);

        et_ip=(EditText)findViewById(R.id.et_ip);
        connect=(Button)findViewById(R.id.btn_connect);
    im=(ImageView)findViewById(R.id.imageView2);

        Picasso.with(getApplicationContext()).load(R.drawable.logo).transform(new CircleTransform()).into(im);
        connect.setOnClickListener(this);
        sh= PreferenceManager.getDefaultSharedPreferences(this);
        et_ip.setText(sh.getString("ip",""));
    }
    public void onClick(View view) {
        if(view==connect){
            String ip_address=et_ip.getText().toString();
            SharedPreferences.Editor edt=sh.edit();
            edt.putString("ip",ip_address);
            edt.putString("url","http://"+ip_address+":4000/");
            edt.commit();
            Intent in=new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(in);
        }
    }
}
