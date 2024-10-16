package com.ppproperti.simarco.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.gson.Gson;
import com.gu.toolargetool.TooLargeTool;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private long splashScreenDuration = 2000l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        progressBar = findViewById(R.id.progress_bar);
        TooLargeTool.startLogging(getApplication());

        // Uncomment to show fingerprint
        //Helpers.fingerprint(this, "SHA1");
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Creates instance of the manager.
        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(SplashActivity.this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE){
                        // For a flexible update, use AppUpdateType.FLEXIBLE
//                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Request the update.
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    new AlertDialog.Builder(SplashActivity.this, R.style.AlertDialogTheme)
                            .setTitle(R.string.confirmation)
                            .setMessage(R.string.new_version_found_please_update)
                            .setPositiveButton(R.string.update_app, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException e) {
                                        e.printStackTrace();
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                }
                            })
                            .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        SplashActivity.this.finishAndRemoveTask();
                                    }
                                    SplashActivity.this.finishAffinity();
                                }
                            })
                            .show();
                } else {
                    startApp();
                }
            }
        });
        appUpdateInfoTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                startApp();
            }
        });
        appUpdateInfoTask.addOnCompleteListener(new OnCompleteListener<AppUpdateInfo>() {
            @Override
            public void onComplete(Task<AppUpdateInfo> task) {
//                startApp();
            }
        });
//        new Handler().postDelayed(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                    }
//                }, splashScreenDuration
//        );


//        new Thread(new Runnable() {
//            public void run() {
//                doWork();
//                startApp();
//                finish();
//            }
//        }).start();
    }

    private void doWork() {
        for (int progress=0; progress<100; progress+=10) {
            try {
                Thread.sleep(1000);
                progressBar.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startApp() {
//        FirebaseUser currentUser = auth.getCurrentUser();

        CustomPreferences customPreferences = new CustomPreferences(this, "user");
        String result = customPreferences.getPreference("user", null);
        Log.d(SplashActivity.class.getSimpleName(), "result:"+result);
        User user = new Gson().fromJson(result, User.class);
        if (user != null && user.getId() != 0){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
