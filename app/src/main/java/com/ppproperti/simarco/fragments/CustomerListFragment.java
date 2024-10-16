package com.ppproperti.simarco.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.CustomerLeadListRecyclerViewAdapter;
import com.ppproperti.simarco.adapters.CustomerListRecyclerViewAdapter;
import com.ppproperti.simarco.entities.Customer;
import com.ppproperti.simarco.entities.CustomerLead;
import com.ppproperti.simarco.entities.CustomerLeadPagination;
import com.ppproperti.simarco.entities.CustomerPagination;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.CustomerLeadService;
import com.ppproperti.simarco.interfaces.CustomerService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.OnBackPressed;
import com.ppproperti.simarco.responses.ResponseCustomerLeadList;
import com.ppproperti.simarco.responses.ResponseCustomerLeadListPagination;
import com.ppproperti.simarco.responses.ResponseCustomerList;
import com.ppproperti.simarco.responses.ResponseCustomerListPagination;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CustomerListFragment extends Fragment implements View.OnClickListener, OnBackPressed {

    private static final String TAG = CustomerListFragment.class.getSimpleName();
    private static String message;
    private int mColumnCount = 1;
    private List<Customer> listCustomer = new ArrayList<>();
    private List<CustomerLead> listCustomerLead = new ArrayList<>();
    private OnListFragmentInteractionListener listener;
    private User user;
    private Context context;
    private FragmentActivity activity;
    private CustomerListRecyclerViewAdapter customerListAdapter;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewLead;
    private FloatingActionButton buttonAdd;
    private ProgressBar progressBar;
    private Retrofit retrofit;
    private CustomerLeadListRecyclerViewAdapter customerLeadListAdapter;
    private TabLayout tabLayout;
    private CustomerPagination customerPagination = null;
    private CustomerLeadPagination customerLeadPagination = null;
    private int position = 0;
    private SearchView searchView;
    private String search = "";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CustomerListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CustomerListFragment newInstance(int columnCount, int position) {
        CustomerListFragment fragment = new CustomerListFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TypefaceProvider.registerDefaultIconSets();
        if (getArguments() != null) {
            message = getArguments().getString("message");
            position = getArguments().getInt("position", 0);
        }
        context = getContext();
        activity = getActivity();

        if (message != null && message.length() > 0){
            Helpers.showLongSnackbar(getView(), message);
        }
        CustomPreferences customPreferences = new CustomPreferences(context);
        user = customPreferences.getUser();

        retrofit = Helpers.initRetrofit(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerViewLead = (RecyclerView) view.findViewById(R.id.list_lead);
        buttonAdd = (FloatingActionButton) view.findViewById(R.id.button_add_float);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        recyclerViewLead.setVisibility(View.GONE);

        buttonAdd.setOnClickListener(this);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                parsePosition(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                position = tab.getPosition();
                parsePosition(position);
            }
        });


        customerListAdapter = new CustomerListRecyclerViewAdapter(context, listCustomer, listener);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(customerListAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    if (customerPagination != null && customerPagination.getCurrentPage() < customerPagination.getLastPage()){
                        parseCustomer(customerPagination.getCurrentPage() + 1, search);
                    }
                }
            }
        });

        customerLeadListAdapter = new CustomerLeadListRecyclerViewAdapter(context, listCustomerLead, listener);
        recyclerViewLead.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewLead.setAdapter(customerLeadListAdapter);
        recyclerViewLead.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    if (customerLeadPagination != null && customerLeadPagination.getCurrentPage() < customerLeadPagination.getLastPage()){
                        parseCustomerLead(customerLeadPagination.getCurrentPage() + 1, search);
                    }
                }
            }
        });

        if (activity != null){
            searchView = activity.findViewById(R.id.search_view);
            searchView.setVisibility(View.VISIBLE);
            searchView.setQuery("", false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    search = query;

                    customerPagination = null;
                    listCustomer = new ArrayList<>();
                    if (customerListAdapter != null) customerListAdapter.notifyDataSetChanged();
                    parseCustomer(1, search);

                    customerLeadPagination = null;
                    listCustomerLead = new ArrayList<>();
                    if (customerLeadListAdapter != null) customerLeadListAdapter.notifyDataSetChanged();
                    parseCustomerLead(1, search);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.trim().equals("") && search.length() > 0){
                        search = "";

                        customerPagination = null;
                        listCustomer = new ArrayList<>();
                        if (customerListAdapter != null) customerListAdapter.notifyDataSetChanged();
                        parseCustomer(1, search);

                        customerLeadPagination = null;
                        listCustomerLead = new ArrayList<>();
                        if (customerLeadListAdapter != null) customerLeadListAdapter.notifyDataSetChanged();
                        parseCustomerLead(1, search);
                    }

                    return false;
                }
            });
        }

        initData();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.customers);
            ((HideShowIconInterface) activity).showHamburgerIcon();
        }

//        initData();
        parsePosition(position);

//        checkApplicationPermissions();

    }

    @Override
    public void onResume() {
        super.onResume();

        parsePosition(position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            listener = (OnListFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.toolbar_menu, menu);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == buttonAdd.getId()){
            Bundle bundle = new Bundle();
            if (tabLayout.getSelectedTabPosition() == 1){
                CustomerLeadAddFragment customerLeadAddFragment = new CustomerLeadAddFragment();
                customerLeadAddFragment.setArguments(bundle);
                Helpers.moveFragment(context, customerLeadAddFragment, null);
            } else {
                CustomerAddFragment customerAddFragment = new CustomerAddFragment();
                customerAddFragment.setArguments(bundle);
                Helpers.moveFragment(context, customerAddFragment, null);
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Customer customer);
    }

    private void initData() {
        if (customerPagination == null) parseCustomer(1, search);
        if (customerLeadPagination == null) parseCustomerLead(1, search);
    }

    private void parseCustomerLead(int page, String search) {
        progressBar.setVisibility(View.VISIBLE);
        final CustomerLeadService customerLeadService = retrofit.create(CustomerLeadService.class);
        final Map<String, String> leadParams = new HashMap<>();
        leadParams.put("marketing_id", String.valueOf(user.getId()));
        leadParams.put("page", String.valueOf(page));
        leadParams.put("limit", String.valueOf(10));
        if (search != null && search.trim().length() > 0) leadParams.put("search", search);
//        leadParams.put("with[0]", "orders.order_installments.order_installment_payment");

        Call<ResponseCustomerLeadListPagination> callLead = customerLeadService.get(leadParams, 10);
        callLead.enqueue(new Callback<ResponseCustomerLeadListPagination>() {
            @Override
            public void onResponse(Call<ResponseCustomerLeadListPagination> call, Response<ResponseCustomerLeadListPagination> response) {
                if(response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        customerLeadPagination = response.body().getData();
                        if (customerLeadPagination != null){
                            if (listCustomerLead.size() <= 0) {
                                listCustomerLead = customerLeadPagination.getData();
                                customerLeadListAdapter = new CustomerLeadListRecyclerViewAdapter(context, listCustomerLead, listener);
                                recyclerViewLead.setLayoutManager(new LinearLayoutManager(context));
                                recyclerViewLead.setAdapter(customerLeadListAdapter);
                            } else {
                                listCustomerLead.addAll(customerLeadPagination.getData());
                                customerLeadListAdapter.notifyDataSetChanged();
                            }
                            Log.d(TAG, "listCustomerLead:"+listCustomerLead.size());
                        }
                    }

                    if (listCustomerLead.size() <= 0 && position == 1){
                        Helpers.showShortSnackbar(getView(), R.string.customer_lead_list_empty);
                    }
                } else {
                    Log.d(TAG, "onResponse:"+response.message());
                    Helpers.showShortSnackbar(getView(), response.message());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseCustomerLeadListPagination> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    private void parseCustomer(int page, String search) {
        progressBar.setVisibility(View.VISIBLE);
        final CustomerService service = retrofit.create(CustomerService.class);
        final Map<String, String> params = new HashMap<>();
        params.put("marketing_id", String.valueOf(user.getId()));
        params.put("with[0]", "orders.order_installments.order_installment_payment");
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(10));
        if (search != null && search.trim().length() > 0) params.put("search", search);

        Call<ResponseCustomerListPagination> call = service.customers(params, 10);
        call.enqueue(new Callback<ResponseCustomerListPagination>() {
            @Override
            public void onResponse(Call<ResponseCustomerListPagination> call, Response<ResponseCustomerListPagination> response) {
                if(response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        customerPagination = response.body().getData();
                        if (customerPagination != null){
                            if (listCustomer.size() <= 0){
                                listCustomer = customerPagination.getData();
                                customerListAdapter = new CustomerListRecyclerViewAdapter(context, listCustomer, listener);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                recyclerView.setAdapter(customerListAdapter);
                            } else {
                                listCustomer.addAll(customerPagination.getData());
                                customerListAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    if (listCustomer.size() <= 0 && position == 0){
                        Helpers.showShortSnackbar(getView(), R.string.customer_list_empty);
                    }
                } else {
                    Log.d(TAG, "onResponse:"+response.message());
                    Helpers.showShortSnackbar(getView(), response.message());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseCustomerListPagination> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void checkApplicationPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CALL_PHONE}, 110);
            }
        }
    }

    private void parsePosition(int position){
        if (position == 0) {
            recyclerViewLead.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            buttonAdd.setVisibility(View.GONE);
            if (customerListAdapter != null) customerListAdapter.notifyDataSetChanged();
        } else if (position == 1){
            recyclerView.setVisibility(View.GONE);
            recyclerViewLead.setVisibility(View.VISIBLE);
            buttonAdd.setVisibility(View.VISIBLE);
            if (customerLeadListAdapter != null) customerLeadListAdapter.notifyDataSetChanged();
        }
    }
}
