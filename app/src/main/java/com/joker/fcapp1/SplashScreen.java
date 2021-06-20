package com.joker.fcapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT=900;
    FirebaseAuth mAuth;
    Intent intent;
    Window window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();
        window=this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.splash_screen));
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.

//        setContentView(R.layout.activity_main);
        //this will bind your MainActivity.class file with activity_main.

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user=mAuth.getCurrentUser();
//        Intent intent=getIntent();

                if (user != null ) {

                    if(user.getPhoneNumber().equals("")) {
                        intent=new Intent(SplashScreen.this, g_s_mobileverification.class);
                    }
                    else if (getIntent().getExtras() != null) {
                        for (String key : getIntent().getExtras().keySet()){
                            String value = getIntent().getExtras().getString(key);
                            intent=new Intent(SplashScreen.this, Main2Activity.class);
                            intent.putExtra("uid",value);

                        }}
                    else{
                        intent=new Intent(SplashScreen.this, Main2Activity.class);

                    }

                }
                else{
                    intent=new Intent(SplashScreen.this,MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.fade_out);
    }
}