package kr.co.tacademy.mongsil.mongsil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ccei on 2016-07-26.
 */
public class MainPostFragment extends Fragment {

    public MainPostFragment() { }
    public static MainPostFragment newInstance() {
        MainPostFragment f = new MainPostFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RecyclerView postRecyclerView =
                (RecyclerView) inflater.inflate(R.layout.fragment_post, container, false);
        PostRecyclerViewAdapter adapter = new PostRecyclerViewAdapter();
        postRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        MongSilApplication.getMongSilContext()));
        postRecyclerView.setAdapter(adapter);

        ////// sample code
        PostData data = new PostData();
        PostData data1 = new PostData();
        PostData data2 = new PostData();
        PostData data3 = new PostData();
        PostData data4 = new PostData();
        data.setTimeData(0, "Today");
        adapter.add(data);
        data1.setData(1, "10:25 AM", R.mipmap.ic_launcher, "스님",
                "날이 밝구나", 0, 0, 10);
        adapter.add(data1);
        data2.setData(1, "02:25 PM", R.mipmap.ic_launcher, "주지스님",
                "날씨가 덥구나", 0, R.drawable.test_back, 3);
        adapter.add(data2);
        data3.setTimeData(0, "2016.07.29");
        adapter.add(data3);
        data4.setData(1, "05:20 PM", R.mipmap.ic_launcher, "동자스님",
                "밖에 나가고 싶어요 빼앢", 0, 0, 1);
        adapter.add(data4);
        //////

        return postRecyclerView;
    }
}
