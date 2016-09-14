package br.com.nopredio;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import br.com.nopredio.model.Condominio;
import br.com.nopredio.model.Perfil;
import br.com.nopredio.model.Usuario;
import br.com.nopredio.util.JSONfunctions;
import br.com.nopredio.util.Mask;
import br.com.nopredio.util.PrefUtils;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {

    private static final String URL_GET_LIST_CONDOMINIOS = "http://52.36.27.122:8080/nopredio/condominio/list";
    private static final String URL_POST_SAVE_USUARIO = "http://52.36.27.122:8080/nopredio/usuario/salvar";
    JSONObject jsonobject;
    JSONArray jsonarray;
    ProgressDialog mProgressDialog;
    ArrayList<String> condominiolist;
    Condominio[] condominios;
    Condominio condominioSelected = null;

    @InjectView(R.id.btnSignup) Button _signupButton;
    @InjectView(R.id.txtTelefone) EditText _telefoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);
        //EditText etTelefone = (EditText) findViewById(R.id.txtTelefone);
        _telefoneText.addTextChangedListener(Mask.insert(Mask.CELULAR_MASK, _telefoneText));

        // Download JSON file AsyncTask
        new DownloadJSON().execute();

        _signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new LoginHandler().execute();

            }
        });
    }


    // Download JSON file AsyncTask
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            jsonobject = JSONfunctions.getJSONfromURL(URL_GET_LIST_CONDOMINIOS);

            try {
                JSONArray jsonList = (JSONArray) jsonobject.getJSONArray("list");
                ObjectMapper mapper = new ObjectMapper();
                Gson gson = new Gson();
                condominios = (Condominio[]) gson.fromJson(jsonList.toString(),Condominio[].class);

            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Locate the spinner in activity_main.xml
            Spinner mySpinner = (Spinner) findViewById(R.id.spCondominio);

            final ArrayAdapter<Condominio> dataAdapter = new ArrayAdapter<Condominio>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, condominios);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mySpinner.setAdapter(dataAdapter);

            mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int position, long arg3) {
                            condominioSelected = dataAdapter.getItem(position);
                            Toast.makeText(SignupActivity.this, "ID: " + condominioSelected.getCod() + "\nName: " + condominioSelected.getNome(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    });
        }
    }


    private class  LoginHandler extends AsyncTask<String, Void, Boolean> {

        final String _emailText = ((TextView) findViewById(R.id.txtEmail)).getText().toString();
        final String _passwordText = ((TextView) findViewById(R.id.txtSenha)).getText().toString();
        final String _nomeText = ((TextView) findViewById(R.id.txtNome)).getText().toString();
        final String _apartamentoText = ((TextView) findViewById(R.id.txtApartamento)).getText().toString();
        final String _blocoText = ((TextView) findViewById(R.id.txtBloco)).getText().toString();
        final String _telefoneText = ((TextView) findViewById(R.id.txtTelefone)).getText().toString().replaceAll("\\D+","");
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);

        protected void onPreExecute() {

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Cadastrando...");
            progressDialog.show();
        }


        @Override
        protected Boolean doInBackground(String... params) {

            try{
                Usuario usuario = Usuario.class.newInstance();
                usuario.setNome(_nomeText);
                usuario.setEmail(_emailText);
                usuario.setSenha(_passwordText);
                usuario.setTelefone(_telefoneText);
                usuario.setCondominio(condominioSelected);
                usuario.setApartamento(_apartamentoText);
                usuario.setBloco(_blocoText);
                usuario.setPerfil(new Perfil(Long.parseLong("4")));
                usuario.setAtivo("Y");
                JSONObject jsonobject = JSONfunctions.sendJSONfromURL(usuario, URL_POST_SAVE_USUARIO);
                JSONObject jsonReturn = (JSONObject) jsonobject.get("usuario");

                /*ObjectMapper mapper = new ObjectMapper();
                Gson gson = new Gson();
                usuario = gson.fromJson(jsonUsu.toString(),Usuario.class);
                // On complete call either onLoginSuccess or onLoginFailed
                if(usuario.getCod() != null) {
                    PrefUtils.saveToPrefs(LoginActivity.this, PrefUtils.PREFS_LOGIN_COD_KEY, usuario.getCod().toString());
                    PrefUtils.saveToPrefs(LoginActivity.this, PrefUtils.PREFS_LOGIN_EMAIL_KEY, usuario.getEmail());
                    PrefUtils.saveToPrefs(LoginActivity.this, PrefUtils.PREFS_LOGIN_NAME_KEY, usuario.getNome());
                    PrefUtils.saveToPrefs(LoginActivity.this, PrefUtils.PREFS_LOGIN_PHONE_KEY, usuario.getTelefone());
                    return true;
                }
                // onLoginFailed();
                else
                    return false;

                //progressDialog.dismiss();
                */
            } catch (IllegalAccessException e){
                Log.e("log_tag", "Erro ao acessar classe " + e.toString());
            } catch (InstantiationException e){
                Log.e("log_tag", "Erro ao instanciar classe " + e.toString());
            } catch (JSONException e){
                Log.e("log_tag", "Erro ao instanciar json " + e.toString());
            } catch (Throwable e){
                Log.e("log_tag", "Erro ao instanciar json " + e.toString());
            }


            return false;
        }

        // TODO Auto-generated method stub

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub


            progressDialog.cancel();

            if (result) {
                //onLoginSuccess();
            }
            else
            {
                //onLoginFailed();
            }




            super.onPostExecute(result);


        }

    }
}
