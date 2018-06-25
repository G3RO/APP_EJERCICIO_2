package com.kevinsolano.firebase_tutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InicioAppActivity extends AppCompatActivity {

    private Button mAnfitrionBot, mCiclistaBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_app);

        mAnfitrionBot = (Button) findViewById(R.id.anfitrion);
        mCiclistaBot = (Button) findViewById(R.id.ciclista);

        mAnfitrionBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intenta = new Intent(InicioAppActivity.this, AnfitrionLoginActivity.class);
                startActivity(intenta);
                finish();
                return;

            }
        });

        mCiclistaBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intenta = new Intent(InicioAppActivity.this, CiclistaLoginActivity.class);
                startActivity(intenta);
                finish();
                return;

            }
        });

    }




}
