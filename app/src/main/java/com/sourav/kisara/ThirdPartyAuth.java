package com.sourav.kisara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import static com.sourav.kisara.LoginPage.writeSessionIdDevice;

public class ThirdPartyAuth extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        if(getIntent().getStringExtra("auth_type").equals("register"))
        {
            setContentView(R.layout.activity_third_party_auth);
            intialiseRegister();
        }
        else if(getIntent().getStringExtra("auth_type").equals("login"))
        {
            intialiseLogin();
        }

    }

    void intialiseLogin() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void intialiseRegister()
    {
        ImageView kisaraRegisterBtn = findViewById(R.id.kisaraRegister);
        kisaraRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent createAccountIntent = new Intent(ThirdPartyAuth.this, CreateAccount.class);
                String path = getIntent().getStringExtra("session_id_file_path");
                createAccountIntent.putExtra("session_id_file_path", path);
                startActivity(createAccountIntent);
            }
        });

        // Initialise google sign in
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        intialiseGoogleSignIn(signInButton);
    }

    private void intialiseGoogleSignIn(SignInButton signInButton)
    {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                handleGoogleSignInResult(task);

        }
    }
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(ThirdPartyAuth.this);


            // Signed in successfully, show authenticated UI.
            updateUIGoogleSignIn(acct);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace();
            Log.w("GOOGLE-SIGNIN-FAILED", "signInResult:failed code=" + e.getStatusCode());
            updateUIGoogleSignIn(null);
        }
    }

    private void updateUIGoogleSignIn(GoogleSignInAccount acct) {
        try {
            if (acct != null) {

                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String username = personEmail.split("@")[0];
                String password = acct.getId();;

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    password = PasswordGenerator.generateStrongPassword();
                }*/
                if(password.isEmpty())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", username);
                    jsonObject.put("first_name", personGivenName);
                    jsonObject.put("last_name", personFamilyName);
                    jsonObject.put("email", personEmail);



                    Intent createAccountIntent = new Intent(ThirdPartyAuth.this, CreateAccount.class);
                    String path = getIntent().getStringExtra("session_id_file_path");
                    createAccountIntent.putExtra("user_data", jsonObject.toString());
                    createAccountIntent.putExtra("session_id_file_path", path);

                    startActivity(createAccountIntent);
                }
                else
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", username);
                    jsonObject.put("first_name", personGivenName);
                    jsonObject.put("last_name", personFamilyName);
                    jsonObject.put("email", personEmail);
                    jsonObject.put("password", password);

                    ValidateAccount va = new ValidateAccount();
                    va.execute(username, password, personGivenName, personFamilyName, "", personEmail, "");

                }


                Toast.makeText(ThirdPartyAuth.this, "Proceeding with "+ personEmail, Toast.LENGTH_LONG).show();

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    private class ValidateAccount extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... data) {

            String username = data[0], password = data[1], firstName = data[2], lastName = data[3], mobile = data[4], email = data[5], yob = data[6];

            try {
                JSONObject req = new JSONObject();
                req.put("username", username);
                req.put("password", password);
                req.put("first_name", firstName);
                req.put("last_name", lastName);
                req.put("yob", yob);
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
                                Intent mainActivityIntent = new Intent(ThirdPartyAuth.this, MainActivity.class);
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

            return null;
        }

        ProgressDialog progressDialog = new ProgressDialog(ThirdPartyAuth.this);;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Verifying your account...");
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