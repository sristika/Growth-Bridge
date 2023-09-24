package com.example.pro_hive;

import android.net.Uri;
import android.widget.ProgressBar;

public interface OnFragmentInteractionListener {
    public void navigateToFragment(String fragmentName);

    public void navigateToFragmentPostData(String fragmentName, Post post);

    public void navigateToFragmentChatData(String fragmentName, String chatId);

    public void navigateToFragmentCreateChat(String fragmentName, String userId);

    void onTakePhoto(Uri imageUri);
    void onOpenGalleryPressed();

    void onRetakePressed();

    void onUploadButtonPressed(Uri imageUri, ProgressBar progressBar);
}
