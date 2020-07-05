package com.noboteco.noboteco;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;

import android.view.MotionEvent;
import android.view.ViewGroup;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class leitor_cod_qr extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private float x1,x2;
    FirebaseAuth mAuth;
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Necessario para acesso perfil
        setContentView(R.layout.leitor_cod_qr);
        ViewGroup frame = (ViewGroup) findViewById(R.id.preview);
        mAuth = FirebaseAuth.getInstance();
        mScannerView = new ZXingScannerView(this);
        mScannerView.setBackgroundColor(Color.LTGRAY);
        frame.addView(mScannerView);

    }

    @Override
    public void onResume(){
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Log.d("Debug", "Resultado: " + result.getText());
        Intent intent = new Intent(leitor_cod_qr.this, FeedBar.class);
        intent.putExtra("nome_bar", result.getText());
        intent.putExtra("from", "leitor_qr");
        startActivity(intent);
    }

    // detecção de swipe
    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {
        final int SWIPE_THRESHOLD = 150;

        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();

                break;
            // Fim do movimento
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();

                if((Math.abs(x1-x2)) > SWIPE_THRESHOLD )
                // valor horizontal
                if (x1 < x2) {
                    // swipe esquerda
                }
                else{
                    // swipe direita
                    Intent goperfil = new Intent(this, perfil.class);
                    goperfil.putExtra("from","primeiraTela");
                    goperfil.putExtra("uid", mAuth.getCurrentUser().getUid());
                    startActivity(goperfil);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }

        }

        return super.onTouchEvent(touchEvent);
    }


}