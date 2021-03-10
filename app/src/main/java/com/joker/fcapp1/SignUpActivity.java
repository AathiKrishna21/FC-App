package com.joker.fcapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Model.User;
import com.joker.fcapp1.Model.Username;

public class SignUpActivity extends AppCompatActivity {
    Window window;
    EditText edtname, edtrepassword, edtpassword, edtemail;
    public String email, name, password, repassword,username, userKey;
    Button submitbutton;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        window=this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));

        edtname = findViewById(R.id.editText5);
        edtpassword = findViewById(R.id.editText3);
        edtrepassword = findViewById(R.id.editText4);
        edtemail = findViewById(R.id.email);
        submitbutton = findViewById(R.id.button2);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = database.getReference("Users");


        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edtname.getText().toString();
                email = edtemail.getText().toString();
                password=edtpassword.getText().toString();
                repassword=edtrepassword.getText().toString();
                String split[]=email.split("@");
                String domain=split[1];

                if(name.isEmpty()){
                    edtname.setError("Enter Your Name");
                    edtname.requestFocus();
                }
                else if(email.isEmpty()){
                    edtemail.setError("Enter Your Name");
                    edtemail.requestFocus();
                }
                else if(password.isEmpty()){
                    edtpassword.setError("Enter Your Password");
                    edtpassword.requestFocus();
                }
                else if(repassword.isEmpty()){
                    edtrepassword.setError("Re type Your Password");
                    edtrepassword.requestFocus();
                }
                else if(domain!="student.tce.edu" || domain!="tce.edu"){
                    edtemail.setError("Use college mailId");
                    edtemail.requestFocus();
                }
                else{
                    if(password.equals(repassword)) {
                        username = split[0];
                        Username u1 = new Username();
                        u1.setUsername(username);
//                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                        userKey = user.getUid();
//                        dbRef.child(userKey).child("Name").setValue(name);
                            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this.getApplicationContext(),
                                                "SignUp unsuccessful: " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        userKey = user.getUid();
                                        dbRef.child(userKey).child("Name").setValue(name);
                                        dbRef.child(userKey).child("Email").setValue(email);
                                        dbRef.child(userKey).child("ProfileUrl").setValue("https://images.app.goo.gl/QeBW9yTr79wquSQz6");


                                        startActivity(new Intent(SignUpActivity.this, g_s_mobileverification.class));
                                    }
                                }
                            });

                    }
                   else{
                       edtrepassword.setText("");
                       edtrepassword.setText("");
                        edtpassword.setError("Password does't match");
                        edtpassword.requestFocus();
                    }
                }

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}