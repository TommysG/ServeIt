package com.example.serveIt.employee_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.serveIt.Food_Item;
import com.example.serveIt.Order;
import com.example.serveIt.Order_Item;
import com.example.serveIt.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class active_order extends Fragment {


    private RecyclerView order_list;
    private DatabaseReference database;

    private TextView tableId;
    private ArrayList<Order_Item> list_items;
    private List<String> orderIDs;
    private OrderAdapter orderAdapter;
    private Button prevBtn,nextBtn;
    private int orderNumber;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_active_order, container, false);

        order_list = root.findViewById(R.id.order_list);
        tableId = root.findViewById(R.id.table_id);
        nextBtn = root.findViewById(R.id.next_btn);
        prevBtn = root.findViewById(R.id.prev_btn);

        database = FirebaseDatabase.getInstance().getReference("Order");
        order_list.setLayoutManager(new LinearLayoutManager(getContext()));
        list_items = new ArrayList<>();
        orderIDs = new ArrayList<>();
        orderAdapter = new OrderAdapter(list_items);
        orderNumber = 0;

        tableId.setPaintFlags(tableId.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tableId.setText("Table: 3");
        order_list.setAdapter(orderAdapter);

        readData(new FirebaseCallback() {
            @Override
            public void onCallback(List<String> list) {
                System.out.println(list.toString());
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order_list.removeAllViews();
                list_items.clear();
                orderAdapter = new OrderAdapter(list_items);

                if(orderNumber > 0 )
                    orderNumber--;
                else
                    orderNumber = orderIDs.size() - 1;

                Query firebaseSearchQuery = database.child("-M7sKK7wobW-3QAIbUvj").child(orderIDs.get(orderNumber));
                firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot order_items: dataSnapshot.getChildren()){
                            //System.out.println(order_items.getKey());
                            Order_Item items = order_items.getValue(Order_Item.class);
                            if(items != null ){
                                list_items.add(items);
                            }

                        }
                        order_list.setAdapter(orderAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order_list.removeAllViews();
                list_items.clear();
                orderAdapter = new OrderAdapter(list_items);

                if(orderNumber < orderIDs.size() - 1 )
                    orderNumber++;
                else
                    orderNumber = 0;

                Query firebaseSearchQuery = database.child("-M7sKK7wobW-3QAIbUvj").child(orderIDs.get(orderNumber));
                firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot order_items: dataSnapshot.getChildren()){
                            //System.out.println(order_items.getKey());
                            Order_Item items = order_items.getValue(Order_Item.class);
                            if(items != null ){
                                list_items.add(items);
                            }

                        }
                        order_list.setAdapter(orderAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        return root;
    }

    private void readData(final FirebaseCallback firebaseCallback){
        final Query firebaseSearchQuery = database.child("-M7sKK7wobW-3QAIbUvj");
        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list_items.clear();
                //  System.out.println("TRIGGERED");
                //  orderAdapter.notifyDataSetChanged();
                for(DataSnapshot order: dataSnapshot.getChildren()){
                    //  if(order_list.getChildCount() == 0) {
                    //System.out.println(order.getKey());
                    orderIDs.add(order.getKey());

                    for(DataSnapshot order_items: order.getChildren()){
                        //System.out.println(order_items.getKey());
                        Order_Item items = order_items.getValue(Order_Item.class);
                        if(items != null ){
                            list_items.add(items);
                        }
                        break;
                    }

                    //   }
                }

                firebaseCallback.onCallback(orderIDs);

                for(Order_Item x: list_items){
                    System.out.println(x.getItem().getName());
                }
                Collections.sort(list_items, new QuantityComparator());
                order_list.setAdapter(orderAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void loadOrders(){


    }


    public static class QuantityComparator implements Comparator<Order_Item> {
        @Override
        public int compare(Order_Item o1, Order_Item o2) {
            return o2.getQuantity() - o1.getQuantity();
        }
    }

    private interface FirebaseCallback{

        void onCallback(List<String> list);
    }
}
