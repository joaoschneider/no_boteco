package com.noboteco.noboteco;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class menu_bar extends AppCompatActivity {

    private float x1, x2;
    FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    DocumentSnapshot mCervejaInfo;
    private List<FeedMenu> cervejas;
    ViewGroup mLayout_menu;
    int size;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_bar);
        // Necessario para acesso perfil
        mAuth = FirebaseAuth.getInstance();
        mLayout_menu = (ViewGroup) getLayoutInflater().inflate(R.layout.menu_bar, null);

        cervejas = new ArrayList<>();

        mFirestore = FirebaseFirestore.getInstance();

        //
        getData();



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
                    Intent goperfil = new Intent(this, perfil.class);
                    goperfil.putExtra("from","menu_bar");
                    goperfil.putExtra("uid", mAuth.getCurrentUser().getUid());
                    startActivity(goperfil);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    // swipe direita
                    Intent gofeedbar = new Intent(this, FeedBar.class);
                    startActivity(gofeedbar);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
        }

        return super.onTouchEvent(touchEvent);
    }

    private void getData(){
        mFirestore.collection("/bares/bar_do_jorge/menu")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshot) {
                        if (!queryDocumentSnapshot.isEmpty()) {
                            int i = 0;
                            for (DocumentSnapshot doc : queryDocumentSnapshot) {
                                Log.d("Debug","info" + doc.get("qtd") + doc.get("name") + getResources().getIdentifier((String) doc.get("name"), "drawable", getPackageName()));
                                cervejas.add(new FeedMenu((String) doc.get("qtd"),
                                                (String) doc.get("name"),
                                                getResources().getIdentifier((String) doc.get("name"), "drawable", getPackageName())
                                        )
                                );
                            }
                        }
                        StartRecycler();
                    }
                });

    }

    private void StartRecycler(){
        Log.d("Debug","info" + cervejas.size());
        rv = mLayout_menu.findViewById(R.id.rv_menu);
        RVAdapter_Menu adapter = new RVAdapter_Menu(cervejas);
        GridLayoutManager llm = new GridLayoutManager(this,  1);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        setContentView(mLayout_menu);

    }
}



