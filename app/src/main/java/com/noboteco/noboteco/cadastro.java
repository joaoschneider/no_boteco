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

public class cadastro extends AppCompatActivity {
    EditText mUsuario;
    EditText mEmail;
    EditText mSenha;
    EditText mConfirmaSenha;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro);

        mAuth = FirebaseAuth.getInstance();

        mUsuario = findViewById(R.id.edtusername);
        mEmail = findViewById(R.id.edtemail);
        mSenha = findViewById(R.id.edtpassword);
        mConfirmaSenha = findViewById(R.id.edtpasswordconfirm);

        Button concluirbtn = findViewById(R.id.concluirbtn);

        concluirbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mUsuario.getText().length() == 0){
                    Toast.makeText(cadastro.this, "Por favor, informe um nome de usuário", Toast.LENGTH_LONG).show();
                }
                else if(mSenha.getText().length() == 0){
                    Toast.makeText(cadastro.this, "Por favor, informe a senha desejada", Toast.LENGTH_LONG).show();
                }
                else if(mEmail.getText().length() == 0){
                    Toast.makeText(cadastro.this, "Por favor, informe o e-mail", Toast.LENGTH_LONG).show();
                }
                else if(mConfirmaSenha.getText().length() == 0 || !mConfirmaSenha.getText().toString().equals(mSenha.getText().toString())){
                    Toast.makeText(cadastro.this, "A senha e a confirmação não são iguais", Toast.LENGTH_LONG).show();
                }
                else{
                    /*
                    Dados informados corretamente, efetuar cadastro
                     */
                    String email = mEmail.getText().toString();
                    String senha = mSenha.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email,senha)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    changeActivity();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(cadastro.this, "Falha ao cadastrar novo usuário.", Toast.LENGTH_LONG).show();
                                }
                            });
                }

            }
        });
    }

    public void changeActivity(){
        Intent gohome = new Intent(this, PrimeiraTela.class);
        gohome.putExtra("from", "cadastro");
        startActivity(gohome);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }


}