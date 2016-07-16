package com.devpicon.android.milibreta.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devpicon.android.milibreta.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by armando on 7/15/16.
 */
public class NoteViewHolder extends RecyclerView.ViewHolder {
    private View view;

    public NoteViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void setText(String text) {
        TextView textViewName = (TextView) view.findViewById(R.id.txt_item_note);
        textViewName.setText(text);
    }

    public void setName(String name) {
        TextView textViewName = (TextView) view.findViewById(R.id.txt_item_name);
        textViewName.setText(name);
    }

    public void setTimestamp(String timestamp) {
        TextView textViewTimestamp = (TextView) view.findViewById(R.id.txt_item_timestamp);
        textViewTimestamp.setText(timestamp);
    }

    public void setImage(String url) {
        ImageView imageViewAvatar = (ImageView) view.findViewById(R.id.img_item_avatar);
        Glide.with(view.getContext())
                .load(url)
                .asBitmap()
                .transform(new CropCircleTransformation(view.getContext()))
                .into(imageViewAvatar);
    }

}
