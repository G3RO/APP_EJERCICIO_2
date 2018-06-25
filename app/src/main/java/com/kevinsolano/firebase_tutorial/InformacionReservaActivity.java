package com.kevinsolano.firebase_tutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class InformacionReservaActivity extends AppCompatActivity {


    private TextView mNombre,mDireccion,mTelefono,mPrecio,mDia,mMes,mAnio,mHora,mServicio;
    private Button mCont;
    private String userId,userId2,precio,garajeId, nombre,direccion,telefono,dia,mes,anio,hora,servicio;
    private FirebaseAuth mAuth,mAuth2;
    private DatabaseReference mAnfitrionReference,mCiclistaReference,mAnfitrionReference2,mCiclistaReference2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_reserva);

        mNombre = (TextView) findViewById(R.id.nombreRes);
        mDireccion = (TextView) findViewById(R.id.direcRes);
        mTelefono = (TextView) findViewById(R.id.telefRes);
        mPrecio = (TextView) findViewById(R.id.precioRes);
        mDia = (TextView) findViewById(R.id.diaRes);
        mMes = (TextView) findViewById(R.id.mesRes);
        mAnio = (TextView) findViewById(R.id.anioRes);
        mHora = (TextView) findViewById(R.id.horaRes);
        mServicio = (TextView) findViewById(R.id.tipoRes);

        mCont = (Button) findViewById(R.id.botContinuar);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mCiclistaReference = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(userId).child("ReservaIniciada");
        obtenerInfoAnfitrion();

        mAuth2 = FirebaseAuth.getInstance();
        userId2 = mAuth.getCurrentUser().getUid();
        mCiclistaReference2 = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(userId2).child("ReservaHecha");
        obtenerInfoCiclista();

        mCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InformacionReservaActivity.this, CalificarActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }

    private  void obtenerInfoAnfitrion(){


        mCiclistaReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String,Object>) dataSnapshot.getValue();


                    if(map.get("ReservaIniciadaId") != null){

                        garajeId = map.get("ReservaIniciadaId").toString();
                        mAnfitrionReference = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Anfitrion").child(garajeId);
                        obtenerInfo();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private  void obtenerInfo(){


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
                        mTelefono.setText("Telefono: " + telefono);
                    }
                    if(map.get("Direccion") != null){

                        direccion = map.get("Direccion").toString();
                        mDireccion.setText("Direcion: " + direccion);
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

    private  void obtenerInfoCiclista() {


        mCiclistaReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("precio") != null) {

                        precio = map.get("precio").toString();
                        mPrecio.setText("Precio" + precio);

                    }
                    if (map.get("dia") != null) {

                        dia = map.get("dia").toString();
                        mDia.setText("Día " + dia);

                    }
                    if (map.get("mes") != null) {

                        mes = map.get("mes").toString();
                        mMes.setText("Mes " + mes);

                    }
                    if (map.get("anio") != null) {

                        anio = map.get("anio").toString();
                        mAnio.setText("Año " + anio);

                    }
                    if(map.get("hora") != null){

                        hora = map.get("hora").toString();
                        mHora.setText("Hora " + hora);

                    }
                    if(map.get("tipo") != null){

                        servicio = map.get("tipo").toString();
                        mServicio.setText("Tipo Servicio " + servicio);

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
