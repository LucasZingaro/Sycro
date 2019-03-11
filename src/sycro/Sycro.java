/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sycro;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe reaponsável pelo timer e funções.
 * @author Lucas Zingaro
 */
public class Sycro {

    protected File origem, destino;
    private Timer timer;
    private boolean fileOverwrite;
    private final int delay=0;
    private int intervalo=5000;
    private CopyArq cpa;
    public Sycro() {
        
    }
    
    /**
     * Define e inicia o timer responsavél pela sincronização das pastas
     * @param listExt - Lista de extensões de arquivos ignorados
     * @param fileOverwrite - Confirmação de sobrescriçao de arquivos
     * @param intervalo -Intervalo de tempo do timer em milesegundos
     */
    public void setCopiaTimer(String[] listExt,boolean fileOverwrite, int intervalo) {
        this.fileOverwrite=fileOverwrite;
        this.intervalo=intervalo;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    cpa = new CopyArq(listExt);
                    cpa.copiarPasta(origem, destino, fileOverwrite);
                } catch (IOException | UnsupportedOperationException ex) {
                    Logger.getLogger(Sycro.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(".run()");
            }
        }, delay, this.intervalo);
    } 
    
    /**
    * Reinicia o timer já definido, adiantando sua execução 
    */
    public void restart(){
        stop();
        setCopiaTimer(cpa.getListExt(), fileOverwrite, intervalo);
        
    }
    /**
     * Encerra o timer
     */
    public void stop(){
        timer.cancel();
        timer.purge();
        System.out.println("Fim");
    }
}
