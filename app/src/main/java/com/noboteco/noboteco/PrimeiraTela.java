package com.noboteco.noboteco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PrimeiraTela extends AppCompatActivity {

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
        Button login_logoutbtn = findViewById(R.id.login_logout);

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

        login_logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity("login_logout");
            }
        });
    }

    public void changeActivity(String destino){
        switch(destino) {
            case "login_logout":
                Intent gologin = new Intent(this, login.class);
                startActivity(gologin);
                break;
            case "entrar_bar":
                Intent gobar = new Intent(this, leitor_cod_qr.class);
                startActivity(gobar);
                break;
            case "ver_perfil":
                Intent goperfil = new Intent(this, perfil.class);
                startActivity(goperfil);
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