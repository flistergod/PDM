package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
/*Esta classe mostra as empresas não fidelizadas*/
public class Procura extends Fragment {

    //variaveis
    String urladdress_empresas =  "http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/empresasNFidelizadas";
    String[] name;
    String[] imagepath;
    String[] email;
    ListView listView_empresasNF;
   private  ListView_empresas_nf _empresas;
  int  tamanho_list_empresa;
EditText filter;

    BufferedInputStream is;
    String line = null;
    String result = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_procura, container, false);

        listView_empresasNF = (ListView) v.findViewById(R.id.lv_empresasF); //listview
        filter= (EditText)v.findViewById(R.id.filter_procura); //filtro da listview

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        collectData(); //retira os dados da base de dados para a listview

        if(tamanho_list_empresa==0){tamanho_list_empresa=1;}

        //muda a altura da listview pelo nº de items
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96 * tamanho_list_empresa, getResources().getDisplayMetrics());


        ViewGroup.LayoutParams params = listView_empresasNF.getLayoutParams();
        params.height = height;



        listView_empresasNF.setLayoutParams(params);

        //verifica se existe items para listar

        if(name!=null){
        //associa o adapter à listview
        _empresas = new ListView_empresas_nf(getActivity(), name, imagepath, email);
        listView_empresasNF.setAdapter(_empresas);
            _empresas.notifyDataSetChanged();
        }else{showAlert("não tem empresas para listar");}

        // o filtro ajusta a listview pelo nome do item
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _empresas.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //ao clicar no item, muda de activitie com os extras necessários para mostrar os dados da empresa provenientes da
        //base de dados
        listView_empresasNF.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(getActivity(),empresaNfidelizada.class);
                intent.putExtra("empresa",name[position]);
                intent.putExtra("email",getActivity().getIntent().getStringExtra("email"));
                intent.putExtra("empresa_email", email[position]);

                startActivity(intent);
            }
        });


        return v;


    }
//toast message
    private void showAlert(String msg) {
        Toast.makeText(getActivity(), msg,
                Toast.LENGTH_LONG).show();
    }
//retira os dados das empresas não fidelizadas para serem colocadas no adapter e depois este associado à listview
    private void collectData() {
        //obtem os items

        //Empresas
        //Connection
        try{

            URL url=new URL(urladdress_empresas);
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);

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




