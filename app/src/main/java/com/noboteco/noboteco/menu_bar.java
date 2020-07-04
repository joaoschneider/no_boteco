package com.noboteco.noboteco;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.google.firebase.auth.FirebaseAuth;


public class menu_bar extends AppCompatActivity{

    private float x1,x2;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_bar);
        // Necessario para acesso perfil
        mAuth = FirebaseAuth.getInstance();
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
                    goperfil.putExtra("from","primeiraTela");
                    goperfil.putExtra("uid", mAuth.getCurrentUser().getUid());
                    startActivity(goperfil);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }
                else {
                    // swipe direita
                    Intent gofeedbar = new Intent(this, FeedBar.class);
                    startActivity(gofeedbar);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                }
                }

        return super.onTouchEvent(touchEvent);
    }


}
