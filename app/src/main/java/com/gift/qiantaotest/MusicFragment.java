package com.gift.qiantaotest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MusicFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    int[] id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_music, container, false);
        id = new int[80];
        for (int i = 0, size = id.length; i < size; i++) {
            id[i] = i + 1;
        }
        RecyclerView rvContainer = root.findViewById(R.id.rv_container);
        rvContainer.setAdapter(new MusicAdapter(id));
        rvContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    private static class MusicAdapter extends RecyclerView.Adapter {
        int[] id;

        public MusicAdapter(int[] id) {
            this.id = id;
        }

        private static class MusicViewHolder extends RecyclerView.ViewHolder {
            TextView tv;

            public MusicViewHolder(@NonNull View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv_id);
            }

            public void dataBindView(int id) {
                tv.setText(id+"");
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MusicViewHolder musicViewHolder = (MusicViewHolder) holder;
            musicViewHolder.dataBindView(id[position]);
        }

        @Override
        public int getItemCount() {
            return id.length;
        }
    }
}