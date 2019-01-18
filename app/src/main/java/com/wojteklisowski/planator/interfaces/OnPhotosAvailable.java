package com.wojteklisowski.planator.interfaces;

import android.graphics.Bitmap;

public interface OnPhotosAvailable {
    void onPhotosAvailable(Bitmap photo, int index, int numberOfPhotos, String author);
}
