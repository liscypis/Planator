package com.wojteklisowski.planator.asynctasks;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wojteklisowski.planator.interfaces.OnPhotosAvailable;

import java.util.ArrayList;
import java.util.List;

public class GetPhotos {
    private static final String TAG = "GetPhotos";
    private OnPhotosAvailable listener;
    private GeoDataClient mGeoDataClient;
    private List<PlacePhotoMetadata> photoMetadataList;
    private int mCurrentIndex;
    private int mNumberOfPhotos;
    private Bitmap mBitmapPhoto;
    private String mAuthor;
    private String mPlaceId;

    public GetPhotos(GeoDataClient geoDataClient, String placeId, OnPhotosAvailable listener) {
        this.mPlaceId = placeId;
        this.mGeoDataClient = geoDataClient;
        this.listener = listener;
        getPhotosMetadata();
    }

    private void getPhotosMetadata() {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(mPlaceId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                mCurrentIndex = 0;
                photoMetadataList = new ArrayList<>();
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                for (int i = 0; i < photoMetadataBuffer.getCount() - 1; i++) {
                    photoMetadataList.add(photoMetadataBuffer.get(i).freeze());
                }
                photoMetadataBuffer.release();
                mNumberOfPhotos = photoMetadataList.size();
                Log.d(TAG, "onComplete: index " + mCurrentIndex + " numberOfPhotos " + mNumberOfPhotos + " place id " + mPlaceId);
                getPhoto();

            }
        });
    }
    private void downloadPhoto(PlacePhotoMetadata photoMetadata) {
        if(mNumberOfPhotos == 0){
            listener.onPhotosAvailable(null, mCurrentIndex, mNumberOfPhotos, mAuthor);
        } else {
            CharSequence attribution = photoMetadata.getAttributions();
            mAuthor = attribution.toString();
            Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
            photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                    PlacePhotoResponse photo = task.getResult();
                    mBitmapPhoto = photo.getBitmap();
                    listener.onPhotosAvailable(mBitmapPhoto, mCurrentIndex, mNumberOfPhotos, mAuthor);
                }
            });
        }

    }

    private void getPhoto() {
        if(photoMetadataList.isEmpty()){
            downloadPhoto(null);
        }else {
            downloadPhoto(photoMetadataList.get(mCurrentIndex));
        }
    }

    public void nextPhoto() {
        mCurrentIndex++;
        getPhoto();
    }

    public void previousPhoto() {
        mCurrentIndex--;
        getPhoto();
    }

}
