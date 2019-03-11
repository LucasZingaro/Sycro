/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sycro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import javax.swing.JOptionPane;

/**
 * Classe responsável cópia dos arquivos
 * @author Lucas Zingaro
 * @author baseado em diego_qmota Access in 08-03-2019
 * (https://www.guj.com.br/t/swing-copiar-pasta-de-um-local-e-salvar-em-outra/132165/6)
 */
public class CopyArq {

    public CopyArq() {
    }

    /**
     * Define a lista de extensões ignoradas
     *
     * @param listExt - Lista de extensões ignoradas
     */
    public CopyArq(String[] listExt) {
        this.listExt = listExt.clone();
    }

    /**
     * Lista de extensões ignoradas.
     */
    private String listExt[];

    /**
     * Pegar lista de extensões ignoradas.
     *
     * @return Array com lista de extenções ignoradas
     */
    public String[] getListExt() {
        return listExt.clone();
    }

    /**
     * Atribuir lista de extensões ignoradas.
     *
     * @param listExt - Array de extensões ignoradas
     */
    public void setListExt(String[] listExt) {
        this.listExt = listExt.clone();
    }

    /**
     * Faz a cópia de um único arquivo de uma origem para um destino. (Desde que
     * não tenha uma extensão da lista de extensões ignoradas)
     *
     * @param fileIn - Arquivo de origem
     * @param fileOut - Arquivo de destino
     * @param fileOverwrite - Confirmação para sobrescrever os arquivos
     * @throws java.io.IOException - Erro de Entrada e Saida
     */
    public void copiarArquivo(File fileIn, File fileOut, boolean fileOverwrite) throws IOException {
        if (fileOut.exists() && !fileOverwrite) {
            return;
        }
        FileInputStream source = new FileInputStream(fileIn);
        FileOutputStream destination = new FileOutputStream(fileOut);
        FileChannel sourceFileChannel = source.getChannel();
        FileChannel destinationFileChannel = destination.getChannel();
        try {
            long size = sourceFileChannel.size();
            if (listExt.length >= 1) {
                for (String ext : listExt) {
                    if (!fileIn.getName().endsWith(ext)) {
                        sourceFileChannel.transferTo(0, size, destinationFileChannel);
                    }
                }
            } else {
                sourceFileChannel.transferTo(0, size, destinationFileChannel);
            }
            // System.out.println("file=" + fileIn.getName());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro"+e,"Error",JOptionPane.ERROR_MESSAGE);
        } finally {
            source.close();
            destination.close();
            sourceFileChannel.close();
            destinationFileChannel.close();
        }
        

    }

    /**
     * Faz a cópia de uma pasta de uma origem para um destino. (Desde que não
     * tenha uma extensão da lista de extensões ignoradas)
     *
     * @param directoryIn - Diretório onde estão os arquivos a serem copiados
     * @param directoryOut - Diretório onde os arquivos serão copiados
     * @param fileOverwrite - Confirmação para sobrescrever os arquivos
     * @throws IOException - Erro de entrada e saida
     * @throws UnsupportedOperationException - Erro de Operação não suportada 
     */
    public void copiarPasta(File directoryIn, File directoryOut, boolean fileOverwrite) throws IOException, UnsupportedOperationException {
        if (!directoryOut.exists()) {
            directoryOut.mkdir();
        }
        if (!directoryOut.isDirectory()) {
            throw new UnsupportedOperationException("Destino deve ser um diretório");
        }
        if (!directoryIn.isDirectory()) {
            copiarArquivo(directoryIn, directoryOut, fileOverwrite);
        } else {
            //caso de pastas
            File[] files = directoryIn.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    copiarPasta(files[i], new File(directoryOut + "\\" + files[i].getName()), fileOverwrite);
                } else {
                    copiarArquivo(files[i], new File(directoryOut + "\\" + files[i].getName()), fileOverwrite);
                }
            }
        }
    }

    /**
     * Retorna o tamanho da pasta desejada
     *
     * @param path - Path da pasta designada
     * @return Tamanho da pasta em bytes
     */
    public static int getFolderSize(String path) {
        File folder = new File(path);
        int size = 0;
        if (folder.isDirectory()) {
            String[] dirList = folder.list();
            if (dirList != null) {
                for (int i = 0; i < dirList.length; i++) {
                    File f = new File(folder, dirList[i]);
                    if (f.isDirectory()) {
                        String filePath = f.getPath();
                        size += getFolderSize(filePath);
                        continue;
                    }
                    size += f.length();
                }
            }
        }
        return size;
    }
}
