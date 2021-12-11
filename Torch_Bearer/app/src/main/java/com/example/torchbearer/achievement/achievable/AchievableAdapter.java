package com.example.torchbearer.achievement.achievable;

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

public class AchievableAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Achievement> list;

    public AchievableAdapter() {
    }

    public AchievableAdapter(Context context, ArrayList<Achievement> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievable_item, parent, false);
        AchievableViewHolder achievableViewHolder = new AchievableViewHolder(view);
        return achievableViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AchievableViewHolder achievableViewHolder = (AchievableViewHolder) holder;
        achievableViewHolder.title_text.setText(list.get(position).getTitle());
        achievableViewHolder.description_text.setText(list.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class AchievableViewHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
        TextView title_text;
        TextView description_text;

        public AchievableViewHolder(@NonNull View itemView) {
            super(itemView);
            title_text = itemView.findViewById(R.id.achievable_title);
            description_text = itemView.findViewById(R.id.achievable_description);
        }
    }
}
