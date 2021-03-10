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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Model.User;

public class SignInActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    Window window;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        window=this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));

        setContentView(R.layout.activity_sign_in);
        firebaseAuth = FirebaseAuth.getInstance();

        final EditText emailId = findViewById(R.id.email);
        final EditText passwd = findViewById(R.id.editText3);
        final Button btnSignIn = findViewById(R.id.button2);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailID = emailId.getText().toString();
                String paswd = passwd.getText().toString();
                if (emailID.isEmpty()) {
                    emailId.setError("Provide your Email first!");
                    emailId.requestFocus();
                } else if (paswd.isEmpty()) {
                    passwd.setError("Set your password");
                    passwd.requestFocus();
                } else if (emailID.isEmpty() && paswd.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                }
                else if(!(emailID.isEmpty() && paswd.isEmpty())){
                    firebaseAuth.signInWithEmailAndPassword(emailID,paswd).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this.getApplicationContext(),
                                        "SignIn unsuccessful: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(SignInActivity.this, Main2Activity.class));
                                finish();
                            }
                        }
                    });
                }
            }

        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
