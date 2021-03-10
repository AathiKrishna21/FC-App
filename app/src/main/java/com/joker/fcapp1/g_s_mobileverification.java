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

public class g_s_mobileverification extends AppCompatActivity {
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    FirebaseAuth auth;
    Window window;
    private String verificationCode;
    Button genotp,signin;
    EditText phnno,enterotp;
    String phnnum,otp,phnnum1;
    String name,email,username;
    String userKey;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private void findviews(){
        genotp=findViewById(R.id.button2);
        signin=findViewById(R.id.button);
        phnno=findViewById(R.id.editText5);
        enterotp=findViewById(R.id.editText3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_s_mobileverifiction);
        window=this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));
        findviews();
        //String[] split = email.split("@");
        //username=split[0];
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = database.getReference("Users");
        enterotp.setVisibility(View.GONE);
        signin.setVisibility(View.GONE);
        StartFirebaseLogin();
        genotp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final ProgressDialog mdialog=new ProgressDialog(g_s_mobileverification.this);
                mdialog.setMessage("Please Wait...");
                mdialog.show();
                phnnum=phnno.getText().toString();
                phnnum1 = "+91" + phnnum;
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phnnum1,60, TimeUnit.SECONDS,g_s_mobileverification.this,mCallback);
                mdialog.dismiss();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog=new ProgressDialog(g_s_mobileverification.this);
                dialog.setMessage("Please Wait...");
                dialog.show();
                otp=enterotp.getText().toString();
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationCode,otp);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                userKey = user.getUid();
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Username u2=new Username();
                                    username=u2.getUsername();
                                    dbRef.child(userKey).child("Phonenumber").setValue(phnnum);
                                    Toast.makeText(g_s_mobileverification.this,"Verification Successful!",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(g_s_mobileverification.this, Main2Activity.class));
                                    finish();
                                }
                                else {
                                    dialog.dismiss();
                                    Toast.makeText(g_s_mobileverification.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

    }
    private void StartFirebaseLogin(){
        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(g_s_mobileverification.this,"Verification completed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed( FirebaseException e) {
                Toast.makeText(g_s_mobileverification.this,"Verification failed",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken forceResendingToken){
                super.onCodeSent(s,forceResendingToken);
                verificationCode = s;
                Toast.makeText(g_s_mobileverification.this,"Code sent",Toast.LENGTH_SHORT).show();
                genotp.setVisibility(View.GONE);
                enterotp.setVisibility(View.VISIBLE);
                signin.setVisibility(View.VISIBLE);
            }
        };
    }
}
