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
/*Esta classe mostra os dados de uma transação*/
public class transacao extends AppCompatActivity {
    TextView titulo, edit_data_inicio, edit_descricao, edit_codigo;
    String id_transacao;
    String url_transacao="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/transacao";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transacao);


        Toolbar toolbar =findViewById(R.id.toolbar_transacao);
        setSupportActionBar(toolbar);
        //é preciso o id da transação da activitie anterior para mostrar os dados
        //da transação que estão na base de dados
id_transacao=getIntent().getStringExtra("id_transacao");


//vai para activitie anteriror no clique da "seta"
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


        titulo=(TextView)findViewById(R.id.transacao_id);
        edit_data_inicio=(TextView)findViewById(R.id.edit_data_inicio);
        edit_descricao=(TextView)findViewById(R.id.edit_descricao);
        edit_codigo=(TextView)findViewById(R.id.edit_codigo);
        titulo.setText(titulo.getText().toString()+" "+id_transacao+" ");

        //retira os dados da trnasação da base de dados
        getDados();


    }

    public  void  getDados(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url =url_transacao;



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
                params.put("post_id_transacao", id_transacao);


                return params;
            }

        };
        queue.add(postRequest);
    }

    public  void parseJson(String jsonStr){

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonStr);

            if(jsonObject.get("MSG").equals("OK")){



                edit_data_inicio.setText(" "+jsonObject.get("DATA_REGISTO").toString()+" ");
                edit_descricao.setText(" "+jsonObject.get("DESCRICAO_TRANSACAO").toString()+" ");
                  edit_codigo.setText(" "+jsonObject.get("CODIGO").toString()+" ");


            }


            if(jsonObject.get("MSG").equals("NOTOK")){
                //conta não encontrada

                //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                showAlert(jsonObject.get("Não foi possível carregar a transação").toString());
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
        Toast.makeText(transacao.this, msg,
                Toast.LENGTH_LONG).show();
    }
}
