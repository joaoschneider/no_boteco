package com.noboteco.noboteco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import android.view.Gravity;
import android.view.View;

import android.view.ViewGroup;
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

        //Tela de carregamento
        setContentView(R.layout.carregando);

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



        //Verificar se a origem da chamada de Primeira Tela é Login ou Cadastro
        String origem = getIntent().getStringExtra("from");
        if(origem != null){
            fromWhichActivity(origem);
        }
    }

    public void changeActivity(String destino){
        /*
            ActivityOptions.makeSceneTransitionAnimation() é o que de fato ativa a transição no Intent
            através do options Bundle.
             */
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        switch(destino) {
            case "login_edittext":
                Intent gologin = new Intent(this, login.class);
                startActivity(gologin);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                break;
            case "entrar_bar":
                Intent gobar = new Intent(this, leitor_cod_qr.class);
                startActivity(gobar);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                break;
            case "ver_perfil":
                Intent goperfil = new Intent(this, perfil.class);
                goperfil.putExtra("from","primeiraTela");
                goperfil.putExtra("uid", mAuth.getCurrentUser().getUid());
                startActivity(goperfil);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                break;
            default :
                Toast.makeText(this, "Atividade não implementada.", Toast.LENGTH_LONG).show();
        }
    }

    /*
    Método responsavel por mostrar aviso ao usuario apos login_edittext ou cadastro + login_edittext
     */
    public void fromWhichActivity(String origem){
        if(origem.equals("cadastro")){
            Toast.makeText(PrimeiraTela.this, "Sucesso ao efetuar cadastro! Você já está logado na plataforma, aproveite!", Toast.LENGTH_LONG).show();
        }else if(origem.equals("login_edittext")){
            Toast.makeText(PrimeiraTela.this, "Aproveite a plataforma!", Toast.LENGTH_SHORT).show();
        }
    }

    public void organizarLayoutBaseadoNoLogin(boolean isLogado){
        if(isLogado){
            //Usuario ja esta logado, então envia-lo para FeedBar
            //La, é feita a verificacao se ele está no bar ou nao. Se nao estiver, aparece o leitor de QR
            Intent goFeed = new Intent(this, FeedBar.class);
            startActivity(goFeed);
            finish();
        }else{
            ViewGroup layout = (ViewGroup) getLayoutInflater().inflate(R.layout.tela_entrada, null);
            login_logoutbtn = layout.findViewById(R.id.login_logout);
            login_logoutbtn.setText(R.string.acessar);
            login_logoutbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(login_logoutbtn.getText().toString().equals("Acessar")) {
                        changeActivity("login_edittext");
                    }else{
                        mAuth.signOut();
                        Toast.makeText(PrimeiraTela.this, "Até logo!", Toast.LENGTH_LONG).show();
                    }
                }
            });
            login_logoutbtn.setGravity(Gravity.CENTER);
            setContentView(layout);
        }
    }




}