package com.noboteco.noboteco;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVAdapter_Feed extends RecyclerView.Adapter<RVAdapter_Feed.FeedProfileViewHolder> {
    List<FeedProfile> mFeedProfiles;

    RVAdapter_Feed(List<FeedProfile> profilesList){
        this.mFeedProfiles = profilesList;
    }

    @Override
    public int getItemCount() {
        return mFeedProfiles.size();
    }

    @NonNull
    @Override
    public FeedProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_dados_feed, parent, false);
        FeedProfileViewHolder fpvh = new FeedProfileViewHolder(v);
        return fpvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedProfileViewHolder holder, int position) {
        holder.username.setText(mFeedProfiles.get(position).username);
        holder.noBarHa.setText(mFeedProfiles.get(position).noBarHa);
        holder.avatar.setImageDrawable(mFeedProfiles.get(position).avatar);
        holder.fav_bebida.setImageDrawable(mFeedProfiles.get(position).fav_cerveja);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class FeedProfileViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView username;
        TextView noBarHa;
        ImageView avatar;
        ImageView fav_bebida;

        FeedProfileViewHolder(View itemView){
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            cv.setRadius(35);
            username = itemView.findViewById(R.id.username);
            noBarHa = itemView.findViewById(R.id.noBarHa);
            avatar = itemView.findViewById(R.id.avatar);
            fav_bebida = itemView.findViewById(R.id.fav_cerveja);
        }
    }
}
