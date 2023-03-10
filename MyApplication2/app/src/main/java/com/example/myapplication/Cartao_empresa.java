package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
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

/*Esta classe apresenta os dados do cartão de uma empresa fidelizada
* São retirados os dados necessários da base de dados para construir o "perfil" do cartão
* Caso o cliente tenha feito trnasações suficientes/ ganho carimbos suficientes, pode desbloquear o código do desconto ao
* clicar no botão, se o desbloquear, o código é redimido e já não o pode usar/desbloquear outra vez
*
* */
public class Cartao_empresa extends AppCompatActivity {

    //variaveis
    String url_cartao="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/cartao";
    String url_usado="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/verificaCartao";
    String mandaCodigo="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/updateCartao";
    String email_cliente;
    String email_empresa;
    TextView data_inicio, data_fim, desconto, empresa, produto, codigo, carimbos, carimbos_atuais, descricao, codigo_final;
    Button btn_usar;
    String geraString;
    int carimbos_int, carimbos_atuais_int;
    boolean foi_usado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartao_empresa);

        //toolbar
        Toolbar toolbar =findViewById(R.id.toolbar_campanha);
        setSupportActionBar(toolbar);
       email_cliente=getIntent().getStringExtra("email");
         email_empresa=getIntent().getStringExtra("empresa_email");



        data_inicio=(TextView)findViewById(R.id.edit_data_inicio);
        data_fim=(TextView)findViewById(R.id.edit_data_fim);
        desconto=(TextView)findViewById(R.id.desconto);
        empresa=(TextView)findViewById(R.id.nome_empresa);
        produto=(TextView)findViewById(R.id.edit_produto_alvo);
        codigo=(TextView)findViewById(R.id.edit_codigo);
        carimbos=(TextView)findViewById(R.id.edit_carimbos);
        carimbos_atuais=(TextView)findViewById(R.id.edit_carimbos_atuais);
        descricao=(TextView)findViewById(R.id.edit_descricao);
        codigo_final=(TextView)findViewById(R.id.codigo_final);
        geraString=geraPalavrapass(10);


        btn_usar = (Button)findViewById(R.id.btn_usar);

        //retira os dados do cartao
        getDados();


        //volta para a outra activitie ao clicar na seta

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                codigo_final.setVisibility(View.GONE);

            }
        });



        btn_usar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (carimbos_atuais_int != carimbos_int) {
                    showAlert("Não tem carimbos suficientes para usar o cartão");

                } else {

                    RequestQueue queue = Volley.newRequestQueue(Cartao_empresa.this);
                    String url = url_usado;


                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("APPLOG", response);
                                    JSONObject jsonObject = null;

                                    try {
                                        jsonObject = new JSONObject(response);


                                        if (jsonObject.get("MSG").equals("OK")) {

                                            if (jsonObject.get("CODIGO_CARTAO").toString() != "null") {

                                                showAlert("Já usou este cartão");


                                            } else {
                                                btn_usar.setVisibility(View.GONE);


                                                if (codigo_final.getVisibility() == View.GONE) {
                                                    codigo_final.setVisibility(View.VISIBLE);
                                                    mandaCodigo();

                                                } else {
                                                    codigo_final.setVisibility(View.GONE);
                                                }
                                            }




                                        }


                                        else {
                                            //conta não encontrada

                                            //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                                            showAlert("Ocorreu um erro a obter o código do cartão");
                                            // startActivity(new Intent(this,homePage.class));
                                        }


                                    } catch (JSONException e) {

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
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            //parametros usados no ficheiro php
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("post_email_empresa", email_empresa);
                            params.put("post_email", email_cliente);


                            return params;
                        }

                    };
                    queue.add(postRequest);


                }
            }
        });

    }

//redime o código
    //não deixa que seja usado outra vez
    public  void mandaCodigo(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url =mandaCodigo;



        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APPLOG", response);
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(response);

                            if(jsonObject.get("MSG").equals("OK")){
                                //se foi encontrada a conta do cliente, será alterado o texto nas textviews;
//é adicionado um "espaço" entre os conteudos inseridos nas textviews, pois fica melhor estéticamente


                               showAlert("Código redimido");


                            }


                            if(jsonObject.get("MSG").equals("NOTOK")){
                                //conta não encontrada

                                //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                                showAlert(jsonObject.get("Ocorreu um erro a obter o código do cartão").toString());
                                // startActivity(new Intent(this,homePage.class));
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
                //parametros usados no ficheiro php
                Map<String,String> params = new HashMap<String,String>();
                params.put("post_email_empresa", email_empresa );
                params.put("post_email", email_cliente);
                params.put("post_codigo", geraString);


                return params;
            }

        };
        queue.add(postRequest);



    }




public  void getDados(){


    RequestQueue queue = Volley.newRequestQueue(this);
    String url =url_cartao;



    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("APPLOG", response);
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(response);

                        if(jsonObject.get("MSG").equals("OK")){
                            //se foi encontrada a conta do cliente, será alterado o texto nas textviews;
//é adicionado um "espaço" entre os conteudos inseridos nas textviews, pois fica melhor estéticamente


                            empresa.setText(" "+jsonObject.get("EMPRESA").toString()+" ");
                            data_inicio.setText(" "+jsonObject.get("DATA_INICIO").toString()+" ");
                            data_fim.setText(" "+jsonObject.get("DATA_FIM").toString()+" ");
                            carimbos.setText(" "+jsonObject.get("CARIMBOS").toString()+" ");
                            desconto.setText(" "+jsonObject.get("DESCONTO").toString()+"% ");
                            descricao.setText(" "+jsonObject.get("DESIGNACAO").toString()+" ");
                            codigo.setText(" "+jsonObject.get("CODIGO").toString()+" ");
                            carimbos_atuais.setText(" "+jsonObject.get("CARIMBOS_ATUAIS").toString()+" ");
                            produto.setText(" "+jsonObject.get("PRODUTO").toString()+" ");
                            codigo_final.setText(" "+geraString+" ");

                            carimbos_int=Integer.parseInt(jsonObject.get("CARIMBOS").toString());
                            carimbos_atuais_int=Integer.parseInt(jsonObject.get("CARIMBOS_ATUAIS").toString());


                        }


                        else{
                            //conta não encontrada

                            //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                            showAlert("A empresa não tem o cartão disponível");
                            // startActivity(new Intent(this,homePage.class));

                            TextView data_inicio2 = (TextView)findViewById(R.id.data_inicio);
                            TextView datafim2= (TextView)findViewById(R.id.data_fim);
                            TextView produtos2= (TextView)findViewById(R.id.produto_alvo);
                            TextView carimbos2= (TextView)findViewById(R.id.carimbos);
                            TextView carimbos_atuais2= (TextView)findViewById(R.id.carimbos_atuais);
                            TextView codigo2= (TextView)findViewById(R.id.codigo);
                            TextView descricao2= (TextView)findViewById(R.id.edit_descricao);
                            btn_usar.setVisibility(View.GONE);
                            data_inicio2.setVisibility(View.GONE);
                            datafim2.setVisibility(View.GONE);
                            produtos2.setVisibility(View.GONE);
                            carimbos2.setVisibility(View.GONE);
                            data_inicio2.setVisibility(View.GONE);
                            carimbos_atuais2.setVisibility(View.GONE);
                            codigo2.setVisibility(View.GONE);
                            descricao2.setVisibility(View.GONE);

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
            //parametros usados no ficheiro php
            Map<String,String> params = new HashMap<String,String>();
            params.put("post_email_empresa", email_empresa );
            params.put("post_email", email_cliente);


            return params;
        }

    };
    queue.add(postRequest);
}

//gera código do cartão
    public  String  geraPalavrapass(int n){

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

//toast
    private void showAlert(String msg) {
        Toast.makeText(Cartao_empresa.this, msg,
                Toast.LENGTH_LONG).show();
    }

}
