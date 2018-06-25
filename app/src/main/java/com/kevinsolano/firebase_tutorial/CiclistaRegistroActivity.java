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

public class CiclistaRegistroActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private EditText mCorreoLogin, mContrasenaLogin, mCorreoRegist, mContrasenaRegist,mUserameRegist
            , mTelefonoRegist, mNombreRegist;

    private Button mBotonLogin, mBotonRegis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ciclista_registro);

        //estas lineas se inicializan liganolas con el edit text de la IU
        mCorreoRegist = (EditText) findViewById(R.id.correoRegis);
        mContrasenaRegist = (EditText) findViewById(R.id.contrasenaRegis);
        mUserameRegist = (EditText) findViewById(R.id.usernameRegis) ;
        mTelefonoRegist = (EditText) findViewById(R.id.telefonoRegis);
        mNombreRegist = (EditText) findViewById(R.id.nombreRegis);
        mBotonRegis = (Button) findViewById(R.id.botonRegis);

        mAuth = FirebaseAuth.getInstance(); // verifica si tiene inicio de sesión o no

        //metodo para obtener el usuario
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//obtiene la info del usuario actual.
                if(user != null){

                    Intent intent = new Intent(CiclistaRegistroActivity.this, MenuDrawerActivity.class);
                    startActivity(intent);
                    finish();
                    return;

                }

            }
        };

        mBotonRegis.setOnClickListener(new View.OnClickListener() {//accion si el boton registro es activado
            @Override
            public void onClick(View v) {

                final String correo = mCorreoRegist.getText().toString();//obtener datos que el usuario ingresa en la IU
                final String contrasena = mContrasenaRegist.getText().toString();
                final String username = mUserameRegist.getText().toString();
                //compara el username que obtenemos del usuario con los de la BD en firebase
                Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").orderByChild("Username").equalTo(username);

                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getChildrenCount() > 0) {//si existe un usuario igual entonces se le dice al usuario que se registre con otro

                            Toast.makeText(CiclistaRegistroActivity.this, "elige otro username", Toast.LENGTH_SHORT).show(); // mensaje al usuario

                        } else {

                            //crea una tarea para crear un usuario
                            mAuth.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener(CiclistaRegistroActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //si la el rigistro no se puede dar
                                    if (!task.isSuccessful()) {
                                        //error al crear sesion
                                        Toast.makeText(CiclistaRegistroActivity.this, "Error en crear de sesión", Toast.LENGTH_SHORT).show();

                                    } else {    //si el registro se puede dara
                                        //obtener un id para el usuario
                                        String user_id = mAuth.getCurrentUser().getUid();
                                        //ir a la instancia para el nuevo usuario en firebase
                                        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(user_id);

                                        //obtiene los datos ingresados por el usuario en la IU
                                        String nombre = mNombreRegist.getText().toString();
                                        String telefono = mTelefonoRegist.getText().toString();
                                        int calificacion = 5;



                                        //se crea un post (instancia en la Bd para un usuario)
                                        Map newPost = new HashMap();
                                        newPost.put("Username", username); // se le da un username en el post que será mandado para registrar en la bd de firebase
                                        newPost.put("Nombre", nombre);
                                        newPost.put("Telefono", telefono);
                                        newPost.put("Calificacion", calificacion);

                                        //se manda toda la info del Map a la bd para que se ingrese y registre al usuario con la nueva información
                                        current_user_db.setValue(newPost);
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {


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
