package com.ppproperti.simarco.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.ImageService;
import com.ppproperti.simarco.interfaces.UserService;
import com.ppproperti.simarco.responses.ResponseUser;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.ppproperti.simarco.utils.Constants.PICK_IMAGE_CAMERA;
import static com.ppproperti.simarco.utils.Constants.PICK_IMAGE_GALLERY;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileEditFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = ProfileEditFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private TextInputEditText textFullName;
    private TextInputEditText textEmail;
    private TextInputEditText textAlias;
    private TextInputEditText textHandphone;
    private Context context;
    private CustomPreferences customPreference;
    private User user = new User();
    private SignaturePad signaturePad;
    private BootstrapButton buttonBack;
    private BootstrapButton buttonClear;
    private BootstrapButton buttonSave;
    private Retrofit retrofit;
    private UserService userService;
    private ImageService imageService;
    private String fullName;
    private String alias;
    private String handphone;
    private String email;
    private String signatureSvg;
    private ImageView imageProfile;
    private BootstrapButton buttonProfile;
    private Activity activity;
    private Object inputStreamImg;
    private Bitmap bitmap;
    private File destination;
    private String imgPath;
    private String message;
    private boolean parsed = false;

    public ProfileEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileEditFragment newInstance(String param1, String param2) {
        ProfileEditFragment fragment = new ProfileEditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString("message");
        }

        context = getContext();
        activity = getActivity();

        customPreference = new CustomPreferences(context);
        user = customPreference.getUser();
        retrofit = Helpers.initRetrofit(context, true);

        userService = retrofit.create(UserService.class);
        imageService = retrofit.create(ImageService.class);

        if (message != null){
            Helpers.showLongSnackbar(getView(), message);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        textFullName = view.findViewById(R.id.text_full_name);
        textAlias = view.findViewById(R.id.text_alias);
        textHandphone = view.findViewById(R.id.text_handphone);
        textEmail = view.findViewById(R.id.text_email);
        buttonBack = view.findViewById(R.id.button_back);
        buttonClear = view.findViewById(R.id.button_clear);
        buttonSave = view.findViewById(R.id.button_save);
        buttonProfile = view.findViewById(R.id.button_profile);
        imageProfile = view.findViewById(R.id.image_profile);
        signaturePad = view.findViewById(R.id.signature_pad);

        parseUser();

        buttonProfile.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        buttonClear.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                if (parsed) {
                    signaturePad.clear();
                    parsed = false;
                }
            }

            @Override
            public void onSigned() {

            }

            @Override
            public void onClear() {

            }
        });
        return view;
    }

    private void parseUser() {
        if (user != null){
            if (user.getName() != null && user.getName().length() > 0){
                textFullName.setText(user.getName());
//                textFullName.setEnabled(false);
            }
            if (user.getAlias() != null && user.getAlias().length() > 0){
                textAlias.setText(user.getAlias());
//                textAlias.setEnabled(false);
            }
            if (user.getHandphone() != null && user.getHandphone().length() > 0){
                textHandphone.setText(user.getHandphone());
//                textHandphone.setEnabled(false);
            }
            if (user.getEmail() != null && user.getEmail().length() > 0){
                textEmail.setText(user.getEmail());
                textEmail.setEnabled(false);
            }
            if (user.getSignature() != null){
                parsed = true;
                generateSignature(user.getSignature());
            }
        }
    }

    private void generateSignature(String signature) {
        SVG svg = null;
        try {
            svg = SVG.getFromString(user.getSignature());

            Bitmap  newBM = Bitmap.createBitmap((int) Math.ceil(svg.getDocumentWidth()),
                    (int) Math.ceil(svg.getDocumentHeight()),
                    Bitmap.Config.ARGB_8888);

            Canvas  bmcanvas = new Canvas(newBM);

            // Clear background to white
            bmcanvas.drawRGB(255, 255, 255);

            // Render our document onto our canvas
            svg.renderToCanvas(bmcanvas);
            signaturePad.setSignatureBitmap(newBM);
        } catch (SVGParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.edit_profile);
            ((HideShowIconInterface) activity).showBackIcon();
        }
//        HashMap<String, String> map = new HashMap<>();
//        Call<ResponseUser> call = userService.users(map);
//        call.enqueue(new Callback<ResponseUser>() {
//            @Override
//            public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
//                if (response.isSuccessful()) {
//                    if (response.body() != null && response.body().getData() != null) {
//                        user = response.body().getData();
//                        parseUser();
//                        customPreference.savePreferences("user", new Gson().toJson(user));
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseUser> call, Throwable t) {
//
//            }
//        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_back){
            backAction(view);
        } else if (view.getId() == R.id.button_clear){
            clearAction(view);
        } else if (view.getId() == R.id.button_save){
            saveAction(view);
        } else if (view.getId() == R.id.button_profile){
            profileAction(view);
        }
    }

    private void profileAction(View view) {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        boolean imageURI = activity.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
//        startActivityForResult(intent, CAMERA_REQUEST_CODE);
        checkApplicationPermissions();
        selectImage();
    }

    private void selectImage() {
        try {
            PackageManager pm = activity.getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, activity.getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Helpers.showShortSnackbar(getView(), "Camera Permission error");
        } catch (Exception e) {
            Helpers.showShortSnackbar(getView(), "Camera Permission error");
            e.printStackTrace();
        }
    }

    private void saveAction(View view) {
        if (validation()){
            HashMap<String, String> map = new HashMap<>();

//            'name', 'alias', 'gender', 'password', 'ktp', 'sim', 'npwp', 'address', 'phone_number', 'fax', 'handphone', 'user_position_id', 'email', 'remember_token', 'api_token', 'signature'
            map.put("name", fullName);
            map.put("alias", alias);
            map.put("handphone", handphone);
            map.put("email", email);
            map.put("signature", signatureSvg);
            Call<ResponseUser> callUser = userService.updateUser(map);
            callUser.enqueue(new Callback<ResponseUser>() {
                @Override
                public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null && response.body().getData() != null){
                            user = response.body().getData();

                            customPreference.savePreferences("user", new Gson().toJson(user));

                            Bundle bundle = new Bundle();
                            bundle.putString("message", getString(R.string.profile_edit_successfully));
                            Helpers.moveFragment(context, new HomeFragment(), bundle);
//                            Helpers.moveFragment(context, new ProfileFragment(), bundle);
                        }
                        if (imgPath != null && imgPath.length() > 0){
                            uploadFile(user, imgPath);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseUser> call, Throwable t) {

                }
            });
        }
    }

    private boolean validation() {
        fullName = textFullName.getText().toString();
        alias = textAlias.getText().toString();
        handphone = textHandphone.getText().toString();
        email = textEmail.getText().toString();
        signatureSvg = signaturePad.getSignatureSvg();

        Log.d(TAG, "validation:"+signatureSvg);

        if (fullName == null || fullName.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.full_name_is_required);
            textFullName.requestFocus();
            return false;
        } else if (alias == null || alias.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.alias_is_required);
            textAlias.requestFocus();
            return false;
        } else if (handphone == null || handphone.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.handphone_is_required);
            textHandphone.requestFocus();
            return false;
        } else if (email == null || email.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.email_is_required);
            textEmail.requestFocus();
            return false;
        } else if (signaturePad.isEmpty()){
            Helpers.showLongSnackbar(getView(), R.string.signature_is_required);
            return false;
        }
        return true;
    }

    private void clearAction(View view) {
        signaturePad.clear();
    }

    private void backAction(View view) {
        Helpers.moveFragment(context, new ProfileFragment(), null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                Log.e("Activity", "Pick from Camera::>>> ");

                String timeStamp = new SimpleDateFormat(Constants.DATE_TIME_SIMPLE, Locale.getDefault()).format(new Date());
                destination = new File(Environment.getExternalStorageDirectory() + "/" +
                        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imgPath = destination.getAbsolutePath();
                imageProfile.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                Log.e("Activity", "Pick from Gallery::>>> ");

                imgPath = getRealPathFromURI(selectedImage);
                destination = new File(imgPath.toString());
                imageProfile.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void uploadFile(User user, String path) {
        try {
            List<MultipartBody.Part> listPart = new ArrayList<>();
            List<RequestBody> listBody = new ArrayList<>();
            ArrayList<String> listCode = new ArrayList<>();

            HashMap<String, String> map = new HashMap<>();
            if (path != null){
                File file = new File(path);
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg;image/png"), file);

                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file[]", file.getName(), requestBody);
                RequestBody fileName = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                listPart.add(fileToUpload);
                listBody.add(fileName);
                listCode.add("profiles");
            }

            if (listPart.size() > 0){
                Call<ResponseUser> call = imageService.user(user.getId(), listPart, listBody, listCode);

                call.enqueue(new Callback<ResponseUser>() {
                    @Override
                    public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
//                        progressBar.setProgress(100);
//                        successOrder();
                    }

                    @Override
                    public void onFailure(Call<ResponseUser> call, Throwable t) {
                        t.printStackTrace();
//                        progressBar.setVisibility(View.GONE);
//                        successOrder();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkApplicationPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 104);
            }
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 105);
            }
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA}, 105);
            }
        }
    }
}
