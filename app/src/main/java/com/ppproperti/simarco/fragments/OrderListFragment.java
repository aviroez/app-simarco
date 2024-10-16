package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.OrderRecyclerViewAdapter;
import com.ppproperti.simarco.entities.Order;
import com.ppproperti.simarco.entities.OrderPagination;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.OrderService;
import com.ppproperti.simarco.responses.ResponseOrderList;
import com.ppproperti.simarco.responses.ResponseOrderListPagination;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class OrderListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener listener;
    private List<Order> listOrder = new ArrayList<>();
    private Context context;
    private Retrofit retrofit;
    private User user;
    private OrderRecyclerViewAdapter orderListAdapter;
    private RecyclerView recyclerView;
    private FragmentActivity activity;
    private ProgressBar progressBar;
    private SearchView searchView;
    private String search = "";
    private OrderPagination orderPagination;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrderListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static OrderListFragment newInstance(int columnCount) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        context = getContext();
        activity = getActivity();
        CustomPreferences customPreferences = new CustomPreferences(context);
        user = customPreferences.getUser();
        retrofit = Helpers.initRetrofit(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        if (activity != null) {
            searchView = activity.findViewById(R.id.search_view);
            searchView.setVisibility(View.VISIBLE);
            searchView.setQuery("", false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    search = query;

                    orderPagination = null;
                    listOrder = new ArrayList<>();
                    if (orderListAdapter != null) orderListAdapter.notifyDataSetChanged();
                    parseOrder(1, search);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.trim().equals("") && search.length() > 0) {
                        search = "";

                        orderPagination = null;
                        listOrder = new ArrayList<>();
                        if (orderListAdapter != null) orderListAdapter.notifyDataSetChanged();
                        parseOrder(1, search);
                    }
                    return false;
                }
            });
        }

        orderListAdapter = new OrderRecyclerViewAdapter(context, listOrder, listener);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(orderListAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    if (orderPagination !=null && orderPagination.getCurrentPage() < orderPagination.getLastPage()){
                        parseOrder(orderPagination.getCurrentPage() + 1, search);
                    }
                }
            }
        });
        parseOrder(1, search);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.order_list);
            ((HideShowIconInterface) activity).showHamburgerIcon();
        }
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
        void onListFragmentInteraction(Order item);
    }

    private void parseOrder(int page, String search) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> map = new HashMap<>();
        map.put("marketing_id", String.valueOf(user.getId()));
        map.put("limit", String.valueOf(10));
//        map.put("not_null[0]", "booking_no");
        map.put("status_in[0]", "booked");
        map.put("status_in[1]", "approved");
        map.put("status_in[2]", "sp1");
        map.put("status_in[3]", "sp2");
        map.put("status_in[4]", "sp3");
        map.put("with[0]", "apartment_unit.apartment_tower.apartment");
        map.put("with[1]", "apartment_unit.apartment_type");
        map.put("with[2]", "apartment_unit.apartment_floor");
        map.put("with[3]", "apartment_unit.apartment_view");
        map.put("with[4]", "payment_type");
        map.put("with[5]", "payment_schema");
        map.put("with[6]", "order_installments.order_installment_fines");
        map.put("with[7]", "order_installments.order_installment_payments");
        if (search != null) map.put("search", search);

        OrderService service = retrofit.create(OrderService.class);
        Call<ResponseOrderListPagination> call = service.orders(map, page);

        call.enqueue(new Callback<ResponseOrderListPagination>() {
            @Override
            public void onResponse(Call<ResponseOrderListPagination> call, Response<ResponseOrderListPagination> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getData() != null && response.body().getData().getData() != null) {
                        orderPagination = response.body().getData();
                        if (orderPagination != null && orderPagination.getData() != null){
                            if (listOrder.size() <= 0){
                                listOrder = orderPagination.getData();

                                orderListAdapter = new OrderRecyclerViewAdapter(context, listOrder, listener);

                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                recyclerView.setAdapter(orderListAdapter);
                            } else {
                                listOrder.addAll(orderPagination.getData());
                                orderListAdapter.notifyDataSetChanged();
                            }

                            if (listOrder.size() <= 0){
                                Helpers.showLongSnackbar(getView(), R.string.order_list_empty);
                            }
                        }
                    } else {
                        Helpers.showLongSnackbar(getView(), R.string.order_list_empty);
                    }
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseOrderListPagination> call, Throwable t) {
                t.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
