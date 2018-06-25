
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
//el bueno
public class AnfitrionLoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText mCorreoLogin, mContrasenaLogin, mCorreoRegis, mContrasenaRegis,mUserameRegis
            , mDireccionRegis, mTelefonoRegis, mGeneroRegis, mNombreRegis;

    private Button mBotonLogin, mBotonRegis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anfitrion_login);

        //estas lineas se inicializan liganolas con el edit text de la IU

        mCorreoLogin = (EditText) findViewById(R.id.correoLogin);
        mContrasenaLogin = (EditText) findViewById(R.id.contrasenaLogin);
        mCorreoRegis = (EditText) findViewById(R.id.correoRegis);
        mContrasenaRegis = (EditText) findViewById(R.id.contrasenaRegis);
        mUserameRegis = (EditText) findViewById(R.id.usernameRegis) ;
        mDireccionRegis = (EditText) findViewById(R.id.direccionRegis);
        mTelefonoRegis = (EditText) findViewById(R.id.telefonoRegis);
        mNombreRegis = (EditText) findViewById(R.id.nombreRegis);
        mBotonLogin = (Button) findViewById(R.id.botonInicioS);
        mBotonRegis = (Button) findViewById(R.id.botonRegis);

        mAuth = FirebaseAuth.getInstance(); // verifica si tiene inicio de sesión o no

        //metodo para obtener el usuario
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//obtiene la info del usuario actual.
                if(user != null){

                    // Intent intent = new Intent(AnfitrionLoginActivity.this, AnfitrionMapsActivity.class);
                    Intent intent = new Intent(AnfitrionLoginActivity.this, AnfitrionMapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;

                }

            }
        };



        mBotonRegis.setOnClickListener(new View.OnClickListener() {//accion si el boton registro es activado
            @Override
            public void onClick(View v) {

                final String correo = mCorreoRegis.getText().toString();//obtener datos que el usuario ingresa en la IU
                final String contrasena = mContrasenaRegis.getText().toString();
                final String username = mUserameRegis.getText().toString();
                //compara el username que obtenemos del usuario con los de la BD en firebase
                Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Anfitrion").orderByChild("Username").equalTo(username);

                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getChildrenCount() > 0) {//si existe un usuario igual entonces se le dice al usuario que se registre con otro

                            Toast.makeText(AnfitrionLoginActivity.this, "elige otro username", Toast.LENGTH_SHORT).show(); // mensaje al usuario

                        } else {

                            //crea una tarea para crear un usuario
                            mAuth.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener(AnfitrionLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //si la el rigistro no se puede dar
                                    if (!task.isSuccessful()) {
                                        //error al crear sesion
                                        Toast.makeText(AnfitrionLoginActivity.this, "Error en crear de sesión", Toast.LENGTH_SHORT).show();

                                    } else {    //si el registro se puede dara
                                        //obtener un id para el usuario
                                        String user_id = mAuth.getCurrentUser().getUid();
                                        //ir a la instancia para el nuevo usuario en firebase
                                        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Anfitrion").child(user_id);

                                        //obtiene los datos ingresados por el usuario en la IU
                                        String nombre = mNombreRegis.getText().toString();
                                        String telefono = mTelefonoRegis.getText().toString();
                                        String direccion = mDireccionRegis.getText().toString();
                                        int calificacionA = 5;
                                        //String edad = mGeneroRegis.getText().toString();

                                        //se crea un post (instancia en la Bd para un usuario)
                                        Map newPost = new HashMap();
                                        newPost.put("Username", username); // se le da un username en el post que será mandado para registrar en la bd de firebase
                                        newPost.put("Nombre", nombre);
                                        newPost.put("Telefono", telefono);
                                        newPost.put("Direccion", direccion);
                                        newPost.put("Calificacion", calificacionA);
                                        // newPost.put("Genero", genero);
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

        //metodo para el inicio de sesión
        mBotonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //obtener los datos que ingreso el usuario : correo e email
                String correo = mCorreoLogin.getText().toString();
                String contrasena = mContrasenaLogin.getText().toString();
                // se trata de hacer el login con correo y contraeña
                mAuth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(AnfitrionLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //si no se se encuentra el usuario con esos datos no se puede iniciar sesión
                        if(!task.isSuccessful()){
                            //mensaje al usuario
                            Toast.makeText(AnfitrionLoginActivity.this, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show();

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

