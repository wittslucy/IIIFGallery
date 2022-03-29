package com.lwitts.iiifgallery;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder> {

    public ArrayList<ImageData> imageDataArrayList;
    private Context iContext;

    public ImageViewAdapter(ArrayList<ImageData> imageDataArrayList, Context iContext){
        this.imageDataArrayList = imageDataArrayList;
        this.iContext = iContext;
    }

    @NonNull
    @Override
    public ImageViewAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewAdapter.ImageViewHolder holder, int position) {
        ImageData imageData = imageDataArrayList.get(position);
        holder.imageTextView.setText(imageData.getTitle());
        //holder.imageImageView.setImageURI(Uri.parse(imageData.getImgId()));
        Glide.with(iContext)
                .load(imageData.getImgId())
                .apply(new RequestOptions().override(300,300))
                .into(holder.imageImageView);
    }

    @Override
    public int getItemCount() {
        //int limit = 5;
        //return Math.min(imageDataArrayList.size(), limit);

        return imageDataArrayList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView imageTextView;
        private ImageView imageImageView;

        public ImageViewHolder(@NonNull View itemView){
            super(itemView);
            imageTextView = itemView.findViewById(R.id.image_text_view_id);
            imageImageView = itemView.findViewById(R.id.image_image_view_id);
        }

    }

}
