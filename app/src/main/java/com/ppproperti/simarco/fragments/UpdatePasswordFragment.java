package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.UserService;
import com.ppproperti.simarco.responses.ResponseUser;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdatePasswordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UpdatePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdatePasswordFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context context;
    private FragmentActivity activity;
    private Retrofit retrofit;
    private TextInputEditText textOldPassword;
    private TextInputEditText textNewPassword;
    private TextInputEditText textRetypeNewPassword;
    private BootstrapButton buttonClear;
    private BootstrapButton buttonUpdate;
    private String oldPassword;
    private String newPassword;
    private String retypeNewPassword;
    private CustomPreferences customPreference;

    public UpdatePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdatePasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdatePasswordFragment newInstance(String param1, String param2) {
        UpdatePasswordFragment fragment = new UpdatePasswordFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = getContext();
        activity = getActivity();

        customPreference = new CustomPreferences(context);
        retrofit = Helpers.initRetrofit(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);
        textOldPassword = view.findViewById(R.id.text_old_password);
        textNewPassword = view.findViewById(R.id.text_new_password);
        textRetypeNewPassword = view.findViewById(R.id.text_retype_new_password);
        buttonClear = view.findViewById(R.id.button_clear);
        buttonUpdate = view.findViewById(R.id.button_update);

        buttonClear.setOnClickListener(this);
        buttonUpdate.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.update_password);
            ((HideShowIconInterface) activity).showHamburgerIcon();
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
        if (view.getId() == buttonClear.getId()){
            clearAction(view);
        } else if (view.getId() == buttonUpdate.getId()){
            nextAction(view);
        }
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

    private void nextAction(View view) {
        if (validation()){
            HashMap<String, String> map = new HashMap<>();
            map.put("old_password", oldPassword);
            map.put("new_password", newPassword);
            UserService userService = retrofit.create(UserService.class);
            userService.updatePassword(map).enqueue(new Callback<ResponseUser>() {
                @Override
                public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){
                            if (response.body().getData() != null){
                                User user = response.body().getData();
                                customPreference.savePreferences("user", new Gson().toJson(user));

                                Bundle bundle = new Bundle();
                                bundle.putString("message", response.body().getMessage());
                                Helpers.moveFragment(context, new ProfileFragment(), bundle);
                            } else {
                                Helpers.showLongSnackbar(getView(), response.body().getMessage());
                            }
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
        oldPassword = textOldPassword.getText().toString();
        newPassword = textNewPassword.getText().toString();
        retypeNewPassword = textRetypeNewPassword.getText().toString();

        if (oldPassword.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.old_password_is_required);
            textOldPassword.requestFocus();
            return false;
        } else if (newPassword.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.new_password_is_required);
            textNewPassword.requestFocus();
            return false;
        } else if (retypeNewPassword.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.retype_new_password_is_required);
            textRetypeNewPassword.requestFocus();
            return false;
        } else if (!newPassword.equals(retypeNewPassword)){
            Helpers.showLongSnackbar(getView(), R.string.retype_new_password_should_be_same);
            textRetypeNewPassword.requestFocus();
            return false;
        } else if (newPassword.equals(oldPassword)){
            Helpers.showLongSnackbar(getView(), R.string.new_password_and_old_password_should_not_be_similar);
            textNewPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void clearAction(View view) {
        textOldPassword.setText("");
        textNewPassword.setText("");
        textRetypeNewPassword.setText("");
    }
}
