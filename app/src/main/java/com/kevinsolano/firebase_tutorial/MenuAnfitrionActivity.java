package com.kevinsolano.firebase_tutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MenuAnfitrionActivity extends AppCompatActivity {


    private ImageView mPerfil,mReserva,mConfigu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_anfitrion);

        mPerfil = (ImageView) findViewById(R.id.perfil);
        mReserva = (ImageView) findViewById(R.id.verReservas);
        mConfigu = (ImageView) findViewById(R.id.configur);

        mPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MenuAnfitrionActivity.this, PerfilAnfitrionActivity.class);
                startActivity(intent);
                finish();
                return;

            }
        });

        mReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intenta = new Intent(MenuAnfitrionActivity.this, InformacionReservaActivity.class);
                startActivity(intenta);
                finish();
            }
        });
        mConfigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
