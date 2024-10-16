package com.ppproperti.simarco.fragments;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.ProductKnowledgeImageAdapter;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.utils.Helpers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductKnowledgeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductKnowledgeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductKnowledgeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = ProductKnowledgeFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private PDFView pdfView;
    private Context context;
    private final String FILE_NAME = "product_knowledge_20200817.pdf";
    private final String FILE_NAME_IMG = "product_knowledge_20200817";
    private final int maxNumber = 71;
    private int currentNumber = 0;
    private File dirs;
    private RecyclerView recyclerView;
    private DownloadManager downloadManager;
    private FragmentActivity activity;
    private BroadcastReceiver onComplete;
    private List<File> listFile = new ArrayList<>();
    private ProductKnowledgeImageAdapter recyclerViewAdapter;
    private ProgressBar progressBar;
    private float density = 0;
    private String densityDir = "xl";

    public ProductKnowledgeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductKnowledgeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductKnowledgeFragment newInstance(String param1, String param2) {
        ProductKnowledgeFragment fragment = new ProductKnowledgeFragment();
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

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        density = context.getResources().getDisplayMetrics().density;
        if (density == 0.75f) {
            densityDir = "xs";
        } else if (density >= 1.0f && density < 1.5f) {
            densityDir = "sm";
        } else if (density == 1.5f) {
            densityDir = "md";
        } else if (density > 1.5f && density <= 2.0f) {
            densityDir = "lg";
        } else if (density > 2.0f && density <= 3.0f) {
            densityDir = "xl";
        } else {
            densityDir = "xl";
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.product_knowledge);
            ((HideShowIconInterface) activity).showHamburgerIcon();
        }

        parseImage();
//        parseImageManager();
//        parsePdfFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_knowledge, container, false);
        pdfView = (PDFView) view.findViewById(R.id.pdf_view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        recyclerViewAdapter = new ProductKnowledgeImageAdapter(context, listFile);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(recyclerViewAdapter);
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

    private void parseImage(){
        try {
            dirs = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name)+"/.imgs");
        } catch (Exception e){
        }
        if (dirs == null){
            try {
                dirs = new File(Environment.getExternalStorageDirectory(), activity.getString(R.string.app_name)+"/.imgs");
            } catch (Exception e){
            }
        }
        if (dirs != null){
            if (!dirs.exists()){
                dirs.mkdirs();
            }
            progressBar.setVisibility(View.VISIBLE);

            String number = String.format("%04d", (++currentNumber));
            String fileName = FILE_NAME_IMG+"-"+number+".jpg";
            String url = context.getString(R.string.url)+"imgs/pk/"+densityDir+"/"+fileName;
            new ParseDownloadImg(context, url, dirs, fileName, density).execute();
        }
    }

    private void parseImageManager(){
        for (int i = 1; i <= maxNumber ; i++){
            String number = String.format("%04d", (maxNumber));
            String fileName = FILE_NAME_IMG+"-"+number+".jpg";
            String url = getString(R.string.url)+"imgs/pk/"+fileName;
            downloadImage(context, downloadManager, url);
        }

        onComplete = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String filePath="";
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(intent.getExtras().getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
                Cursor c = downloadManager.query(q);

                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        String downloadFileLocalUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        filePath = Uri.parse(downloadFileLocalUri).getPath();
                    }
                }
                c.close();
                listFile.add(new File(filePath));
                recyclerViewAdapter.notifyItemInserted(0);
            }
        };

        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    public class ParseDownloadImg extends AsyncTask<String, String, File> {
        private final String TAG = ParseDownloadImg.class.getSimpleName();
        private Context context;
        private String url;
        private File outputDirs;
        private String fileName;
        private boolean urlValid = false;

        public ParseDownloadImg(Context context, String url, File outputDirs, String fileName, float density) {
            this.context = context;
            this.url = url;
            this.outputDirs = outputDirs;
            this.fileName = fileName;
        }

        @Override
        protected File doInBackground(String... strings) {
            Log.d(TAG, "doInBackground:url:"+url);
            try {
                if (!outputDirs.exists()){
                    outputDirs.mkdirs();
                }

                File outputFile = new File(outputDirs, fileName);

                if (outputFile.exists()){
                    return outputFile;
                } else {
                    if (Helpers.exists(url)){
                        URL u = new URL(url);
                        URLConnection conn = u.openConnection();
                        int contentLength = conn.getContentLength();

                        DataInputStream stream = new DataInputStream(u.openStream());

                        byte[] buffer = new byte[contentLength];
                        stream.readFully(buffer);
                        stream.close();

                        DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
                        fos.write(buffer);
                        fos.flush();
                        fos.close();

                        Log.d(TAG, "doInBackground:outputFile:"+outputFile.getTotalSpace()+":"+outputFile.getAbsolutePath());

                        return outputFile;
                    } else {
                        return null;
                    }
                }
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            progressBar.setVisibility(View.GONE);

            if (file != null){
                Log.d(TAG, "onPostExecute:"+file.getAbsolutePath());
                listFile.add(file);

//                recyclerViewAdapter = new ProductKnowledgeImageAdapter(context, listFile);
//                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyItemInserted(listFile.size()-1);
                parseImage();
            }
        }
    }



    public static void downloadImage(Context context, DownloadManager downloadmanager, String url) {
        long ts = System.currentTimeMillis();
        Uri uri = Uri.parse(url);
        String fileName = URLUtil.guessFileName(url, null, null);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(fileName);
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName);


        downloadmanager.enqueue(request);
    }
}
