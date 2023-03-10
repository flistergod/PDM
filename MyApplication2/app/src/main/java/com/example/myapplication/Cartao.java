package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
 * Esta classe mostra as empresas fidelizadas do cliente
 * 1º é feito o fetch das empresas na base de dados
 * 2º os dados que vieram do "return" são guardados em arrays de strings
 * 3º é calculado o tamanho da listview pelo o número de items
 * 4º é feito o adaptar das empresas fidelizadas com os arrays de strings
 * 5º o adaptar é associado à listview
 * 6º é feito um clickListener no item da listview, caso o cliente queira ver a empresa
 * 7º é criado uma editText que é um "filtro" que filtra a listview pelo nome
 * 8º um dos arrays é dos emails das empresas, este nunca aparece na lsitview, serve  para enviar o email dessa
 * empresa para outra activitie, neste caso a activitie de uma empresa fidelizada, onde com o email, é possível ir buscar
 * à base de dados os dados dessa empresa*
 * --->o filtro de procura não funciona, só lista o 1º item */

public class Cartao extends Fragment {

    //variaveis
    String urladdress_empresas = "http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/empresasFidelizadas";
    String[] name;
    String[] imagepath;
    String[] email;
    ListView listView_empresasF;
    int tamanho_list_empresa;
    private ListView_empresas_nf empresas;
    EditText filter;

    BufferedInputStream is;
    String line = null;
    String result = null;

    public Cartao() {
        // Required empty public constructor
    }

    private  ArrayAdapter adapter;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cartao, container, false);

        setHasOptionsMenu(true);

        //listview
        listView_empresasF = (ListView) v.findViewById(R.id.lv_empresasFi);

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));

        collectData();   //vai buscar os dados à base de dados
        if(tamanho_list_empresa==0){tamanho_list_empresa=1;}


        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96 * tamanho_list_empresa, getResources().getDisplayMetrics());


        ViewGroup.LayoutParams params = listView_empresasF.getLayoutParams();
        params.height = height;

        listView_empresasF.setLayoutParams(params);   //muda o tamanho da listview consoante o nº de items


        filter= (EditText)v.findViewById(R.id.filter_cartao);   //filtro da listview

        //verifica se há items para listar
        if(name!=null){
       empresas = new ListView_empresas_nf(getActivity(), name, imagepath, email);    //adapter
        listView_empresasF.setAdapter(empresas);
           empresas.notifyDataSetChanged();
        }
        else{showAlert("não tem empresas para listar");}//associaão do adapter à listview

        //aplicação do filtro
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                (Cartao.this).empresas.getFilter().filter(s);
                empresas.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //deteta o clique num item
        //muda para activitie de uma empresa fidelizada com o seu email e o seu nome
        // o nome serve para mudar o nome da toolbar da activitie destino
        listView_empresasF.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent= new Intent(getActivity(),empresaFidelizada.class);
                intent.putExtra("empresa",name[position]);
                intent.putExtra("email",getActivity().getIntent().getStringExtra("email"));
                intent.putExtra("empresa_email", email[position]);

                startActivity(intent);
               // Toast.makeText(getActivity(), name[position],
                       // Toast.LENGTH_LONG).show();
            }
        });




        return v;


    }



    //metodo prático para fazer toast message
    private void showAlert(String msg) {
        Toast.makeText(getActivity(), msg,
                Toast.LENGTH_LONG).show();
    }

    //coleta os dados necessários da base de dados
    private void collectData() {

        //Empresas
        //Connection
        try{

            URL url=new URL(urladdress_empresas);
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);

            //o post data é "levado" para o php como parametros
            OutputStream outputStream= con.getOutputStream();
            BufferedWriter writer= new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data= URLEncoder.encode("post_email","UTF-8")+"="+URLEncoder.encode(getActivity().getIntent().getStringExtra("email"),"UTF-8");
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
            //preenchimento dos arrays
            JSONArray ja=new JSONArray(result);
            JSONObject jo=null;
            name=new String[ja.length()];
            imagepath=new String[ja.length()];
            email=new String[ja.length()];

            tamanho_list_empresa=ja.length();

            for(int i=0;i<=ja.length();i++){
                jo=ja.getJSONObject(i);
                name[i]=jo.getString("NOMEEMPRESA");
                imagepath[i]=jo.getString("FOTO_PERFIL");
                email[i]=jo.getString("EMAIL");
            }
        }
        catch (Exception ex)
        {

            ex.printStackTrace();
        }
    }

}