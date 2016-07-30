package com.devpicon.android.milibreta.addNote;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.devpicon.android.milibreta.R;
import com.devpicon.android.milibreta.models.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by armando on 7/16/16.
 */
public class AddNoteDialogFragment extends DialogFragment {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Button buttonAdd;
    private Button buttonCancel;
    private EditText editTextNote;

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
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth = FirebaseAuth.getInstance();
                databaseReference = FirebaseDatabase.getInstance().getReference();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                //TODO: Corregir a  una mejor forma de obtener el timestamp
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(new Date());


                databaseReference.child("notes").push().setValue(
                        new Note(
                                currentUser.getDisplayName(),
                                editTextNote.getText().toString(),
                                currentUser.getUid(),
                                formattedDate,
                                currentUser.getPhotoUrl().toString()));
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
}
