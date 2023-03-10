package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.example.myapplication.mail.GMailSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*Esta classe mostra todas as funcionalidades de uma empresa fidelizada
* Campanhas, cartão, transações, informações, telefonar, enviar email, desfidelizar, direções para a morada da empresa
* */
public class empresaFidelizada extends AppCompatActivity implements recomenda.recomendaListener {


    String desfidelizar="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/desfidelizar";
    String getMoradaEmpresa="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/getMoradaEmpresa";
    String getTelefoneEmpresa="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/getTelefoneEmpresa";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_fidelizada);

        //toolbar
        Toolbar toolbar =findViewById(R.id.toolbar_empresaF);
        setSupportActionBar(toolbar);
        //muda titulo
        toolbar.setTitle(getIntent().getStringExtra("empresa"));//mudar

//caso clique na "seta" da toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        Button perfil = (Button)findViewById(R.id.button_info);
        Button campanhas = (Button)findViewById(R.id.button_campanhas);
        Button cartao = (Button)findViewById(R.id.button_cartao);
        Button transacoes = (Button)findViewById(R.id.button_transacoes);
        Button recomendar = (Button)findViewById(R.id.button_recomendar);
        Button ligar = (Button)findViewById(R.id.button_ligar);
        Button enviar = (Button)findViewById(R.id.button_enviar_email);
        Button localizacao = (Button)findViewById(R.id.button_localizacao);
        Button desfedelizar = (Button)findViewById(R.id.button_desfidelizar);


        //muda de activitie para o perfil da empresa, com os necessários extras que contém as
        // informações para fazer o fetch dos dados na base de dados
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(empresaFidelizada.this,perfil_empresaf.class);
                intent.putExtra("empresa",getIntent().getStringExtra("empresa"));
                intent.putExtra("email",getIntent().getStringExtra("email"));
                intent.putExtra("empresa_email", getIntent().getStringExtra("empresa_email"));

                startActivity(intent);
            }
        });



        //muda de activitie para as campanhas da empresa, com os necessários extras que contém as
        // informações para fazer o fetch dos dados na base de dados
        campanhas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(empresaFidelizada.this,Campanhas.class);
                intent.putExtra("empresa_email",getIntent().getStringExtra("empresa_email"));
                intent.putExtra("email",getIntent().getStringExtra("email"));
                startActivity(intent);
            }
        });


        //muda de activitie para  o cartão da empresa, com os necessários extras que contém as
        // informações para fazer o fetch dos dados na base de dados
        cartao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(empresaFidelizada.this,Cartao_empresa.class);
                intent.putExtra("empresa_email",getIntent().getStringExtra("empresa_email"));
                intent.putExtra("email",getIntent().getStringExtra("email"));
                startActivity(intent);
            }
        });

        //muda de activitie para as transações do cliente relativas à empresa da empresa, com os necessários extras que contém as
        // informações para fazer o fetch dos dados na base de dados
        transacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(empresaFidelizada.this,transacoes.class);
               intent.putExtra("email",getIntent().getStringExtra("email"));
                intent.putExtra("empresa_email",getIntent().getStringExtra("empresa_email"));
                startActivity(intent);
            }
        });


        //cria um alertdialog a pedir o email do amigo do cliente
        //é enviado um email a esse amigo a recomendar esta empresa e a APP
        recomendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openDialog();
            }
        });

//faz um intent com ACTION_DIAL com o numero de telefone da empresa
        ligar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String empresa_email=getIntent().getStringExtra("empresa_email");
                ligaEmpresa(empresa_email);

            }
        });


        //cria um ACTION_SEND para enviar um email à empresa
        //o email já é inserido automaticamente
        // o cliente só escolhe que aplicação usar (foi testado com gmail)
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=getIntent().getStringExtra("empresa_email");
                Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setData(Uri.parse("mailto:"));
    intent.setType("text/plain");
    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
    try{
startActivity(Intent.createChooser(intent, "Escolha um dos abaixo:"));

    } catch (Exception e){
        showAlert(e.getMessage());

    }

            }
        });



        //faz intent com ACTION_VIEW par ao google maps
        //contendo o caminho entre a localização do cliente e a morada da empresa
        localizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String empresa_email=getIntent().getStringExtra("empresa_email");


                DirecionaCliente(empresa_email);

            }
        });


        //desfideliza o cliente da empresa
        //o client eé redirecionada à página inicial
        desfedelizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= getIntent().getStringExtra("empresa_email");
               String email_cliente=getIntent().getStringExtra("email");
                desfidelizar(email, email_cliente);
            }
        });



    }
//desfideliza na base de dados e atualiza a listview das empresas fidelizadas
    public  void  desfidelizar(final String email, final String email_cliente){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url =desfidelizar;



        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APPLOG", response);
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(response);

                            if(jsonObject.get("MSG").equals("OK")){

                                Intent intent = new Intent(empresaFidelizada.this,homePage.class);
                                intent.putExtra("email",getIntent().getStringExtra("email"));
                                setResult(RESULT_OK,intent);
                                startActivity(intent);
                                finish();



                            }
                            else{showAlert("Nao foi possivel  desfidelizar");}




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
                params.put("post_email_empresa", email);
                params.put("post_email", email_cliente);
                return params;
            }

        };
        queue.add(postRequest);


    }

    //com o email da empresa, retira-se a morada da base de dados
    // e é feito o intent para o google maps com os extras necessários
    public void DirecionaCliente(final String email){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url =getMoradaEmpresa;



        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APPLOG", response);
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(response);

                            if(jsonObject.get("MSG").equals("OK")){

                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse("google.navigation:q="+jsonObject.get("MORADA").toString()));
                                startActivity(intent);

                            }
                            else{showAlert("Nao foi possivel ligar à empresa");}




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
                params.put("post_email", email);
                return params;
            }

        };
        queue.add(postRequest);


    }

    //com o email da empresa, retira-se o telefone da base de dados
    // e é feito o intent para o "ligar do android" com os extras necessários
    public  void  ligaEmpresa(final String email){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url =getTelefoneEmpresa;



        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APPLOG", response);
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(response);

                            if(jsonObject.get("MSG").equals("OK")){

                                String phone = jsonObject.get("TELEFONE").toString();
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                                startActivity(intent);

                            }
                            else{showAlert("Nao foi possivel ligar à empresa");}




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
                params.put("post_email", email);
                return params;
            }

        };
        queue.add(postRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_home,menu);
        return true;
    }

    //icons da toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
// icons da toolbar
        if(id== R.id.settings){
            Intent intent = new Intent(getBaseContext(), definicoes.class);
            intent.putExtra("email",getIntent().getStringExtra("email"));
            startActivity(intent);
        }
        else if(id== R.id.exit){
            Intent intent = new Intent(getBaseContext(), Inicio.class);
            startActivity(intent);
            finish();
        }

        return true;
    }


//toast message
    private void showAlert(String msg) {
        Toast.makeText(empresaFidelizada.this, msg,
                Toast.LENGTH_LONG).show();
    }

//envia email
    //o email (corpo, assunto, etc... já estão pré-defenidos)
    private  String  buttonSend_onClick(String email){
        String novaPass="email enviado com sucesso";

        try{

            //corpo do email enviado para o cliente
            String subject = "Recomendação do seu amigo";
            String body = "Foi-nos dito por: " + getIntent().getStringExtra("email") + ", que poderá gostar desta empresa: "+getIntent().getStringExtra("empresa")+", que se encontra na nossa APP - BIZZ BIZZ";
            String name = "Bizdirect Tecnical SuportTeam<br>";
            String phone = "Contacto para informações adicionais:<br>Telefone: +351 210 100 520<br>Email: contact@bizdirect.pt";
            String address = "Lisboa: Rua Viriato, 13 1050-233 Lisboa<br> Porto: Lugar do Espido, Via Norte 4470-177 Maia<br>" +
                    "Viseu: Bizdirect Competence Center Campus Politécnico 3504-510 Viseu<br>";
            String toAddress = email;
            String fromAddress="contact@bizdirect.pt";
            String content = "Nome: " + name;
            content += "<br>Moradas:<br> " + address;
            content += "<br>" + phone;
            content += "<br><br>Informação: " + body;


            boolean result = new SendMailAssync().execute(fromAddress, toAddress, subject,
                    content).get();


        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }

        return novaPass;

    }

    private class SendMailAssync extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                //este email é um email provisório só para mostrar que funciona
                String fromAdress = strings[0];
                String toAddress = strings[1];
                String subject = strings[2];
                String body = strings[3];
                GMailSender gMailSender = new GMailSender("nelson.andrade98@gmail.com",
                        "Leitinho1.");
                gMailSender.sendMail(subject,body,fromAdress,toAddress);
                return true;

            }catch (Exception e){
                return false;

            }
        }
    }

    public  void openDialog(){


        recomenda recomendaDialog = new recomenda();
        recomendaDialog.show(getSupportFragmentManager(), "recomenda dialog");

    }


    @Override
    public void enviaEmail( final String email) {

        showAlert(buttonSend_onClick(email));



    }


}


