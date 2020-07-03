package com.noboteco.noboteco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class PrimeiraTela extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button meuperfilbtn;
    Button entrarbarbtn;
    Button login_logoutbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        As definições de Transição devem ser geradas antes de informar ao View o layout a ser usado
         */
        setTransitionAnimation();

        setContentView(R.layout.tela_entrada);

        //Controle de estado de acesso do usuário: está logado ou não?
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    Log.d("Debug", "Usuario logado: " + firebaseAuth.getCurrentUser().getEmail());
                    organizarLayoutBaseadoNoLogin(true);
                }else{
                    Log.d("Debug", "Nenhum usuario logado.");
                    organizarLayoutBaseadoNoLogin(false);
                }
            }
        });

        meuperfilbtn = findViewById(R.id.meu_perfil);
        entrarbarbtn = findViewById(R.id.entrar_no_bar);
        login_logoutbtn = findViewById(R.id.login_logout);

        //Verificar se a origem da chamada de Primeira Tela é Login ou Cadastro
        String origem = getIntent().getStringExtra("from");
        if(origem != null){
            fromWhichActivity(origem);
        }

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
                if(login_logoutbtn.getText().toString().equals("Acessar")) {
                    changeActivity("login");
                }else{
                    mAuth.signOut();
                    Toast.makeText(PrimeiraTela.this, "Até logo!", Toast.LENGTH_LONG).show();
                }
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
            case "login":
                Intent gologin = new Intent(this, login.class);
                startActivity(gologin, options.toBundle());
                break;
            case "entrar_bar":
                Intent gobar = new Intent(this, leitor_cod_qr.class);
                startActivity(gobar, options.toBundle());
                break;
            case "ver_perfil":
                Intent goperfil = new Intent(this, perfil.class);
                goperfil.putExtra("from","primeiraTela");
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

    /*
    Método responsavel por mostrar aviso ao usuario apos login ou cadastro + login
     */
    public void fromWhichActivity(String origem){
        if(origem.equals("cadastro")){
            Toast.makeText(PrimeiraTela.this, "Sucesso ao efetuar cadastro! Você já está logado na plataforma, aproveite!", Toast.LENGTH_LONG).show();
        }else if(origem.equals("login")){
            Toast.makeText(PrimeiraTela.this, "Aproveite a plataforma!", Toast.LENGTH_SHORT).show();
        }
    }

    public void organizarLayoutBaseadoNoLogin(boolean isLogado){
        if(isLogado){
            login_logoutbtn.setText("Sair");
            meuperfilbtn.setVisibility(View.VISIBLE);
            entrarbarbtn.setVisibility(View.VISIBLE);
        }else{
            login_logoutbtn.setText("Acessar");
            meuperfilbtn.setVisibility(View.GONE);
            entrarbarbtn.setVisibility(View.GONE);
        }
    }




}