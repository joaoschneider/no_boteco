package com.noboteco.noboteco;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;

import java.io.File;
/*
É essencial que toda a chamada para a activity Perfil seja acompanhada, no Intent, pelo UID do usuário. Isso garante que o avatar estará atualizado
no perfil e no feed do bar.
 */
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
                        setUserNameView();
                        Toast.makeText(perfil.this, "Informações atualizadas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(perfil.this, "Falha ao buscar informações...", Toast.LENGTH_SHORT).show();
                        Log.d("Debug", "Erro: " + e.toString());
                    }
                });

        //Após buscar mUid, buscar o avatar no Storage (nome do arquivo é mUid.jpg)
        getUserAvatar();
    }

    /*
    Método responsavel por buscar o avatar do usuário no Storage
     */
    private void getUserAvatar(){
        mAvatar = new File(getCacheDir() + "avatar.jpg");
        FirebaseStorage.getInstance().getReference("/"+mUid+".jpg").getFile(mAvatar)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Debug", "Avatar baixado.");
                        setUserAvatarView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Debug", "Falha ao baixar avatar. Aviso: " + e.toString());
                    }
                });
    }

    private void setUserNameView(){
        TextView username = findViewById(R.id.titulo_perfil);
        username.setText(mUserInfo.get("username").toString());
    }
    private void setUserAvatarView(){
        ImageView avatar = findViewById(R.id.avatar_usuario);
        Bitmap avatarBmp = BitmapFactory.decodeFile(mAvatar.getPath());
        RoundedBitmapDrawable rndBmp = RoundedBitmapDrawableFactory.create(getResources(), avatarBmp);
        rndBmp.setCircular(true);
        avatar.setImageDrawable(rndBmp);
    }

}

