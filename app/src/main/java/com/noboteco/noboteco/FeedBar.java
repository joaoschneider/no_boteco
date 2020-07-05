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

        loginToBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Debug", "onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Debug", "onDestroy()");
    }

    /*
            Metodo responsavel por definir os dados do usuario par ao login (uid, username e cerveja favorita)
            */
    private void loginToBar(){
        String uid = mAuth.getUid();
        final Map<String,String> dadosParaLogin = new HashMap<>();
        dadosParaLogin.put("uid", uid);
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

    /*
    Criar documento do usuario no users_online do bar de modo que ele esteja visivel para outros usuários
     */
    private void createUserDocumentAtBar(Map<String,String> dadosUserIn){
        mFirestore.document("bares/bar_do_jorge/users_online/" + mAuth.getUid()).set(dadosUserIn)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getUsersOnline();
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
    private void prepareProfilesList(){
        Log.d("Debug", "Quantos usuarios online? " + avatars.size());
        int i=0;
        for(String username : usersNoBar){
            mProfileList.add(new FeedProfile(username, "1 hora", avatars.get(i), drawablesDaCeva.get(i)));
            i+=1;
            Log.d("Debug", "Perfil de Feed do usuario criado");
        }
        for(FeedProfile p : mProfileList){
            Log.d("Debug", p.username);
            Log.d("Debug", p.avatar.toString());
            Log.d("Debug", p.fav_cerveja.toString());
        }
        Log.d("Debug", mProfileList.toString());
        setAndStartRecyclerView();
    }

    /*
    Metodo responsavel por verificar quais usuarios estão online no bar que o usuário acessou
     */
    private void getUsersOnline(){
        uidsNoBar = new LinkedList<>();
        usersNoBar = new LinkedList<>();
        cevasNoBar = new LinkedList<>();
        mFirestore.collection("/bares/bar_do_jorge/users_online").get()
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
                    //Verificar se existe alguem no bar além do usuário
                    if(!uidsNoBar.isEmpty()) {
                        //Se sim, buscar avatares
                        getUserAvatars();
                    }else{
                        //Se nao, está sozinho... avisar
                        sozinhoNoBar();
                    }
                }
            });
    }

    /*
    Metodo responsavel por buscar os avatares dos usuarios que estao no bar para visualizacao no feed
     */
    private void getUserAvatars(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        drawablesDaCeva = new LinkedList<>();
        for(String ceva : cevasNoBar){
            int drawableId = getResources().getIdentifier(ceva, "drawable", getPackageName());
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawableId);
            RoundedBitmapDrawable rnd = RoundedBitmapDrawableFactory.create(getResources(), bmp);
            rnd.setCircular(true);
            drawablesDaCeva.add(rnd);
        }
        for(String uid : uidsNoBar){
            final File tempAvatar = new File(getCacheDir() + uid + "_tempAvatar.jpg");
            storage.getReference("/" + uid + ".jpg").getFile(tempAvatar)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("Debug", tempAvatar.getName());
                            cachePaths.add(tempAvatar.getPath());
                            if(uidsNoBar.size() == cachePaths.size()){
                                saveAvatarToList();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Debug", "Falha ao buscar avatar.");
                        }
                    });
        }
    }

    /*
    Metodo responsavel por salvar os avatares na lista de avatares para envio ao ViewHolder
     */
    private void saveAvatarToList(){
       for(String path : cachePaths){
           Bitmap bmp = BitmapFactory.decodeFile(path);
           RoundedBitmapDrawable rnd = RoundedBitmapDrawableFactory.create(getResources(), bmp);
           rnd.setCircular(true);
           avatars.add(rnd);
       }
       prepareProfilesList();
    }

    /*
    Metodo responsavel por configurar o RecyclerView e trocar a View para a View dele, ultimo metodo chamado na sequencia
    iniciada pela chamada de getUsersOnline()
     */
    private void setAndStartRecyclerView(){
        ViewGroup layout = (ViewGroup) getLayoutInflater().inflate(R.layout.feed_bar, null);
        RVAdapter_Feed adapt = new RVAdapter_Feed(mProfileList);
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
                    Intent goqr = new Intent(this, leitor_cod_qr.class);
                    startActivity(goqr);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }


        }

        return super.onTouchEvent(touchEvent);
    }
}
