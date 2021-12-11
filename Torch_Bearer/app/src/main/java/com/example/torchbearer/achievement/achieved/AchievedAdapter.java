package com.example.torchbearer.achievement.achieved;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.torchbearer.R;
import com.example.torchbearer.achievement.Achievement;

import java.util.ArrayList;

public class AchievedAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Achievement> list;

    public AchievedAdapter() {
    }

    public AchievedAdapter(Context context, ArrayList<Achievement> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achieved_item, parent, false);
        AchievedViewHolder achievedViewHolder = new AchievedViewHolder(view);
        return achievedViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AchievedViewHolder achievedViewHolder = (AchievedViewHolder) holder;
        achievedViewHolder.title_text.setText(list.get(position).getTitle());
        achievedViewHolder.description_text.setText(list.get(position).getDescription());
        achievedViewHolder.date_text.setText(list.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class AchievedViewHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
        TextView title_text;
        TextView description_text;
        TextView date_text;
        public AchievedViewHolder(@NonNull View itemView) {
            super(itemView);
            title_text = itemView.findViewById(R.id.achieved_title);
            description_text = itemView.findViewById(R.id.achieved_description);
            date_text = itemView.findViewById(R.id.achieved_date);
        }
    }
}
