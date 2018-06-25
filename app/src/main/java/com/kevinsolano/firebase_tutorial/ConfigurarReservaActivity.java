package com.kevinsolano.firebase_tutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigurarReservaActivity extends AppCompatActivity {

    private Button mRegresar,mReservar,mCalcular;
    private TextView mPrecio,mHora;
    private FirebaseAuth mAuth;
    private String userId,precio,garajeId;
    private DatabaseReference mAnfitrionReference,mCiclistaReference;
    private int precioT = 0;
    private int black = 15;
    private int plus = 10;
    private int basico = 0;
    private Spinner tipoSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar_reserva);
        mRegresar = (Button) findViewById(R.id.bac);
        mReservar = (Button) findViewById(R.id.reservar);
        mPrecio = (TextView) findViewById(R.id.precioGar);
        mCalcular = (Button) findViewById(R.id.calcPrecio);

        final Spinner diaSpinner = (Spinner) findViewById(R.id.diasSpin);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.dias_spinner, android.R.layout.simple_spinner_item);

// Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        diaSpinner.setAdapter(adapter1);



        final Spinner messpinner = (Spinner) findViewById(R.id.mesesSpin);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        messpinner.setAdapter(adapter);




        final Spinner anioSpinner = (Spinner) findViewById(R.id.anioSpin);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.anio_spinner, android.R.layout.simple_spinner_item);

// Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        anioSpinner.setAdapter(adapter2);




        final Spinner horasSpinner = (Spinner) findViewById(R.id.horaSpin);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.horas_Spinner, android.R.layout.simple_spinner_item);

// Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        horasSpinner.setAdapter(adapter3);


         tipoSpinner = (Spinner) findViewById(R.id.tipoGar);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.tipo_Spinner, android.R.layout.simple_spinner_item);

// Specify the layout to use when the list of choices appears
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        tipoSpinner.setAdapter(adapter4);

        mCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                userId = mAuth.getCurrentUser().getUid();
                mCiclistaReference = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(userId).child("ReservaIniciada");
                obtenerInfoCiclista();
            }
        });



        mRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ConfigurarReservaActivity.this, InformacionGaraje.class);
                startActivity(intent);
                finish();
                return;
            }
        });


        mReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_id = mAuth.getCurrentUser().getUid();
                //ir a la instancia para el nuevo usuario en firebase
                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(user_id).child("ReservaHecha");

                //obtiene los datos ingresados por el usuario en la IU
                String dia = diaSpinner.getSelectedItem().toString();
                String mes = messpinner.getSelectedItem().toString();
                String anio = anioSpinner.getSelectedItem().toString();
                String tipo = tipoSpinner.getSelectedItem().toString();
                String precio = mPrecio.getText().toString();
                String hora = horasSpinner.getSelectedItem().toString();



                //se crea un post (instancia en la Bd para un usuario)
                Map newPost = new HashMap();
                newPost.put("dia", dia); // se le da un username en el post que será mandado para registrar en la bd de firebase
                newPost.put("mes", mes);
                newPost.put("anio", anio);
                newPost.put("tipo", tipo);
                newPost.put("hora", hora);
                newPost.put("precio", precio);

                //se manda toda la info del Map a la bd para que se ingrese y registre al usuario con la nueva información
                current_user_db.setValue(newPost);
                Intent intent = new Intent(ConfigurarReservaActivity.this, PagoActivity.class);
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
                        mAnfitrionReference = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Anfitrion").child(garajeId);
                        obtenerPrecio();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private  void obtenerPrecio(){


        mAnfitrionReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String,Object>) dataSnapshot.getValue();

                    if(map.get("Precio") != null){

                        precio = map.get("Precio").toString();

                        precioT = Integer.parseInt(precio);

                       if(tipoSpinner.getSelectedItem().equals("Black") ) {

                          precioT = black + precioT;
                           mPrecio.setText("Precio: " + precioT);
                       }
                        if(tipoSpinner.getSelectedItem().equals("Plus") ) {

                            precioT = plus + precioT;
                            mPrecio.setText("Precio: " + precioT);
                        }

                        if(tipoSpinner.getSelectedItem().equals("Basico") ) {

                            precioT = basico + precioT;
                            mPrecio.setText("Precio: " + precioT);
                        }

                       // mPrecio.setText("Precio: " + precioT);




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
