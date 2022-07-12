package com.example.searchapp;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.sax.SAXResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RealtimeListAdapter extends BaseAdapter {

    Context context;
    List<String> data;
    LayoutInflater inflater;

    public RealtimeListAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.listview_item, null);
        TextView rank = (TextView) view.findViewById(R.id.rank);
        TextView tv = (TextView) view.findViewById(R.id.tv);
        rank.setText(i+1+"");
        tv.setText(data.get(i));
        return view;
    }
}