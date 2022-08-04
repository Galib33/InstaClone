package com.galib.instaclone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galib.instaclone.databinding.RecyclerrowBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    ArrayList<Details> detailsArrayList;

    public Adapter(ArrayList<Details> detailsArrayList) {
        this.detailsArrayList = detailsArrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerrowBinding recyclerrowBinding=RecyclerrowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new Holder(recyclerrowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.binding.EmailTextfromrecycler.setText(detailsArrayList.get(position).email);
        Picasso.get().load(detailsArrayList.get(position).url).into(holder.binding.imageId);
        holder.binding.commenttextfromRecycler.setText(detailsArrayList.get(position).comment);

    }

    @Override
    public int getItemCount() {
        return detailsArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        private RecyclerrowBinding binding;

        public Holder(RecyclerrowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
