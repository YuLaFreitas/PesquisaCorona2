package com.softcod.pesquisacorona.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;


public class TelaApresentacao extends Activity {
    /*RelativeLayout telaIncial;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apresntacao);
        ImageView peca_u = pecas(R.drawable.ic_eng_um, R.color.colorPrimary);
        ImageView peca_d = pecas(R.drawable.ic_eng_dois, R.color.colorAccent);
        ImageView peca_t = pecas(R.drawable.ic_eng_tres, R.color.colorDivider);
        ImageView peca_q = pecas(R.drawable.ic_eng_quatro, R.color.colorSecondaryText);
        telaIncial = findViewById(R.id.telaApresnta);
        int y1 = 600;
        int x1 = 650;
        int y2 = 400;
        int x2 = 450;

        telaIncial.animate().withStartAction(start(peca_t, y1, x1, 2000)).start();
        telaIncial.animate().withStartAction(start(peca_d, y2, x1, 2000)).start();
        telaIncial.animate().withStartAction(start(peca_u, y2, x2, 2000)).start();
        telaIncial.animate().withStartAction(start(peca_q, y1, x2, 2000)).start();
        // telaIncial.addView(peca_u);
         // y1       y2
        //x1 x1y1   x1y2
        //x2 x2y1   x2y2

    }

    public Runnable start(View view, int y, int x, int tempo){
        return () -> {
            view.animate()
                    .translationX(x)
                    .translationY(y)
                    .setDuration(tempo)
                    .withEndAction(end());
            telaIncial.addView(view);
        };
    }
    public Runnable end(){
        Intent intent = new Intent(this, MainActivity.class);
        return new Runnable() {
            @Override
            public void run() {
                startActivity(intent);

            }
        };
    }

    private ImageView pecas(int res, int cor){
        ImageView iv = new ImageView(this);
        iv.setColorFilter(getColor(cor));
        iv.setImageResource(res);
        return iv;
    }
*/
}
