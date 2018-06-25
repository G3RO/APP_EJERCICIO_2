package com.kevinsolano.firebase_tutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CalificarActivity extends AppCompatActivity {

    private Button mCalificar;
    private FirebaseAuth mAuth;
    private String userId,garajeId, calif,calficar;
    private int calific, cal,calificacionT;
    private DatabaseReference mAnfitrionReference,mCiclistaReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);

        mCalificar = (Button) findViewById(R.id.calificarBut);

        final Spinner califSpinner = (Spinner) findViewById(R.id.calificarSpin);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.calificaciones_Spinner, android.R.layout.simple_spinner_item);

// Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        califSpinner.setAdapter(adapter1);


        calficar = califSpinner.getSelectedItem().toString();

        mCalificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mAuth = FirebaseAuth.getInstance();
                userId = mAuth.getCurrentUser().getUid();
                mCiclistaReference = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(userId).child("ReservaIniciada");
                obtenerInfoCiclista();
                Intent intent = new Intent(CalificarActivity.this, PerfilCiclistaActivity.class);
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


                    if(map.get("ReservaIniciadaId") != null){

                        garajeId = map.get("ReservaIniciadaId").toString();
                        mAnfitrionReference = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Anfitrion").child(garajeId).child("Calif");
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

                    if(map.get("Calificacion") != null){

                        /*calif = map.get("Calificacion").toString();

                        calific = Integer.parseInt(calif);
                        cal = Integer.parseInt(calficar);

                       calific = calific + cal;
                       calificacionT = calific ;
                         Map newPost2 = new HashMap();
                                        newPost2.put("Calificacion", calificacionT);

                                        mAnfitrionReference.setValue(newPost2);

                    */
                        terminarFuncion();
                        // se le da un username en el post que será mandado para registrar en la bd de firebase


                        //se manda toda la info del Map a la bd para que se ingrese y registre al usuario con la nueva información
                        //mAnfitrionReference.setValue(newPost);

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

    private void terminarFuncion(){

        Intent intent = new Intent(CalificarActivity.this, PerfilCiclistaActivity.class);
        startActivity(intent);
        finish();
        return;

    }
}
