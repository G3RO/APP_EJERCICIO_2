package com.kevinsolano.firebase_tutorial;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PerfilAnfitrionActivity extends AppCompatActivity {

    private TextView mNombre,mTelef,mCalif,mDireccion;
    private Button mBack, mIniciaRes,mcambiarIma;
    private FirebaseAuth mAuth;
    private DatabaseReference mAnfitrionReference;
    private String userId, nombre,telefono,calificacionA,direccionA, imagCiclurl;
    private ImageView mImageCic;
    private Uri resultadoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_anfitrion);

        mNombre = (TextView) findViewById(R.id.nombreAnf);
        mCalif = (TextView) findViewById(R.id.califAnf);
        mTelef = (TextView) findViewById(R.id.telefAnf);
        mDireccion = (TextView) findViewById(R.id.direccionAnf);

        mImageCic = (ImageView) findViewById(R.id.imagCic);

        mBack = (Button) findViewById(R.id.backMenu);
        mcambiarIma = (Button) findViewById(R.id.cambiarIma);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mAnfitrionReference = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Anfitrion").child(userId);
        obtenerInfoAnfitrion();
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PerfilAnfitrionActivity.this, MenuAnfitrionActivity.class);
                startActivity(intent);
                finish();
                return;
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

                        calificacionA = map.get("Calificacion").toString();
                        mCalif.setText("Calificación: " + calificacionA);

                    }
                    if(map.get("Direccion") != null){

                        direccionA = map.get("Direccion").toString();
                        mDireccion.setText("Direccion:" + direccionA);

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
