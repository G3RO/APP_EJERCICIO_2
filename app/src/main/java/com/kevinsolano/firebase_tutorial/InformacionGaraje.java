package com.kevinsolano.firebase_tutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Map;




public class InformacionGaraje extends AppCompatActivity {


    private TextView mNombre,mTelef,mCalif,mDirec;
    private Button mBack, mIniciaRes;
    private FirebaseAuth mAuth;
    private DatabaseReference mCiclistaRef, mAnfitrionReference, mRef;
    private String userId, nombre,telefono,calificacionI,direccionI,garajeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_garaje);

        mNombre = (TextView) findViewById(R.id.nombreInfo);
        mCalif = (TextView) findViewById(R.id.califInfo);
        mTelef = (TextView) findViewById(R.id.telefInfo);
        mDirec = (TextView) findViewById(R.id.direcInfo);

        mBack = (Button) findViewById(R.id.backMenuI);
        mIniciaRes = (Button) findViewById(R.id.botReservar);


        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
       // mCiclistaRef = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(userId);

        mCiclistaRef = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(userId).child("ReservaIniciada");
        obtenerInfoCiclista();

       //String garajeIdA = "D8Gah0xcArQOWMput3ehCmtfumK2";
        //mAnfitrionReference = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Anfitrion").child(garajeIdA);
        //obtenerInfoAnfitrion();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                mRef = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(userId).child("ReservaIniciada");
                mRef.removeValue();
                */
                mCiclistaRef.removeValue();
                Intent intent = new Intent(InformacionGaraje.this, CiclistaMapsActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mIniciaRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InformacionGaraje.this, ConfigurarReservaActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    private  void obtenerInfoCiclista(){


        mCiclistaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String,Object>) dataSnapshot.getValue();


                    if(map.get("ReservaIniciadaId") != null){

                        garajeId = map.get("ReservaIniciadaId").toString();
                        mAnfitrionReference = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Anfitrion").child(garajeId);
                        obtenerInfoAnfitrion();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }
    private  void obtenerInfoAnfitrion(){


        mAnfitrionReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String,Object>) dataSnapshot.getValue();

                    if(map.get("Nombre") != null){

                        nombre = map.get("Nombre").toString();
                        mNombre.setText("Nombre: " + nombre);

                    }
                    if(map.get("Telefono") != null){

                        telefono = map.get("Telefono").toString();
                        mTelef.setText("Telefono: " + telefono);

                    }

                    if(map.get("Calificacion") != null){

                        calificacionI = map.get("Calificacion").toString();
                        mCalif.setText("Calificación: " + calificacionI);

                    }
                    if(map.get("Direccion") != null){

                        direccionI = map.get("Direccion").toString();
                        mDirec.setText("Direccion:" + direccionI);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}
