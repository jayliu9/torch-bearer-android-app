package com.example.torchbearer.achievement.achieved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.torchbearer.R;
import com.example.torchbearer.achievement.Achievement;

import java.util.ArrayList;

public class AchievedFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Achievement> list;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achieved, container, false);
        recyclerView = view.findViewById(R.id.achieved_recyclerview);
        list = new ArrayList<>();

        //get list data from firebase

        AchievedAdapter adapter = new AchievedAdapter(getContext(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
