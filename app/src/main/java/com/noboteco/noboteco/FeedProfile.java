package com.noboteco.noboteco;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;

class FeedProfile {
    public RoundedBitmapDrawable avatar;
    public String username;
    public String noBarHa;
    File _avatarCache;

    FeedProfile(String un, String nbh, RoundedBitmapDrawable d){
        this.avatar = d;
        this.username = un;
        this.noBarHa = nbh;

    }

}
