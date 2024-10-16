package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ppproperti.simarco.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProductKnowledgeImageAdapter extends RecyclerView.Adapter<ProductKnowledgeImageAdapter.ViewHolder> {
    private final String TAG = ProductKnowledgeImageAdapter.class.getSimpleName();
    private Context context;
    private List<File> list = new ArrayList<>();

    public ProductKnowledgeImageAdapter(Context context, List<File> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_pk, parent, false);
        Log.d(TAG, "onCreateViewHolder:");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        File file = list.get(position);
        Log.d(TAG, "onBindViewHolder:"+file.getAbsolutePath());

        if(file.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            holder.imageView.setImageBitmap(myBitmap);
//            Glide.with(context).load(file).into(holder.imageView);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            imageView = view.findViewById(R.id.image_view);
        }
    }
}
