package com.softcod.pesquisacorona.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by Desenvweb on 14/11/2016.
 * <h1>Mensagens</h1>
 * Fun&ccedil;&atilde;o para a gera&ccedil;&atilde;o de mensagens e alertas no aplicativo
 *
 * @author Pablo Diehl
 * @version 2016.11.14
 * @since 11-14-2016
 */

public class Mensagens {

    /**
     * <h1>okDialog</h1>
     * Exibe uma popup de alerta na tela, apenas para notificar o usu&aacute;rio sobre algum evento
     *
     * @author Pablo Diehl
     * @version 2016.11.14
     * @since 11-14-2016
     *
     */
    public static void okDialog (Context context, String titulo,
                                 String mensagem, String botao){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensagem);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, botao,
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }


}
