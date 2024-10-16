package com.ppproperti.simarco.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.entities.UserAccess;
import com.ppproperti.simarco.fragments.ApartmentFragment;
import com.ppproperti.simarco.interfaces.UserService;
import com.ppproperti.simarco.responses.ResponseUser;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = LoginActivity.class.getSimpleName();
    private User user = new User();
    private LinearLayout linearLogin;
    private ProgressBar loadingProgressBar;
    private View parentLayout;
    private CustomPreferences customPreference;
    private CustomPreferences profilePreference;
    private String email;
    private ImageView imageShow;
    private EditText textEmail;
    private EditText textPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parentLayout = findViewById(android.R.id.content);
        textEmail = findViewById(R.id.text_email);
        textPassword = findViewById(R.id.text_password);
        final BootstrapButton loginButton = findViewById(R.id.button_login);
        loadingProgressBar = findViewById(R.id.loading);
        linearLogin = findViewById(R.id.linearLogin);
        imageShow = findViewById(R.id.image_show);
        loadingProgressBar.setVisibility(View.GONE);

        profilePreference = new CustomPreferences(this, "profile");
        customPreference = new CustomPreferences(this);
        user = customPreference.getUser();
        email = profilePreference.getPreference("email", null);

        if (user != null){
            textEmail.setText(user.getEmail());
        } else if (email != null){
            textEmail.setText(email);
        }

        imageShow.setOnClickListener(this);

        textPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doLogin(textEmail.getText().toString(),
                            textPassword.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doLogin(textEmail.getText().toString(),
                        textPassword.getText().toString());

//                loginViewModel.login(textEmail.getText().toString(),
//                        textPassword.getText().toString());
            }
        });
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void doLogin(String email, String password){
        profilePreference.savePreferences("email", email);

        loadingProgressBar.setVisibility(View.VISIBLE);
        linearLogin.setVisibility(View.INVISIBLE);

        final Retrofit retrofit = Helpers.initRetrofit(this);

        UserService service = retrofit.create(UserService.class);
//        Call<ResponseUser> call = service.login(email, password);
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);
        Call<ResponseUser> call = service.login(map);
        call.enqueue(new Callback<ResponseUser>() {
            @Override
            public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                loadingProgressBar.setVisibility(View.GONE);
                linearLogin.setVisibility(View.VISIBLE);
                if(response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        user = response.body().getData();
                        boolean marketingValid = false;

                        if (user != null && user.getUserAccesses().size() > 0){
                            for (UserAccess userAccess: user.getUserAccesses()) {
                                if (userAccess.getUserPosition() != null && Helpers.inArray(userAccess.getUserPosition().getCode(), "marketing", "admin_marketing", "management_marketing")) {
                                    marketingValid = true;
                                    break;
                                }
                            }
                        }

                        if (marketingValid){
                            customPreference.savePreferences("user", new Gson().toJson(user));
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Helpers.showLongSnackbar(parentLayout, R.string.user_access_invalid);
                        }

                        Log.d(TAG, "onResponse:isSuccessful:"+new Gson().toJson(user));
                    } else {
                        Helpers.showLongSnackbar(parentLayout, response.body().getMessage());
                    }
                } else {
                    Log.d(ApartmentFragment.class.getSimpleName(), "onResponse:"+response.message());
                    Helpers.showLongSnackbar(parentLayout, R.string.email_or_password_invalid);
                }
            }

            @Override
            public void onFailure(Call<ResponseUser> call, Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);
                linearLogin.setVisibility(View.VISIBLE);
                t.printStackTrace();
                String loginFailed = getString(R.string.login_failed);
                loginFailed += ": "+t.getMessage();
                Helpers.showLongSnackbar(parentLayout, loginFailed);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == imageShow.getId()){
            toggleShowPassword();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogTheme)
                .setTitle(R.string.confirmation)
                .setMessage(R.string.sure_to_exit_this_app)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            LoginActivity.this.finishAndRemoveTask();
                        }
                        LoginActivity.this.finishAffinity();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void toggleShowPassword() {
        if(textPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            imageShow.setImageResource(R.drawable.ic_eye_slash);

            //Show Password
            textPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else{
            imageShow.setImageResource(R.drawable.ic_eye);

            //Hide Password
            textPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
