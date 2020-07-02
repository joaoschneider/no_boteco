package com.noboteco.noboteco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    FirebaseAuth mAuth;
    // Dados do usuário para serem usados
    EditText mEditUsername;
    EditText mEditPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mAuth = FirebaseAuth.getInstance();

        Button btnentrar = findViewById(R.id.login);
        mEditUsername = findViewById(R.id.editusername);
        mEditPassword = findViewById(R.id.editpassword);

        btnentrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = mEditUsername.getText().toString();
                String password = mEditPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(username,password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                changeActivity();
                                //TODO: adicionar ao intent a informação de userID para uso futuro do banco de dados
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(login.this, "Verifique os dados informados e tente novamente.", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }

    public void changeActivity(){
        Intent home = new Intent(this, PrimeiraTela.class);
        startActivity(home);
    }


}