package com.softcod.pesquisacorona.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("http://www.saude.ba.gov.br/temasdesaude/coronavirus/dicas-de-protecao-covid-19/");
    }

    public LiveData<String> getText() {
        return mText;
    }
}