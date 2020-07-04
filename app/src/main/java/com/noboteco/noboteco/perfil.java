package com.noboteco.noboteco;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
import java.util.HashMap;
import java.util.Map;

/*
É essencial que toda a chamada para a activity Perfil seja acompanhada, no Intent, pelo UID do usuário. Isso garante que o avatar estará atualizado
no perfil e no feed do bar.
 */
public class perfil extends AppCompatActivity {
    String mUid;
    File mAvatar;
    DocumentSnapshot mUserInfo;
    FirebaseFirestore mFirestore;

    private float x1, x2;
    private GestureDetector gestureDetector;

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
        Button newFav = findViewById(R.id.btn_newfav);
        newFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewFavBeer();
            }
        });
        //Configurar linear layout das cervejas favoritas
        //Todo: implementar isso em um metodo que busque as favoritas no banco de dados e mostre elas na ordem de preferida para menos preferida
        // da esquerda para direita
        LinearLayout ll = findViewById(R.id.listaFavoritas);
        ImageView favorita1 = new ImageView(this);
        favorita1.setAdjustViewBounds(true);
        favorita1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        favorita1.setLayoutParams(params);
        favorita1.setImageResource(R.drawable.brahma_teste);
        ll.addView(favorita1);

    }

    /*
    Método responsavel por mostrar aviso ao usuario apos login_edittext ou cadastro + login_edittext
     */
    private void fromWhichActivity(String origem) {
        if (origem.equals("cadastro")) {
            Toast.makeText(perfil.this, "Sucesso ao efetuar cadastro! Você já está logado na plataforma, aproveite!", Toast.LENGTH_LONG).show();
        } else if (origem.equals("login_edittext")) {
            Toast.makeText(perfil.this, "Aproveite a plataforma!", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    Método responsavel por buscar o documento do usuario no Firestore, onde estarão guardadas as
    informações de preferências e UID para buscar avatar
     */
    private void getUserInfo() {
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
    private void getUserAvatar() {
        mAvatar = new File(getCacheDir() + "avatar.jpg");
        FirebaseStorage.getInstance().getReference("/" + mUid + ".jpg").getFile(mAvatar)
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

    private void setUserNameView() {
        TextView username = findViewById(R.id.titulo_perfil);
        username.setText(mUserInfo.get("username").toString());
    }

    private void setUserAvatarView() {
        ImageView avatar = findViewById(R.id.avatar_perfil);
        Bitmap avatarBmp = BitmapFactory.decodeFile(mAvatar.getPath());
        RoundedBitmapDrawable rndBmp = RoundedBitmapDrawableFactory.create(getResources(), avatarBmp);
        rndBmp.setCircular(true);
        avatar.setImageDrawable(rndBmp);
    }

    // detecção de swipe
    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {


        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();

                break;
            // Fim do movimento
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();

                // valor horizontal
                if (x1 < x2) {
                    // swipe esquerda
                    Intent goqr = new Intent(this, leitor_cod_qr.class);
                    startActivity(goqr);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }
                else {
                    // swipe direita
                    Intent gomenu = new Intent(this, menu_bar.class);
                    startActivity(gomenu);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }


        }

        return super.onTouchEvent(touchEvent);
    }

    /*
    Metodo responsavel por montar o dialogo de adicionar novo rating de cerveja e mostrar ao usuario
    Chamado por onClick do botao "+"
     */
    private void addNewFavBeer(){
        final Dialog dlg = new Dialog(this);
        View layout = getLayoutInflater().inflate(R.layout.dialog_newfavbeer,(ViewGroup) getCurrentFocus());
        final RatingBar rtg = layout.findViewById(R.id.ratingBar);
        final Spinner choices = layout.findViewById(R.id.select_fav);
        final Button enviar = layout.findViewById(R.id.enviar_rating);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cervejas, android.R.layout.simple_spinner_dropdown_item);
        choices.setAdapter(adapter);
        rtg.setIsIndicator(false);
        rtg.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
            }
        });
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = rtg.getRating();
                String cerveja = choices.getSelectedItem().toString();
                updateRatingOnDatabase(cerveja,rating);
                dlg.dismiss();
            }
        });
        dlg.setCancelable(true);
        dlg.setContentView(layout);
        dlg.show();
    }

    /*
    Metodo responsavel por atualizar o banco de dados após o uso do diálogo criado por addNewFavBeer()
     */
    private void updateRatingOnDatabase(final String cerveja, final float rating){

        mFirestore.document("users/" + mUid + "/bebidas/cervejas/").update(cerveja,rating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(perfil.this, "Rating de " + cerveja + " atualizado para " + rating, Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(perfil.this, "Falha ao atualizar informação.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

