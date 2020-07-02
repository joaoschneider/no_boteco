package com.noboteco.noboteco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class cadastro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro);

        Button concluirbtn = findViewById(R.id.concluirbtn);

        concluirbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeActivity();
            }
        });
    }

    public void changeActivity(){
        Intent gologin = new Intent(this, login.class);
        startActivity(gologin);
    }


}