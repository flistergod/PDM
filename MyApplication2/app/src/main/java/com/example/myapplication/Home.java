package com.example.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
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

import de.hdodenhof.circleimageview.CircleImageView;

/*Esta classe representa a página inicial da aplicação
* São mostradas até 5 campanhas das mais recentes de todas as fidelizações do cliente
*  São mostradas até 5 empresas não fidelizadas das mais recentes como recomendação ao cliente
*  Existe uma animação no nome da app
**/
public class Home extends Fragment {
    //variaveis
    TextView txt_animation;
    String urladdress_empresas= "http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/empresasRecentes";
    String urladdress_campanhas= "http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/campanhasRecentes";
    String getIDcampanha="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/getIDcampanha";
    String[] name;
    String[] imagepath;
    String[] email;
    String[] titulo_campanhas;
    String[] empresaname;
    ListView listView_empresas, listView_campanhas;
    int tamanho_list_campanha, tamanho_list_empresa;

    BufferedInputStream is;
    String line=null;
    String result=null;



    public Home() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_home, container, false);
        txt_animation = (TextView) v.findViewById(R.id.BizzAnim); //textview para aplicar a animação

    //    txt_animation.setText(getActivity().getIntent().getStringExtra("email"));
        startAnimation();//aplica a animação

        //retira as listviews
        listView_empresas=(ListView)v.findViewById(R.id.listview_fidelizacoes);
        listView_campanhas=(ListView)v.findViewById(R.id.listview_campanhas);

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        collectData(); //vai buscar os dados para as listviews

        if(tamanho_list_empresa==0){tamanho_list_empresa=1;}
        if(tamanho_list_campanha==0){tamanho_list_campanha=1;}

        //altera a altura das listviews pelo nº de items
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96*tamanho_list_empresa, getResources().getDisplayMetrics());


        ViewGroup.LayoutParams params = listView_empresas.getLayoutParams();
        params.height=height;

        listView_empresas.setLayoutParams(params);

         height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96*tamanho_list_campanha, getResources().getDisplayMetrics());


         params = listView_campanhas.getLayoutParams();
        params.height=height;

        listView_campanhas.setLayoutParams(params);




//icons para mostrar as listviews
        final CircleImageView btn_empresas = (CircleImageView) v.findViewById(R.id.dropEmpresas);
        final CircleImageView btn_campanhas = (CircleImageView) v.findViewById(R.id.dropCampanhas);
        final Drawable drawable = getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp);
        final Drawable drawable1 = getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp);

//ao clicar nesses icons
        //a visibilidade das listas passa para visivel
        //o icon muda de drawable
        //o adapter é associado à listview

        btn_empresas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listView_empresas.getVisibility() == View.GONE) {
                    listView_empresas.setVisibility(View.VISIBLE);
                    btn_empresas.setImageDrawable(drawable);

                    //verifica se há items para listar
                    if(name!=null) {
                        ListView_empresas_nf _empresas = new ListView_empresas_nf(getActivity(), name, imagepath, email);
                        listView_empresas.setAdapter(_empresas);
                    }else{showAlert("não tem campanhas para listar");}

                    listView_empresas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            //muda-se de activitie para o perfil de uma empresa não fidelizada com os extras necessários
                            //para retirar os dados da base de dados para o perfil da dita empresa
                            Intent intent= new Intent(getActivity(),empresaNfidelizada.class);
                            intent.putExtra("empresa",name[position]);
                            intent.putExtra("email",getActivity().getIntent().getStringExtra("email"));
                            intent.putExtra("empresa_email", email[position]);
                            startActivity(intent);
                        }
                    });
                }

                else{
                    listView_empresas.setVisibility(View.GONE);
                    btn_empresas.setImageDrawable(drawable1);


                }


            }
        });

        btn_campanhas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (listView_campanhas.getVisibility() == View.GONE) {
                    listView_campanhas.setVisibility(View.VISIBLE);
                    btn_campanhas.setImageDrawable(drawable);

                    if(titulo_campanhas!=null) {
                    ListView_campanhas _campanhas=new ListView_campanhas(getActivity(),titulo_campanhas,empresaname);
                    listView_campanhas.setAdapter(_campanhas);}
                    else{showAlert("não tem campanhas para listar");}

                    listView_campanhas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //muda-se de activitie para o perfil de uma campanha  com os extras necessários
                            //para retirar os dados da base de dados para o perfil da dita campanha
                            //mas para mostrar os dados da campanha temos de ir descobrir qual o ID da mesma na base de dados
                            getIDcampanha(empresaname[position],titulo_campanhas[position]);

                        }
                    });

                }

                else{
                    listView_campanhas.setVisibility(View.GONE);
                    btn_campanhas.setImageDrawable(drawable1);


                }


            }
        });
        return v;
    }

    public  void getIDcampanha(final String empresa_nome, final String titulo){

        //Sabendo o nome da empresa e o titulo da campanha, retira-se o id da campanha
        //muda-se de activitie com o id descoberto
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url =getIDcampanha;



        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APPLOG", response);
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(response);

                            if(jsonObject.get("MSG").equals("OK")){

                                Intent intent= new Intent(getActivity(),campanha.class);
                                intent.putExtra("id_campanha", jsonObject.get("ID").toString());
                                intent.putExtra("email",getActivity().getIntent().getStringExtra("email"));
                                showAlert( getActivity().getIntent().getStringExtra("email"));
                                startActivity(intent);



                            }
                            else{showAlert("Nao foi possivel carregar a campanha");}




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
                params.put("post_titulo", titulo);
                params.put("post_empresa", empresa_nome);
                return params;
            }

        };
        queue.add(postRequest);
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    //retira os dados da base de dados para arrays de strings
    //estes arrays serão usados nos adapters para atribuir valores aos layouts de cada item


    //retira os dados das empresas não fidelizadas e das campanhas para listar
    private void collectData() {
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
            titulo_campanhas=new String[ja.length()];
            empresaname=new String[ja.length()];
            tamanho_list_campanha=ja.length();

            for(int i=0;i<=ja.length();i++){
                jo=ja.getJSONObject(i);
                titulo_campanhas[i]=jo.getString("TITULO");
                empresaname[i]=jo.getString("NOMEEMPRESA");
            }

        }
        catch (Exception ex)
        {

            ex.printStackTrace();
        }


    }

//toast message
    private void showAlert(String msg) {
        Toast.makeText(getActivity(), msg,
                Toast.LENGTH_LONG).show();
    }
//inicia a animação do "BIZZBIZZ"
    private  void startAnimation(){
        Animation anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim2);
        txt_animation.startAnimation(anim);


    }


}
