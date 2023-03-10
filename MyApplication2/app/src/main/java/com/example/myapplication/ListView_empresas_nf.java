package com.example.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/*Esta classe "faz o corpo" de cada item da listview das empresas*/
public class ListView_empresas_nf extends ArrayAdapter<String> {
//variaveis
    private String[] profilename;
    private String[] imagepath;
    private String[] email;
    private Activity context;
    Bitmap bitmap;

    public ListView_empresas_nf(Activity context, String[] profilename, String[] imagepath, String[] email) {
        super(context, R.layout.lv_empresas_nf,profilename);
        this.context=context;
        this.profilename=profilename;
        this.imagepath=imagepath;
        this.email=email;
    }

    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View r=convertView;
        ViewHolder viewHolder=null;
        if(r==null){
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.lv_empresas_nf,null,true);
            viewHolder=new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder)r.getTag();

        }

        viewHolder.tvw1.setText(profilename[position]);
        viewHolder.tvw2.setText(email[position]);

        return r;
    }

    class ViewHolder{

        TextView tvw1;
        TextView tvw2;
        CircleImageView ivw;

        ViewHolder(View v){
            tvw1=(TextView)v.findViewById(R.id.tvprofilename);
            tvw2=(TextView)v.findViewById(R.id.empresa_email);
            ivw=(CircleImageView)v.findViewById(R.id.imageView);
        }

    }

    public class GetImageFromURL extends AsyncTask<String, Void, Bitmap>
    {

        CircleImageView imgView;
        public GetImageFromURL(CircleImageView imgv)
        {
            this.imgView=imgv;
        }
        @Override
        protected Bitmap doInBackground(String... url) {
            String urldisplay=url[0];
            bitmap=null;

            try{

                InputStream ist=new java.net.URL(urldisplay).openStream();
                bitmap= BitmapFactory.decodeStream(ist);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){

            super.onPostExecute(bitmap);
            imgView.setImageBitmap(bitmap);
        }
    }



}
