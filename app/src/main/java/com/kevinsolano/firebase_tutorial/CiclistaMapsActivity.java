package com.kevinsolano.firebase_tutorial;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CiclistaMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private FirebaseAuth mAuth;
    LocationRequest mLocationRequest;
    SupportMapFragment mapFragment;
    private Button mBack, mRequest;
    private LatLng ubicacionRequerida, ubicacionGaraje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ciclista_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.Mapa);
        mBack = (Button) findViewById(R.id.back);
        mRequest = (Button) findViewById(R.id.request);
        mAuth = FirebaseAuth.getInstance();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CiclistaMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }else{

            mapFragment.getMapAsync(this);
        }


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CiclistaMapsActivity.this, MenuDrawerActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UbicacionRequerida");

                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                ubicacionRequerida = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(ubicacionRequerida).title("Estas Aquí").icon(BitmapDescriptorFactory.fromResource(R.mipmap.bicicleta)));

               // mRequest.setText("Mejores opciones...");
                //mRequest.setEnabled(true);

                obtenerGarajesCercanos();

            }
        });

    }

    private int radio = 1;
    private Boolean garajeEncontrado = false;
    private String garajeId;

    public void obtenerGarajesCercanos(){

        final DatabaseReference garajeUbicacion = FirebaseDatabase.getInstance().getReference().child("AnfitrionDisponible");

        GeoFire geoFire = new GeoFire(garajeUbicacion);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(ubicacionRequerida.latitude,ubicacionRequerida.longitude), radio);
        //GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(19.33111,-99.183335), radio);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if(!garajeEncontrado) {
                    garajeEncontrado = true;
                    garajeId = key;


                    DatabaseReference anfitrionRef = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Anfitrion").child(garajeId);
                    String ciclistaId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map = new HashMap();
                    map.put("ciclistaReservaId", ciclistaId);
                    anfitrionRef.updateChildren(map);
                    obtenerLocalizacionGaraje();

                    mRequest.setText("Ver Garaje");




                    //ubicacionGaraje = new LatLng(19.34111,-99.183335);
                    //mMap.addMarker(new MarkerOptions().position(ubicacionGaraje).title("Garaje Kevin"));
                    //mRequest.setText("Selecciona el garaje que más te guste");

                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                if(!garajeEncontrado){

                    radio++;
                    obtenerGarajesCercanos();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private Marker garajeMarker;
    private void obtenerLocalizacionGaraje(){

        DatabaseReference anfitrionLocRef = FirebaseDatabase.getInstance().getReference().child("AnfitrionDisponible").child(garajeId).child("l");
        anfitrionLocRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double localizacionLat = 0;
                    double localizacionLng = 0;
                    mRequest.setText("Garaje encontrado!");
                    if(map.get(0) != null){

                        localizacionLat = Double.parseDouble(map.get(0).toString());

                    }
                    if(map.get(0) != null){

                        localizacionLng = Double.parseDouble(map.get(1).toString());

                    }

                    LatLng anfitrionLoc = new LatLng(localizacionLat,localizacionLng);
                    if(garajeMarker != null){

                        garajeMarker.remove();

                    }

                    Location loc1 = new Location("");
                    loc1.setLatitude(ubicacionRequerida.latitude);
                    loc1.setLongitude(ubicacionRequerida.longitude);
                    Location loc2 = new Location("");
                    loc2.setLatitude(anfitrionLoc.latitude);
                    loc2.setLongitude(anfitrionLoc.longitude);


                    float distancia = loc1.distanceTo(loc2);

/*                    if(distancia <= 50.0){

                        mRequest.setText("Estas a unos metros de llegar!");

                    }else {
                        mRequest.setText("Garaje encontrado: " + String.valueOf(distancia) + " metros");
                    }
*/
                    garajeMarker = mMap.addMarker(new MarkerOptions().position(anfitrionLoc).title("tu Garaje").icon(BitmapDescriptorFactory.fromResource(R.mipmap.garaje)));
                    mRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //obtener un id para el usuario
                            String user_id = mAuth.getCurrentUser().getUid();
                            //ir a la instancia para el nuevo usuario en firebase
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Usuario").child("Ciclista").child(user_id).child("ReservaIniciada");


                            //se crea un post (instancia en la Bd para un usuario)
                            Map newPost = new HashMap();
                            newPost.put("ReservaIniciadaId", garajeId); // se le da un username en el post que será mandado para registrar en la bd de firebase


                            //se manda toda la info del Map a la bd para que se ingrese y registre al usuario con la nueva información
                            current_user_db.setValue(newPost);
                            Intent intent = new Intent(CiclistaMapsActivity.this, InformacionGaraje.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    });
                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CiclistaMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        buidGoogleApiClient();
        mMap.setMyLocationEnabled(true);

        /*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(19.340407, -99.183335);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Garaje Kevingio"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
    }

    protected synchronized void buidGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        LatLng lastLng = new LatLng(location.getLatitude(), location.getLongitude());

        //poner la posición en medio de la pantalla siempre

        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

         }


    @Override
    public void onConnected(@Nullable Bundle bundle) {


        //cuando el mapa es llamado y esta listo para mostrarse
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //intervalos de mil milisegundos (un segundo)
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CiclistaMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    final int LOCATION_REQUEST_CODE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){

            case LOCATION_REQUEST_CODE:{

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    mapFragment.getMapAsync(this);
                }else{

                    Toast.makeText(getApplicationContext(), "Por favor acepta los permisos para acceder a tu ubicación",Toast.LENGTH_LONG).show();

                }

                break;

            }

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }


}