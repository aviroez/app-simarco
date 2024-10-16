package com.ppproperti.simarco.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Customer;
import com.ppproperti.simarco.entities.CustomerLead;
import com.ppproperti.simarco.entities.CustomerLeadJoin;
import com.ppproperti.simarco.entities.CustomerLeadPagination;
import com.ppproperti.simarco.entities.CustomerPagination;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.CustomerLeadService;
import com.ppproperti.simarco.interfaces.CustomerService;
import com.ppproperti.simarco.responses.ResponseCustomerLeadListPagination;
import com.ppproperti.simarco.responses.ResponseCustomerListPagination;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CustomerLeadJoinAdapter extends ArrayAdapter<CustomerLeadJoin> implements Filterable {

    private Retrofit retrofit;
    private User user = new User();
    private List<CustomerLeadJoin> list = new ArrayList<>();
    private boolean customerProcess = false;
    private boolean customerLeadProcess = false;
    private List<Customer> listCustomer = new ArrayList<>();
    private List<CustomerLead> listCustomerLead = new ArrayList<>();
    private CustomerPagination customerPagination = new CustomerPagination();
    private CustomerLeadPagination customerLeadPagination = new CustomerLeadPagination();

    public CustomerLeadJoinAdapter(@NonNull Context context, int resource, @NonNull List<CustomerLeadJoin> list) {
        super(context, resource, list);
        this.list = list;
        retrofit = Helpers.initRetrofit(context);
        user = new CustomPreferences(context).getUser();
    }

    @Nullable
    @Override
    public CustomerLeadJoin getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }


    public List<CustomerLeadJoin> getItems() {
        return this.list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try{
            if(convertView == null){
                LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
                convertView = inflater.inflate(R.layout.item_customer_spinner, parent, false);
            }

            // object item based on the position
            CustomerLeadJoin customerLeadJoin = super.getItem(position);

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textName = (TextView) convertView.findViewById(R.id.text_name);
            TextView textDescription = (TextView) convertView.findViewById(R.id.text_description);
            textName.setText(customerLeadJoin.getName());
            if (customerLeadJoin.getCustomer() != null){
                StringBuilder builder = new StringBuilder();
                if (customerLeadJoin.getCustomer().getEmail() != null) builder.append(customerLeadJoin.getCustomer().getEmail());
                if (builder.length() > 0) builder.append(" - ");
                if (customerLeadJoin.getCustomer().getHandphone() != null) builder.append(customerLeadJoin.getCustomer().getHandphone());
                textDescription.setText(builder.toString());
            } else if (customerLeadJoin.getCustomerLead() != null){
                StringBuilder builder = new StringBuilder();
                if (customerLeadJoin.getCustomerLead().getEmail() != null) builder.append(customerLeadJoin.getCustomerLead().getEmail());
                if (builder.length() > 0) builder.append(" - ");
                if (customerLeadJoin.getCustomerLead().getHandphone() != null) builder.append(customerLeadJoin.getCustomerLead().getHandphone());
                textDescription.setText(builder.toString());
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
//        return super.getFilter();
        Filter f = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence != null) {
                    // Retrieve the autocomplete results.
                    listCustomer = parseCustomer(charSequence.toString());
                    listCustomerLead = parseCustomerLead(charSequence.toString());

                    list = joinCustomerLead();
                    filterResults.values = list;
                    filterResults.count = list.size();
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                CustomerLeadJoinAdapter.super.clear();
                if (filterResults != null && filterResults.count > 0) {
//                    list = (List<CustomerLeadJoin>) filterResults.values;
                    CustomerLeadJoinAdapter.super.addAll((List<CustomerLeadJoin>)filterResults.values);

                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                CustomerLeadJoin customerLeadJoin = (CustomerLeadJoin) resultValue;
                if (customerLeadJoin != null){
                    return customerLeadJoin.getName();
                }
                return super.convertResultToString(resultValue);
            }
        };
        return f;
    }


    public void updateList(List<CustomerLeadJoin> list){
        this.list = list;
        super.clear();
        super.addAll(list);
        notifyDataSetChanged();
    }

    private List<Customer> parseCustomer(String search) {
        customerProcess = true;
        List<Customer> listCustomer = new ArrayList<>();
        CustomerService service = retrofit.create(CustomerService.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("marketing_id", String.valueOf(user.getId()));
        if (search.length() > 0) map.put("search", search);
        try {
            Response<ResponseCustomerListPagination> response = service.customers(map, 5).execute();
            if (response.isSuccessful()){
                if (response.body() != null) {
                    if (response.body().getData() != null) {
                        customerPagination = response.body().getData();
                        if (customerPagination.getData() != null){
                            listCustomer = response.body().getData().getData();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        customerProcess = false;
        return listCustomer;
    }

    private List<CustomerLead> parseCustomerLead(String search) {
        customerLeadProcess = true;
        List<CustomerLead> listCustomerLead = new ArrayList<>();
        CustomerLeadService service = retrofit.create(CustomerLeadService.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("marketing_id", String.valueOf(user.getId()));
        if (search.length() > 0) map.put("search", search);
        Response<ResponseCustomerLeadListPagination> response = null;
        try {
            response = service.get(map, 5).execute();
            if (response.isSuccessful()){
                if (response.body() != null) {
                    if (response.body().getData() != null) {
                        customerLeadPagination = response.body().getData();
                        if (customerLeadPagination.getData() != null){
                            listCustomerLead = customerLeadPagination.getData();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        customerLeadProcess = false;
        return listCustomerLead;
    }

    private List<CustomerLeadJoin> joinCustomerLead() {
        List<CustomerLeadJoin> listCustomerLeadJoin = new ArrayList<>();
        if (!customerProcess && !customerLeadProcess){
            CustomerLeadJoin customerLeadJoin = null;

            for (Customer customer: listCustomer){
                customerLeadJoin = new CustomerLeadJoin();
                customerLeadJoin.setCustomerId(customer.getId());
                customerLeadJoin.setName(customer.getName());
                customerLeadJoin.setCustomer(customer);
                listCustomerLeadJoin.add(customerLeadJoin);
            }

            for (CustomerLead customerLead: listCustomerLead){
                customerLeadJoin = new CustomerLeadJoin();
                customerLeadJoin.setCustomerLeadId(customerLead.getId());
                customerLeadJoin.setName(customerLead.getName());
                customerLeadJoin.setCustomerLead(customerLead);
                listCustomerLeadJoin.add(customerLeadJoin);
            }
        }
        return listCustomerLeadJoin;
    }
}
