package adhi.avi.animaldetection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OTPconfirm extends AppCompatActivity implements View.OnClickListener {
    EditText etOTP,etPassword,etPasswordconfirm;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpconfirm);
        etOTP=(EditText)findViewById(R.id.etOTP);
        etPassword=(EditText)findViewById(R.id.etpassword);
        etPasswordconfirm=(EditText)findViewById(R.id.etpasswordconfirm);
        btn=(Button)findViewById(R.id.btn_reset);

        btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view==btn){
            String OTP=etOTP.getText().toString();
            String password=etPassword.getText().toString();
            String confirmpass=etPasswordconfirm.getText().toString();

        }    }
}
