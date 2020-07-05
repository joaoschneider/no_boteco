package com.noboteco.noboteco;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVAdapter_Menu extends RecyclerView.Adapter<RVAdapter_Menu.MyViewHolder> {
    List<FeedMenu> cervejas;

    RVAdapter_Menu(List<FeedMenu> cevas){
        this.cervejas = cevas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_bar_itens, parent, false);
        MyViewHolder myvh = new MyViewHolder(v);
        return myvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.Foto.setImageResource(cervejas.get(position).photoId);
        holder.Nome.setText(cervejas.get(position).name);
        holder.Quantidade.setText(cervejas.get(position).qtd);
        Log.d("Debug","nomea" + cervejas.get(position).name);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return cervejas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView Foto;
        TextView Nome;
        TextView Quantidade;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Foto = itemView.findViewById(R.id.fotoceva);
            Nome = itemView.findViewById(R.id.nomeceva);
            Quantidade = itemView.findViewById(R.id.qntceva);
        }
    }
}
