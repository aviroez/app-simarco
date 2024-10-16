package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.EventActivityLog;
import com.ppproperti.simarco.entities.Image;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.utils.Helpers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventActivityLogDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventActivityLogDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventActivityLogDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EventActivityLog eventActivityLog;
    private TextView textEvent;
    private TextView textActivity;
    private TextView textTime;
    private TextView textSeparator;
    private Context context;
    private ImageView imageView;
    private FragmentActivity activity;

    public EventActivityLogDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventActivityLogDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventActivityLogDetailFragment newInstance(String param1, String param2) {
        EventActivityLogDetailFragment fragment = new EventActivityLogDetailFragment();
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
            eventActivityLog = getArguments().getParcelable("event_activity_log");
        }

        context = getContext();
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_activity_log_detail, container, false);
        textEvent = view.findViewById(R.id.text_event);
        textActivity = view.findViewById(R.id.text_activity);
        textTime = view.findViewById(R.id.text_time);
        textSeparator = view.findViewById(R.id.text_separator);
        imageView = view.findViewById(R.id.image_view);

        if (eventActivityLog != null){
            textSeparator.setVisibility(View.GONE);
            if (eventActivityLog.getEvent() != null && eventActivityLog.getActivity() != null){
                textSeparator.setVisibility(View.VISIBLE);
            }

            if (eventActivityLog.getEvent() != null){
                textEvent.setText(eventActivityLog.getEvent().getName());
            }

            if (eventActivityLog.getActivity() != null){
                textActivity.setText(eventActivityLog.getActivity().getName());
            }

            textTime.setText(eventActivityLog.getCreatedAt());

            if (eventActivityLog.getImages() != null && eventActivityLog.getImages().size() > 0){
                for (Image image: eventActivityLog.getImages()) {
                    Helpers.imageUrl(context, image.getUrl(), imageView);
                    break;
                }
            }
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.detail_activity_log);
            ((HideShowIconInterface) activity).showBackIcon();
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
}
