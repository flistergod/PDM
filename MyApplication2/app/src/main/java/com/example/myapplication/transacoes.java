package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import java.util.HashMap;
import java.util.Map;
/*Esta classe mostra as trnasações entre cliente e a empresa */
public class transacoes extends AppCompatActivity {
//variaveis
    String urladdress_transacoes="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/Transacoes";
    String[] ID_transacoes;
    ListView listView_transacoes;
    int tamanho_list_transacao;

    BufferedInputStream is;
    String line=null;
    String result=null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transacoes);
        Toolbar toolbar =findViewById(R.id.toolbar_transacoes);
        setSupportActionBar(toolbar);


//volta para a activitie anterior ao clicar na seta

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });



        listView_transacoes=(ListView) findViewById(R.id.lv_transacoes);


        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));

        //vai buscar os dados paa os items da listview
        collectData();

        //muda o tamanho da listview consoante o nº de items
        if(tamanho_list_transacao==0){tamanho_list_transacao=1;}

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96*tamanho_list_transacao, getResources().getDisplayMetrics());


        ViewGroup.LayoutParams params = listView_transacoes.getLayoutParams();

        params.height=height;

        listView_transacoes.setLayoutParams(params);

        //verifica se tem items para listar
if(ID_transacoes!=null){
        lv_transacoes  _transacoes=new lv_transacoes(this,ID_transacoes);


        listView_transacoes.setAdapter(_transacoes);}

        else{showAlert("não tem transações para listar");}


        //ao clicar numa transação
        //é redirecionada para os dados da transação com os extras necessários
        listView_transacoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(transacoes.this,transacao.class);
                intent.putExtra("id_transacao", ID_transacoes[position]);
                intent.putExtra("email",getIntent().getStringExtra("email"));
                startActivity(intent);
            }
        });


    }

//toast message
    private void showAlert(String msg) {
        Toast.makeText(transacoes.this, msg,
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


String cliente=getIntent().getStringExtra("email");
        String empresa=getIntent().getStringExtra("empresa_email");

        //Connection
        try{

            URL url2=new URL(urladdress_transacoes);
            HttpURLConnection con=(HttpURLConnection)url2.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);


            OutputStream outputStream= con.getOutputStream();
            BufferedWriter writer= new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data= URLEncoder.encode("post_email","UTF-8")+"="+URLEncoder.encode(cliente,"UTF-8")+"&"+URLEncoder.encode("post_email_empresa","UTF-8")+"="+URLEncoder.encode(empresa,"UTF-8");
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
            JSONArray ja=new JSONArray(result);
            JSONObject jo=null;
            ID_transacoes=new String[ja.length()];
            tamanho_list_transacao=ja.length();

            for(int i=0;i<=ja.length();i++){
                jo=ja.getJSONObject(i);
                ID_transacoes[i]=jo.getString("ID_TRANSACAO");
            }

        }
        catch (Exception ex)
        {

            ex.printStackTrace();
        }


    }
}
