package com.sourav.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class LoginPage extends AppCompatActivity {


    static Boolean writeSessionIdDevice(String sessionIdFilePath, String sessionId)
    {
        File sessionFile = new File(sessionIdFilePath);
        if(!sessionFile.exists())
        {
            try {
                sessionFile.createNewFile();
            } catch (Exception e) {
                e.getMessage();
            }
        }

        try {
            sessionFile.createNewFile();
            ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(sessionFile));
            objOut.writeObject(sessionId);
            objOut.close();
            return true;
        } catch (Exception e) {
            e.getMessage();
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        getSupportActionBar().hide();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        TextView username = findViewById(R.id.usernameLogin), password = findViewById(R.id.passwordLogin);
        Button loginBtn = findViewById(R.id.loginBtn);


        TextView textView = (TextView) findViewById(R.id.createAcc);
        SpannableString content = new SpannableString(textView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                String usernameText = username.getText().toString(), passwordText = password.getText().toString();
                if(!usernameText.isEmpty() && !passwordText.isEmpty())
                {
                    Login lg = new Login();
                    lg.execute(usernameText, passwordText);
                }
                else
                {
                    Toast.makeText(LoginPage.this, "All inputs are mandatory", Toast.LENGTH_LONG).show();
                }
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegisterIntent = new Intent(LoginPage.this, ThirdPartyAuth.class);
                String path = getIntent().getStringExtra("session_id_file_path");
                RegisterIntent.putExtra("session_id_file_path", path);
                RegisterIntent.putExtra("auth_type", "register");
                startActivity(RegisterIntent);
            }
        });

        // Google sign in

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegisterIntent = new Intent(LoginPage.this, ThirdPartyAuth.class);
                String path = getIntent().getStringExtra("session_id_file_path");
                RegisterIntent.putExtra("session_id_file_path", path);
                RegisterIntent.putExtra("auth_type", "login");
                startActivity(RegisterIntent);
            }
        });
    }

    private class Login extends AsyncTask<String, Void, Integer> {
        JSONObject userInfo = new JSONObject(), output = new JSONObject();
        protected Integer doInBackground(String... data) {

            //login

            try {
                userInfo.put("username", data[0]);
                userInfo.put("password", data[1]);
                output = MiscOperations.getDataFromServerPOST(MainActivity.loginUrl, userInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        ProgressDialog progressDialog = new ProgressDialog(LoginPage.this);;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Logging you in...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(output != null && output.length()!=0)
            {
                try {
                    String path = getIntent().getStringExtra("session_id_file_path");
                    writeSessionIdDevice(path, output.toString());
                    Intent mainActivityIntent = new Intent(LoginPage.this, MainActivity.class);
                    mainActivityIntent.putExtra("user_data", output.toString());
                    startActivity(mainActivityIntent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginPage.this);
                alertDialogBuilder.setMessage("Invalid username or password, or account doesn't exist");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialogBuilder.show();
            }
            progressDialog.dismiss();

        }
    }
}