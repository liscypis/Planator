package com.wojteklisowski.planator.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.wojteklisowski.planator.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlaceAutocompleteArrayAdapter extends ArrayAdapter implements Filterable {

    private static final String TAG = "PlaceAutocompleteArrayAdapter";
    private static final CharacterStyle STYLE = new StyleSpan(Typeface.BOLD_ITALIC);

    private ArrayList<AutocompletePrediction> mResultList;
    private GeoDataClient mGeoDataClient;
    private LatLngBounds mBounds;
    private AutocompleteFilter mPlaceFilter;

    /**
     * konstruktor
     */
    public PlaceAutocompleteArrayAdapter(Context context, GeoDataClient geoDataClient,
                                         LatLngBounds bounds, AutocompleteFilter filter) {
        super(context, R.layout.item_autocomplete);
        mGeoDataClient = geoDataClient;
        mBounds = bounds;
        mPlaceFilter = filter;
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public AutocompletePrediction getItem(int position) {
        return mResultList.get(position);
    }


    // https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AutocompletePrediction item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_autocomplete, parent, false);
        }

        TextView tvPrimary = (TextView) convertView.findViewById(R.id.tvPrimaryText);
        TextView tvSecondary = (TextView) convertView.findViewById(R.id.tvSecondaryText);
        tvPrimary.setText(item.getPrimaryText(STYLE));
        tvSecondary.setText(item.getSecondaryText(STYLE));
        tvPrimary.setTextColor(Color.BLACK);

        return convertView;

    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<AutocompletePrediction> filterData = new ArrayList<>();

                if (constraint != null) {
                    filterData = downloadAutocompletePlaces(constraint);
                }
                results.values = filterData;
                if (filterData != null) {
                    results.count = filterData.size();
                } else {
                    results.count = 0;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null && results.count > 0) {
                    mResultList = (ArrayList<AutocompletePrediction>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue instanceof AutocompletePrediction) {
                    return ((AutocompletePrediction) resultValue).getFullText(null);
                } else {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }

    /**
     * pobiera dane
     */
    private ArrayList<AutocompletePrediction> downloadAutocompletePlaces(CharSequence constraint) {
        Log.d(TAG, "autocomplete query for: " + constraint);

        Task<AutocompletePredictionBufferResponse> results =
                mGeoDataClient.getAutocompletePredictions(constraint.toString(), mBounds,
                        mPlaceFilter);
        // czekaj na odpowiedz
        try {
            Tasks.await(results, 30, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        try {
            AutocompletePredictionBufferResponse autocompletePredictions = results.getResult();

            Log.d(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                    + " addresses.");

            // Freeze the results immutable representation that can be stored safely.
            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        } catch (RuntimeExecutionException e) {
            // If the query did not complete successfully return null
            Toast.makeText(getContext(), "API error " + e.toString(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error getting autocomplete prediction API call", e);
            return null;
        }
    }

    private static class ViewHolder {
        TextView tvPrimary;
        TextView tvSecondary;
    }
}
