package com.ppproperti.simarco.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.Gson;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.ApartmentUnit;
import com.ppproperti.simarco.entities.Customer;
import com.ppproperti.simarco.entities.Order;
import com.ppproperti.simarco.entities.PaymentSchema;
import com.ppproperti.simarco.entities.PaymentSchemaCalculation;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.Helpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.ppproperti.simarco.utils.Constants.PICK_IMAGE_GALLERY;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = CameraFragment.class.getSimpleName();
    public static final int PICK_IMAGE_CAMERA_KTP = 101;
    public static final int PICK_IMAGE_CAMERA_NPWP = 102;
    public static final int PICK_IMAGE_GALLERY_KTP = 201;
    public static final int PICK_IMAGE_GALLERY_NPWP = 202;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ImageView imageViewFullSized;
    private ImageView imageViewThumbnail;
    private BootstrapButton buttonBack;
    private BootstrapButton buttonNext;
    private Order order;
    private Customer customer;
    private ApartmentUnit apartmentUnit;
    private PaymentSchema paymentSchema;
    private PaymentSchemaCalculation paymentSchemaCalculation;
    private Context context;
    private Activity activity;
    private CameraView camera;
    private ImageView imageCamera;
    private BootstrapButton buttonRecapture;
    private BootstrapButton buttonCapture;
    private String absolutePath = null;
    private ProgressBar progressBar;
    private boolean ktpCaptured = false;
    private boolean npwpCaptured = false;
    private String ktpPath = null;
    private String npwpPath = null;
    private TextView textViewTop;
    private TextView textViewWatermark;
    private BootstrapButton buttonSkip;
    private BootstrapButton buttonImport;
    private CardView viewCaptureKtp;
    private CardView viewCaptureNpwp;
    private ImageView imageViewKtp;
    private ImageView imageViewNpwp;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = getArguments().getParcelable("order");
            customer = getArguments().getParcelable("customer");
            apartmentUnit = getArguments().getParcelable("apartmentUnit");
            paymentSchema = getArguments().getParcelable("paymentSchema");
            paymentSchemaCalculation = getArguments().getParcelable("paymentSchemaCalculation");
        }

        context = getContext();
        activity = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.scan_id_tax_id);
            ((HideShowIconInterface) activity).showBackIcon();
        }

        checkApplicationPermissions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        // Set the image view
        imageViewFullSized = (ImageView)view.findViewById(R.id.image_view_full_sized);
        imageViewThumbnail = (ImageView)view.findViewById(R.id.image_view_thumbnail);
        imageCamera = (ImageView)view.findViewById(R.id.image_camera);
        buttonBack = (BootstrapButton)view.findViewById(R.id.button_back);
        buttonNext = (BootstrapButton)view.findViewById(R.id.button_next);
        buttonSkip = (BootstrapButton)view.findViewById(R.id.button_skip);
        buttonImport = (BootstrapButton)view.findViewById(R.id.button_import);
        buttonCapture = (BootstrapButton)view.findViewById(R.id.button_capture);
        buttonRecapture = (BootstrapButton)view.findViewById(R.id.button_recapture);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        textViewTop = (TextView)view.findViewById(R.id.text_view_top);
        textViewWatermark = (TextView)view.findViewById(R.id.text_view_watermark);
        viewCaptureKtp = (CardView) view.findViewById(R.id.view_capture_ktp);
        viewCaptureNpwp = (CardView) view.findViewById(R.id.view_capture_npwp);
        imageViewKtp = (ImageView) view.findViewById(R.id.image_view_ktp);
        imageViewNpwp = (ImageView) view.findViewById(R.id.image_view_npwp);

        buttonRecapture.setVisibility(View.GONE);

        camera = view.findViewById(R.id.camera);
        camera.setLifecycleOwner(getViewLifecycleOwner());

        // Set OnItemClickListener so we can be notified on button clicks
        buttonImport.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        buttonSkip.setOnClickListener(this);
        buttonRecapture.setOnClickListener(this);
        buttonCapture.setOnClickListener(this);
        viewCaptureKtp.setOnClickListener(this);
        viewCaptureNpwp.setOnClickListener(this);
        createTitle();

        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                result.toBitmap(new BitmapCallback() {
                    @Override
                    public void onBitmapReady(@Nullable Bitmap bitmap) {
                        camera.close();
                        buttonRecapture.setVisibility(View.VISIBLE);
                        buttonCapture.setVisibility(View.GONE);

                        String fileName = customer.getName().replaceAll("[^a-zA-Z0-9]", "_") + "_";

                        if (customer.getNik() != null){
                            fileName += customer.getNik();
                        } else if (customer.getHandphone() != null){
                            fileName += customer.getHandphone();
                        }


                        if (!ktpCaptured){
                            fileName += "_ktp_";
                        } else {
                            fileName += "_npwp_";
                        }

                        fileName += ".png";
                        try {
                            File file = new ParseFileCapture(bitmap, fileName).execute().get();
                            absolutePath = file.getAbsolutePath();
                            if (!ktpCaptured){
                                ktpPath = absolutePath;
                                identityCapturedDialog();
                            } else {
                                npwpPath = absolutePath;
                                taxCardCapturedDialog();
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onPictureTaken:"+absolutePath);
                    }
                });
            }

            // And much more
        });

        return view;

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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_back){
            backAction();
        } else if (view.getId() == R.id.button_next){
//            nextAction();
            moveAction();
        } else if (view.getId() == R.id.button_skip){
            skipAction();
        } else if (view.getId() == R.id.button_capture){
            if (camera != null){
                camera.takePicture();
                progressBar.setVisibility(View.VISIBLE);
                buttonCapture.setVisibility(View.GONE);
            }
        } else if (view.getId() == R.id.button_recapture){
            camera.open();
            camera.setVisibility(View.VISIBLE);

            buttonRecapture.setVisibility(View.GONE);
            buttonCapture.setVisibility(View.VISIBLE);
        } else if (view.getId() == buttonImport.getId()){
            importAction(view);
        } else if (view.getId() == viewCaptureKtp.getId()){
            selectImage(view, 1);
        } else if (view.getId() == viewCaptureNpwp.getId()){
            selectImage(view, 2);
        }
    }

    private void importAction(View view) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
//        try {
//            final CharSequence[] options = {getString(R.string.pick_image_from_gallety), getString(R.string.cancel)};
//            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//            builder.setTitle(getString(R.string.pick_image));
//            builder.setItems(options, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int item) {
//                    if (options[item].equals(getString(R.string.pick_image_from_gallety))) {
//                        dialog.dismiss();
//                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
//                    } else if (options[item].equals(getString(R.string.cancel))) {
//                        dialog.dismiss();
//                    }
//                }
//            });
//            builder.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
        void onFragmentInteraction(Uri uri);
    }

    private void backAction() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("order", order);
        bundle.putParcelable("customer", customer);
        bundle.putParcelable("apartmentUnit", apartmentUnit);
        bundle.putParcelable("paymentSchema", paymentSchema);
        bundle.putParcelable("paymentSchemaCalculation", paymentSchemaCalculation);

        Helpers.moveFragment(context, new CustomerIdentityFragment(), bundle);
    }

    private void nextAction() {
        Log.d(TAG, "nextAction:"+npwpPath+":"+ktpPath);
        if (ktpCaptured){
            if (npwpPath == null){
                notification();
            } else {
                moveAction();
            }
        } else {
            if (ktpPath == null){
                notification();
            } else {
//                ktpCaptured = true;
//                buttonCapture.setVisibility(View.VISIBLE);
//                imageCamera.setVisibility(View.GONE);
//                camera.setVisibility(View.VISIBLE);
            }
        }
//        createTitle();
    }

    private void moveAction(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("order", order);
        bundle.putParcelable("customer", customer);
        bundle.putParcelable("apartmentUnit", apartmentUnit);
        bundle.putParcelable("paymentSchema", paymentSchema);
        bundle.putParcelable("paymentSchemaCalculation", paymentSchemaCalculation);
        if (ktpPath != null) bundle.putString("ktpPath", ktpPath);
        if (npwpPath != null) bundle.putString("npwpPath", npwpPath);

        Helpers.moveFragment(context, new InstallmentFragment(), bundle);
    }

    private void checkApplicationPermissions() {
        Log.d(TAG, "checkApplicationPermissions:READ_EXTERNAL_STORAGE:"+(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "checkApplicationPermissions:WRITE_EXTERNAL_STORAGE:"+(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED));

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
    }

    private class ParseFileCapture extends AsyncTask<String, Integer, File>{
        private Bitmap bitmapImage;
        private String fileName;

        public ParseFileCapture(Bitmap bitmapImage, String fileName) {
            this.bitmapImage = bitmapImage;
            this.fileName = fileName;
        }

        @Override
        protected File doInBackground(String... strings) {
            Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            Boolean isSDSupportedDevice = Environment.isExternalStorageRemovable();
            File dirs = null;
            if(isSDSupportedDevice && isSDPresent) {
            } else {
            }
            dirs = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name)+"/captures");

            if (!dirs.exists()){
                dirs.mkdirs();
            }

            // Create imageDir
            File myPath = new File(dirs,fileName);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(myPath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 75, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null){
                        fos.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return myPath;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void createTitle(){
        if (ktpCaptured){
            activity.setTitle(R.string.scan_tax_card);
            textViewWatermark.setText(getString(R.string.scan_tax_name, customer.getNpwp()));
            textViewTop.setText(R.string.scanning_your_tax_card);
        } else {
            activity.setTitle(R.string.scan_identity_card);
            textViewWatermark.setText(getString(R.string.scan_identity_name, customer.getNik()));
            textViewTop.setText(R.string.scanning_your_identity_card);
        }
        if (!camera.isOpened()){
            camera.open();
        }

        buttonCapture.setVisibility(View.VISIBLE);
        buttonRecapture.setVisibility(View.GONE);
    }

    private void notification(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setPositiveButton(R.string.skip, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                skipAction();
            }
        });
        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setTitle(R.string.confirmation);

        if (ktpCaptured){
            builder.setMessage(R.string.sure_to_skip_capturing_tax_card);
        } else {
            builder.setMessage(R.string.sure_to_skip_capturing_identity_card);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void skipAction(){
        if (ktpCaptured){
            moveAction();
        } else {
            ktpCaptured = true;
            createTitle();
        }
    }

    private void identityCapturedDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setTitle(R.string.identity_captured_success);
        builder.setMessage(R.string.identity_captured_success_desc);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void taxCardCapturedDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setTitle(R.string.tax_card_captured_success);
        builder.setMessage(R.string.tax_card_captured_success_desc);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:"+requestCode+", "+resultCode);

        if (Helpers.contains(requestCode, PICK_IMAGE_CAMERA_KTP, PICK_IMAGE_CAMERA_NPWP, PICK_IMAGE_GALLERY_KTP, PICK_IMAGE_GALLERY_NPWP) && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                Log.e("Activity", "Pick from Gallery::>>> ");

                String imgPath = getRealPathFromURI(selectedImage);
                File destination = new File(imgPath);
                absolutePath = destination.getAbsolutePath();

                if (Helpers.contains(requestCode, PICK_IMAGE_CAMERA_KTP, PICK_IMAGE_GALLERY_KTP)){
                    imageViewKtp.setImageBitmap(bitmap);
                    ktpPath = absolutePath;
                } else if(Helpers.contains(requestCode, PICK_IMAGE_CAMERA_NPWP, PICK_IMAGE_GALLERY_NPWP)){
                    imageViewNpwp.setImageBitmap(bitmap);
                    npwpPath = absolutePath;
                }
//                if (camera.isOpened()){
//                    camera.close();
//                }
//                camera.setVisibility(View.GONE);
//                buttonCapture.setVisibility(View.GONE);
//                imageCamera.setVisibility(View.VISIBLE);
//                imageCamera.setImageBitmap(bitmap);
//
//                absolutePath = destination.getAbsolutePath();
//                if (!ktpCaptured){
//                    ktpPath = absolutePath;
//                    identityCapturedDialog();
//                } else {
//                    npwpPath = absolutePath;
//                    taxCardCapturedDialog();
//                }
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

    // Select image from camera and gallery
    private void selectImage(View view, final int code) { // 1 ktp, 2 npwp
        try {
            PackageManager pm = context.getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, context.getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            try {
                                if (code == 1) startActivityForResult(intent, PICK_IMAGE_CAMERA_KTP);
                                else if (code == 2) startActivityForResult(intent, PICK_IMAGE_CAMERA_NPWP);
                            } catch (ActivityNotFoundException e){
                                e.printStackTrace();
                            }
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            try {
                                if (code == 1) startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY_KTP);
                                else if (code == 2) startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY_NPWP);
                            } catch (ActivityNotFoundException e){
                                e.printStackTrace();
                            }
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else {
//                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
//            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
