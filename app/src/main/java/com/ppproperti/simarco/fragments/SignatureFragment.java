package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.ApartmentUnit;
import com.ppproperti.simarco.entities.Customer;
import com.ppproperti.simarco.entities.Order;
import com.ppproperti.simarco.entities.PaymentSchema;
import com.ppproperti.simarco.entities.PaymentSchemaCalculation;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.ImageService;
import com.ppproperti.simarco.interfaces.OrderService;
import com.ppproperti.simarco.responses.ResponseOrder;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignatureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignatureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignatureFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Customer customer;
    private ApartmentUnit apartmentUnit;
    private PaymentSchema paymentSchema;
    private PaymentSchemaCalculation paymentSchemaCalculation;
    private Context context;
    private Retrofit retrofit;
    private OrderService serviceOrder;
    private Order order;
    private SignaturePad signaturePad;
    private User user;
    private FragmentActivity activity;
    private ImageService serviceImage;
    private Call<ResponseOrder> call;
    private ProgressBar progressBar;
    private BootstrapButton buttonSave;
    private String ktpPath;
    private String npwpPath;

    public SignatureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignatureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignatureFragment newInstance(String param1, String param2) {
        SignatureFragment fragment = new SignatureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            ktpPath = getArguments().getString("ktpPath");
            npwpPath = getArguments().getString("npwpPath");
        }

        context = getContext();
        activity = getActivity();

        CustomPreferences customPreferences = new CustomPreferences(context);
        user = customPreferences.getUser();

        retrofit = Helpers.initRetrofit(context);
        serviceOrder = retrofit.create(OrderService.class);
        serviceImage = retrofit.create(ImageService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signature, container, false);
        signaturePad = view.findViewById(R.id.signature_pad);
        BootstrapButton buttonClear = view.findViewById(R.id.button_clear);
        buttonSave = view.findViewById(R.id.button_save);
        TextView textName = view.findViewById(R.id.text_name);
        progressBar = view.findViewById(R.id.progress_bar);

        if (customer != null){
            textName.setText(customer.getName());

            buttonClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signaturePad.clear();
                }
            });

            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveAction();
                }
            });
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.signature_customer);
            ((HideShowIconInterface) activity).showBackIcon();
        }
    }

    private void saveAction() {
        Helpers.showLongSnackbar(getView(), R.string.processing_order_please_wait);

        buttonSave.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        Bitmap bitmap = signaturePad.getTransparentSignatureBitmap();
        String signatureSvg = signaturePad.getSignatureSvg();

        HashMap<String, String> map = new HashMap<>();
        map.put("signature", signatureSvg);

        Call<ResponseOrder> callOrder = serviceOrder.process(order.getId(), map);

        callOrder.enqueue(new Callback<ResponseOrder>() {
            @Override
            public void onResponse(Call<ResponseOrder> call, Response<ResponseOrder> response) {
                if (response.isSuccessful()){
                    Order order = response.body().getData();

                    progressBar.setProgress(50);
                    if (ktpPath != null || npwpPath != null){
                        uploadFile(order, ktpPath, npwpPath);
                    } else {
                        progressBar.setProgress(100);
                        successOrder();
                    }
                } else {
                    successOrder();
                }
            }

            @Override
            public void onFailure(Call<ResponseOrder> call, Throwable t) {
                Log.e(SignatureFragment.class.getSimpleName(), "onFailure:"+t.getMessage());
                progressBar.setVisibility(View.GONE);
//                Helpers.showLongSnackbar(getView(), R.string.failed_to_add_order);
//                buttonSave.setEnabled(true);
                successOrder();
            }
        });
    }

    private void successOrder() {
        CustomPreferences customPreferences = new CustomPreferences(context, "order");
        customPreferences.savePreferences("order", null);
        customPreferences.savePreferences("apartmentUnit", null);
        customPreferences.savePreferences("paymentSchema", null);
        customPreferences.savePreferences("paymentSchemaCalculation", null);
        customPreferences.savePreferences("absolutePath", null);

        Bundle bundle = new Bundle();
        bundle.putString("message", getString(R.string.order_is_added_successfully));

        Helpers.moveFragment(context, new SuccessFragment(), bundle);
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
        void onFragmentInteraction(Uri uri);
    }

    private void uploadFile(Order order, String ktpPath, String npwpPath) {
        try {
            List<MultipartBody.Part> listPart = new ArrayList<>();
            List<RequestBody> listBody = new ArrayList<>();
            ArrayList<String> listCode = new ArrayList<>();

            HashMap<String, String> map = new HashMap<>();
            if (ktpPath != null){
                File file = new File(ktpPath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg;image/png"), file);

                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file[]", file.getName(), requestBody);
                RequestBody fileName = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                listPart.add(fileToUpload);
                listBody.add(fileName);
                listCode.add("ktp");
            }
            if (npwpPath != null){
                File file = new File(npwpPath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg;image/png"), file);

                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file[]", file.getName(), requestBody);
                RequestBody fileName = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                listPart.add(fileToUpload);
                listBody.add(fileName);
                listCode.add("npwp");
            }

            if (listPart.size() > 0){
                call = serviceImage.order(order.getId(), listPart, listBody, listCode);

                call.enqueue(new Callback<ResponseOrder>() {
                    @Override
                    public void onResponse(Call<ResponseOrder> call, Response<ResponseOrder> response) {
                        progressBar.setProgress(100);
                        successOrder();
                    }

                    @Override
                    public void onFailure(Call<ResponseOrder> call, Throwable t) {
                        t.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        successOrder();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            successOrder();
        }
    }

    private void backAction() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("order", order);
        bundle.putParcelable("customer", customer);
        bundle.putParcelable("apartmentUnit", apartmentUnit);
        bundle.putParcelable("paymentSchema", paymentSchema);
        bundle.putParcelable("paymentSchemaCalculation", paymentSchemaCalculation);
        bundle.putString("ktpPath", ktpPath);
        bundle.putString("npwpPath", npwpPath);

        Helpers.moveFragment(context, new TosFragment(), bundle);
    }
}
