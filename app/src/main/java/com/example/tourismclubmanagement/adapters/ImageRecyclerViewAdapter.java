package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.Image;

import java.util.List;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder> {
    private List<Image> imagesList;
    private ImageRecyclerViewAdapter.OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(Image image);

    }
    public void setOnItemClickListener(ImageRecyclerViewAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    public ImageRecyclerViewAdapter(List<Image> imagesList) {
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public ImageRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageRecyclerViewAdapter.ViewHolder holder, int position) {
        Image image = imagesList.get(position);
        Glide.with(holder.imageContainer.getContext()).load(image.getUri()).into(holder.imageContainer);
        int width = (int) (holder.itemView.getResources().getDisplayMetrics().widthPixels);
        holder.imageContainer.setMaxHeight(width/2);
        holder.imageContainer.setMinimumHeight(width/2);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(image);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageContainer = itemView.findViewById(R.id.imageContainer);
        }

    }
    public void updateImagesList(List<Image> images){
        this.imagesList = images;
        notifyDataSetChanged();
    }
}
