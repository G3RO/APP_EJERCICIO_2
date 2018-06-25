package com.kevinsolano.firebase_tutorial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText mchildValueEditText;

    private Button mAnadirBut, mEliminarBut;

    private TextView mChildValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mchildValueEditText = (EditText) findViewById(R.id.childValueEditText);
        mAnadirBut = (Button) findViewById(R.id.anadir);
        mEliminarBut = (Button) findViewById(R.id.eliminar);
        mChildValueTextView = (TextView) findViewById(R.id.childValueTextView);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mRef = database.getReference("Gacy");

        mAnadirBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String childValue = mchildValueEditText.getText().toString();
                mRef.setValue(childValue);
            }
        });

        mEliminarBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String childValue = mchildValueEditText.getText().toString();

                mRef.removeValue();


            }
        });

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String childVal = String.valueOf(dataSnapshot.getValue());
                mChildValueTextView.setText(childVal);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
