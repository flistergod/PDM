package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/*
* Esta classe mostra as campanhas de uma empresa
* 1º é feito o fetch das campanhas na base de dados
* 2º os dados que vieram do "return" são guardados em arrays de strings
* 3º é calculado o tamanho da listview pelo o número de items
* 4º é feito o adapter das campanhas com os arrays de strings
* 5º o adapter é associado à listview
* 6º é feito um clickListener no item da listview, caso o cliente queira ver a campanha*/

public class Campanhas extends AppCompatActivity {

//variaveis
    String urladdress_campanhas="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/Campanhas";
    String[] titulo_campanhas;
    String[] empresaname;
    ListView listView_campanhas;
    int tamanho_list_campanha;

    BufferedInputStream is;
    String line=null;
    String result=null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campanhas);

        Toolbar toolbar =findViewById(R.id.toolbar_campanhas);
        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        }); //para voltar à activity anterior ao clicar na "seta"



        listView_campanhas=(ListView) findViewById(R.id.lv_campanhas_empresa);


        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));

         collectData();//retira as campanhas da base de dados


        //adapta o tamanho da listview
         if(tamanho_list_campanha==0){tamanho_list_campanha=1;}

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96*tamanho_list_campanha, getResources().getDisplayMetrics());


        ViewGroup.LayoutParams params = listView_campanhas.getLayoutParams();

        params.height=height;

        listView_campanhas.setLayoutParams(params);


        //verifica se existem campanhas para listar
        if(titulo_campanhas!=null){
        ListView_campanhas_todas  _campanhas=new ListView_campanhas_todas(this,titulo_campanhas,empresaname);


        listView_campanhas.setAdapter(_campanhas);}
        else{showAlert("não tem campanhas para listar");}


        //caso queira ver uma campanha
        listView_campanhas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent= new Intent(Campanhas.this,campanha.class);
               intent.putExtra("id_campanha", empresaname[position]);
                intent.putExtra("email",getIntent().getStringExtra("email"));
               startActivity(intent);
            }
        });


    }

    //toast message
    private void showAlert(String msg) {
        Toast.makeText(Campanhas.this, msg,
                Toast.LENGTH_LONG).show();
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void collectData() {

        //Campanhas
        BufferedInputStream is=null;
        String line=null;
        String result=null;


        //Connection
        try{

            URL url2=new URL(urladdress_campanhas);
            HttpURLConnection con=(HttpURLConnection)url2.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);

            //é no psot data onde se mete os parametros que vão ser usados no php/mvc
            OutputStream outputStream= con.getOutputStream();
            BufferedWriter writer= new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data= URLEncoder.encode("post_email","UTF-8")+"="+URLEncoder.encode(getIntent().getStringExtra("empresa_email"),"UTF-8");
            writer.write(post_data);
            writer.flush();
            writer.close();
            outputStream.close();
            is=new BufferedInputStream(con.getInputStream());

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        //content
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            StringBuilder sb=new StringBuilder();
            while ((line=br.readLine())!=null){
                sb.append(line+"\n");
            }
            is.close();
            result=sb.toString();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();

        }

//JSON
        try{
            //preenchimento dos arrays de strings para fazer o adapter
            JSONArray ja=new JSONArray(result);
            JSONObject jo=null;
            titulo_campanhas=new String[ja.length()];
            empresaname=new String[ja.length()];
            tamanho_list_campanha=ja.length();

            for(int i=0;i<=ja.length();i++){
                jo=ja.getJSONObject(i);
                titulo_campanhas[i]=jo.getString("TITULO");
                empresaname[i]=jo.getString("ID_CAMPANHA");
            }

        }
        catch (Exception ex)
        {

            ex.printStackTrace();
        }


    }
}
