package com.example.vemar.gitsquare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Square_Contribs extends AppCompatActivity {

    RecyclerView recyclerView;
    JSONArray MainArray,SortArray;
    Boolean flag = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square__contribs);

        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getdata();

        ((SwipeRefreshLayout) findViewById(R.id.swiplayt)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((SwipeRefreshLayout) findViewById(R.id.swiplayt)).setRefreshing(false);
                getdata();
            }
        });
    }

    public void getdata(){
        StringRequest stringRequest =new StringRequest("https://api.github.com/repos/square/retrofit/contributors",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try  {

                            MainArray = new JSONArray(response);
                            SortArray = MainArray;
                            parsedata();
                        } catch (JSONException e) {
                            e.printStackTrace();
                             Toast.makeText(Square_Contribs.this,"Error Occured",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Square_Contribs.this,error.getMessage(),Toast.LENGTH_SHORT).show();
               // Toast.makeText(Square_Contribs.this,"Check Your Internet Connection",Toast.LENGTH_SHORT).show();
            }
        });

        //stringRequest.setShouldCache(false);
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public void parsedata(){
        try {
            JSONArray jsonArray = SortArray;

            List<dataadpater> list=new ArrayList<>();

            for(int i=0;i<jsonArray.length();i++){

                dataadpater adapter=new dataadpater();

                JSONObject jobj=jsonArray.getJSONObject(i);

                adapter.setName(jobj.getString("login"));
                adapter.setImage(jobj.getString("avatar_url"));
                adapter.setLink(jobj.getString("repos_url"));
                adapter.setCount(jobj.getString("contributions"));

                list.add(adapter);
            }

            recyclerView.setAdapter(new Recycle_adapter(list));

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(Square_Contribs.this,"Error Occured",Toast.LENGTH_SHORT).show();
        }
    }

    public void filterdata(View view) {

        try{

            List<JSONObject> jsonList = new ArrayList<>();
            SortArray = new JSONArray(new ArrayList<String>());

            for (int i = 0; i < MainArray.length(); i++) {
                jsonList.add(MainArray.getJSONObject(i));
            }
            if(flag){
                flag = false;
                Collections.sort(jsonList, new Comparator<JSONObject>() {
                    @SuppressLint("NewApi")
                    public int compare(JSONObject a, JSONObject b) {
                        int sort = 0;
                        try {
                            int valA = (int) a.get("contributions");
                            int valB = (int) b.get("contributions");
                            sort = Integer.compare(valA,valB);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Square_Contribs.this,"Error Occured",Toast.LENGTH_SHORT).show();
                        }
                        return sort;
                    }
                });
            }else{
                flag = true;
                Collections.sort(jsonList, new Comparator<JSONObject>() {
                    @SuppressLint("NewApi")
                    public int compare(JSONObject a, JSONObject b) {
                        int sort = 0;
                        try {
                            int valA = (int) a.get("contributions");
                            int valB = (int) b.get("contributions");
                            sort = Integer.compare(valB,valA);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(Square_Contribs.this,"Error Occured",Toast.LENGTH_SHORT).show();
                            }
                            return sort;
                        }
                    });
            }


            for (int i = 0; i < MainArray.length(); i++) {
                SortArray.put(jsonList.get(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(Square_Contribs.this,"Error Occured",Toast.LENGTH_SHORT).show();
        }

        parsedata();
    }

    private class Recycle_adapter extends RecyclerView.Adapter<Recycle_adapter.ViewHolder>{

        private List<dataadpater> listadap;
        Context context;

        Recycle_adapter(List<dataadpater> list){
            super();
            this.listadap = list;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView NameTv,linkTv,CountTv;
            CircleImageView ImageIv;
            ViewHolder(View iv) {
                super(iv);
                NameTv = (TextView) iv.findViewById(R.id.nametv);
                linkTv = (TextView) iv.findViewById(R.id.linktv);
                CountTv = (TextView) iv.findViewById(R.id.counttv);
                ImageIv = (CircleImageView) iv.findViewById(R.id.circle);
                context=iv.getContext();
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_recycle, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {
            dataadpater dadp = listadap.get(position);
            vh.NameTv.setText(dadp.getName());
            vh.linkTv.setText(dadp.getLink());
            vh.CountTv.setText("Contributions : "+dadp.getCount());

            Glide.with(context).load(dadp.getImage())
                    .placeholder(R.drawable.load)
                    .into(vh.ImageIv);
        }

        @Override
        public int getItemCount() {return listadap.size();}

    }

}
