package com.noboteco.noboteco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button btnentrar = findViewById(R.id.login);
        EditText edtusername = findViewById(R.id.editusername);
        EditText edtpassword = findViewById(R.id.editpassword);

        // Dados do usuário para serem usados

        String username = edtusername.getText().toString();
        String password = edtpassword.getText().toString();


        btnentrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeActivity();
            }
        });

    }

    public void changeActivity(){
        Intent home = new Intent(this, PrimeiraTela.class);
        startActivity(home);
    }


}