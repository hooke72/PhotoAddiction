package com.example.hooke.photoaddiction.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hooke.photoaddiction.R;
import com.example.hooke.photoaddiction.models.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    List<Photo> photos;
    View view;

    public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView photoCV;
        TextView timeTV;
        ImageView photoIV;

        PhotoViewHolder(View itemView) {
            super(itemView);
            photoCV = (CardView) itemView.findViewById(R.id.photo_cardview);
            photoCV.setOnClickListener(this);
            timeTV = (TextView) itemView.findViewById(R.id.photo_time_textview);
            photoIV = (ImageView) itemView.findViewById(R.id.photo_imageview);
        }

        @Override
        public void onClick(View v) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("image/jpeg");
            sendIntent.putExtra(Intent.EXTRA_STREAM, photos.get(getPosition()).getUri());
            view.getContext().startActivity(Intent.createChooser(sendIntent, "Share Image"));
        }
    }

    public PhotoAdapter(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photoview_item, viewGroup, false);
        PhotoViewHolder photoVH = new PhotoViewHolder(view);
        return photoVH;
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder photoViewHolder, final int i) {
        photoViewHolder.timeTV.setText(photos.get(i).getTime());
        Picasso.get().load(photos.get(i).getUri()).resize(512, 512).centerCrop().into(photoViewHolder.photoIV);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
