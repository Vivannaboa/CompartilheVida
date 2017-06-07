package br.com.compartilhevida.compartilhevida;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.compartilhevida.compartilhevida.models.Hemocentro;

import static br.com.compartilhevida.compartilhevida.LoginActivity.mGoogleApiClient;

public class HemocentrosActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "HemocentroActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 10;
    private GoogleMap mMap;
    private Location myLocation;
    private Location mLastLocation;
    private String mLatitude, mLongitude;
    private DatabaseReference mDatabase;
    private List<Hemocentro> hemocentroList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hemocentros);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Criando um listner para buscad dados do banco
        mDatabase.child("hemocentros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                hemocentroList.clear();
                int i = 0;
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                    Hemocentro hemocentro = suggestionSnapshot.getValue(Hemocentro.class);
                    hemocentro.setUid(suggestionSnapshot.getKey());
                    hemocentroList.add(hemocentro);
                    Log.i(TAG, "banco: " + i++);
                }
                Log.i(TAG, "carregou os dados do banco");
                adivionarMarcadores();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    public void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_LOCATION);
            return;
        } else {
            mMap.setMyLocationEnabled(true);
//            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_LOCATION);
                        return;
                    }
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(this).
                                setTitle("Permissão para acessar sua localização").
                                setMessage("Você precisa conceder permissão para acessar a sua localização." +
                                        "Concedendo esse acesso podemos melhorar as sugestõs de hemocentros proximos a você!").show();
                    } else {
                        new AlertDialog.Builder(this).
                                setTitle("Permissão de de acesso a localizção não concedida").
                                setMessage("Infelizmente não será possivel encontrar os hemocentros próximos a você sem a permissão de localização").show();
                    }
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_LOCATION);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = String.valueOf(mLastLocation.getLatitude());
            mLongitude = String.valueOf(mLastLocation.getLongitude());
            // Add a marker in Sydney and move the camera
            LatLng mLocation = new LatLng(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));
            mMap.addMarker(new MarkerOptions().position(mLocation).title("Minha Localização"));

            mMap.animateCamera(CameraUpdateFactory.newLatLng(mLocation));

            // Zoom in, animating the camera.
            mMap.animateCamera(CameraUpdateFactory.zoomIn());

            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(mLocation)      // Sets the center of the map to Mountain View
                    .zoom(5)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            Log.i(TAG, "Adicinando os pinos no mapa");

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void adivionarMarcadores() {
        LatLng mLocation2;
        try {
            int i = 0;
            for (Hemocentro hemocentro : hemocentroList) {
                if (hemocentro.getLng()==0 && hemocentro.getLat()==0){
                    mLocation2 = pegaCoordenadaDoEndereco(hemocentro.getEndereco());
                }else{
                    mLocation2 = new LatLng(hemocentro.getLat(),hemocentro.getLat());
                    updateHemocentro(hemocentro);
                }
                mMap.addMarker(new MarkerOptions().position(mLocation2).title(String.valueOf(i++)));
            }

        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    private void updateHemocentro(Hemocentro hemocentro){
        Map<String, Object> postValues = hemocentro.toMap();
        mDatabase.child("hemocentros").child(hemocentro.getUid()).updateChildren(postValues);
    }

    private LatLng pegaCoordenadaDoEndereco(String endereco) {
        try{
            Geocoder geocoder = new Geocoder(this);

            List<Address> resultados = geocoder.getFromLocationName(endereco, 1);

            if(!resultados.isEmpty()){
                LatLng posicao = new LatLng(resultados.get(0).getLatitude(),
                        resultados.get(0).getLongitude());
                return posicao;
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
