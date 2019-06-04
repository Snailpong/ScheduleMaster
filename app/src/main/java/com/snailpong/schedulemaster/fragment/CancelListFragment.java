package com.snailpong.schedulemaster.fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.snailpong.schedulemaster.AddDeadlineActivity;
import com.snailpong.schedulemaster.DBHelper;
import com.snailpong.schedulemaster.R;
import com.snailpong.schedulemaster.TabbedActivity;

public class CancelListFragment extends Fragment {

    private ListView list;
    private DBHelper helper;
    private SQLiteDatabase db;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cancel, container, false);
        list = view.findViewById(R.id.cancelfragment_list);
        helper = new DBHelper(getActivity(), "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        Cursor c = db.query("noclass", null, null, null, null, null, null, null);

        //SimpleCursorAdapter adapter  = new SimpleCursorAdapter(getActivity(),
                //R.layout.item_cancel, c,
                //new String[]{"whatid", "day"},
               // new int[] {R.id.});
//        SimpleCursorAdapter adapter  = new SimpleCursorAdapter(getActivity(),
//                R.layout.item_cancel, c,
//                new String[]{"whatid", "day"},
//                new int[] {R.id.});


        return view;
    }

}
