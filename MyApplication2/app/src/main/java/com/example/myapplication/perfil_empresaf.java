package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
/*Esta classe mostra o perfil/informações de uma empresa fidelizada
* */
public class perfil_empresaf extends AppCompatActivity {

    //variaveis
    String perfil_empresa="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/perfil_empresa";

    TextView empresa, telefone, email, morada, descricao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_empresaf);



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





        empresa=(TextView)findViewById(R.id.donoEmpresa);
        telefone=(TextView)findViewById(R.id.edit_tlf);
        morada=(TextView)findViewById(R.id.edit_morada);
        email=(TextView)findViewById(R.id.edit_email);
        descricao=(TextView)findViewById(R.id.edit_descricao);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = perfil_empresa;



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
                params.put("post_email", getIntent().getStringExtra("empresa_email"));


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






//processa os dados do Json
    public  void parseJson(String jsonStr){

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonStr);

            if(jsonObject.get("MSG").equals("OK")){
                //se foi encontrada a conta do cliente, será alterado o texto nas textviews;
//é adicionado um "espaço" entre os conteudos inseridos nas textviews, pois fica melhor estéticamente
                empresa.setText(" "+jsonObject.get("NOME").toString()+" ");
                telefone.setText(" "+jsonObject.get("TELEFONE").toString()+" ");
                email.setText(" "+jsonObject.get("EMAIL").toString()+" ");
                morada.setText(" "+jsonObject.get("MORADA").toString()+" ");
                descricao.setText("\n  "+jsonObject.get("DESCRICAO").toString()+" ");

            }

            if(jsonObject.get("CONTA").equals("NAOENCONTRADA")){
                //conta não encontrada

                //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                showAlert(jsonObject.get("Não foi possível carregar o perfil").toString());
                // startActivity(new Intent(this,homePage.class));
            }


        }catch (JSONException e){

        }
    }


//toast message
    private void showAlert(String msg) {
        Toast.makeText(this, msg,
                Toast.LENGTH_LONG).show();
    }
}

