package com.sourav.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static com.sourav.shop.LoginPage.writeSessionIdDevice;

public class CreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().hide();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // check if the password entered twice match
        setPasswordValidation();

        // Check the username if registered
        setUsernameValidation();

        // Check the mobile if registered
        setMobileValidation();

        Button createBtn = findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateUserAccount cua = new CreateUserAccount();
                cua.execute();
            }
        });


    }
    public void setMobileValidation()
    {
        EditText mobileET = findViewById(R.id.phoneCreate);
        mobileET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            checkMobile(mobileET);
                        }
                    }).start();
                    // usernameET.requestFocus();
                }
            }
        });
    }

    public Boolean checkMobile(EditText mobileET)
    {
        String mobile = mobileET.getText().toString();
        try {
            JSONObject req = new JSONObject();
            req.put("chosen_mobile", mobile);
            JSONObject res = MiscOperations.getDataFromServerPOST(MainActivity.verifyinfoUrl, req);
            if(res != null && res.length() != 0)
            {
                if(res.has("allowed"))
                {
                    if(!res.getString("allowed").equals("true"))
                    {
                        popUpOk("mobile : "+mobile+" is already registered to a different account");
                        mobileET.setTextColor(Color.RED);
                        return false;
                    }
                }
                else
                {
                    popUpOk("Technical error");
                    mobileET.setTextColor(Color.RED);
                    return false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        mobileET.setTextColor(Color.BLACK);
        return true;
    }

    public void setUsernameValidation()
    {
        EditText usernameET = findViewById(R.id.usernameCreate);
        usernameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            checkUsername(usernameET);
                        }
                    }).start();
                   // usernameET.requestFocus();
                }
            }
        });
    }

    public Boolean checkUsername(EditText usernameET)
    {
        String username = usernameET.getText().toString();
        try {
            JSONObject req = new JSONObject();
            req.put("chosen_username", username);
            JSONObject res = MiscOperations.getDataFromServerPOST(MainActivity.verifyinfoUrl, req);
            if(res != null && res.length() != 0)
            {
                if(res.has("allowed"))
                {
                    if(!res.getString("allowed").equals("true"))
                    {
                        popUpOk("username : "+username+" is already taken. Please choose a different one");
                        usernameET.setTextColor(Color.RED);
                        return false;
                    }
                }
                else
                {
                    popUpOk("Technical error");
                    usernameET.setTextColor(Color.RED);
                    return false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        usernameET.setTextColor(Color.BLACK);
        return true;
    }
    public void setPasswordValidation()
    {
        EditText reEnterPassword = findViewById(R.id.repasswordCreate), passwordET = findViewById(R.id.passwordCreate);
        reEnterPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    Boolean flag = checkPasswords(reEnterPassword, passwordET);
                    if(flag)
                    {
                        reEnterPassword.setTextColor(Color.BLACK);
                        passwordET.setTextColor(Color.BLACK);
                    }
                    else
                    {
                        reEnterPassword.setTextColor(Color.RED);
                        passwordET.setTextColor(Color.RED);
                    }
                   // passwordET.requestFocus();
                }
            }
        });
    }

    public Boolean checkPasswords(EditText reEnterPassword, EditText passwordET)
    {
        String passwordETText = passwordET.getText().toString(), reEnterPasswordText = reEnterPassword.getText().toString();
        if(!passwordETText.isEmpty() && !reEnterPasswordText.isEmpty())
        {
            if(!passwordETText.equals(reEnterPasswordText))
            {
                popUpOk("Passwords don't match");
                return false;
            }
            else
                return true;
        }
        else
        {
            popUpOk("Password fields cannot be empty");
            return false;
        }
    }

    public void popUpOk(String Message)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateAccount.this);
                alertDialogBuilder.setMessage(Message);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                alertDialogBuilder.show();
            }
        });

    }

    public void createCustomerAccount(String username, String password, String firstName, String lastName, String mobile, String email)
    {
        username = username.toLowerCase();
        try {
            JSONObject req = new JSONObject();
            req.put("username", username);
            req.put("password", password);
            req.put("first_name", firstName);
            req.put("last_name", lastName);
            req.put("phone_number", mobile);
            req.put("email_id", email);
            JSONObject res = MiscOperations.getDataFromServerPOST(MainActivity.createCustAccountUrl, req);

            if(res != null && res.length() != 0) {
                if (res.has("allowed")) {
                    if (res.getString("allowed").equals("true")) {
                        try {

                            JSONObject userInfo = new JSONObject(), output = new JSONObject();
                            userInfo.put("username", username);
                            userInfo.put("password", password);
                            output = MiscOperations.getDataFromServerPOST(MainActivity.loginUrl, userInfo);

                            String path = getIntent().getStringExtra("session_id_file_path");
                            writeSessionIdDevice(path, output.toString());
                            Intent mainActivityIntent = new Intent(CreateAccount.this, MainActivity.class);
                            mainActivityIntent.putExtra("user_data", output.toString());
                            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainActivityIntent);

                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class CreateUserAccount extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... data) {

            EditText reEnterPassword = findViewById(R.id.repasswordCreate),
                    passwordET = findViewById(R.id.passwordCreate),
                    usernameET = findViewById(R.id.usernameCreate),
                    firstnameET = findViewById(R.id.firstNameCreate),
                    lastnameET = findViewById(R.id.lasteNameCreate),
                    mobileET = findViewById(R.id.phoneCreate),
                    emailET = findViewById(R.id.emailCreate);

            String password = passwordET.getText().toString(),
                    username = usernameET.getText().toString(),
                    firstName = firstnameET.getText().toString(),
                    lastName = lastnameET.getText().toString(),
                    mobile = mobileET.getText().toString(),
                    email = emailET.getText().toString(),
                    repassword = reEnterPassword.getText().toString();


            if(!repassword.isEmpty() && !password.isEmpty() && !username.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !mobile.isEmpty())
            {
                if(checkPasswords(reEnterPassword, passwordET) && checkUsername(usernameET) && checkMobile(mobileET))
                {
                    createCustomerAccount(username, password, firstName, lastName, mobile, email);
                }
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CreateAccount.this, "Please fill the mandatory inputs", Toast.LENGTH_LONG).show();
                    }
                });
            }


            return null;
        }

        ProgressDialog progressDialog = new ProgressDialog(CreateAccount.this);;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Creating your account...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            progressDialog.dismiss();

        }
    }
}