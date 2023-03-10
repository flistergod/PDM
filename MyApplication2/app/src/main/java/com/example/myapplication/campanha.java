package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

/*Esta classe mostra os dados de uma campanha*/
public class campanha extends AppCompatActivity {

    //variaveis
    String campanha_especifica="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/campanha_especifica";
    TextView data_inicio, data_fim, desconto, titulo, empresa,
            produto, localizacao, genero, ec, idade, animais, codigo, local, edit_genero, edit_ec, edit_animais, editIdade;
    Button btn_usar, btn_info;
    String id_campanha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campanha);



        //toolbar
        Toolbar toolbar =findViewById(R.id.toolbar_campanha);
        setSupportActionBar(toolbar);

        data_inicio = (TextView)findViewById(R.id.edit_data_inicio);
        data_fim = (TextView)findViewById(R.id.edit_data_fim);
        desconto = (TextView)findViewById(R.id.desconto);
        titulo = (TextView)findViewById(R.id.titulo);
        empresa = (TextView)findViewById(R.id.nome_empresa);
        produto = (TextView)findViewById(R.id.produto_alvo);
        localizacao = (TextView)findViewById(R.id.localizacao);
        local = (TextView)findViewById(R.id.edit_localizacao);
        genero = (TextView)findViewById(R.id.genero);
        edit_genero = (TextView)findViewById(R.id.edit_genero);
        ec = (TextView)findViewById(R.id.estadoCivil);
        edit_ec = (TextView)findViewById(R.id.edit_estadocivil);
        idade = (TextView)findViewById(R.id.idade);
        editIdade = (TextView)findViewById(R.id.edit_idade);
        animais = (TextView)findViewById(R.id.animais);
        edit_animais= (TextView)findViewById(R.id.edit_animais);
        codigo = (TextView)findViewById(R.id.codigo);


        btn_info = (Button)findViewById(R.id.btn_info);
        btn_usar = (Button)findViewById(R.id.btn_usar);



        //há elementos mais específicios que ficam escondidos, só aparecem se o utilizador quiser
        codigo.setVisibility(View.GONE);

        localizacao.setVisibility(View.GONE);
        local.setVisibility(View.GONE);
        genero.setVisibility(View.GONE);

        edit_genero.setVisibility(View.GONE);

        ec.setVisibility(View.GONE);
        edit_ec.setVisibility(View.GONE);
        idade.setVisibility(View.GONE);
        editIdade.setVisibility(View.GONE);
        animais.setVisibility(View.GONE);
        edit_animais.setVisibility(View.GONE);


        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(localizacao.getVisibility()== View.GONE) {
                    localizacao.setVisibility(View.VISIBLE);
                    local.setVisibility(View.VISIBLE);
                    genero.setVisibility(View.VISIBLE);
                    edit_genero.setVisibility(View.VISIBLE);
                    ec.setVisibility(View.VISIBLE);
                    edit_ec.setVisibility(View.VISIBLE);
                    idade.setVisibility(View.VISIBLE);
                    editIdade.setVisibility(View.VISIBLE);
                    animais.setVisibility(View.VISIBLE);
                    edit_animais.setVisibility(View.VISIBLE);
                    btn_info.setText("MOSTRAR MENOS");
                }
                else{
                    localizacao.setVisibility(View.GONE);
                    local.setVisibility(View.GONE);
                    genero.setVisibility(View.GONE);
                    edit_genero.setVisibility(View.GONE);
                    ec.setVisibility(View.GONE);
                    edit_ec.setVisibility(View.GONE);
                    idade.setVisibility(View.GONE);
                    editIdade.setVisibility(View.GONE);
                    animais.setVisibility(View.GONE);
                    edit_animais.setVisibility(View.GONE);
                    btn_info.setText("INFORMAÇÕES ADICIONAIS");

                }
            }
        });


        btn_usar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_usar.setVisibility(View.GONE);

                if(codigo.getVisibility()== View.GONE) {
                    codigo.setVisibility(View.VISIBLE);
                }
                else{
                    codigo.setVisibility(View.GONE);
                }
            }
        });




        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                codigo.setVisibility(View.GONE);

            }
        });

        mudaDados();


    }


    //retira os dados da campanha na base de dados e mostra-os

    private void mudaDados() {


        RequestQueue queue = Volley.newRequestQueue(this);
        String url =campanha_especifica;



        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APPLOG", response);
                        parseJson(response); //processa o JSON recebido


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
                //parametros usados no ficheiro php
                Map<String,String> params = new HashMap<String,String>();
                params.put("post_id_campanha", getIntent().getStringExtra("id_campanha"));


                return params;
            }

        };
        queue.add(postRequest);
    }

    public  String  geraCodigo(int n){

// chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }


    //processa os dados que vieram da base de dados
    public  void parseJson(String jsonStr){

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonStr);

            if(jsonObject.get("MSG").equals("OK")){
                //se foi encontrada a conta do cliente, será alterado o texto nas textviews;
//é adicionado um "espaço" entre os conteudos inseridos nas textviews, pois fica melhor estéticamente


                data_inicio.setText(" "+jsonObject.get("DATA_INICIO").toString()+" ");
                data_fim.setText(" "+jsonObject.get("DATA_FIM").toString()+" ");
                desconto.setText(" "+jsonObject.get("DESCONTO").toString()+"% ");
                titulo.setText(" "+jsonObject.get("TITULO").toString()+" ");
                empresa.setText(" "+jsonObject.get("EMPRESA").toString()+" ");
                produto.setText(" "+jsonObject.get("PRODUTO_ALVO").toString()+" ");
                local.setText(" "+jsonObject.get("LOCALIZACAO").toString()+" ");
                edit_genero.setText(" "+jsonObject.get("GENERO").toString()+" ");
                edit_ec.setText(" "+jsonObject.get("ESTADOCIVIL").toString()+" ");
                editIdade.setText(" "+jsonObject.get("IDADE").toString()+" ");
                edit_animais.setText(" "+jsonObject.get("ANIMAIS").toString()+" ");
                codigo.setText(" "+jsonObject.get("CODIGO").toString()+" ");



            }

            if(jsonObject.get("LOCALIZACAO").toString()=="null"){
                local.setText(" Não incluído ");
                local.setTextColor(Color.RED);

            }

            if(jsonObject.get("GENERO").toString()=="null"){

                edit_genero.setText(" Não incluído ");
                edit_genero.setTextColor(Color.RED);

            }

            if(jsonObject.get("ESTADOCIVIL").toString()=="null"){
                edit_ec.setText(" Não incluído ");
                edit_ec.setTextColor(Color.RED);

            }

            if(jsonObject.get("IDADE").toString()=="null"){
                editIdade.setText(" Não incluído ");
                editIdade.setTextColor(Color.RED);

            }

            if(jsonObject.get("ANIMAIS").toString()=="null"){
                edit_animais.setText(" Não incluído ");
                edit_animais.setTextColor(Color.RED);

            }


            if(jsonObject.get("MSG").equals("NOTOK")){
                //conta não encontrada

                //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                showAlert(jsonObject.get("Não foi possível carregar a campanha").toString());
                // startActivity(new Intent(this,homePage.class));
            }


        }catch (JSONException e){

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_home,menu);
        return true;
    }


    //icons toolbar
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


//faz toast message
    private void showAlert(String msg) {
        Toast.makeText(campanha.this, msg,
                Toast.LENGTH_LONG).show();
    }
}
