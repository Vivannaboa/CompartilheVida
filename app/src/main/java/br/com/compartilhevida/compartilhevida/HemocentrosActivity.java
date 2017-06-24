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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private static final String TAG = "HemocentrosActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 10;
    public SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Location myLocation;
    private Location mLastLocation;
    private LatLng mLocation = null;
    private String mLatitude, mLongitude;
    private DatabaseReference mDatabase;
    private List<Hemocentro> hemocentroList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hemocentros);
        // obtem o mapa para o fragmento e aguarda as notificações quando o mapa estiver pronto para ser usado.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Cria uma instância do GoogleAPIClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //pega a refêrencia do banco de dados
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Criando um listner para buscad dados do banco
        //addListenerForSingleValueEvent esse listener busca apenas uma vez os dados, não fica observando se teve mudanças
        mDatabase.child("hemocentros").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    hemocentroList.clear();
                    for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                        //converte o json em objeto
                        Hemocentro hemocentro = suggestionSnapshot.getValue(Hemocentro.class);
                        hemocentro.setUid(suggestionSnapshot.getKey());
                        //adiciona a lista
                        hemocentroList.add(hemocentro);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "carregou os dados do banco");
                getLocation();
                adicionarMarcadores();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "O carregamento dos Dados foi cancelado");
            }
        });

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        Log.i(TAG, "Passou no onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        Log.i(TAG, "Passou no OnStop");
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "Terminou de carregar o mapa");
        mMap = googleMap;
        //configura o mapa e pede permisão ao usuário para acessar a localização se ainda não possuir
        // setUpMap();
    }

    public void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_LOCATION);
            Log.i(TAG, "Pediu permissão ao usuário");
            return;
        } else {
            //habilita o botão para Mylocstion
            mMap.setMyLocationEnabled(true);
            Log.i(TAG, "Já tem permissão ao usuário");
        }
    }

    //pega o retorno da mensagem de permissão e se o usuário não consedeu permissão avisa que não vai conseguir proceguir
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_LOCATION);
                        return;
                    } else {
                        getLocation();
                        adicionarMarcadores();
                    }
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(this).
                                setTitle("Permissão para acessar sua localização").
                                setMessage("Você precisa conceder permissão para acessar a sua localização." +
                                        "Concedendo esse acesso podemos melhorar as sugestõs de hemocentros próximos a você!").show();
                    } else {
                        new AlertDialog.Builder(this).
                                setTitle("Permissão de acesso a localização não concedida").
                                setMessage("Infelizmente não será possivel encontrar os hemocentros próximos a você sem a permissão de localização").show();
                    }
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void adicionarMarcadores() {
        LatLng mLocation2 = null;
        try {
            //percore a lista de hemocentros
            for (Hemocentro hemocentro : hemocentroList) {
                if (hemocentro.getLat() != 0.0 && hemocentro.getLng() != 0.0) {//se não tem lat/lng tem que procurar pelo endereços
                    mLocation2 = new LatLng(hemocentro.getLat(), hemocentro.getLng());
                } else {
                    if (hemocentro.getEndereco() != null) {
//                        if (!hemocentro.getEndereco().isEmpty()) {
//                            mLocation2 = pegaCoordenadaDoEndereco(hemocentro.getEndereco());
//                        }
                    }
                    if (mLocation2 != null) {
                        hemocentro.setLat(mLocation2.latitude);
                        hemocentro.setLng(mLocation2.longitude);
                        updateHemocentro(hemocentro);
                    }
                }
                if (mLocation != null && mLocation2 != null) {
                    MarkerOptions markerOptions = new MarkerOptions();//cria uma nova configuração para o marcador
                    markerOptions.position(mLocation2);//passa a posição
                    if (hemocentro.getNome() != null) {
                        markerOptions.title(hemocentro.getNome());//coloca o nome do ponto
                    }
                    mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(mLocation));//atualiza avisualização
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
        Log.i(TAG, "Adicionou os hemocentros no mapa");
    }

    //atualiza os dados do hemocentro
    private void updateHemocentro(Hemocentro hemocentro) {
        Map<String, Object> postValues = hemocentro.toMap();
        mDatabase.child("hemocentros").child(hemocentro.getUid()).updateChildren(postValues);
    }

    //decolve as cordenadas de um endereço
    private LatLng pegaCoordenadaDoEndereco(String endereco) {
        try {
            Geocoder geocoder = new Geocoder(this);

            List<Address> resultados = geocoder.getFromLocationName(endereco, 1);

            if (!resultados.isEmpty()) {
                LatLng posicao = new LatLng(resultados.get(0).getLatitude(),
                        resultados.get(0).getLongitude());
                return posicao;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_LOCATION);
            Log.i(TAG, "Pediu permissão ao usuário");
            return;

        }
        try{


        mMap.setMyLocationEnabled(true);
        //pega a localização atual
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            mLocation = new LatLng(-15.7217175, -48.0783246);
            Log.i(TAG,"Passou");
        }else {
            Log.i(TAG, "Adicionou a minha localização");
            mLatitude = String.valueOf(mLastLocation.getLatitude());
            mLongitude = String.valueOf(mLastLocation.getLongitude());
            // Add a marker in Sydney and move the camera
            mLocation = new LatLng(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));
        }
        MarkerOptions mMarkerOptions = new MarkerOptions();
        mMarkerOptions.position(mLocation);
        mMarkerOptions.title("Minha Localização");
        mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.addMarker(mMarkerOptions);
            Log.i(TAG,mLocation.toString());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(mLocation));

        // Zoom animando a câmera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Diminuindo o zoom e aumentado o nível para 10, animando com uma duração de 2 segundos.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(30), 2000, null);

        // contruindo uma CameraPosition com foco Mountain View.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mLocation)      // Define o centro do mapa para Mountain View
                .bearing(10)                // Define a orientação da câmera para o leste
                .tilt(5)                   // Define a inclinação da câmera para 30 graus
                .build();                   // chama o conrturor
        //passa a posição para o mapa
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(7));
            Log.i(TAG,cameraPosition.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
