package com.sns.docsapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sns.docsapp.Model.MessageModel;
import com.sns.docsapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatAdapter extends Adapter<ChatAdapter.ChatViewHolder> {

    private ArrayList<MessageModel> mList;

    private Context mContext;

    public ChatAdapter(Context context) {
        this.mContext = context;
    }

    public void updateDataSet(ArrayList<MessageModel> list) {
        Log.e("sns", "updateDataSet: " + list.size());
        this.mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int id = 0;
        ChatViewHolder chatViewHolder = new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container, viewGroup, false));
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {
        Log.e("sns", "onBindViewHolder: " + i);
        MessageModel model = mList.get(i);
        Log.e("sns", "model: " + model.isDate() + " : " + model.isSelf());

        int id = 0;

        if (mList.get(i).isDate()) {
            chatViewHolder.mSelfView.setVisibility(View.GONE);
            chatViewHolder.mBotView.setVisibility(View.GONE);
            chatViewHolder.mDateView.setVisibility(View.VISIBLE);
            setData(chatViewHolder.mDateView, model);
        } else if (mList.get(i).isSelf()) {
            chatViewHolder.mSelfView.setVisibility(View.VISIBLE);
            chatViewHolder.mBotView.setVisibility(View.GONE);
            chatViewHolder.mDateView.setVisibility(View.GONE);
            setData(chatViewHolder.mSelfView, model);
        } else {
            chatViewHolder.mSelfView.setVisibility(View.GONE);
            chatViewHolder.mBotView.setVisibility(View.VISIBLE);
            chatViewHolder.mDateView.setVisibility(View.GONE);
            setData(chatViewHolder.mBotView, model);
        }
    }

    private void setData(RelativeLayout viewHolder, MessageModel model) {
        TextView message = viewHolder.findViewById(R.id.message);
        TextView time = viewHolder.findViewById(R.id.time);

        if (model.isDate()) {
            message.setText(getDate(model.getTimeUTC(), "d MMM yyyy"));
        } else {
            message.setText(model.getMessage());
        }


        String timeFormat = getDate(model.getTimeUTC(), "hh:mm a");
        String[] data = timeFormat.split(" ");
        String valueTime[] = data[0].split(":");
        time.setText(valueTime[0] + "." + valueTime[1] + " " + data[1]);
    }

    @Override
    public int getItemCount() {
        int size = mList == null ? 0 : mList.size();
        Log.e("sns", "getItemCount: " + size);
        return mList == null ? 0 : mList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mBotView;
        public RelativeLayout mDateView;
        public RelativeLayout mSelfView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mBotView = itemView.findViewById(R.id.bot);
            mSelfView = itemView.findViewById(R.id.self);
            mDateView = itemView.findViewById(R.id.date);
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
