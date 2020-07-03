package com.noboteco.noboteco;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import android.view.View;
import android.view.ViewGroup;
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
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
            case "entrar_bar":
                Intent gobar = new Intent(this, leitor_cod_qr.class);
                startActivity(gobar);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
            case "ver_perfil":
                Intent goperfil = new Intent(this, perfil.class);
                startActivity(goperfil);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
            default :
                Toast.makeText(this, "Atividade não implementada.", Toast.LENGTH_LONG).show();
        }
    }


}