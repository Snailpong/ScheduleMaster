package com.snailpong.schedulemaster;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private ArrayList<AlarmClass> mData = null ;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        TextView content;
        TextView time;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            img = itemView.findViewById(R.id.alarm_img);
            title = itemView.findViewById(R.id.alarm_title) ;
            content = itemView.findViewById(R.id.alarm_content);
            time = itemView.findViewById(R.id.alarm_time);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    AlarmAdapter(ArrayList<AlarmClass> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_alarm, parent, false) ;
        AlarmAdapter.ViewHolder vh = new AlarmAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(AlarmAdapter.ViewHolder holder, int position) {
        AlarmClass al = mData.get(position);
        holder.title.setText(al.getTitle());
        holder.content.setText(al.getContent());
        switch(al.getCategory()) {
            case 1:
                holder.img.setImageResource(R.drawable.ic_check_green_24dp);
                break;
            case 2:
                holder.img.setImageResource(R.drawable.ic_clear_pink_24dp);
                break;
        }
        SimpleDateFormat dayTime = new SimpleDateFormat("yy-mm-dd hh:mm");
        holder.time.setText(dayTime.format(new Date(al.getTime())));
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }
}
