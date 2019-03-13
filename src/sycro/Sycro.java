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
import javax.swing.JOptionPane;

/**
 * Classe reaponsável pelo timer e funções.
 *
 * @author Lucas Zingaro
 */
public class Sycro {

    protected File origem, destino;
    private Timer timer;
    private boolean fileOverwrite;
    private final int DELAY = 0;
    private int intervalo = 5000;
    private CopyArq cpa;
    
    /**
     * Guarda os Paths do último ciclo do Sycro.
     */
    public static File[] lastSycro = new File[0];
    /**
     * Contador de ciclos.
     */
    public static int contSycro = 0;

    /**
     * Limite do tamanho da pasta Origem.
     */
    public static double limiteDeTamanhoDaPasta = 0.1;
    
    /**
     * Objeto do Formulário para manipulação da barra de progresso
     */
    private FrmSycro frmSycro;
    
    public Sycro(FrmSycro frm) {
        frmSycro = frm;
    }

    /**
     * Define e inicia o timer responsavél pela sincronização das pastas
     *
     * @param listExt - Lista de extensões de arquivos ignorados
     * @param fileOverwrite - Confirmação de sobrescriçao de arquivos
     * @param intervalo -Intervalo de tempo do timer em milesegundos
     */
    public void setCopiaTimer(String[] listExt, boolean fileOverwrite, int intervalo) {
        this.fileOverwrite = fileOverwrite;
        this.intervalo = intervalo;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    sycroDeleted();
                    cpa = new CopyArq(listExt);
                    if (!origem.exists()) {
                        stop();
                        frmSycro.frmClear();
                        JOptionPane.showMessageDialog(null, "Origem não existe", "Alerta", JOptionPane.ERROR_MESSAGE);
                    }
                    cpa.copiarPasta(origem, destino, fileOverwrite);
                } catch (IOException | UnsupportedOperationException ex) {
                    Logger.getLogger(Sycro.class.getName()).log(Level.SEVERE, null, ex);
                }
                //System.out.println(".run()");
                Sycro.contSycro++;
            }
        }, DELAY, this.intervalo);
    }

    /**
     * Reinicia o timer já definido, adiantando sua execução
     */
    public void restart() {
        stop();
        setCopiaTimer(cpa.getListExt(), fileOverwrite, intervalo);

    }

    /**
     * Encerra o timer
     */
    public void stop() {
        timer.cancel();
        timer.purge();
        contSycro=0;
        frmSycro.stopProgressBar();
        //System.out.println("Fim");
    }

    /**
     * Verifica e sincroniza os arquivos deletados
     */
    public void sycroDeleted() {
        if (!destino.isDirectory()) {
            stop();
            throw new UnsupportedOperationException("Destino deve ser um diretório");
        }
        if (!destino.exists()) {
            destino.mkdir();
        }
        if (origem.exists()) {
            if (Sycro.contSycro > 0) {
                for (File lastFile : Sycro.lastSycro) {
                    if (!lastFile.exists()) {
                        for (File fileDestino : CopyArq.getPathsInDir(destino)) {
                            String sOrigem = lastFile.getPath().replace(origem.getPath(), "");
                            String sDestino = fileDestino.getPath().replace(destino.getPath(), "");
                            System.out.println(sOrigem + "==" + sDestino + "? R:" + sOrigem.compareTo(sDestino));
                            if (sOrigem.compareTo(sDestino) == 0) {
                                if (fileDestino.isDirectory()) {
                                    for(File f : fileDestino.listFiles()){
                                        f.delete();
                                    }
                                    fileDestino.delete();
                                    setLastSycro();
                                    sycroDeleted();
                                    return;
                                } else {
                                    fileDestino.delete();
                                }
                            }
                        }
                    }
                }
            }
            setLastSycro();

        } else {
            new File(destino + "\\" + origem.getName()).delete();
            stop();
        }

    }

    /**
     * Define e Redefine a última sincronização
     */
    public void setLastSycro() {
        if (!origem.isDirectory()) {
            //caso de arquivo único
            Sycro.lastSycro = new File[1];
            Sycro.lastSycro[0] = origem;
        } else {
            //caso de pastas
            Sycro.lastSycro = new File[CopyArq.contPaths(origem.getPath())];
            Sycro.lastSycro = CopyArq.getPathsInDir(origem);
        }
    }

}
