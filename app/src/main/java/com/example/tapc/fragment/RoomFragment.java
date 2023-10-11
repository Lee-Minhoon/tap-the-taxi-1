package com.example.tapc.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tapc.ChatActivity;
import com.example.tapc.R;
import com.example.tapc.model.RoomModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RoomFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fr_rvRoom);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        return view;
    }

    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<RoomModel> roomModels;
        public PeopleFragmentRecyclerViewAdapter() {
            roomModels = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("rooms").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    roomModels.clear();
                    for(DataSnapshot snapshot :dataSnapshot.getChildren()) {
                        roomModels.add(snapshot.getValue(RoomModel.class));
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            /*Glide.with
                    (holder.itemView.getContext())
                    .load(roomModels.get(position).userProfile)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)holder).imageView);*/
            ((CustomViewHolder)holder).textView.setText(roomModels.get(position).roomTitle);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ChatActivity.class);
                    intent.putExtra("roomKEY", roomModels.get(position).roomKEY);
                    intent.putExtra("roomTitle", roomModels.get(position).roomTitle);
                    //intent.putExtra("enterUID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return roomModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            /*public ImageView imageView;*/
            public TextView textView;
            public CustomViewHolder(View view) {
                super(view);
                /*imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);*/
                textView = (TextView) view.findViewById(R.id.ir_tvTitle);
            }
        }
    }
}
