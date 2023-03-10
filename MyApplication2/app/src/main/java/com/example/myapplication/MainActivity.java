package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;
/*Esta classe "é uma brincadeira", não é necessária, puramente estética
* É feita uma animação com o nome da Bizdirect e é feito um RingProgressBar (Parecido a um ProgressBar)
* a imitar o processamento de dados*/
public class MainActivity extends AppCompatActivity {

    RingProgressBar progressBar1;
    int progress=0;
    Handler myHandler;
    TextView txt_animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

        progressBar1 = (RingProgressBar) findViewById(R.id.progress);
        txt_animation = (TextView) findViewById(R.id.animTextV);
        startAnimation();
        ringProgress();
    }


    private  void startAnimation(){
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim);
        txt_animation.startAnimation(anim);
    }


    public void ringProgress(){
        progressBar1.setOnProgressListener(new RingProgressBar.OnProgressListener() {
            @Override
            public void progressToComplete() {
                Toast.makeText(MainActivity.this, "Carregamento com sucesso!", Toast.LENGTH_SHORT).show();
//quando o RingProgressbar chegar aos "100" muda de activitie e termina esta
                Intent intent = new Intent(getApplicationContext(),Inicio.class);
                startActivity(intent);
                finish();
            }
        });
        myHandler= new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0){
                    if(progress<100){
                        progress=progress+4;
                        progressBar1.setProgress(progress);
                    }
                }

            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<100;i++){
                    try{
                        Thread.sleep(100);
                        myHandler.sendEmptyMessage(0);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
