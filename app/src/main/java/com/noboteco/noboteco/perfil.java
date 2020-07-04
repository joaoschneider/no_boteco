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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;

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

        //Buscar todas as informações necessarias para renderizar o perfil do usuário:
        //username, avatar, cervejas avaliadas e atividade recente
        getUserInfo();
        getUserAvatar();
        getGradedBeers();
        getRecentActivity();
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

    /*
    Metodo responsavel por buscar as cervejas avaliadas pelo usuário no banco de dados
     */
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

    /*
    Metodo responsavel por configurar o recyclerview responsavel por mostrar as bebidas buscadas por getGradedBeers()
     */
    private void setGradedBeersView(){
        RecyclerView rv = mLayout_perfil.findViewById(R.id.rv_cervejas);
        List<Integer> resourceIds = new ArrayList<>();
        if(!cervejas_avaliadas.isEmpty()){
            for(Map.Entry<String, Object> entry : cervejas_avaliadas.entrySet()){
                int resourceId = getResources().getIdentifier(entry.getKey(), "drawable", getPackageName());
                resourceIds.add(resourceId);
            }
            RVAdapter_Profile adapter = new RVAdapter_Profile(resourceIds);
            LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
            rv.setLayoutManager(llm);
            rv.setAdapter(adapter);
        }
        //sempre adicionar o botao "+" no Layout
        Button newFav = mLayout_perfil.findViewById(R.id.btn_newfav);
        newFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewFavBeer();
            }
        });
        Button sair = mLayout_perfil.findViewById(R.id.btn_sair);
        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sair do bar
                mFirestore.document("bares/bar_do_jorge/users_online/" + mUid).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                auth.signOut();
                                Intent goHome = new Intent(perfil.this, PrimeiraTela.class);
                                startActivity(goHome);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(perfil.this, "Falha ao sair do bar.", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        //Finalmente, trocar o View
        this.setContentView(mLayout_perfil);
    }

    private void getRecentActivity(){
        long agoraMillis = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo")).getTimeInMillis();
        long limite = 0;
        Log.d("Debug", "UID: " + mUid);
        Log.d("Debug", "Agora: " + agoraMillis + "\n" + "Ultimas 2 horas: " + (agoraMillis-7200000));
        mFirestore.collection("bares/bar_do_jorge/users_online/" + mUid + "/recente")
                .whereGreaterThan("horario", limite)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("Debug", "Atividade recente buscada.");
                        if(queryDocumentSnapshots.isEmpty()){
                            setRecentActivityView(null);
                        }else{
                            List<String> recente = new ArrayList<>();
                            for(DocumentSnapshot doc : queryDocumentSnapshots){
                                recente.addAll(doc.getData().keySet());
                            }
                            setRecentActivityView(recente);
                        }
                    }
                });
    }

    private void setRecentActivityView(List<String> recente){
        if(recente == null){
            Toast.makeText(perfil.this, "Nenhuma atividade nas últimas 2 horas.", Toast.LENGTH_LONG).show();
        }else{
            RecyclerView rv = mLayout_perfil.findViewById(R.id.rv_recente);
            LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
            rv.setLayoutManager(llm);
            List<Integer> resourceIds = new ArrayList<>();
            for(String ceva : recente){
                if(ceva.equals("horario")){
                    continue;
                }
                ceva = ceva.toLowerCase();
                int resourceId = getResources().getIdentifier(ceva, "drawable", getPackageName());
                resourceIds.add(resourceId);
            }
            RVAdapter_Profile adapter = new RVAdapter_Profile(resourceIds);
            rv.setAdapter(adapter);
            mLayout_perfil.invalidate();
        }
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

