package com.noboteco.noboteco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_entrada);

        Button meuperfilbtn = findViewById(R.id.meu_perfil);
        Button entrarbarbtn = findViewById(R.id.entrar_no_bar);




        meuperfilbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoperfil();
            }
        });

        entrarbarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotobar();
            }
        });


    }
        public void gotoperfil(){
            Intent goperfil = new Intent(this, perfil.class);
            startActivity(goperfil);
        }

        public void gotobar(){
            Intent gobar = new Intent(this, leitor_cod_qr.class);
            startActivity(gobar);
        }




}