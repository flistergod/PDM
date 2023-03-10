package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
* Esta classe trata das definições do cliente (conta, email, password)
* Esta classe implementa uma interface por cada funcionalidade
* -Altera_email.Altera_emailListener - email
* -Altera_pass.Altera_passListener - password
* -Apaga_conta.Apaga_contaListener - conta
* Esta classe tem acesso aos dados do cliente ao fazer o fetch desses dados sabendo o email desse cliente
* que foi dado pelo intent feito para esta activitie*/

public class definicoes extends AppCompatActivity implements Altera_email.Altera_emailListener, Altera_pass.Altera_passListener, Apaga_conta.Apaga_contaListener {

    //variaveis
    Button btn_pass, btn_email, btn_dados, btn_pref, btn_apaga;
    String updateEmail="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/updateEmail";
    String updatePassDefinicoes="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/updatePassDefinicoes";
    String apagaConta="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/apagaConta";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definicoes);


        //toolbar
        Toolbar toolbar =findViewById(R.id.toolbar_definicoes);
        setSupportActionBar(toolbar);

       // toolbar.setTitle(getIntent().getStringExtra("email"));

        //caso seja clicado na "seta" para voltar atrás
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        //buttons das funcionalidades onde é detetado o seu clique e originado o alertDialog de cada um

        btn_email= (Button) findViewById(R.id.button_email);
        btn_pass= (Button) findViewById(R.id.button_pass);
        btn_apaga= (Button) findViewById(R.id.button_conta);

        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAltera_Email();
            }
        });

        btn_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAltera_Pass();
            }
        });

        btn_apaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApaga_conta();
            }
        });

    }

    //alert Dialogs
    public void openAltera_Email(){

        Altera_email altera_email = new Altera_email();
        altera_email.show(getSupportFragmentManager(), "alteraEmail dialog");

    }

    public void openAltera_Pass(){

        Altera_pass altera_pass = new Altera_pass();
        altera_pass.show(getSupportFragmentManager(), "alteraPass dialog");

    }

    public void openApaga_conta(){

        Apaga_conta apaga_conta = new Apaga_conta();
        apaga_conta.show(getSupportFragmentManager(), "apagaConta dialog");

    }


    //metodos das interfaces implementadas para obter os dados que o cliente colocou nos alertDialogs

    @Override
    public void getEmails(final String email_novo) {
        showAlert("A processar...");

//verifica se o email dado é válido para substituir o atual
                if(Patterns.EMAIL_ADDRESS.matcher(email_novo).matches() == false){
                    showAlert("email inválido");
                }




        else{
//irá substituir o email atual pelo "email_novo" caso não haja problemas de SQL/PHP
 // é usado o "Volley"
            RequestQueue queue = Volley.newRequestQueue(definicoes.this);
            String url = updateEmail;

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("APPLOG", response);
// é devolvido um JSON onde será processado o seu conteúdo

                            JSONObject jsonObject = null;

                            try {
                                jsonObject = new JSONObject(response);

                                //verifica se no php, o email novo dado já existe
                                if(jsonObject.get("EMAIL").equals("EXISTENTE")){

                                    //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                                    showAlert("O email que escolheu, já existe");
                                }

                                //verifica se no php, o email novo dado foi aceite
                                if(jsonObject.get("EMAIL").equals("ALTERADO")){

                                    //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                                    showAlert("Email alterado com sucesso");
                                }


                            }catch (JSONException e){

                            }

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d("APPLOG", error.toString());
                            showAlert("Sem conexão!");
                            //consegue ver o erro com mais detalhe no Logcat ao pesquisar "APPLOG"


                        }
                    }
            ){
                @Override
                protected Map<String,String> getParams()
                {

                    //parametros passados para o ficheiro php
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("post_email_atual", getIntent().getStringExtra("email"));
                    params.put("post_email_novo", email_novo.trim());
                    return params;
                }

            };
            queue.add(postRequest);

        }
        }

    @Override
    public void getPass(final String pass_atual, final String pass_nova, final String pass_nova_rep) {
        showAlert("A processar...");

        //verifica os parametros dados, a pass_atual é verificada no ficheiro php
        if(!pass_nova.equals(pass_nova_rep)){
            showAlert("Campos da nova password diferentes");

        }


        else{

//é utilizado volley
//use "APPLOG" no Logcat para ver o que é feito com mais detalhe
// é devolvido um JSon, onde o seu conteúdo será processsado, caso não haja erros de PHP/SQL

            RequestQueue queue = Volley.newRequestQueue(definicoes.this);
            String url = updatePassDefinicoes;

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("APPLOG", response);



                            JSONObject jsonObject = null;

                            try {
                                jsonObject = new JSONObject(response);

                                if(jsonObject.get("PASS").equals("ALTERADA")){

                                    //foi possivel alterar a password
                                    //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                                    showAlert("A password foi alterada com sucesso");
                                }
                                // não foi possivel alterar a password
                                else{ showAlert("Não foi possível alterar a passoword");}

                            }catch (JSONException e){

                            }

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d("APPLOG", error.toString());
                            showAlert("Sem conexão!");


                        }
                    }
            ){
                @Override
                protected Map<String,String> getParams()
                {
                    //parametros enviados para o ficheiro php
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("post_pass_atual", pass_atual);
                    params.put("post_pass_nova", pass_nova);
                    params.put("post_email", getIntent().getStringExtra("email"));
                    return params;
                }

            };
            queue.add(postRequest);

        }
    }


    @Override
    public void getApagar(final String conta_email, final String conta_pass) {
        showAlert("A processar...");

        //verifica os parametros dados
        if(!conta_email.equals(getIntent().getStringExtra("email"))){
            showAlert("Não foi possível apagar a conta");

        }

        else{

//é utilizado volley
//use "APPLOG" no Logcat para ver o que é feito com mais detalhe
// é devolvido um JSon, onde o seu conteúdo será processsado, caso não haja erros de PHP/SQL

            RequestQueue queue = Volley.newRequestQueue(definicoes.this);
            String url = apagaConta;

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("APPLOG", response);

                            JSONObject jsonObject = null;

                            try {
                                jsonObject = new JSONObject(response);

                                //conta apagada
                                //"sai" da conta que foi apagada
                                if(jsonObject.get("CONTA").equals("APAGADA")){

                                    //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                                    showAlert("A conta foi apagada com sucesso");
                                    startActivity(new Intent(definicoes.this,Inicio.class));
                                    finish();

                                }

                                //não encontrou a conta
                                if(jsonObject.get("CONTA").equals("NAOENCONTRADA")){

                                    //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                                    showAlert("Não foi possível apagar a conta, verifique os dados inseridos");
                                }

                            }catch (JSONException e){

                            }

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d("APPLOG", error.toString());
                            showAlert("Sem conexão!");


                        }
                    }
            ){
                @Override
                protected Map<String,String> getParams()
                {
 //parametros enviados para o ficheiro php
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("post_email", getIntent().getStringExtra("email"));
                    params.put("post_pass", conta_pass);

                    return params;
                }

            };
            queue.add(postRequest);

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_definicoes,menu);
        return true;
    }


    //icons toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //para "sair" da conta
        if(id== R.id.exit1){
            Intent intent = new Intent(getBaseContext(), Inicio.class);
            startActivity(intent);
            finish();
        }

        return true;
    }


//toast
    private void showAlert(String msg) {
        Toast.makeText(definicoes.this, msg,
                Toast.LENGTH_LONG).show();
    }
}
