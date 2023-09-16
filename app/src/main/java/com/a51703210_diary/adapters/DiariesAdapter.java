package com.a51703210_diary.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.a51703210_diary.R;
import com.a51703210_diary.activities.ViewDiaryActivity;
import com.a51703210_diary.models.Diary;

import java.util.List;

public class DiariesAdapter extends RecyclerView.Adapter<DiariesAdapter.ViewHolder> {
    private List<Diary> listDiary;
    private Context mContext;
    public DiariesAdapter(Context context, List<Diary> listDiary) {
        this.mContext = context;
        this.listDiary = listDiary;
    }
    @NonNull
    @Override
    public DiariesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_row, parent,false);
        DiariesAdapter.ViewHolder holder = new DiariesAdapter.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiariesAdapter.ViewHolder holder, int position) {
        Diary diary = listDiary.get(position);
        if(diary==null)
            return;

        holder.tvTitle.setText(diary.getTitle());
        holder.tvDate.setText(diary.getDate());
        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Size", diary.getTitle());
                onClickDetail(diary);
            }
        });
    }

    private void onClickDetail(Diary diary) {
        Intent i =new Intent(mContext, ViewDiaryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("diary_mode","viewDetail");
        bundle.putString("diary_id",diary.getId());
        bundle.putString("diary_title", diary.getTitle());
        bundle.putString("diary_date", diary.getDate());
        bundle.putString("diary_content", diary.getContent());

        i.putExtras(bundle);
        mContext.startActivity(i);
    }

    @Override
    public int getItemCount() {
        if(listDiary!=null)
            return listDiary.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout_item;
        TextView tvTitle, tvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle =  itemView.findViewById(R.id.tvTitle);
            tvDate =  itemView.findViewById(R.id.tvDate);
            layout_item = itemView.findViewById(R.id.layout_item);
        }
    }
    public void release(){
        mContext=null;
    }
}
