package com.noboteco.noboteco;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FeedBar extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<FeedProfile> mProfileList;
    private List<String> uidsNoBar;
    private List<String> usersNoBar;
    private List<String> cevasNoBar;
    private List<RoundedBitmapDrawable> drawablesDaCeva;
    private List<RoundedBitmapDrawable> avatars;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private List<String> cachePaths;
    private float x1, x2;
    private GestureDetector gestureDetector;
    private List<FeedProfile> mAdapter;
    private String bar;
    private static Bundle mRecyclerState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Tela de carregamento, inicialmente
        setContentView(R.layout.carregando);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mProfileList = new LinkedList<>();
        cachePaths = new LinkedList<>();
        avatars = new LinkedList<>();
        String origem;

        if(getIntent().getStringExtra("from") != null){
            origem = getIntent().getStringExtra("from");
            if(origem.equals("leitor_qr")) {
                Log.d("Debug", "Veio do Leitor. Logando atraves do link");
                bar = getIntent().getStringExtra("nome_bar");
                loginToBar(bar);
            }
        }else{
            //Verificar se o usuario está logado em algum bar
            checkIsUserLogged();
        }
    }

    /*
    Metodo responsavel por definir os dados do usuario par ao login (uid, username e cerveja favorita)
    */
    private void loginToBar(String nomeBar){
        String uid = mAuth.getUid();
        final Map<String,String> dadosParaLogin = new HashMap<>();
        dadosParaLogin.put("uid", uid);
        dadosParaLogin.put("bar",nomeBar);
        mFirestore.document("users/" + uid).update("noBar", dadosParaLogin.get("bar"));
        mFirestore.document("users/" + uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        dadosParaLogin.put("username", (String) documentSnapshot.get("username"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Debug", "Falha ao buscar username");
                    }
                });
        mFirestore.collection("users/" + uid +"/bebidas")
                .orderBy("nota", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            Iterator<QueryDocumentSnapshot> iterator = queryDocumentSnapshots.iterator();
                            dadosParaLogin.put("favorita", iterator.next().getId());
                        }
                        createUserDocumentAtBar(dadosParaLogin);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FeedBar.this, "Falha ao buscar cerveja favorita.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIsUserLogged(){
        mFirestore.document("users/" + mAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try{
                            bar = documentSnapshot.get("noBar").toString();
                            getUidsOnline(bar);
                        }catch(NullPointerException e){
                            //Usuario nao esta logado, enviar para leitor qr
                            Intent intent = new Intent(FeedBar.this, leitor_cod_qr.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Debug", "Falha ao verificar se usuario esta logado. Indo para leitor_qr");
                        Intent intent = new Intent(FeedBar.this, leitor_cod_qr.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    /*
    Criar documento do usuario no users_online do bar de modo que ele esteja visivel para outros usuários
     */
    private void createUserDocumentAtBar(Map<String,String> dadosUserIn){
        final String linkBar = dadosUserIn.get("bar");
        mFirestore.document(linkBar + "/users_online/" + mAuth.getUid()).set(dadosUserIn)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getUidsOnline(linkBar);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Debug", "Falha ao criar documento do usuario dentro de users_online. Erro: " + e.toString());
                    }
                });
    }

    /*
    Metodo responsavel por preparar a lista de dados passada para o Adapter do RecyclerView
     */

    private void getUidsOnline(String bar) {
        uidsNoBar = new LinkedList<>();
        usersNoBar = new LinkedList<>();
        cevasNoBar = new LinkedList<>();
        mFirestore.collection(bar + "/users_online").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc : queryDocumentSnapshots){
                            //Evitar visualizar a si mesmo no feed
                            if(!doc.get("uid").toString().equals(mAuth.getCurrentUser().getUid())) {
                                uidsNoBar.add(Objects.requireNonNull(doc.get("uid")).toString());
                                usersNoBar.add(Objects.requireNonNull(doc.get("username")).toString());
                                cevasNoBar.add(Objects.requireNonNull(doc.get("favorita")).toString());
                                Log.d("Debug", "Usuario online detectado");
                            }
                        }
                        if(uidsNoBar.isEmpty()){
                            sozinhoNoBar();
                        }else {
                            criarFeedProfileIndividualmente(uidsNoBar.get(0), 0);
                        }
                    }
                });
    }

    private void criarFeedProfileIndividualmente(String uid, final int currentIndex){
        final File tempAvatar = new File(getCacheDir() + uid + "_tempAvatar.jpg");
        FirebaseStorage.getInstance().getReference("/" + uid + ".jpg")
                .getFile(tempAvatar)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        gerarDrawables(tempAvatar, currentIndex);
                    }
                });
    }

    private void gerarDrawables(File avatar, int current){
        Bitmap _avatar = BitmapFactory.decodeFile(avatar.getPath());
        RoundedBitmapDrawable rndAvatar = RoundedBitmapDrawableFactory.create(getResources(), _avatar);
        rndAvatar.setCircular(true);

        int resourceId = getResources().getIdentifier(cevasNoBar.get(current), "drawable", getPackageName());
        Bitmap ceva = BitmapFactory.decodeResource(getResources(), resourceId);
        RoundedBitmapDrawable rndCeva = RoundedBitmapDrawableFactory.create(getResources(), ceva);
        rndCeva.setCircular(true);

        mProfileList.add(new FeedProfile(usersNoBar.get(current), "1 hora", rndAvatar, rndCeva));
        Log.d("Debug", "Perfil criado para " + uidsNoBar.get(current));
        Log.d("Debug", "Avatar: " + avatar.getPath());
        Log.d("Debug", "Ceva: " + cevasNoBar.get(current));
        Log.d("Debug", "Username: " + usersNoBar.get(current));

        current+=1;
        if(mRecyclerView == null){
            setAndStartRecyclerView();
        }else{
            RVAdapter_Feed adapter = new RVAdapter_Feed(mProfileList);
            mRecyclerView.setAdapter(adapter);
        }
        if(current < uidsNoBar.size()){
            criarFeedProfileIndividualmente(uidsNoBar.get(current), current);
        }else{
            Log.d("Debug", "Todos perfis fqoram criados.");
        }
    }


    /*
    Metodo responsavel por configurar o RecyclerView e trocar a View para a View dele, ultimo metodo chamado na sequencia
    iniciada pela chamada de getUsersOnline()
     */
    private void setAndStartRecyclerView(){
        ViewGroup layout = (ViewGroup) getLayoutInflater().inflate(R.layout.feed_bar, null);
        RVAdapter_Feed adapt = new RVAdapter_Feed(mProfileList);
        final TextView mNomeBar = layout.findViewById(R.id.nome_bar);
        mFirestore.document(bar).get()
        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mNomeBar.setText(documentSnapshot.get("nome").toString().toUpperCase());
            }
        });
        mRecyclerView = layout.findViewById(R.id.feed_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(adapt);
        setContentView(layout);
    }

    private void sozinhoNoBar(){
        Toast.makeText(this, "Voce está sozinho por aqui... Que tal mostrar o app para seus amigos?", Toast.LENGTH_LONG).show();
    }

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
                    Intent gomenu = new Intent(this, menu_bar.class);
                    startActivity(gomenu);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }
                else {
                    // swipe direita
                    Intent goperfil = new Intent(this, perfil.class);
                    goperfil.putExtra("uid", mAuth.getUid());
                    goperfil.putExtra("from", "feed");
                    startActivity(goperfil);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }


        }

        return super.onTouchEvent(touchEvent);
    }
}
