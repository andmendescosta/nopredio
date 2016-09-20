package br.com.nopredio;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import br.com.nopredio.model.Usuario;
import br.com.nopredio.util.Configuration;
import br.com.nopredio.util.JSONfunctions;
import br.com.nopredio.util.PrefUtils;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {

    private static final String MAP_API_URL = Configuration.URL_APLICTATION+"usuario/login";
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private Usuario usuario;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.btn_signup) Button _signupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        loginButton = (LoginButton) findViewById(R.id.btn_login_face);

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                String email = "";
                                String birthday = "";
                                // Application code
                                try{
                                    email = object.getString("email");
                                    birthday = object.getString("birthday"); // 01/31/1980 format
                                } catch(JSONException e){

                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                // App code
                Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        new LoginHandler().execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        //Toast.makeText(getBaseContext(), "Sucesso no login", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Falha no Login", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("insira um email válido");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("entre 4 e 10 caracteres alfanuméricos");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private class  LoginHandler extends AsyncTask<String, Void, Boolean> {

        final String email = ((TextView) findViewById(R.id.input_email)).getText().toString();
        final String password = ((TextView) findViewById(R.id.input_password)).getText().toString();
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);

        protected void onPreExecute() {

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Autenticando...");
            progressDialog.show();
        }


        @Override
        protected Boolean doInBackground(String... params) {






            try{
                usuario = Usuario.class.newInstance();
                usuario.setEmail(email);
                usuario.setSenha(password);
                JSONObject jsonobject = JSONfunctions.sendJSONfromURL(usuario, MAP_API_URL);
                JSONObject jsonUsu = (JSONObject) jsonobject.get("usuario");
                ObjectMapper mapper = new ObjectMapper();
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
                onLoginSuccess();
            }
            else
            {
                onLoginFailed();
            }




            super.onPostExecute(result);


        }

    }

}

