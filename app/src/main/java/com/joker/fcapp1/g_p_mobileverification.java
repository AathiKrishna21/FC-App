package com.joker.fcapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joker.fcapp1.Model.G_SignIn_Details;
import com.joker.fcapp1.Model.Username;

import java.util.concurrent.TimeUnit;

public class g_p_mobileverification extends AppCompatActivity {
    EditText phnnum;
    Button sign_in;
    String phnnum1,phnnum2;
    Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_p_mobileverification);
        window=this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));

        phnnum = findViewById(R.id.editText5);
        sign_in = findViewById(R.id.button);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = database.getReference("Users");

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phnnum1 = phnnum.getText().toString();
                phnnum2 = "+91" + phnnum1;

                Username u3 = new Username();
                String username = u3.getUsername();
                if (phnnum1.length()==10){
                    dbRef.child(username).child("Phonenumber").setValue(phnnum1);
                    startActivity(new Intent(g_p_mobileverification.this,Main2Activity.class));
                    finish();
                }
                else{
                    phnnum.setError("Enter valid number");
                    phnnum.requestFocus();
                }
            }
        });
    }
}
