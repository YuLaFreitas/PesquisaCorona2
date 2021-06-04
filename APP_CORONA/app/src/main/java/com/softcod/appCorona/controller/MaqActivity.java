package com.softcod.appCorona.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softcod.appCorona.R;
import com.softcod.appCorona.model.MaqNew;
import com.softcod.appCorona.utils.RetrieveHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MaqActivity extends AppCompatActivity {

    private List<MaqNew> maqLista;
    private RecyclerView maqsRecyclerView;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_maquinas);
        Intent intent = new Intent(this, MainActivity.class);

        LinearLayout ll = findViewById(R.id.sair);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.authenticating);
        progressDialog.setMessage(getString(R.string.pleaseWait));
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        //String message = "";
        JSONArray maqsJSON = new JSONArray();

        RetrieveHttp http = new RetrieveHttp();
        JSONObject json = null;
        try {
            json = http.execute(getString(R.string.servidor) +
                            "/get_maquinas",
                    "POST", "email="+preferences
                            .getString(getString(R.string.keyEmail), "")).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            progressDialog.dismiss();
        }

        try {
            //message = (String) json.get("message");
            maqsJSON = (JSONArray) json.get("data");
            if (maqsJSON != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<MaqNew>>(){}.getType();
                maqLista = gson.fromJson(maqsJSON.toString(), type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        maqsRecyclerView = (RecyclerView) findViewById(R.id.maqs_recyclerView);
        maqsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        MaquAdapter maquAdapter = new MaquAdapter(getApplicationContext(), maqLista);

        maqsRecyclerView.setAdapter(maquAdapter);

        //TextView noMaqsTextView = (TextView) findViewById(R.id.noMaqs_TextView);
        //noMaqsTextView.setVisibility(maqLista.size() > 0 ? View.INVISIBLE : View.VISIBLE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();

        return super.onOptionsItemSelected(item);
    }

    public void receiveLedOnClick(View view) {
    }

    public void chooseSoundOnClick(View view) {
    }

    public void receiveVibrationAlertsOnClick(View view) {
    }

    private class MaquAdapter extends RecyclerView.Adapter<MaquAdapter.maqViewHolder> {

        private Context context;
        private List<MaqNew> maqLista;

        MaquAdapter(Context context, List<MaqNew> maqsList) {
            this.context = context;
            this.maqLista = maqsList;
        }

        @Override
        public maqViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.maq_item, parent, false);

            return new maqViewHolder(itemView);

        }

        @Override
        public void onBindViewHolder(maqViewHolder holder, int position) {

            MaqNew maqMod = maqLista.get(position);

            holder.nome.setText(maqMod.getNOME());
        }

        @Override
        public int getItemCount() {
            return maqLista.size();
        }

        class maqViewHolder extends RecyclerView.ViewHolder {
            private TextView nome;

            maqViewHolder(final View itemView) {
                super(itemView);

                nome = (TextView) itemView.findViewById(R.id.maquina);

                itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(MaqActivity.this,
                            CadMACActivity.class);
                    intent.putExtra(getString(R.string.keyMaquina),
                            maqLista.get(getAdapterPosition()));
                    startActivity(intent);
                });
            }
        }
    }
}
