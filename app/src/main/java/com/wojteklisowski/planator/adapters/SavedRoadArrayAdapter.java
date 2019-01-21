package com.wojteklisowski.planator.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wojteklisowski.planator.R;
import com.wojteklisowski.planator.activities.MapsActivity;
import com.wojteklisowski.planator.database.AppDatabase;
import com.wojteklisowski.planator.entities.SavedRoad;
import com.wojteklisowski.planator.utils.ConvertTime;

import java.util.ArrayList;
import java.util.List;

public class SavedRoadArrayAdapter extends ArrayAdapter {

    private static final String TAG = "SavedRoadArrayAdapter";
    private ArrayList<SavedRoad> mSavedRoad;
    private AppDatabase database;
    private Context mContext;

    public SavedRoadArrayAdapter(@NonNull Context context, int resource, List<SavedRoad> savedRoads) {
        super(context, resource);
        mSavedRoad = (ArrayList<SavedRoad>) savedRoads;
        database = AppDatabase.getDatabase(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        return mSavedRoad.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            Log.d(TAG, "getView: called with null convertview");
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.saved_road_record, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final SavedRoad savedRoad = mSavedRoad.get(position);

        viewHolder.show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: tag" + viewHolder.show.getTag());
                Intent intent = new Intent(mContext, MapsActivity.class);
                intent.putExtra("SAVED_ROAD_ID", (int) viewHolder.show.getTag());
                intent.putExtra("SAVED_DURATION", savedRoad.getDuration());
                intent.putExtra("SAVED_DISTANCE", savedRoad.getDistance());
                intent.putExtra("SAVED_TRAVEL_MODE", savedRoad.getTravelMode());
                mContext.startActivity(intent);

            }

        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (SavedRoad sr : mSavedRoad) {
                    if (sr.getId() == (int) viewHolder.delete.getTag()) {
                        final int id = sr.getId();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                database.savedRoadDao().deleteSavedRoad(id);
                            }
                        }).start();
                        mSavedRoad.remove(sr);
                        break;
                    }
                }
                Log.d(TAG, "onClick: delete " + viewHolder.delete.getTag());
                notifyDataSetChanged();
            }
        });

        if (savedRoad.getTravelMode().equals("driving"))
            viewHolder.tMode.setImageResource(R.drawable.car_black_36dp);
        if (savedRoad.getTravelMode().equals("bicycling"))
            viewHolder.tMode.setImageResource(R.drawable.bike_black_36dp);
        if (savedRoad.getTravelMode().equals("walking"))
            viewHolder.tMode.setImageResource(R.drawable.walk_black_36dp);
        viewHolder.delete.setImageResource(R.drawable.delete);
        viewHolder.show.setImageResource(R.drawable.map_black_36dp);
        viewHolder.distance.setText("Długość " + savedRoad.getDistance() / 1000 + "km");
        viewHolder.duration.setText("Czas " + ConvertTime.convertTime(savedRoad.getDuration() / 60));
        viewHolder.name.setText(savedRoad.getName());
        viewHolder.delete.setTag(savedRoad.getId());
        viewHolder.show.setTag(savedRoad.getId());
        viewHolder.tMode.setTag(savedRoad.getId());


        return convertView;
    }

    private class ViewHolder {
        final TextView distance;
        final TextView duration;
        final TextView name;
        final ImageView delete;
        final ImageView show;
        final ImageView tMode;

        ViewHolder(View v) {
            this.distance = (TextView) v.findViewById(R.id.tvSavedRoadDistance);
            this.duration = (TextView) v.findViewById(R.id.tvSavedRoadDuration);
            this.name = (TextView) v.findViewById(R.id.tvSavedRoadName);
            this.show = (ImageView) v.findViewById(R.id.ivShowSavedRoad);
            this.delete = (ImageView) v.findViewById(R.id.ivDeleteSavedRoad);
            this.tMode = (ImageView) v.findViewById(R.id.ivSavedRoadTravelMode);
        }
    }
}
