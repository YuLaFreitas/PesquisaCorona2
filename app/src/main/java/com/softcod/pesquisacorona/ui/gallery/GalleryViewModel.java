package com.softcod.pesquisacorona.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(
                "http://portal.saude.pe.gov.br"
        );
        //  Resources.getSystem().getResourceTypeName(R.string.urlWebExterno)

    }

    public LiveData<String> getText() {
        return mText;
    }
}