package com.softcod.pesquisacorona.ui.home;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.softcod.pesquisacorona.utils.RetrieveHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    String[] saida =new String[14];
    RetrieveHttp http;
    JSONObject json = null;
    int dia;
    int mes;
    int ano;
    public HomeViewModel() {
        mText = new MutableLiveData<>();

        Date data = new Date();
        Calendar cal = Calendar.getInstance();

        cal.setTime(data);

        dia = cal.get(Calendar.DAY_OF_MONTH);
        mes = cal.get(Calendar.MONTH);
        ano = cal.get(Calendar.YEAR);
        Log.d("HOJE\n\n\n\n\n", "DATA"+dia+"/"+mes+"/"+ano);

        mText.setValue(
                calculeMediaMovel(dia,mes,ano)
        );


    }


    public void modificar(long data){
        mText = new MutableLiveData<>();
        /*
        mText.setValue(
             //   calculeMediaMovel(dia,mes,ano)
        );*/
    }
    public void carregarBanco(){

    }

    public String calculeMediaMovel(int dia, int mes, int ano){


        Integer[] quant = new Integer[14];
        int i= 0;

        do{
            quant[i] = getDado(dia,mes,ano);

            saida[i] = "\nEm: " + dia+"/"+mes+"/"+ano +
                    " \nfoi contabilizados \n"+
                    quant[i]
                    +"\n casos da covide\n---------------\n";
            i++;

            if(dia>1) {dia -= 1;}
            else{mes -=1;dia = quantosDiasMes(mes, ano);}



        }while (i<14);
        int semana1 = (quant[0]+quant[1]+quant[2]
                +quant[3]+quant[4]+quant[5]
                +quant[6])/7;

        int semana2 = (quant[7]+quant[8]+quant[9]+quant[10]
                +quant[11]+quant[12]+quant[13])/7;
        int flutuacao =(semana1-semana2);

        return "Durante a 1ª semana:\n"+
                saida[0] +saida[1] +saida[2] +saida[3] +
                saida[4] + saida[5] +saida[6] +
                "\n\n\nMédia variavel da primeira semana:\n\n"+
                +semana1+
                "\n---------\nDurante a 2ª semana:\n"+
                saida[7] +saida[8] +saida[9] +saida[10] +
                saida[11] + saida[12] +saida[13] +
                "\n\n\nMédia variavel da segunda semana:\n\n"+
                semana2+
                "\n\n\n--------\n\nNotamos uma flutuação de:\n"
                +flutuacao;
    }

    public void setDados(){
        String zm="", zd = "";
        if(mes<10){ zm="0"; }
        if(dia<10){ zd="0"; }

        http = new RetrieveHttp();

        //a pessoa seleciona, envia para a api, e vou colocar outro borão
        //para ela poder excluir
        try {
            json = http.execute(
                    "http://softcod.com.br/api/"+
                            "filtros.php/?",
                    "POST",
                    "tabela=dados_gerais" +
                            "&coluna=data" +
                            "&pesquisa=" + ano +"-"+zm+mes+"-"+zd+dia
            ).get();

            int quant =json.getInt("q");

            Log.d("Quantidade", "Quantidade de casos:" + quant);

        } catch (NullPointerException | InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

    }

    public String dadosMesPassado(){

        int maxCasos=0, maxMortes = 0;
        Integer[] casos = new Integer[30];
        Integer[] mortes = new Integer[30];
        int n=0;
        do{
            casos[n] = getDado(dia, mes, ano, "casos");
            mortes[n] = getDado(dia, mes, ano, "mortes");
            n++;
        }while (n<30);

        for(int i = 1; i <casos.length; i++){
            if(casos[i]>maxCasos){
                maxCasos = casos[i];
            }
        }

        for(int i = 1; i <mortes.length; i++){
            if(mortes[i]>maxMortes){
                maxMortes = mortes[i];
            }
        }

        return "Casos: " + maxCasos + ", \nmortes: " + maxMortes +
                ", \ndados extraidos no dia: " + dia +"/"+mes +"/"+ano;

    }
    public int quantosDiasMes(int i, int ano){
        //levando que em conta que se trata de uma pesquisa
        //sobre a corona, foi feito uma formula simles do
        //ano bissexto

        Integer[] mes = new Integer[12];
        mes[0]=31; mes[1]=ano%400==0?29:28;mes[2]=31;
        mes[3]=30; mes[4]=31;mes[5]=30;
        mes[6]=31; mes[7]=31; mes[8]=30;
        mes[9]=31; mes[10]=30;mes[11]=31;
        return mes[i+1];
    }

    @SuppressLint("RestrictedApi")
    public int getDado(int dia, int mes, int ano){
        String zm="", zd = "";
        if(mes<10){ zm="0"; }
        if(dia<10){ zd="0"; }

        int resultado;
        http = new RetrieveHttp();

        //a pessoa seleciona, envia para a api, e vou colocar outro borão
        //para ela poder excluir
        try {
            json = http.execute(
                    "http://softcod.com.br/api/"+
                            "filtros.php/?",
                    "POST",
                    "tabela=dados_gerais" +
                            "&coluna=data" +
                            "&pesquisa=" + ano +"-"+zm+mes+"-"+zd+dia
            ).get();

            int quant =json.getInt("q");
            Log.d("Quantidade", "Quantidade de casos:" + quant);
            resultado = quant;
        } catch (NullPointerException | InterruptedException | ExecutionException | JSONException e) {
            return 0;
        }
        return resultado;

    }

    @SuppressLint("RestrictedApi")
    public int getDado(int dia, int mes, int ano, String coluna){
        String zm="", zd = "";
        if(mes<10){ zm="0"; }
        if(dia<10){ zd="0"; }

        int resultado;
        http = new RetrieveHttp();

        //a pessoa seleciona, envia para a api, e vou colocar outro borão
        //para ela poder excluir
        try {
            json = http.execute(
                    "http://softcod.com.br/api/"+
                            "filtros.php/?",
                    "POST",
                    "tabela=dados_gerais" +
                            "&coluna=" + coluna +
                            "&pesquisa=" + ano +"-"+zm+mes+"-"+zd+dia
            ).get();

            int quant =json.getInt("q");
            Log.d("Quantidade", "Quantidade de casos:" + quant);
            resultado = quant;
        } catch (NullPointerException | InterruptedException | ExecutionException | JSONException e) {
            return 0;
        }
        return resultado;

    }


    public LiveData<String> getText() {
        return mText;
    }
}