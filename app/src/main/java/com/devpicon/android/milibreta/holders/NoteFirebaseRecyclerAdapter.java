package com.devpicon.android.milibreta.holders;

import com.devpicon.android.milibreta.R;
import com.devpicon.android.milibreta.models.Note;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by armando on 7/15/16.
 */
public class NoteFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Note, NoteViewHolder> {

    DatabaseReference databaseReference;

    public NoteFirebaseRecyclerAdapter(DatabaseReference ref){
        super(Note.class, R.layout.item_note, NoteViewHolder.class, ref);
        databaseReference = ref;
    }

    @Override
    protected void populateViewHolder(NoteViewHolder viewHolder, Note model, int position) {
        viewHolder.setText(model.getText());
        viewHolder.setName(model.getName());
        viewHolder.setTimestamp(model.getTimestamp());
        viewHolder.setImage(model.getUserImageUrl());
    }
}
