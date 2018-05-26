package com.midas.mobile3.midas_mobile.MasterFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.midas.mobile3.midas_mobile.MasterActivity;
import com.midas.mobile3.midas_mobile.Model.Drink;
import com.midas.mobile3.midas_mobile.R;

import java.util.ArrayList;
import java.util.List;


public class MasterMenuFragment extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private Button button;
    private FirebaseStorage storage;
    private DatabaseReference mDatabase;
    private GridLayoutManager mLayoutManager;

    private List<Drink> Drinks = new ArrayList<>();
    private List<String> uidLists = new ArrayList<>();
    MenuRecyclerViewAdapter menuRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_master_menu,container,false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        menuRecyclerViewAdapter = new MenuRecyclerViewAdapter();
        button = view.findViewById(R.id.button);
        recyclerView = view.findViewById(R.id.recyclerView);
        button.setOnClickListener(addmenu());
        mLayoutManager = new GridLayoutManager(view.getContext(),2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(menuRecyclerViewAdapter);

        mDatabase.child("Menu").addValueEventListener(new ValueEventListener() {//옵저버 패턴
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Drinks.clear();
                uidLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Drink drink = snapshot.getValue(Drink.class);
                    String uidKey = snapshot.getKey();
                    System.out.println(drink.kcal+"     "+drink.cost+drink.ImageUrl+drink.name);
                    Drinks.add(drink);
                    uidLists.add(uidKey);
                }
                menuRecyclerViewAdapter.notifyDataSetChanged(); // 새로고침
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }
    public View.OnClickListener addmenu(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("Status","insert");
                MenuInsertDiaglog dialog = new MenuInsertDiaglog();
                dialog.setArguments(args);
                dialog.show(getActivity().getFragmentManager(),"few");
            }
        };
    }




    public class MenuRecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((CustomViewHolder) holder).name.setText(Drinks.get(position).name);
            ((CustomViewHolder) holder).cost.setText(Drinks.get(position).cost+"원");
            ((CustomViewHolder) holder).kcal.setText(Drinks.get(position).kcal+"Kcal");

            Glide.with(((CustomViewHolder) holder).itemView.getContext())
                    .load(Drinks.get(position).ImageUrl)
                    .into(((CustomViewHolder) holder).imageView);

            ((CustomViewHolder) holder).motifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();
                    args.putString("Status","motify");
                    args.putString("Imageurl", Drinks.get(position).ImageUrl);
                    args.putString("name",Drinks.get(position).name);
                    args.putString("cost",Drinks.get(position).cost);
                    args.putString("kcal",Drinks.get(position).kcal);
                    args.putString("key", uidLists.get(position));



                    MenuInsertDiaglog dialog = new MenuInsertDiaglog();
                    dialog.setArguments(args);
                    dialog.show(getActivity().getFragmentManager(),"few");

                    // 수정버튼
                }
            });

            ((CustomViewHolder) holder).deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete_content(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return Drinks.size();
        }


        private void delete_content(final int position) {

                    mDatabase.child("Menu").child(uidLists.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView name;
            TextView cost;
            TextView kcal;
            ImageView motifyButton;
            ImageView deleteButton;

            public CustomViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.imageView);
                name = view.findViewById(R.id.item_name);
                cost = view.findViewById(R.id.item_cost);
                kcal = view.findViewById(R.id.item_kcal);
                motifyButton = view.findViewById(R.id.item_modify);
                deleteButton = view.findViewById(R.id.item_delete);
            }
        }
    }
}
