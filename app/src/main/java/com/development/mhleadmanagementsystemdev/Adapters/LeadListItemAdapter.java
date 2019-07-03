package com.development.mhleadmanagementsystemdev.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.R;

import java.util.List;

public class LeadListItemAdapter extends RecyclerView.Adapter<LeadListItemAdapter.LeadListViewHolder> {
    private List<LeadDetails> chapter;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView identification, name;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.chapter_name);
            identification = view.findViewById(R.id.identification);
        }
    }

    public LeadListItemAdapter(List<ChapterListItem> chapter, Context context) {
        this.chapter = chapter;
        this.context = context;
    }

    @NonNull
    @Override
    public LeadListItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        LeadDetails leadDetails = chapter.get(position);
        holder.name.setText(leadDetails.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(context, ChapterDetailActivity.class);
                //intent.putExtra("position", chapter.get(position).getName());
                //context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapter.size();
    }
}
