package com.kevinsolano.firebase_tutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PagoActivity extends AppCompatActivity {

    private Button mRegresar,mReservar,mCalcular,mPagar;
    private EditText mFechaTarj,mFechaAtarjeta,mNumTarjeta,mNumSeguridad;
    private FirebaseAuth mAuth;
    private String userId,garajeId,anio,dia,hora,mes,precio,tipo,horaRec,diaRec,anioTarjeta,mesTarjeta,numTarjeta,numSeguridad;
    private DatabaseReference mAnfitrionReference,mCiclistaReference,mCiclistaReference2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);

        //obtener datoa de la ReservaHecha en db
        //guardar todos de nuevo ya con los datos del pago

        mPagar = (Button) findViewById(R.id.pagar);
        mFechaAtarjeta = (EditText) findViewById(R.id.anioTarjeta1);
        mFechaTarj = (EditText) findViewById(R.id.mesTarjeta1);
        mNumTarjeta = (EditText) findViewById(R.id.numTarjeta1);
        mNumSeguridad = (EditText) findViewById(R.id.numSeguridad1);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mCiclistaReference = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(userId).child("ReservaHecha");
        mCiclistaReference2 = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(userId).child("ReservaIniciada");
        obtenerInfoCiclista();

        mPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerIdAnfitrion();
                Intent intent = new Intent(PagoActivity.this, InformacionReservaActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
    private  void obtenerInfoCiclista(){


        mCiclistaReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String,Object>) dataSnapshot.getValue();


                    if(map.get("anio") != null) {

                        anio = map.get("anio").toString();

                    }if(map.get("hora") != null) {

                        hora = map.get("hora").toString();

                    }if(map.get("dia") != null) {

                        dia = map.get("dia").toString();

                    }if(map.get("mes") != null) {

                        mes = map.get("mes").toString();

                    }if(map.get("precio") != null) {

                        precio = map.get("precio").toString();

                    }if(map.get("tipo") != null) {

                        tipo = map.get("tipo").toString();

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private  void obtenerIdAnfitrion(){


        mCiclistaReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String,Object>) dataSnapshot.getValue();


                    if(map.get("ReservaIniciadaId") != null){

                        garajeId = map.get("ReservaIniciadaId").toString();

                        DatabaseReference mCiclistaReference = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(userId).child("ReservaHecha");
                        //obtiene los datos ingresados por el usuario en la IU
                        //se crea un post (instancia en la Bd para un usuario)
                        anioTarjeta = mFechaAtarjeta.getText().toString();
                        mesTarjeta = mFechaTarj.getText().toString();
                        numSeguridad = mNumSeguridad.getText().toString();
                        numTarjeta = mNumTarjeta.getText().toString();

                        Map newPost = new HashMap();
                        newPost.put("fechaAnTarjeta", anioTarjeta);
                        // se le da un username en el post que será mandado para registrar en la bd de firebase
                        newPost.put("fechaTarjeta", mesTarjeta);
                        newPost.put("numSeguridad", numSeguridad);
                        newPost.put("numTarjeta", numTarjeta);
                        newPost.put("dia", dia);
                        // se le da un username en el post que será mandado para registrar en la bd de firebase
                        newPost.put("mes", mes);
                        newPost.put("anio", anio);
                        newPost.put("tipo", tipo);
                        newPost.put("hora", hora);
                        newPost.put("precio", precio);
                        newPost.put("IdAnfitrion", garajeId);


                        //se manda toda la info del Map a la bd para que se ingrese y registre al usuario con la nueva información
                        mCiclistaReference.setValue(newPost);

                        //ir a la instancia para el nuevo usuario en firebase
                        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Anfitrion").child(garajeId).child("ReservaHecha");

                        //obtiene los datos ingresados por el usuario en la IU
                        //se crea un post (instancia en la Bd para un usuario)
                        Map newPost2 = new HashMap();
                        newPost2.put("dia", dia); // se le da un username en el post que será mandado para registrar en la bd de firebase
                        newPost2.put("mes", mes);
                        newPost2.put("anio", anio);
                        newPost2.put("tipo", tipo);
                        newPost2.put("hora", hora);
                        newPost2.put("precio", precio);
                        newPost2.put("IdAnfitrion", userId);

                        //se manda toda la info del Map a la bd para que se ingrese y registre al usuario con la nueva información
                        current_user_db.setValue(newPost2);
                        Intent intent = new Intent(PagoActivity.this, InformacionReservaActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

}



