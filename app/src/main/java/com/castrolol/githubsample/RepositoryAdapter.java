package com.castrolol.githubsample;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.castrolol.githubsample.models.GHRepo;
import com.castrolol.githubsample.models.GHUser;
import com.google.gson.Gson;

import org.json.JSONArray;

/**
 * Created by 'Luan on 25/06/2016.
 */

public class RepositoryAdapter extends ArrayAdapter<GHRepo> {

    final String url;
    final RequestQueue queue;
    final Gson gson = new Gson();


    public RepositoryAdapter(Context context, String url) {
        super(context, R.layout.item_repository, R.id.tv_nome);

        this.url = url;
        this.queue = Volley.newRequestQueue(context);
    }

    public void setUser(String username) {

        Request request = new JsonArrayRequest(Request.Method.GET, url + username + "/repos", null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    GHRepo[] repos = gson.fromJson(response.toString(), GHRepo[].class);
                    processRepos(repos);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Erroo... miau... " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        });

        queue.add(request);

    }

    private void processRepos(GHRepo[] repos) {

        addAll(repos);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view =  super.getView(position, convertView, parent);
        final GHRepo repo = getItem(position);


        TextView textNome = (TextView)view.findViewById(R.id.tv_nome);
        TextView textLang = (TextView)view.findViewById(R.id.tv_lang);
        TextView textDesc = (TextView)view.findViewById(R.id.tv_descricao);
        TextView textVis = (TextView)view.findViewById(R.id.tv_visualizacoes);
        TextView textIssues = (TextView)view.findViewById(R.id.tv_issues);
        TextView textForks = (TextView)view.findViewById(R.id.tv_forks);
        ImageView imgIcon = (ImageView)view.findViewById(R.id.iv_icon);


        textNome.setText(repo.getName());

        textVis.setText(repo.getWatchers_count() + "");
        textIssues.setText(repo.getOpen_issues_count() + "");
        textForks.setText(repo.getForks_count() + "");
        textDesc.setText(repo.getDescription());
        textLang.setText(repo.getLanguage());

        if(repo.isFork()) {
            imgIcon.setImageResource(R.drawable.ic_fork);
        }else{
            imgIcon.setImageResource(R.drawable.ic_code);
        }
        return view;
    }
}
