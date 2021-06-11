package com.softcod.pesquisacorona.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.softcod.pesquisacorona.databinding.FragmentGalleryBinding;

public class InformeFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final WebView web = binding.webDicas;
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setLoadsImagesAutomatically(true);
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setAllowContentAccess(true);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setBlockNetworkLoads(false);
        web.getSettings().getBlockNetworkLoads();
        web.getSettings().setDomStorageEnabled(true);

        web.setTouchscreenBlocksFocus(true);
        web.getTouchscreenBlocksFocus();

        web.setKeepScreenOn(true);



        galleryViewModel.getText().observe(
                getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                web.loadUrl(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}