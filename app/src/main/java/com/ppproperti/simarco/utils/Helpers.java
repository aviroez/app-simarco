package com.ppproperti.simarco.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.ppproperti.simarco.BuildConfig;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.activities.LoginActivity;
import com.ppproperti.simarco.entities.User;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.TELEPHONY_SERVICE;

public class Helpers {
    private static final String TAG = Helpers.class.getSimpleName();

    public static String currency(long nominal) {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(Constants.locale);
        return formatRupiah.format(nominal);
    }

    public static String currency(double nominal) {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(Constants.locale);
        return formatRupiah.format(nominal);
    }

    public static String currency(String nominal) {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(Constants.locale);
        return formatRupiah.format(Double.parseDouble(nominal));
    }

    public static String currency(String nominal, boolean symbol) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Constants.locale);
//        return formatRupiah.format(nominal);

        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);
        return numberFormat.format(Double.parseDouble(nominal)).trim();
    }

    public static String removeNonDigit(String nominal) {
        if (nominal != null){
            return nominal.replaceAll("[^\\d]", "");
        }
        return null;
    }

    public static void moveFragment(Context context, Fragment fragment, Bundle bundle) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layout_navigation_fragment, fragment);
        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static Retrofit initRetrofit(Context context) {
        return initRetrofit(context, true);
    }

    public static Retrofit initRetrofit(final Context context, boolean auth) {
        CustomPreferences preferences = new CustomPreferences(context);
        final User user = preferences.getUser();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor);

        if (user != null && auth){
            Interceptor interceptorAuth = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    String token = "Bearer " + user.getApiToken();
                    Log.d("Authorization", token);
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", token)
                            .method(original.method(), original.body())
                            .build();
                    Response response = chain.proceed(newRequest);
                    Log.d("MyApp", "Code : "+response.code());
                    if (response.code() == 401){
                        logout(context);
                        return response;
                    }
                    return response;
                }
            };
            builder.addInterceptor(interceptorAuth);
        }

        OkHttpClient client = builder.build();

        return new Retrofit.Builder()
                .baseUrl(context.getString(R.string.url))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static BootstrapBrand parseStatus(String status) {
        if (status.equals("new")) {
            return DefaultBootstrapBrand.SECONDARY;
        } else if (status.equals("process")) {
            return DefaultBootstrapBrand.INFO;
        } else if (status.equals("booked")) {
            return DefaultBootstrapBrand.PRIMARY;
        } else if (status.equals("sold")) {
            return DefaultBootstrapBrand.PRIMARY;
        } else if (status.equals("paid")) {
            return DefaultBootstrapBrand.SUCCESS;
        } else if (status.equals("cancel")) {
            return DefaultBootstrapBrand.DANGER;
        } else if (status.equals("sp1")) {
            return DefaultBootstrapBrand.WARNING;
        } else if (status.equals("sp2")) {
            return DefaultBootstrapBrand.WARNING;
        } else if (status.equals("sp3")) {
            return DefaultBootstrapBrand.DANGER;
        }

        return DefaultBootstrapBrand.SECONDARY;
    }

    public static void whatsapp(Context context, String phone, String message) {

        PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber pn = null;
        try {
            pn = pnu.parse(phone, "ID");
            String formattedNumber = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.E164);

            String url = "https://api.whatsapp.com/send?phone="+formattedNumber;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
//        try {
//            pn = pnu.parse(phone, "ID");
//
//            String formattedNumber = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.E164);
//            try {
//                Intent sendIntent = new Intent("android.intent.action.MAIN");
//                sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.setType("text/plain");
//                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
//                sendIntent.putExtra("jid", formattedNumber + "@s.whatsapp.net");
//                sendIntent.setPackage("com.whatsapp");
//                context.startActivity(sendIntent);
//            } catch (Exception e) {
//                Toast.makeText(context, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
//            }
//        } catch (NumberParseException e) {
//            e.printStackTrace();
//        }
    }

    public static void call(Context context, String phone) {
//        Intent callIntent = new Intent(Intent.ACTION_CALL);
//        callIntent.setData(Uri.parse("tel:" + phone));
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        context.startActivity(callIntent);
        TelephonyManager tm = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
        if (tm != null && tm.getSimState()==TelephonyManager.SIM_STATE_READY){
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sms(Context context, String phone, String message) {
//        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//        smsIntent.setType("vnd.android-dir/mms-sms");
//        smsIntent.putExtra("address", phone);
//        smsIntent.putExtra("sms_body",message);
//        context.startActivity(smsIntent);

        String defaultSmsPackageName = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_DEFAULT).setType("vnd.android-dir/mms-sms");
            final List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
            if (resolveInfos != null && !resolveInfos.isEmpty())
                defaultSmsPackageName =  resolveInfos.get(0).activityInfo.packageName;

        }

        if (defaultSmsPackageName != null) {
            Uri smsUri=  Uri.parse("smsto:" + phone);
            Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
            intent.putExtra("sms_body", message);
            intent.setType("vnd.android-dir/mms-sms");
            context.startActivity(intent);
        }
    }

    public static boolean arrayContains(String[] hayshacks, String needle){
        if (hayshacks != null && hayshacks.length > 0){
            for (int i = 0; i < hayshacks.length; i++) {
                if (needle.equals(hayshacks[i])){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean contains(String needle, String... hayshacks){
        if (hayshacks != null && hayshacks.length > 0){
            for (int i = 0; i < hayshacks.length; i++) {
                if (needle.equals(hayshacks[i])){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean contains(int needle, int... hayshacks){
        if (hayshacks != null && hayshacks.length > 0){
            for (int i = 0; i < hayshacks.length; i++) {
                if (needle == hayshacks[i]){
                    return true;
                }
            }
        }
        return false;
    }

    public static String formatPhone(String phone){
        String value = phone;
        value = value.replace("+", "");
//        value = value.replaceFirst("0", "62");
        if (value.length() > 0){
            if(value.substring(0,1).equals("0")) {
                value = "62"+value.substring(1);
            }
        }
        return value;
    }

    public static String formatDate(Date date, String pattern){
        if (pattern == null){
            pattern = Constants.DATE_FORMAT_LOCAL_M;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Constants.locale);
        sdf.setTimeZone(TimeZone.getTimeZone(Constants.DATE_TIME_ZONE));
        return sdf.format(date);
    }

    public static String formatDate(String dateString){
        SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT_SQL, Constants.locale);

        String pattern = Constants.DATE_FORMAT_LOCAL_M;
        Date date = null;
        try {
            date = format.parse(dateString);

            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Constants.locale);
            return sdf.format(date);
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseDate(String dateString){
        return parseDate(dateString, Constants.DATE_FORMAT_SQL);
    }

    public static Date parseDate(String dateString, String formatFrom){
        if (formatFrom == null){
            formatFrom = Constants.DATE_TIME_FORMAT_SQL;
        }
        SimpleDateFormat format = new SimpleDateFormat(formatFrom, Constants.locale);

        try {
            return format.parse(dateString);
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String reformatDate(String dateString, String formatFrom, String formatTo){
        if (formatFrom == null){
            formatFrom = Constants.DATE_TIME_FORMAT_SQL;
        }
        if (formatTo == null){
            formatTo = Constants.DATE_TIME_FORMAT_LOCAL_M;
        }
        SimpleDateFormat format = new SimpleDateFormat(formatFrom, Constants.locale);

        try {
            Date date = format.parse(dateString);

            SimpleDateFormat sdf = new SimpleDateFormat(formatTo, Constants.locale);
            return sdf.format(date);
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStatus(String status){
//        'new','process','booked','sold','paid','cancel','sp1','sp2','sp3'
        if (status.equals("new")){
            return "BARU";
        } else if(status.equals("process")){
            return "PROSES";
        } else if(status.equals("booked")){
            return "BOOKED";
        } else if(status.equals("sold")){
            return "SOLD";
        } else if(status.equals("paid")){
            return "LUNAS";
        } else if(status.equals("cancel")){
            return "BATAL";
        } else {
            if (status.length() > 0){
                return status.toUpperCase();
            }
        }
        return "-";
    }

    public static DefaultBootstrapBrand getStatusLabel(String status){
        if (status.equals("new")){
            return DefaultBootstrapBrand.INFO;
        } else if(status.equals("process")){
            return DefaultBootstrapBrand.INFO;
        } else if (status.equals("progress")){
            return DefaultBootstrapBrand.INFO;
        } else if (status.equals("booked")){
            return DefaultBootstrapBrand.WARNING;
        } else if (status.equals("ready")){
            return DefaultBootstrapBrand.REGULAR;
        } else if (status.equals("danger")){
            return DefaultBootstrapBrand.DANGER;
        } else if (status.equals("sold")){
            return DefaultBootstrapBrand.PRIMARY;
        } else if (status.equals("hold")){
            return DefaultBootstrapBrand.SUCCESS;
        }
        return DefaultBootstrapBrand.REGULAR;
    }

    public static double parseDouble(String str){
        try {
            return Double.parseDouble(str);
        } catch(Exception e) {
            return 0;
        }
    }

    public static int parseInteger(String str){
        try {
            return Integer.parseInt(str);
        } catch(Exception e) {
            return 0;
        }
    }

    public static int parseInteger(double d){
        try {
            return (int) d;
        } catch(Exception e) {
            return 0;
        }
    }

    public static String toString(double value){
        try {
            String valueString = String.valueOf(value);
            return !valueString.contains(".") ? valueString : valueString.replaceAll("0*$", "").replaceAll("\\.$", "");
        } catch(Exception e) {
        }
        return "";
    }

    public static String replaceUniqueChar(String sentences, String replacedBy){
        String result = null;

        if (replacedBy == null){
            replacedBy = "";
        }

        if (sentences != null && sentences.trim().length() > 0){
            result = sentences.trim().replaceAll("[^a-zA-Z0-9]", replacedBy);
        }

        return result;
    }

    public static void showSoftwareKeyboard(Activity activity, boolean showKeyboard){
        try {
            final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), showKeyboard ? InputMethodManager.SHOW_FORCED : InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e){

        }
    }

    private static final Pattern bound = Pattern.compile("\\b(\\w)");

    public static String titleize(final String input) {
        StringBuffer sb = new StringBuffer(input.length());
        Matcher mat = bound.matcher(input);
        while (mat.find()) {
            mat.appendReplacement(sb, mat.group().toUpperCase());
        }
        mat.appendTail(sb);
        return sb.toString();
    }

    public static void logout(Context context) {
        CustomPreferences customPreferences = new CustomPreferences(context, "user");
        customPreferences.savePreferences("user", null);

        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static boolean exists(String URLName){
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String padLeft(String text, int digit, String pad) {
        return String.format("%"+ pad + digit + "s", text);
    }

    public static String padLeft(int number, int digit, String pad) {
        return String.format("%"+ pad + digit + "s", String.valueOf(number));
    }

    public static String digitToString(double value) {
        DecimalFormat format = new DecimalFormat("0.#");
        return format.format(value);
    }

    public static void showLongSnackbar(View view, String msg){
        if (view != null){
            Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
        }
    }

    public static void showLongSnackbar(View view, int msgId){
        if (view != null){
            Snackbar.make(view, msgId, Snackbar.LENGTH_LONG).show();
        }
    }

    public static void showShortSnackbar(View view, String msg){
        if (view != null){
            Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
        }
    }

    public static void showShortSnackbar(View view, int msgId){
        if (view != null){
            Snackbar.make(view, msgId, Snackbar.LENGTH_SHORT).show();
        }
    }

    public static String imageUrl(Context context, String url, ImageView imageView){
        String fullPath = context.getString(R.string.url)+url.replaceAll("public/", "storage/");
        Glide.with(context).load(fullPath).fitCenter().into(imageView);

        return fullPath;
    }

    public static String imageUrl(Context context, String url, ImageView imageView, int placeholder){
        String fullPath = context.getString(R.string.url)+url.replaceAll("public/", "storage/");
        Glide.with(context).load(fullPath).placeholder(placeholder).fitCenter().into(imageView);

        return fullPath;
    }

    public static String getCompleteAddressString(Context context, double latitude, double longitude) {
        String strAdd = null;
        Geocoder geocoder = new Geocoder(context, Constants.locale);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.d(TAG, strReturnedAddress.toString());
            } else {
                Log.d(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Canont get Address!");
        }
        return strAdd;
    }

    public Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }



    public static String getDecimalFormattedString(String value)
    {
        StringTokenizer lst = new StringTokenizer(value, ".");
        String str1 = value;
        String str2 = "";
        if (lst.countTokens() > 1) {
            str1 = lst.nextToken();
            str2 = lst.nextToken();
        }
        String str3 = "";
        int i = 0;
        int j = -1 + str1.length();
        if (str1.charAt( -1 + str1.length()) == '.') {
            j--;
            str3 = ".";
        }
        for (int k = j;; k--) {
            if (k < 0) {
                if (str2.length() > 0)
                    str3 = str3 + "." + str2;
                return str3;
            }
            if (i == 3) {
                str3 = "," + str3;
                i = 0;
            }
            str3 = str1.charAt(k) + str3;
            i++;
        }

    }

    public static String trimCommaOfString(String string) {
//        String returnString;
        if(string.contains(",")){
            return string.replace(",","");}
        else {
            return string;
        }

    }

    public static boolean inArray(String value, String ... list){
        if (list.length > 0){
            for (String s: list) {
                if (s.equals(value)) return true;
            }
        }
        return false;
    }

    public static boolean inArray(int value, int ... list){
        if (list.length > 0){
            for (int s: list) {
                if (s == value) return true;
            }
        }
        return false;
    }

    public static void getKey(Context context, String key) {
        try {
            final PackageInfo info = context.getPackageManager()
                    .getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance(key);
                md.update(signature.toByteArray());

                final byte[] digest = md.digest();
                final StringBuilder toRet = new StringBuilder();
                for (int i = 0; i < digest.length; i++) {
                    if (i != 0) toRet.append(":");
                    int b = digest[i] & 0xff;
                    String hex = Integer.toHexString(b);
                    if (hex.length() == 1) toRet.append("0");
                    toRet.append(hex);
                }

                Log.e(TAG, key + " " + toRet.toString());
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    /**
     * @param key string like: SHA1, SHA256, MD5.
     */
    @SuppressLint("PackageManagerGetSignatures") // test purpose
    public static void fingerprint(Context context, String key) {
        try {
            final PackageInfo info = context.getPackageManager()
                    .getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance(key);
                md.update(signature.toByteArray());

                final byte[] digest = md.digest();
                final StringBuilder toRet = new StringBuilder();
                for (int i = 0; i < digest.length; i++) {
                    if (i != 0) toRet.append(":");
                    int b = digest[i] & 0xff;
                    String hex = Integer.toHexString(b);
                    if (hex.length() == 1) toRet.append("0");
                    toRet.append(hex);
                }

                Log.e(TAG, key + " " + toRet.toString());
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    public static String capitalizedSpace(String text){
        if (text != null){
            return text.toUpperCase().replaceAll("[^a-zA-Z0-9]", " ");
        }
        return "";
    }

    public static String unCapitalizedSpace(String text, String replaceWith){
        if (text != null){
            if (replaceWith == null) replaceWith = " ";
            return text.toLowerCase().replaceAll("[^a-zA-Z0-9]", replaceWith);
        }
        return "";
    }
}
