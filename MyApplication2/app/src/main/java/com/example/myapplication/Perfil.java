package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
/*Esta classe "mostra os dados do cliente"*/
public class Perfil extends Fragment {

//variaveis
    TextView nome, telefone, datanasc, morada, ec, genero, animais, nif, cc, registo;
    String perfil="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/perfil";

    public Perfil() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_perfil, container, false);

//inicialização das variaveis
        nome=(TextView)v.findViewById(R.id.getNome);
        telefone=(TextView)v.findViewById(R.id.getTlf);
        datanasc=(TextView)v.findViewById(R.id.getdata);
        morada=(TextView)v.findViewById(R.id.getmorada);
        ec=(TextView)v.findViewById(R.id.getec);
        genero=(TextView)v.findViewById(R.id.getgenero);
        animais=(TextView)v.findViewById(R.id.getanimais);
        nif=(TextView)v.findViewById(R.id.getnif);
        cc=(TextView)v.findViewById(R.id.getcc);
        registo=(TextView)v.findViewById(R.id.get_registo);

//carregamento dos dados do cliente nas variaveis usando volley

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = perfil;



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
                params.put("post_email", getActivity().getIntent().getStringExtra("email"));


                return params;
            }

        };
        queue.add(postRequest);

        //deteta o clique de alterar perfil e muda de activitie
        Button button_edita = (Button)v.findViewById(R.id.button_editar);
        button_edita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(),EditaPerfil.class);
                //envia o email, pois é necessário na outra activitei para outras funcionalidades
               intent.putExtra("email", getActivity().getIntent().getStringExtra("email"));
                startActivity(intent);
            }
        });


        return v;
    }




//processa Json
    public  void parseJson(String jsonStr){

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonStr);

            if(jsonObject.get("MSG").equals("OK")){
                //se foi encontrada a conta do cliente, será alterado o texto nas textviews;
//é adicionado um "espaço" entre os conteudos inseridos nas textviews, pois fica melhor estéticamente
              nome.setText(" "+jsonObject.get("NOME").toString()+" ");
                telefone.setText(" "+jsonObject.get("TELEFONE").toString()+" ");
                datanasc.setText(" "+jsonObject.get("DATA").toString()+" ");
                morada.setText(" "+jsonObject.get("MORADA").toString()+" ");
                ec.setText(" "+jsonObject.get("EC").toString()+" ");
                genero.setText(" "+jsonObject.get("GENERO").toString()+" ");
                nif.setText(" "+jsonObject.get("NIF").toString()+" ");
                cc.setText(" "+jsonObject.get("CC").toString()+" ");
                registo.setText(" "+jsonObject.get("REGISTO").toString()+" ");

                if(jsonObject.get("ANIMAIS").equals("Sim")) {
                    animais.setText(" sim ");
                }else{
                    animais.setText(" não ");
                }



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
        Toast.makeText(getActivity(), msg,
                Toast.LENGTH_LONG).show();
    }

}
