package com.noboteco.noboteco;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

/*
É essencial que toda a chamada para a activity Perfil seja acompanhada, no Intent, pelo UID do usuário. Isso garante que o avatar estará atualizado
no perfil e no feed do bar.
 */
public class perfil extends AppCompatActivity {
    String mUid;
    File mAvatar;
    DocumentSnapshot mUserInfo;
    FirebaseFirestore mFirestore;
    ViewGroup mLayout_perfil;
    Map<String, Object> cervejas_avaliadas;

    private float x1, x2;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carregando);
        mLayout_perfil = (ViewGroup) getLayoutInflater().inflate(R.layout.perfil, null);
        //Buscar uid enviado a através do Intent recebido
        mUid = getIntent().getStringExtra("uid");
        String origem = getIntent().getStringExtra("from");
        assert origem != null;
        fromWhichActivity(origem);
        mFirestore = FirebaseFirestore.getInstance();
        cervejas_avaliadas = new HashMap<>();
        getUserInfo();
        getUserAvatar();
        getGradedBeers();


        //Configurar linear layout das cervejas favoritas
        //Todo: implementar isso em um metodo que busque as favoritas no banco de dados e mostre elas na ordem de preferida para menos preferida
        // da esquerda para direita


    }

    /*
    Método responsavel por mostrar aviso ao usuario apos login ou cadastro + login
     */
    private void fromWhichActivity(String origem) {
        if (origem.equals("cadastro")) {
            Toast.makeText(perfil.this, "Sucesso ao efetuar cadastro! Você já está logado na plataforma, aproveite!", Toast.LENGTH_LONG).show();
        } else if (origem.equals("login")) {
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
    }

    /*
    Metodo responsavel por setar a TextView do Username (layout R.layout.perfil)
     */
    private void setUserNameView() {
        TextView username = mLayout_perfil.findViewById(R.id.titulo_perfil);
        username.setText(mUserInfo.get("username").toString());
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

    /*
    Metodo responsavel por popular o ImageView avatar_perfil (R.layout.perfil) com o avatar do usuario
     */
    private void setUserAvatarView() {
        ImageView avatar = mLayout_perfil.findViewById(R.id.avatar_perfil);
        Bitmap avatarBmp = BitmapFactory.decodeFile(mAvatar.getPath());
        RoundedBitmapDrawable rndBmp = RoundedBitmapDrawableFactory.create(getResources(), avatarBmp);
        rndBmp.setCircular(true);
        avatar.setImageDrawable(rndBmp);
    }

    private void getGradedBeers(){
        mFirestore.document("users/" + mUid + "/bebidas/cervejas").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        cervejas_avaliadas = documentSnapshot.getData();
                        Log.d("Debug ","Cervejas buscadas:" + cervejas_avaliadas.toString());
                        setGradedBeersView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Debug", "Falha ao buscar cervejas avaliadas.");
                        Toast.makeText(perfil.this, "Ocorreu um problema ao buscar suas cervejas preferidas.", Toast.LENGTH_LONG).show();
                        setGradedBeersView();
                    }
                });

    }

    private void setGradedBeersView(){
        LinearLayout ll = mLayout_perfil.findViewById(R.id.listaFavoritas);
        if(!cervejas_avaliadas.isEmpty()){
            Resources res = getResources();
            for(Map.Entry<String, Object> entry : cervejas_avaliadas.entrySet()){
                ImageView favorita1 = new ImageView(this);
                favorita1.setAdjustViewBounds(true);
                favorita1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                favorita1.setLayoutParams(params);
                int drawableId = res.getIdentifier(entry.getKey(), "drawable", getPackageName());
                favorita1.setImageResource(drawableId);
                ll.addView(favorita1);
            }
        }
        //sempre adicionar o botao "+" no Layout
        Button newFav = mLayout_perfil.findViewById(R.id.btn_newfav);
        newFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewFavBeer();
            }
        });


        //Finalmente, trocar o View
        this.setContentView(mLayout_perfil);
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
                updateRatingOnDatabase(cerveja.toLowerCase(),rating);
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


}

