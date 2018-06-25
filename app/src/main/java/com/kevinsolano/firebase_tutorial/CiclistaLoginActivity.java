package com.kevinsolano.firebase_tutorial;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CiclistaLoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private EditText mCorreoLogin, mContrasenaLogin, mCorreoRegist, mContrasenaRegist,mUserameRegist
            , mTelefonoRegist, mDireccionRegist, mNombreRegist;

    private Button mBotonLogin, mBotonRegis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //estas lineas se inicializan liganolas con el edit text de la IU
        mCorreoLogin = (EditText) findViewById(R.id.correoLogin);
        mContrasenaLogin = (EditText) findViewById(R.id.contrasenaLogin);
        mCorreoRegist = (EditText) findViewById(R.id.correoRegis);
        mContrasenaRegist = (EditText) findViewById(R.id.contrasenaRegis);
        mUserameRegist = (EditText) findViewById(R.id.usernameRegis) ;
        mTelefonoRegist = (EditText) findViewById(R.id.telefonoRegis);
        mNombreRegist = (EditText) findViewById(R.id.nombreRegis);
        mBotonLogin = (Button) findViewById(R.id.botonInicioS);
        mBotonRegis = (Button) findViewById(R.id.botonIrRegistro);

        mAuth = FirebaseAuth.getInstance(); // verifica si tiene inicio de sesión o no

        //metodo para obtener el usuario
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//obtiene la info del usuario actual.
                if(user != null){

                    Intent intent = new Intent(CiclistaLoginActivity.this, MenuDrawerActivity.class);
                    startActivity(intent);
                    finish();
                    return;

                }

            }
        };

        mBotonRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CiclistaLoginActivity.this, CiclistaRegistroActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

                //metodo para el inicio de sesión
        mBotonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //obtener los datos que ingreso el usuario : correo e email
                String correo = mCorreoLogin.getText().toString();
                String contrasena = mContrasenaLogin.getText().toString();
               // se trata de hacer el login con correo y contraeña
                mAuth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(CiclistaLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //si no se se encuentra el usuario con esos datos no se puede iniciar sesión
                        if(!task.isSuccessful()){
                            //mensaje al usuario
                            Toast.makeText(CiclistaLoginActivity.this, "Error en el inicio de sesión, Verifica tus datos", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

            }
        });

    }
//metodo para parar la accion
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
