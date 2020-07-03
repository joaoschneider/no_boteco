package com.noboteco.noboteco;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class perfil extends AppCompatActivity {
    String mUid;
    File mAvatar;
    DocumentSnapshot mUserInfo;
    FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        //Buscar uid enviado a através do Intent recebido
        mUid = getIntent().getStringExtra("uid");
        String origem = getIntent().getStringExtra("from");
        assert origem != null;
        fromWhichActivity(origem);
        mFirestore = FirebaseFirestore.getInstance();
        getUserInfo();
        ImageView avatar = findViewById(R.id.avatar_usuario);
        //Setando avatar no ImageView
        Bitmap avatarBmp = BitmapFactory.decodeFile(mAvatar.getPath());
        avatar.setImageBitmap(avatarBmp);

    }


    /*
    Método responsavel por mostrar aviso ao usuario apos login ou cadastro + login
     */
    private void fromWhichActivity(String origem){
        if(origem.equals("cadastro")){
            Toast.makeText(perfil.this, "Sucesso ao efetuar cadastro! Você já está logado na plataforma, aproveite!", Toast.LENGTH_LONG).show();
        }else if(origem.equals("login")){
            Toast.makeText(perfil.this, "Aproveite a plataforma!", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    Método responsavel por buscar o documento do usuario no Firestore, onde estarão guardadas as
    informações de preferências e UID para buscar avatar
     */
    private void getUserInfo(){
        mFirestore.document("users/" + mUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mUserInfo = documentSnapshot;
                        Toast.makeText(perfil.this, "Informações atualizadas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(perfil.this, "Falha ao buscar informações...", Toast.LENGTH_SHORT).show();
                    }
                });
        mAvatar = new File(getCacheDir() + "avatar.jpg");
        //Após buscar mUid, buscar o avatar no Storage (nome do arquivo é mUid.jpg)
        getUserAvatar();
    }

    /*
    Método responsavel por buscar o avatar do usuário no Storage
     */
    private void getUserAvatar(){
        FirebaseStorage.getInstance().getReference("/"+mUid+".jpg").getFile(mAvatar)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Debug", "Avatar baixado.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Debug", "Falha ao baixar avatar.");
                    }
                });
    }

}

