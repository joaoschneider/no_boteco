package com.noboteco.noboteco;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVAdapter_Profile extends RecyclerView.Adapter<RVAdapter_Profile.ProfileBeersViewHolder> {
    List<Integer> cervejasAvaliadas;

    RVAdapter_Profile(List<Integer> cevas){
        this.cervejasAvaliadas = cevas;
    }

    @NonNull
    @Override
    public ProfileBeersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listadecerveja, parent, false);
        ProfileBeersViewHolder pbvh = new ProfileBeersViewHolder(v);
        return pbvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileBeersViewHolder holder, int position) {
        int cerveja = cervejasAvaliadas.get(position);
        holder.cerveja.setImageResource(cerveja);
    }

    @Override
    public int getItemCount() {
        return cervejasAvaliadas.size();
    }

    public static class ProfileBeersViewHolder extends RecyclerView.ViewHolder{
        ImageView cerveja;

        public ProfileBeersViewHolder(@NonNull View itemView) {
            super(itemView);
            cerveja = itemView.findViewById(R.id.cerveja);
        }
    }
}
