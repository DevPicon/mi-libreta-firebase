package com.devpicon.android.milibreta.addNote;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.devpicon.android.milibreta.Constants;
import com.devpicon.android.milibreta.R;
import com.devpicon.android.milibreta.app.MiLibretaApplication;
import com.devpicon.android.milibreta.models.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by armando on 7/16/16.
 */
public class AddNoteDialogFragment extends DialogFragment implements
        EasyPermissions.PermissionCallbacks {


    private static final String TAG = AddNoteDialogFragment.class.getSimpleName();

    private static final int RC_TAKE_PICTURE = 101;
    private static final int RC_STORAGE_AND_CAMERA_PERMS = 102;
    private static final int RC_CAMERA_PERMS = 103;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Button buttonAdd;
    private Button buttonCancel;
    private EditText editTextNote;
    private ImageButton buttonCamera;


    private Uri fileUri = null;
    private Uri downloadUrl = null;

    private SharedPreferences sharedPreferences;

    private DatabaseReference notesDatabaseReference;
    private StorageReference photoStorageReference;
    private ProgressDialog progressDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_add_note, container, false);
        buttonAdd = (Button) view.findViewById(R.id.btn_ok);
        buttonCancel = (Button) view.findViewById(R.id.btn_cancel);
        editTextNote = (EditText) view.findViewById(R.id.edt_note);
        buttonCamera = (ImageButton) view.findViewById(R.id.btn_camera);


        MiLibretaApplication application = (MiLibretaApplication) getActivity().getApplicationContext();
        photoStorageReference = application.getPhotoStorageReference();
        notesDatabaseReference = application.getChildNoteReference();
        sharedPreferences = getActivity()
                .getApplication()
                .getSharedPreferences(Constants.MI_LIBRETA_PREFERENCES, Context.MODE_PRIVATE);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addNote();
                dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @AfterPermissionGranted(RC_STORAGE_AND_CAMERA_PERMS)
    private void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Check that we have permission to read images from external storage.
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !EasyPermissions.hasPermissions(getActivity(), permissions)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage_camera),
                    RC_STORAGE_AND_CAMERA_PERMS, permissions);
            return;
        }


        // Create intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Choose file storage location
        File file = new File(Environment.getExternalStorageDirectory(), UUID.randomUUID().toString() + ".jpg");
        fileUri = Uri.fromFile(file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // Launch intent
        startActivityForResult(takePictureIntent, RC_TAKE_PICTURE);

    }

    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        final StorageReference photoReference = photoStorageReference.child(fileUri.getLastPathSegment());
        showProgressDialog();
        Log.d(TAG, "uploadFromUri:dst:" + photoReference.getPath());
        photoReference.putFile(fileUri).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "uploadFromUri:onFailure", e);
                downloadUrl = null;
                hideProgressDialog();
                String message = "Error: upload failed";
                showMessageToast(message);

            }
        }).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "uploadFromUri:onSuccess");
                // Get the public download URL
                downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                savePictureUrlAsANote(downloadUrl);
                showMessageToast("upload completed! " + downloadUrl.toString());
                hideProgressDialog();
                dismiss();
            }
        });
    }

    private void savePictureUrlAsANote(Uri downloadUrl) {

        String formattedDate = getFormattedDate();

        Note note = new Note(
                sharedPreferences.getString(Constants.PREFERENCE_DISPLAY_NAME, "Desconocido"),
                "Foto",
                downloadUrl.toString(),
                sharedPreferences.getString(Constants.PREFERENCE_UID, null),
                formattedDate,
                sharedPreferences.getString(Constants.PREFERENCE_PHOTO_URI, ""));

        notesDatabaseReference.push().setValue(note);
    }

    private void addNote() {
        //TODO: Corregir a  una mejor forma de obtener el timestamp
        String formattedDate = getFormattedDate();

        Note note = new Note(
                sharedPreferences.getString(Constants.PREFERENCE_DISPLAY_NAME, "Desconocido"),
                editTextNote.getText().toString(),
                sharedPreferences.getString(Constants.PREFERENCE_UID, null),
                formattedDate,
                sharedPreferences.getString(Constants.PREFERENCE_PHOTO_URI, ""));


        notesDatabaseReference.push().setValue(note);
    }

    private String getFormattedDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == getActivity().RESULT_OK) {
                if (fileUri != null) {
                    Log.d(TAG, "Taking picture succeded");
                    uploadFromUri(fileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                showMessageToast("Taking picture failed.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private void showMessageToast(String message) {
        Toast.makeText(getActivity(), message,
                Toast.LENGTH_SHORT).show();
    }

    protected void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Cargando...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
    }

    protected void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
