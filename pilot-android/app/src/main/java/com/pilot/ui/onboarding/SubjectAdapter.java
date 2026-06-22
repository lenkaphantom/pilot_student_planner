package com.pilot.ui.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pilot.R;
import com.pilot.data.model.SubjectsRequest;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    public interface OnRemoveListener {
        void onRemove(int position);
    }

    private final List<SubjectsRequest.SubjectItem> items;
    private final OnRemoveListener removeListener;

    public SubjectAdapter(List<SubjectsRequest.SubjectItem> items, OnRemoveListener listener) {
        this.items = items;
        this.removeListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubjectsRequest.SubjectItem item = items.get(position);
        holder.tvName.setText(item.name);
        holder.tvGoal.setText(item.goal != null ? item.goal : "—");
        holder.btnRemove.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID) removeListener.onRemove(pos);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvGoal;
        ImageButton btnRemove;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName    = itemView.findViewById(R.id.tvSubjectName);
            tvGoal    = itemView.findViewById(R.id.tvSubjectGoal);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}