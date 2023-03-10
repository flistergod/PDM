package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*Esta classe "faz o corpo" de cada item da listview das transações*/
public class lv_transacoes extends ArrayAdapter<String> {
    //variaveis
      private String[] empresaname;
    private Activity context;


    public lv_transacoes(Activity context , String[] empresaname) {
        super(context, R.layout.lv_transacoes,empresaname);
        this.context=context;
        this.empresaname=empresaname;
    }

    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View r=convertView;
        ViewHolder viewHolder=null;
        if(r==null){
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.lv_transacoes,null,true);
            viewHolder=new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder)r.getTag();

        }

        viewHolder.tvw2.setText("ID: "+empresaname[position]);


        return r;
    }

    class ViewHolder{

        TextView tvw2;


        ViewHolder(View v){
            tvw2=(TextView) v.findViewById(R.id.tvempresaname);

        }

    }



}

