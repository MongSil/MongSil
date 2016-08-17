package kr.co.tacademy.mongsil.mongsil;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-16.
 */
// 주소 검색 리사이클러
public class SearchPOIRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int LAYOUT_TYPE_HEADER = 100;
    private static final int LAYOUT_TYPE_ITEM = 111;

    private AdapterCallback callback;

    ArrayList<POIData> items;
    SearchPOIRecyclerViewAdapter(AdapterCallback callback) {
            this.callback = callback;
        items = new ArrayList<POIData>();
    }

    public void add(ArrayList<POIData> datas) {
        this.items = datas;
        this.notifyDataSetChanged();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final TextView headTitle;

        public HeaderViewHolder(View view) {
            super(view);
            this.view = view;
            headTitle =
                    (TextView) view.findViewById(R.id.text_search_location_header);
        }

        private void setData(int select) {
            if (select == 0) {
                headTitle.setText(R.string.search_location_mark_header);
            } else if (select == 2) {
                headTitle.setText(R.string.search_location_content_header);
            }
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final ImageView imgStar;
        final TextView locationItem;

        public ItemViewHolder(View view) {
            super(view);
            this.view = view;
            imgStar = (ImageView) view.findViewById(R.id.img_star);
            locationItem =
                    (TextView) view.findViewById(R.id.text_location_item);
        }

        private void setData(final POIData poiData, final int position) {
            if (poiData.typeCode == 1) {
                imgStar.setImageResource(R.drawable.yellow_star);
                imgStar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imgStar.setImageResource(R.drawable.gray_star);
                        callback.onMarkCallback(false, poiData, position);
                    }
                });
                locationItem.setText(poiData.name);
            } else if (poiData.typeCode == 3) {
                imgStar.setImageResource(R.drawable.gray_star);
                locationItem.setText(poiData.name);
                imgStar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        items.get(position);
                        imgStar.setImageResource(R.drawable.yellow_star);
                        callback.onMarkCallback(true, poiData, position);
                    }
                });
            }
            locationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onSelectCallback(poiData);
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case LAYOUT_TYPE_HEADER :
                view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.layout_search_location_header, parent, false);
                return new HeaderViewHolder(view);
            case LAYOUT_TYPE_ITEM :
                view =  LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.layout_search_location_item, parent, false);
                return new ItemViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case LAYOUT_TYPE_HEADER:
                ((HeaderViewHolder) holder).setData(items.get(position).typeCode);
                break;
            case LAYOUT_TYPE_ITEM:
                ((ItemViewHolder) holder).setData(items.get(position), position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = items.get(position).typeCode;
        switch (viewType) {
            case POIData.TYPE_MARK_HEADER :
                return LAYOUT_TYPE_HEADER;
            case POIData.TYPE_CONTENT_HEADER:
                return LAYOUT_TYPE_HEADER;
            case POIData.TYPE_MARK_ITEM :
                return LAYOUT_TYPE_ITEM;
            case POIData.TYPE_CONTENT_ITEM:
                return LAYOUT_TYPE_ITEM;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}