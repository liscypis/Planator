package com.wojteklisowski.planator.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wojteklisowski.planator.R;
import com.wojteklisowski.planator.entities.SavedRoad;

import java.util.ArrayList;
import java.util.List;

public class SavedRoadArrayAdapter extends ArrayAdapter {

    private static final String TAG = "SavedRoadArrayAdapter";
    private ArrayList<SavedRoad> mSavedRoad;

    public SavedRoadArrayAdapter(@NonNull Context context, int resource, List<SavedRoad> savedRoads) {
        super(context, resource);
        mSavedRoad = (ArrayList<SavedRoad>) savedRoads;
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
                //TODO LINENT DO MAPT Z ID i moze z dlugoscia wycieczki xD
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO uduwanie xDD
                for(SavedRoad sr: mSavedRoad){
                    if(sr.getId() == (int)viewHolder.delete.getTag()){
                        mSavedRoad.remove(sr);
                        break;
                    }
                }
                Log.d(TAG, "onClick: delete " + viewHolder.delete.getTag());
                notifyDataSetChanged();

            }
        });

        viewHolder.distance.setText("Długość" + savedRoad.getDistance());
        viewHolder.duration.setText("Czas" + savedRoad.getDuration());
        viewHolder.name.setText(savedRoad.getName());
        viewHolder.delete.setTag(savedRoad.getId());
        viewHolder.show.setTag(savedRoad.getId());

        return convertView;
    }

    private class ViewHolder {
       final TextView distance;
       final TextView duration;
       final TextView name;
       final Button delete;
       final Button show;
        ViewHolder(View v) {
            this.distance = (TextView) v.findViewById(R.id.tvSavedRoadDistance);
            this.duration = (TextView) v.findViewById(R.id.tvSavedRoadDuration);
            this.name = (TextView) v.findViewById(R.id.tvSavedRoadName);
            this.show = (Button)v.findViewById(R.id.bntShowSavedRoad);
            this.delete = (Button)v.findViewById(R.id.bntDeleteSavedRoad);
        }
    }
}
