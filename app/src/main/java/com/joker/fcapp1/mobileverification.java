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
import com.joker.fcapp1.Model.User;
import com.joker.fcapp1.Model.Username;

import java.util.concurrent.TimeUnit;

public class mobileverification extends AppCompatActivity {

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    FirebaseAuth auth;
    private String verificationCode;
    Button genotp,signin;
    EditText phnno,enterotp;
    String phnnum,otp;
    String name,email,username;
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
        Window window;
        super.onCreate(savedInstanceState);                                             
        setContentView(R.layout.activity_mobileverification);
        window=this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));
        findviews();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = database.getReference("Users");
        enterotp.setVisibility(View.GONE);
        signin.setVisibility(View.GONE);
        StartFirebaseLogin();
        genotp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final ProgressDialog mdialog=new ProgressDialog(mobileverification.this);
                mdialog.setMessage("Please Wait...");
                mdialog.show();
                phnnum=phnno.getText().toString();
                String phnnum1="+91"+phnnum;
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phnnum1,60, TimeUnit.SECONDS,mobileverification.this,mCallback);
                mdialog.dismiss();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog=new ProgressDialog(mobileverification.this);
                dialog.setMessage("Please Wait...");
                dialog.show();
                otp=enterotp.getText().toString();
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationCode,otp);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Username u2=new Username();
                                    username =u2.getUsername();
                                    dbRef.child(username).child("Phonenumber").setValue(phnnum);
                                    Toast.makeText(mobileverification.this,"Verification Successful!",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(mobileverification.this, SignInActivity.class));
                                    finish();
                                }
                                else {
                                    dialog.dismiss();
                                    Toast.makeText(mobileverification.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mobileverification.this,"Verification completed",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onVerificationFailed( FirebaseException e) {
                Toast.makeText(mobileverification.this,"Verification failed",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken forceResendingToken){
                super.onCodeSent(s,forceResendingToken);
                verificationCode = s;
                Toast.makeText(mobileverification.this,"Code sent",Toast.LENGTH_SHORT).show();
                genotp.setVisibility(View.GONE);
                enterotp.setVisibility(View.VISIBLE);
                signin.setVisibility(View.VISIBLE);
            }
        };
    }
}
