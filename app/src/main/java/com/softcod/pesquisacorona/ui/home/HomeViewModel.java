package com.softcod.pesquisacorona.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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
    Intent intent;

    private MutableLiveData<String> mText;
    String[] saida =new String[14];
    RetrieveHttp http;
    JSONObject json = null;

    int mortes;  int casos;
    double latitude = 0.0, longitude = 0.0;
    int dia; int mes;  int ano;

    public int getMortes() {
        return mortes;
    }

    public void setMortes(int mortes) {
        this.mortes = mortes;
    }

    public int getCasos() {
        return casos;
    }

    public void setCasos(int casos) {
        this.casos = casos;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public HomeViewModel() {
        intent = new Intent();
        mText = new MutableLiveData<>();

        Date data = new Date();
        Calendar cal = Calendar.getInstance();

        cal.setTime(data);

        setDia(cal.get(Calendar.DAY_OF_MONTH));
        setMes(cal.get(Calendar.MONTH));
        setAno(cal.get(Calendar.YEAR));
        Log.d("HOJE\n\n\n\n\n", "DATA"+getDia()+"/"+getMes()+"/"+getAno());


        mText.setValue(
                calculeMediaMovel(dia,mes,ano)
        );
    }

    public void alerta(Context contexto){
        setLongitude(intent.getDoubleExtra("longitude", 0.0));
        setLatitude(intent.getDoubleExtra("latitude", 0.0));

        String dados = "No mês passado tivemos:\n"+ dadosMesPassado();
        dados += ".\n Essa pesquisa foi feita na";
        dados += "\n latitude:" + getLongitude() + "\ne na longitude" + getLatitude();
        dados += ".\n Hoje é: " + getDia() +"/"+ getMes() +"/"+ getAno();

        AlertDialog.Builder alerta =new AlertDialog.Builder(contexto);
        TextView textView = new TextView(contexto);
        textView.setText(dados);
        textView.setTextColor(Color.RED);
        alerta.setView(textView);
        alerta.setMessage("Podemos armazenar o histórico de pesquisa?\n");

        alerta.setPositiveButton("SIM",
                new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registrarConsulta();
            }
        });
        alerta.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        alerta.show();
            }

    public void registrarConsulta(){
        RetrieveHttp http = new RetrieveHttp();
        JSONObject json = null;
        try {
            json = http.execute(
                    "http://softcod.com.br/api/teste/modificar.php?",
                    "POST", "do=finalizarPesquisa"
                            +"&data=" + getDia() +"/" + getMes()+"/" + getAno()
                            +"&casos=" + getCasos()
                            +"&mortes=" + getMortes()
                            +"&long=" + getLongitude()
                            +"&lat=" +getLatitude()
            ).get();

            Log.d("JSON____", String.valueOf(json));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            Log.d("JSON____", String.valueOf(json));
        }
    }
    public void carregarBanco() {
        RetrieveHttp http = new RetrieveHttp();
        JSONObject json = null;
        try {
            json = http.execute(
                    "http://softcod.com.br/api/teste/modificar.php/?",
                    "POST", "do=criar"
            ).get();

            Log.d("JSON____", String.valueOf(json));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            Log.d("JSON____", String.valueOf(json));
        }
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

    public String dadosMesPassado(){

        int maxCasos=0, maxMortes = 0;
        Integer[] casos = new Integer[30];
        Integer[] mortes = new Integer[30];
        int n=0;
        do{
            casos[n] = getDado(getDia(), getMes(), getAno(), "casos");
            mortes[n] = getDado(getDia(), getMes(), getAno(), "mortes");
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
        this.mortes = maxMortes;
        this.casos = maxCasos;
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
                    "http://softcod.com.br/api/teste/filtrar.php/?",
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
                    "http://softcod.com.br/api/teste/filtrar.php/?",
                    "POST",
                    "tabela=dados_gerais" +
                            "&coluna=" + coluna +
                            "&pesquisa=" + ano +"-"+zm+mes+"-"+zd+dia
            ).get();

            int quant =json.getInt("q");
            Log.d("Quantidade", "\nQuantidade de casos: " + quant);
            resultado = quant;
        } catch (NullPointerException | InterruptedException | ExecutionException | JSONException e) {
            return 0;
        }
        return resultado;

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
                    "http://softcod.com.br/api/teste/filtrar.php/?",
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

    public LiveData<String> getText() {
        return mText;
    }
}