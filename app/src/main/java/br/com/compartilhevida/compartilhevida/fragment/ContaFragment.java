package br.com.compartilhevida.compartilhevida.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.compartilhevida.compartilhevida.BuildConfig;
import br.com.compartilhevida.compartilhevida.LoginActivity;
import br.com.compartilhevida.compartilhevida.R;
import br.com.compartilhevida.compartilhevida.models.Usuario;
import br.com.compartilhevida.compartilhevida.util.CircleTransform;

import static android.app.Activity.RESULT_OK;


public class ContaFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private Button btnSendResetEmail, btnRemoveUser,
            signOut;

    private EditText email;
    private ProgressBar progressBar;
    private FirebaseUser userFirebase;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mUserDatabase;
    private ImageView photoPerfil;
    private FloatingActionButton floatingActionButtonCamera;
private Uri currentUri;
    public String mCurrentPhotoPath;
    private final int CAMERA_REQUEST_CODE = 100;
    // Request code for runtime permissions
    private final int REQUEST_CODE_STORAGE_PERMS = 321;
    private static final int PICK_IMAGE_REQUEST = 234;

    private OnFragmentInteractionListener mListener;

    public ContaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewConta = inflater.inflate(R.layout.fragment_conta, container, false);
        //recupera componentes
        userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        btnSendResetEmail = (Button) viewConta.findViewById(R.id.sending_pass_reset_button);
        btnRemoveUser = (Button) viewConta.findViewById(R.id.remove_user_button);
        signOut = (Button) viewConta.findViewById(R.id.sign_out);
        email = (EditText) viewConta.findViewById(R.id.email);
        progressBar = (ProgressBar) viewConta.findViewById(R.id.progressBar);
        photoPerfil = (ImageView) viewConta.findViewById(R.id.photo_perfil);
        floatingActionButtonCamera = (FloatingActionButton) viewConta.findViewById(R.id.floatingButtonCamera);

        //instacia do usuário no firebase
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userFirebase.getUid());

        //visibilidade dos componentes
        email.setVisibility(View.GONE);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (Usuario.getInstance().getProvider() != null) {
            if (!Usuario.getInstance().getProvider().equalsIgnoreCase("email")) {
                btnSendResetEmail.setVisibility(View.GONE);
            }
        } else {
            btnSendResetEmail.setVisibility(View.GONE);
        }
        //carrega a foto do usuário
        if (userFirebase.getPhotoUrl() != null) {
            final Uri uri = userFirebase.getPhotoUrl();
            Glide.with(getActivity()).load(uri).transform(new CircleTransform(getActivity())).into(photoPerfil);
        }
        //listner for button
        btnSendResetEmail.setOnClickListener(this);
        btnRemoveUser.setOnClickListener(this);
        signOut.setOnClickListener(this);
        floatingActionButtonCamera.setOnClickListener(this);
        return viewConta;
    }

    //sign out method
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingButtonCamera:
                PopupMenu popup = new PopupMenu(getActivity(), floatingActionButtonCamera);
                //Inflating the Popup using xml file  
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener  
                popup.setOnMenuItemClickListener(this);
                popup.show();
                break;
            case R.id.sending_pass_reset_button:
                email.setVisibility(View.VISIBLE);
                break;
            case R.id.sign_out:
                signOut();
                break;
            case R.id.remove_user_button:
                progressBar.setVisibility(View.VISIBLE);
                mUserDatabase.removeValue();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Seu perfil foi excluído!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(getActivity(), "Ops, aconteceu uma falha ao tentar excluir seu perfil. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    if (!hasPermissions()){
                        // your app doesn't have permissions, ask for them.
                        requestNecessaryPermissions();
                    }
                    else {
                        // your app already have permissions allowed.
                        // do what you want.
                        dispatchTakePictureIntent();
                    }

                } else {
                    Toast.makeText(getActivity(), "Camera não suportada.", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.galeria:
                showFileChooser();
                break;
            default:
                break;
        }
        return true;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    private boolean hasPermissions() {
        int res = 0;
        // list all permissions which you want to check are granted or not.
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String perms : permissions){
            res = getActivity().checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                // it return false because your app dosen't have permissions.
                return false;
            }

        }
        // it return true, your app has permissions.
        return true;
    }

    private void requestNecessaryPermissions() {
        // make array of permissions which you want to ask from user.
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // have arry for permissions to requestPermissions method.
            // and also send unique Request code.
            requestPermissions(permissions, REQUEST_CODE_STORAGE_PERMS);
        }
    }

    /* when user grant or deny permission then your app will check in
      onRequestPermissionsReqult about user's response. */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandResults) {
        // this boolean will tell us that user granted permission or not.
        boolean allowed = true;
        switch (requestCode) {
            case REQUEST_CODE_STORAGE_PERMS:
                for (int res : grandResults) {
                    // if user granted all required permissions then 'allowed' will return true.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                // if user denied then 'allowed' return false.
                allowed = false;
                break;
        }
        if (allowed) {
            // if user granted permissions then do your work.
            dispatchTakePictureIntent();
        }
        else {
            // else give any custom waring message.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(getActivity(), "Permissões de acesso a camera negada!", Toast.LENGTH_SHORT).show();
                }
                else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(getActivity(), "Permissões de acesso a Storage negada!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("lpl", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
    private void setPic() throws Exception {
        if (currentUri!=null){
            Glide.with(getActivity()).load(currentUri).transform(new CircleTransform(getActivity())).into(photoPerfil);
        }else{
            Glide.with(getActivity()).load(mCurrentPhotoPath).transform(new CircleTransform(getActivity())).into(photoPerfil);
        }
        uploadFile();
    }


    public File createImageFile() throws IOException {
        // Create an image file name
        String folderName = "CompartilheVida";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File f = new File(Environment.getExternalStorageDirectory(), folderName);
        if (!f.exists()) {
            f.mkdirs();
        }
        File storageDir = new File(Environment.getExternalStorageDirectory()+"/"+folderName);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                currentUri = null;
                setPic();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mCurrentPhotoPath = data.getData().getPath();
            currentUri = data.getData();
            try {
                setPic();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private void uploadFile() {
        //if there is a file to upload
        if (mCurrentPhotoPath != null) {
            Uri file = Uri.fromFile(new File(mCurrentPhotoPath));
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageReference riversRef;
            if (currentUri!=null){
                 riversRef = mStorageRef.child(mAuth.getCurrentUser().getUid() + "/" + currentUri.getLastPathSegment());
                file = currentUri;
            }else{
                 riversRef = mStorageRef.child(mAuth.getCurrentUser().getUid() + "/" + file.getLastPathSegment());
            }

            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            @SuppressWarnings("VisibleForTests")
                            Uri ui = taskSnapshot.getDownloadUrl();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(ui)
                                    .build();
                            mAuth.getCurrentUser().updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Imagem alterada. ", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }
}

