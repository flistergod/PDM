package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*Esta classe "faz o corpo" de cada item da listview das campanhas*/
public class ListView_campanhas extends ArrayAdapter<String> {
//variaveis
    private String[] campanha_titulo;
    private String[] empresaname;
    private Activity context;


    public ListView_campanhas(Activity context, String[] campanha_titulo, String[] empresaname) {
        super(context, R.layout.lv_campanhas,campanha_titulo);
        this.context=context;
        this.campanha_titulo=campanha_titulo;
        this.empresaname=empresaname;
    }

    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View r=convertView;
        ViewHolder viewHolder=null;
        if(r==null){
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.lv_campanhas,null,true);
            viewHolder=new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder)r.getTag();

        }

        viewHolder.tvw1.setText(campanha_titulo[position]);
        viewHolder.tvw2.setText(empresaname[position]);


        return r;
    }

    class ViewHolder{

        TextView tvw1;
        TextView tvw2;


        ViewHolder(View v){
            tvw1=(TextView)v.findViewById(R.id.tvcampanha_titulo);
            tvw2=(TextView) v.findViewById(R.id.tvempresaname);
        }

    }



}
