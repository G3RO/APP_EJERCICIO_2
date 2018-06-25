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

public class PerfilCiclistaActivity extends AppCompatActivity {

    private TextView mNombre,mTelef,mCalif;
    private Button mBack, mIniciaRes,mcambiarIma;
    private FirebaseAuth mAuth;
    private DatabaseReference mCiclistaReference;
    private String userId, nombre,telefono,calificacion, imagCiclurl;
    private ImageView mImageCic;
    private Uri resultadoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_ciclista);

        mNombre = (TextView) findViewById(R.id.nombreCic);
        mCalif = (TextView) findViewById(R.id.califCic);
        mTelef = (TextView) findViewById(R.id.telefCic);

        mImageCic = (ImageView) findViewById(R.id.imagCic);

        mBack = (Button) findViewById(R.id.backMe);
        mIniciaRes = (Button) findViewById(R.id.botReservar);
        mcambiarIma = (Button) findViewById(R.id.cambiarIma);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mCiclistaReference = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(userId);
        obtenerInfoCiclista();

        mcambiarIma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarImagenCic();
            }
        });

        mImageCic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenta = new Intent(Intent.ACTION_PICK);
                intenta.setType("image/*");
                startActivityForResult(intenta,1);

            }
        });

    mBack.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(PerfilCiclistaActivity.this, CiclistaMapsActivity.class);
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

                    if(map.get("Nombre") != null){

                        nombre = map.get("Nombre").toString();
                        mNombre.setText(nombre);

                    }
                    if(map.get("Telefono") != null){

                        telefono = map.get("Telefono").toString();
                        mTelef.setText(telefono);

                    }
                    if(map.get("Calificacion") != null){

                        calificacion = map.get("Calificacion").toString();
                        mCalif.setText(calificacion);

                    }

                    if(map.get("imagenPerfilUrl") != null){

                        imagCiclurl = map.get("imagenPerfilUrl").toString();
                        Glide.with(getApplication()).load(imagCiclurl).into(mImageCic);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void guardarImagenCic(){

        if(resultadoUri != null){

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("imagenes_Ciclista").child(userId);
            Bitmap bitMap = null;

            try {
                bitMap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultadoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitMap.compress(Bitmap.CompressFormat.JPEG,20,baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Map nuevaImagen = new HashMap();
                    nuevaImagen.put("imagenPerfilUrl",downloadUrl.toString());
                    mCiclistaReference.updateChildren(nuevaImagen);
                    finish();
                    return;

                }
            });

        }else{

            finish();

        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && requestCode == Activity.RESULT_OK){

            final Uri imageUri = data.getData();
            resultadoUri = imageUri;
            mImageCic.setImageURI(resultadoUri);
        }
    }
}
