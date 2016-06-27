package com.castrolol.githubsample;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.castrolol.githubsample.models.GHUser;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    final String BASE_URL = "https://api.github.com/";
    final String USER_URL = BASE_URL + "users/";

    RequestQueue queue;
    Gson gson = new Gson();

    TextView txtName;
    TextView txtUsername;
    ImageView imgAvatar;
    ListView listRepos;
    RepositoryAdapter adapter;


    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = (TextView)findViewById(R.id.tv_name);
        txtUsername = (TextView)findViewById(R.id.tv_username);
        imgAvatar = (ImageView)findViewById(R.id.iv_avatar);
        listRepos = (ListView)findViewById(R.id.lv_repos);

        adapter = new RepositoryAdapter(this, USER_URL);
        listRepos.setAdapter(adapter);


        txtName.setText(R.string.devmt);
        txtUsername.setText("@devmatogrosso");


        progress = new ProgressDialog(this);
        progress.setMessage("Carregando...");
        progress.setIndeterminate(true);
        progress.setCancelable(false);

        queue = Volley.newRequestQueue(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_search){
            buscarPerfil();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buscarPerfil() {
        abrirDialog();
    }

    private void abrirDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText txtUsernameBusca = new EditText(this);


        builder.setTitle("Buscar Perfil");
        builder.setView(txtUsernameBusca);

        builder.setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String username = txtUsernameBusca.getText().toString();

                buscarPerfil(username);
            }
        });

        builder.show();


    }

    private void buscarPerfil(String username) {
        progress.show();

        Request request = new JsonObjectRequest(Request.Method.GET, USER_URL + username, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.hide();
                final GHUser user = gson.fromJson(response.toString(), GHUser.class);
                processUser(user);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.hide();

                Toast.makeText(MainActivity.this, "erro: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        queue.add(request);

    }

    private void processUser(GHUser user) {

        txtName.setText(user.getName());
        txtUsername.setText("@" + user.getLogin());
        adapter.setUser(user.getLogin());
        loadAvatar(user.getAvatar_url());

    }

    private void loadAvatar(String url) {

        Request request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imgAvatar.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, null);

        queue.add(request);

    }


}
