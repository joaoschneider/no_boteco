package com.noboteco.noboteco;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ImageView _logoAmbev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        As definições de Transição devem ser geradas antes de informar ao View o layout a ser usado
         */
        setTransitionAnimation();

        setContentView(R.layout.tela_entrada);

        Button meuperfilbtn = findViewById(R.id.meu_perfil);
        Button entrarbarbtn = findViewById(R.id.entrar_no_bar);

        meuperfilbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity("ver_perfil");
            }
        });

        entrarbarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity("entrar_bar");
            }
        });


    }

    public void changeActivity(String destino){
        /*
            ActivityOptions.makeSceneTransitionAnimation() é o que de fato ativa a transição no Intent
            através do options Bundle.
             */
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        switch(destino) {
            case "entrar_bar":
                Intent gobar = new Intent(this, leitor_cod_qr.class);
                startActivity(gobar, options.toBundle());
                break;
            case "ver_perfil":
                Intent goperfil = new Intent(this, perfil.class);
                startActivity(goperfil, options.toBundle());
                break;
            default :
                Toast.makeText(this, "Atividade não implementada.", Toast.LENGTH_LONG).show();
        }
    }

    /*
        Método responsavel por definir o tipo de Transição aplicada ao View, tanto na entrada quanto
        na saída da Activity.
         */
    public void setTransitionAnimation(){
        Slide slide = new Slide();
        slide.setDuration(500L);
        slide.setSlideEdge(Gravity.START);
        slide.setInterpolator(new AccelerateDecelerateInterpolator());
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);
    }





}