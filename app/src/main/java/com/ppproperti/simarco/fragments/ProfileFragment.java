package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.bumptech.glide.Glide;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context context;
    private CustomPreferences customPreference;
    private User user = new User();
    private TextView textAlias;
    private TextView textFullName;
    private TextView textEmail;
    private TextView textHandphone;
    private TextView textPosition;
    private TextView textSignature;
    private ImageView imageSignature;
    private SignaturePad signaturePad;
    private BootstrapButton buttonEditProfile;
    private String message;
    private FragmentActivity activity;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
            message = getArguments().getString("message");
        }

        context = getContext();
        activity = getActivity();

        customPreference = new CustomPreferences(context);
        user = customPreference.getUser();

        if (message != null){
            Helpers.showLongSnackbar(getView(), message);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        buttonEditProfile = view.findViewById(R.id.button_edit_profile);
        textAlias = view.findViewById(R.id.text_alias);
        textFullName = view.findViewById(R.id.text_full_name);
        textEmail = view.findViewById(R.id.text_email);
        textHandphone = view.findViewById(R.id.text_handphone);
        textPosition = view.findViewById(R.id.text_position);
        textSignature = view.findViewById(R.id.text_signature);
        imageSignature = view.findViewById(R.id.image_signature);
        signaturePad = view.findViewById(R.id.signature_pad);

        buttonEditProfile.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.my_profile);
            ((HideShowIconInterface) activity).showHamburgerIcon();
        }

        parseUser();
    }

    private void parseUser() {
        if (user != null){
            textAlias.setText(user.getAlias());
            textFullName.setText(user.getName());
            textEmail.setText(user.getEmail());
            textHandphone.setText(user.getHandphone());
            if (user.getUserPosition() != null){
                textPosition.setText(user.getUserPosition().getName());
            }
            if (user.getSignature() != null){
                imageSignature.setVisibility(View.VISIBLE);
                generateSignature(user.getSignature());
            }
        }
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
            backAction(view);
        } else if (view.getId() == R.id.button_clear){
            clearAction(view);
        } else if (view.getId() == R.id.button_save){
            saveAction(view);
        } else if (view.getId() == R.id.button_edit_profile){
            editProfileAction(view);
        }
    }

    private void editProfileAction(View view) {
        Helpers.moveFragment(context, new ProfileEditFragment(), null);
    }

    private void saveAction(View view) {

    }

    private void clearAction(View view) {

    }

    private void backAction(View view) {

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

    private void generateSignature(String signature) {
        SVG svg = null;
        try {
            svg = SVG.getFromString(signature);

            Bitmap newBM = Bitmap.createBitmap((int) Math.ceil(svg.getDocumentWidth()),
                    (int) Math.ceil(svg.getDocumentHeight()),
                    Bitmap.Config.ARGB_8888);

            Canvas bmcanvas = new Canvas(newBM);

            // Clear background to white
            bmcanvas.drawRGB(255, 255, 255);

            // Render our document onto our canvas
            svg.renderToCanvas(bmcanvas);
//            signaturePad.setSignatureBitmap(newBM);
            imageSignature.setImageBitmap(newBM);
        } catch (SVGParseException e) {
            e.printStackTrace();
        }
    }
}
