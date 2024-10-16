package com.ppproperti.simarco.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.manager.SupportRequestManagerFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Apartment;
import com.ppproperti.simarco.entities.EventActivityLog;
import com.ppproperti.simarco.entities.Setting;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.fragments.ApartmentFragment;
import com.ppproperti.simarco.fragments.ApartmentTowerFragment;
import com.ppproperti.simarco.fragments.CustomerAddFragment;
import com.ppproperti.simarco.fragments.CustomerDetailFragment;
import com.ppproperti.simarco.fragments.CustomerEditFragment;
import com.ppproperti.simarco.fragments.CustomerListFragment;
import com.ppproperti.simarco.fragments.EventListFragment;
import com.ppproperti.simarco.fragments.HomeFragment;
import com.ppproperti.simarco.fragments.LoanFragment;
import com.ppproperti.simarco.fragments.LoginFragment;
import com.ppproperti.simarco.fragments.MapFragment;
import com.ppproperti.simarco.fragments.OrderListFragment;
import com.ppproperti.simarco.fragments.ProductKnowledgeFragment;
import com.ppproperti.simarco.fragments.ProfileEditFragment;
import com.ppproperti.simarco.fragments.ProfileFragment;
import com.ppproperti.simarco.fragments.UpdatePasswordFragment;
import com.ppproperti.simarco.interfaces.ActivityService;
import com.ppproperti.simarco.interfaces.ApartmentService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.SettingService;
import com.ppproperti.simarco.responses.ResponseApartmentList;
import com.ppproperti.simarco.responses.ResponseEventActivityLog;
import com.ppproperti.simarco.responses.ResponseSettingList;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.GPSTracker;
import com.ppproperti.simarco.utils.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        LocationListener, HomeFragment.OnHeadlineSelectedListener, HideShowIconInterface {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;
    private NavigationView navigationView;
    private CustomPreferences customPreferences;
    private User user;
    private TextView textUserName;
    private TextView textEmail;
    private Context context;
    private Button buttonProfile;
    private View layoutProfile;
    private DrawerLayout drawerLayout;
    private Retrofit retrofit;
    private ActivityService activityService;
    private ApartmentService apartmentService;
    private double latitude = 0;
    private double longitude = 0;
    private GPSTracker gpsTracker;
    private EventActivityLog eventActivityLog = new EventActivityLog();
    private Fragment fragment;
    private List<Setting> listSetting = new ArrayList<>();
    private CustomPreferences settingPreferences;
    private List<Apartment> listApartment = new ArrayList<>();
    private Apartment apartment = new Apartment();
    private View parentLayout;
    private ActionBarDrawerToggle drawerToggle;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentLayout = findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        searchView = (SearchView) findViewById(R.id.search_view);

//        layoutProfile = navigationView.findViewById(R.id.layout_profile);

        View headerView =  navigationView.getHeaderView(0);

        if (headerView != null){
            textUserName = (TextView) headerView.findViewById(R.id.text_user_name);
            textEmail = (TextView) headerView.findViewById(R.id.text_user_email);

            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showProfileAction(view);
                }
            });
        }

        Locale.setDefault(Constants.locale);

        context = this;
        checkApplicationPermissions();

        showFragment(R.id.nav_home, null);
        customPreferences = new CustomPreferences(context, "user");
        settingPreferences = new CustomPreferences(context, "settings");

        retrofit = Helpers.initRetrofit(context);
        activityService = retrofit.create(ActivityService.class);
        apartmentService = retrofit.create(ApartmentService.class);
        initSetting();

        apartment = customPreferences.getApartment();
        listApartment = customPreferences.getListApartment();

        getApartment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = customPreferences.getUser();

        if (user != null){
            if (textUserName != null && textEmail != null){
                textUserName.setText(user.getName());
                textEmail.setText(user.getEmail());
            }

            if (user.getSignature() == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                        .setCancelable(false)
                        .setTitle(R.string.warning)
                        .setMessage(R.string.signature_is_not_specified)
                        .setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Bundle bundle = new Bundle();
                                bundle.putString("message", getString(R.string.specify_your_signature));
                                Helpers.moveFragment(context, new ProfileEditFragment(), bundle);
                            }
                        });

                final AlertDialog dialog = builder.create();
                        //2. now setup to change color of the button
//                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
//                    @Override
//                    public void onShow(DialogInterface arg0) {
//                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
//                    }
//                });

                dialog.show();
            } else if (apartment == null){
                initApartment();
            }
            checkGps();
        } else {
            moveToLogin();
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
//        super.onAttachFragment(fragment);
        if (fragment instanceof HomeFragment) {
            HomeFragment homeFragment = (HomeFragment) fragment;
            homeFragment.setOnHeadlineSelectedListener(this);
        }
    }

    private void initApartment() {
        if (listApartment == null || listApartment.size() <= 0){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("with", "images");
            apartmentService.listApartments(map).enqueue(new Callback<ResponseApartmentList>() {
                @Override
                public void onResponse(Call<ResponseApartmentList> call, Response<ResponseApartmentList> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){
                            listApartment = response.body().getData();

                            customPreferences.setListApartment(listApartment);

                            getApartment();
//                            showListApartment();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseApartmentList> call, Throwable t) {

                }
            });
        }
    }

    private void showListApartment() {
        if (listApartment.size() == 1){
            apartment = listApartment.get(0);
            customPreferences.setApartment(apartment);
        } else {
            int index  = 0;
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
            builderSingle.setIcon(R.mipmap.ic_simarco);
            builderSingle.setTitle("Pilih apartment:");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
            for (Apartment ap: listApartment) {
                arrayAdapter.add(ap.getName());
                if (apartment != null && ap.getId() == apartment.getId()) index = listApartment.indexOf(ap);
            }

            builderSingle.setSingleChoiceItems(arrayAdapter, index, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    String strName = arrayAdapter.getItem(position);
                    apartment = listApartment.get(position);
                    customPreferences.setApartment(apartment);
                    Log.d(TAG, "setSingleChoiceItems:"+new Gson().toJson(customPreferences.getApartment()));
                    Helpers.showLongSnackbar(parentLayout, strName+" dipilih");
                    showFragment(R.id.nav_home, null);

                    dialog.dismiss();
                }
            });

            builderSingle.show();

        }

        customPreferences.setListApartment(listApartment);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        checkGps();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Fragment fragment = getVisibleFragment();

        if(fragment instanceof HomeFragment){
            new AlertDialog.Builder(context, R.style.AlertDialogTheme)
                    .setTitle(R.string.confirmation)
                    .setMessage(R.string.sure_to_exit_this_app)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                MainActivity.this.finishAndRemoveTask();
                            }
                            MainActivity.this.finishAffinity();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        } else if (count > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            Log.d(TAG, "onBackPressed:count:"+count);
            Fragment backFragment = onBackPressedFragments();
            if (backFragment != null){
                super.onBackPressed();
                return;
            }
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                showFragment(R.id.nav_home, null);
            }
        }
//      super.onBackPressed();
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                Log.d(TAG, "getVisibleFragment:"+fragment.getClass().getSimpleName());
                if (fragment != null && fragment.isVisible()){
                    return fragment;
                } else if (!(fragment instanceof SupportRequestManagerFragment)){
                    return fragment;
                }
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_edit_profiles) {
            showProfileAction(null);
            return true;
        } else if (id == R.id.action_update_password) {
            updatePasswordAction(null);
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        showFragment(id, item);
        return true;
    }

    private void showFragment(int id, MenuItem item){
        searchView.setVisibility(View.GONE);
        fragment = null;
        Class fragmentClass = null;

        switch (id) {
            case R.id.nav_home: fragmentClass = HomeFragment.class; break;
            case R.id.nav_apartment: fragmentClass = ApartmentFragment.class; break;
            case R.id.nav_apartment_tower: fragmentClass = ApartmentTowerFragment.class; break;
            case R.id.nav_list_customers:
                fragmentClass = CustomerListFragment.class;
                searchView.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_list_orders:
                fragmentClass = OrderListFragment.class;
                searchView.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_login: fragmentClass = LoginFragment.class; break;
            case R.id.nav_logout: logout(); break;
            case R.id.nav_map: fragmentClass = MapFragment.class; break;
            case R.id.nav_product: fragmentClass = ProductKnowledgeFragment.class; break;
            case R.id.nav_setting: fragmentClass = MapFragment.class; break;
            case R.id.nav_list_events:
                searchView.setVisibility(View.VISIBLE);
                fragmentClass = EventListFragment.class;
                break;
            case R.id.nav_edit_profile: fragmentClass = ProfileFragment.class; break;
            case R.id.nav_update_password: fragmentClass = UpdatePasswordFragment.class; break;
            case R.id.nav_calculator: fragmentClass = LoanFragment.class; break;
            default: fragmentClass = HomeFragment.class;
        }

        if (fragmentClass != null){
            try {
                fragment = (Fragment) fragmentClass.newInstance();

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.layout_navigation_fragment, fragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (item != null){
                // Highlight the selected item has been done by NavigationView
                item.setChecked(true);
                // Set action bar title
                setTitle(item.getTitle());
            }
        }

        if (drawerLayout == null){
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void logout() {
        customPreferences.savePreferences("user", null);
        CustomPreferences customPreferencesApartment = new CustomPreferences(context, "apartments");
        customPreferencesApartment.clear("apartments");
        moveToLogin();
    }

    private void moveToLogin(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void checkApplicationPermissions() {
        Log.d(TAG, "checkApplicationPermissions:ACCESS_FINE_LOCATION:"+(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "checkApplicationPermissions:ACCESS_COARSE_LOCATION:"+(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "checkApplicationPermissions:INTERNET:"+(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "checkApplicationPermissions:READ_EXTERNAL_STORAGE:"+(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "checkApplicationPermissions:WRITE_EXTERNAL_STORAGE:"+(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "checkApplicationPermissions:ACCESS_WIFI_STATE:"+(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 102);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET}, 103);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 104);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 105);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_WIFI_STATE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 106);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, 107);
            }
        }
    }

    public void showProfileAction(View view){
        if (drawerLayout != null){
            drawerLayout.closeDrawers();
        }
        Helpers.moveFragment(MainActivity.this, new ProfileFragment(), null);
    }

    public void updatePasswordAction(View view){
        if (drawerLayout != null){
            drawerLayout.closeDrawers();
        }
        Helpers.moveFragment(MainActivity.this, new UpdatePasswordFragment(), null);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == layoutProfile.getId()){
            showProfileAction(view);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        getMyLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void checkGps() {
        gpsTracker = new GPSTracker(context);

        // Check if GPS enabled
        if (gpsTracker.canGetLocation()) {
            getMyLocation(gpsTracker.getLocation());
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gpsTracker.showSettingsAlert();
        }
    }

    private void getMyLocation(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d(TAG, "getMyLocation:"+latitude+","+longitude);
/* Hide update location
            if (eventActivityLog == null || ((latitude != 0 && longitude != 0) && (eventActivityLog.getLatitude() != latitude || eventActivityLog.getLongitude() != longitude))){
                HashMap<String, String> mapLog = new HashMap<>();
                mapLog.put("marketing_id", String.valueOf(user.getId()));
                mapLog.put("latitude", String.valueOf(latitude));
                mapLog.put("longitude", String.valueOf(longitude));
                Call<ResponseEventActivityLog> call = activityService.addEventActivityLog(mapLog);
                call.enqueue(new Callback<ResponseEventActivityLog>() {
                    @Override
                    public void onResponse(Call<ResponseEventActivityLog> call, Response<ResponseEventActivityLog> response) {
                        if (response.isSuccessful()){
                            if (response.body() != null && response.body().getData() != null){
                                eventActivityLog = response.body().getData();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseEventActivityLog> call, Throwable t) {

                    }
                });
            }
*/
        }
    }

    private void initSetting(){
        HashMap<String, String> map = new HashMap<>();
        SettingService settingService = retrofit.create(SettingService.class);
        settingService.get(map).enqueue(new Callback<ResponseSettingList>() {
            @Override
            public void onResponse(Call<ResponseSettingList> call, Response<ResponseSettingList> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        listSetting = response.body().getData();
                        if (listSetting.size() > 0){
                            for (Setting setting: listSetting){
                                settingPreferences.saveParcelableJson(setting.getCode(), new Gson().toJson(setting));
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseSettingList> call, Throwable t) {

            }
        });
    }

    private Fragment onBackPressedFragments(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment f : fragments){
            if(f instanceof CustomerEditFragment) {
                ((CustomerEditFragment) f).onBackPressed();
                Log.d(TAG, "onBackPressedFragments:CustomerEditFragment");
                return f;
            } else if(f instanceof CustomerDetailFragment) {
                ((CustomerDetailFragment) f).onBackPressed();
                Log.d(TAG, "onBackPressedFragments:CustomerDetailFragment");
                return f;
            } else if(f instanceof CustomerAddFragment) {
                ((CustomerAddFragment) f).onBackPressed();
                Log.d(TAG, "onBackPressedFragments:CustomerAddFragment");
                return f;
            }
        }
        return null;
    }

    @Override
    public void onArticleSelected(Apartment apartment) {

    }

    private void getApartment(){
        Menu menu = navigationView.getMenu();
        if (listApartment.size() > 0){
            if (listApartment.size() == 1){
                apartment = listApartment.get(0);
                if (apartment.getCode() != null && apartment.getCode().equals("GPR")){
                    menu.findItem(R.id.nav_product).setVisible(true);
                }
                menu.findItem(R.id.nav_apartment_tower).setVisible(true);
                menu.findItem(R.id.nav_apartment).setVisible(false);
            } else {
                menu.findItem(R.id.nav_apartment).setVisible(true);
                menu.findItem(R.id.nav_apartment_tower).setVisible(false);
//                for (Apartment apartment: listApartment) {
//                    Log.d(TAG, "apartment:code:"+apartment.getCode());
//                    if (apartment.getCode().equals("GPR")){
//                        menu.findItem(R.id.nav_product).setVisible(true);
//                        break;
//                    }
//                }
            }
        }
    }

    boolean backIcon = false;

    @Override
    public void showHamburgerIcon() {
        if (getSupportActionBar() != null){
            backIcon = false;
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
        }
    }
    @Override
    public void showBackIcon() {
        if (getSupportActionBar() != null){
            backIcon = true;
            drawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick:"+backIcon);
                    if (backIcon) {
                        onBackPressed();
                    } else {
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            drawerLayout.openDrawer(GravityCompat.START);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void showFilterIcon() {
        if (getSupportActionBar() != null){

        }
    }
}
