package com.noboteco.noboteco;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FeedBar extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<FeedProfile> mProfileList;
    private List<String> uidsNoBar;
    private List<String> usersNoBar;
    private List<RoundedBitmapDrawable> avatars;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private List<String> cachePaths;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Tela de carregamento, inicialmente
        setContentView(R.layout.carregando);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mProfileList = new ArrayList<>();
        cachePaths = new ArrayList<>();
        avatars = new ArrayList<>();

        //Iniciar sequencia de inicializacao do feed do bar
        getUsersOnline();

    }


    /*
    Metodo responsavel por preparar a lista de dados passada para o Adapter do RecyclerView
     */
    private void prepareProfilesList(){
        Log.d("Debug", "Quantos usuarios online? " + avatars.size());
        int i=0;
        for(String username : usersNoBar){
            mProfileList.add(new FeedProfile(username, "1 hora", avatars.get(i)));
            Log.d("Debug", "Perfil de Feed do usuario criado");
        }
        setAndStartRecyclerView();
    }

    /*
    Metodo responsavel por verificar quais usuarios estão online no bar que o usuário acessou
     */
    private void getUsersOnline(){
        uidsNoBar = new ArrayList<>();
        usersNoBar = new ArrayList<>();
        mFirestore.collection("/bares/bar_do_jorge/users_online").get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(DocumentSnapshot doc : queryDocumentSnapshots){
                        if(!doc.get("uid").toString().equals(mAuth.getCurrentUser().getUid())) {
                            uidsNoBar.add(doc.get("uid").toString());
                            usersNoBar.add(doc.get("username").toString());
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

        for(String uid : uidsNoBar){
            final File tempAvatar = new File(getCacheDir() + uid + "_tempAvatar.jpg");
            storage.getReference("/" + uid + ".jpg").getFile(tempAvatar)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
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
        RVAdapter adapt = new RVAdapter(mProfileList);
        mRecyclerView = layout.findViewById(R.id.feed_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(adapt);
        setContentView(layout);
    }

    private void sozinhoNoBar(){
        Toast.makeText(this, "Voce está sozinho por aqui... Que tal mostrar o app para seus amigos?", Toast.LENGTH_LONG).show();
    }
}
