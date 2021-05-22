package com.softcod.pesquisacorona.ui.slideshow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.softcod.pesquisacorona.MainActivity;
import com.softcod.pesquisacorona.R;
import com.softcod.pesquisacorona.databinding.FragmentSlideshowBinding;

import java.io.File;
import java.io.IOException;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private FragmentSlideshowBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

       final WebView web = binding.web;
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
         /*
        webView.getKeepScreenOn();

        webView.getSettings().setDefaultFontSize(40);
        webView.setContextClickable(true);
        webView.getSettings().setSupportZoom(true);
        webView.showContextMenu();

        webView.getSettings().getTextZoom();
        webView.getSettings().setTextZoom(webView.getSettings().getTextZoom());
        webView.getSettings().setBuiltInZoomControls(true);
         */

        try {
            web.saveWebArchive(String.valueOf(File.createTempFile("perguntas", ".html")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        slideshowViewModel.getText().observe(getViewLifecycleOwner(),
                new Observer<String>() {
            @Override
            public void onChanged(String s) {
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