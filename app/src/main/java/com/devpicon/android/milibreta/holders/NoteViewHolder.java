package com.devpicon.android.milibreta.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by armando on 7/15/16.
 */
public class NoteViewHolder extends RecyclerView.ViewHolder {
    private View view;

    public NoteViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void setText(String text){
        TextView textViewName = (TextView) view.findViewById(android.R.id.text1);
        textViewName.setText(text);
    }

    public void setName(String name){
        TextView textViewName = (TextView) view.findViewById(android.R.id.text2);
        textViewName.setText(name);
    }

}
