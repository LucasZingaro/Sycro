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
 *
 * @author Lucas Zingaro
 * @author baseado em diego_qmota Access in 08-03-2019
 * (https://www.guj.com.br/t/swing-copiar-pasta-de-um-local-e-salvar-em-outra/132165/6)
 */
public class CopyArq {

    public CopyArq() {
    }

    /**
     * Define a lista de extensões, arquivos e pastas ignoradas
     *
     * @param listIgnore - Lista de extensões, arquivos e pastas ignoradas
     */
    public CopyArq(String[] listIgnore) {
        this.listIgnore = listIgnore.clone();
    }

    /**
     * Lista de extensões, arquivos e pastas ignoradas.
     */
    private String[] listIgnore;

    /**
     * Pegar lista de extensões, arquivos e pastas ignoradas.
     *
     * @return Array com lista de extensões, arquivos e pastas ignoradas.
     */
    public String[] getListIgnore() {
        return listIgnore.clone();
    }

    /**
     * Atribuir lista de extensões, arquivos e pastas ignoradas.
     *
     * @param listIgnore - Array de extensões, arquivos e pastas ignoradas
     */
    public void setListIgnore(String[] listIgnore) {
        this.listIgnore = listIgnore.clone();
    }

    /**
     * Faz a cópia de um único arquivo de uma origem para um destino. (Desde que
     * não tenha uma extensão da lista de extensões, arquivos e pastas ignoradas)
     *
     * @param fileIn - Arquivo de origem
     * @param fileOut - Arquivo de destino
     * @param fileOverwrite - Confirmação para sobrescrever os arquivos
     * @throws java.io.IOException - Erro de Entrada e Saida
     */
    public void copiarArquivo(File fileIn, File fileOut, boolean fileOverwrite) throws IOException {
        for (String objIg : listIgnore) {
            if (isEqualsPasta(fileIn.getParent(), objIg)) {
                return;
            }
            if (isEqualsExt(fileIn.getPath(), objIg)) {
                return;
            }
        }

        if (fileOut.exists()
                && !fileOverwrite) {
            return;
        }
        FileInputStream source = null;
        FileOutputStream destination = null;
        FileChannel sourceFileChannel = null;
        FileChannel destinationFileChannel = null;

        try {
            source = new FileInputStream(fileIn);
            if (listIgnore.length >= 1) {
                destination = new FileOutputStream(fileOut);
                sourceFileChannel = source.getChannel();
                destinationFileChannel = destination.getChannel();
                sourceFileChannel.transferTo(0, sourceFileChannel.size(), destinationFileChannel);

            } else {
                destination = new FileOutputStream(fileOut);
                sourceFileChannel = source.getChannel();
                destinationFileChannel = destination.getChannel();
                sourceFileChannel.transferTo(0, sourceFileChannel.size(), destinationFileChannel);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro" + e, "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
            if (sourceFileChannel != null) {
                sourceFileChannel.close();
            }
            if (destinationFileChannel != null) {
                destinationFileChannel.close();
            }
        }

    }

    /**
     * Faz a cópia de uma pasta de uma origem para um destino. (Desde que não
     * tenha uma extensão da lista de extensões, arquivos e pastas ignoradas)
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
            for (String pastaIg : listIgnore) {
                if (isEqualsPasta(directoryIn.getName(), pastaIg)) {
                    directoryOut.delete();
                    return;
                }
            }
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
     * Retorna o tamanho da pasta desejada em Bytes
     *
     * @param path - Path da pasta designada
     * @return Tamanho da pasta em bytes
     */
    public static int getFolderSizeInB(String path) {
        File folder = new File(path);
        int size = 0;
        if (folder.isDirectory()) {
            String[] dirList = folder.list();
            if (dirList != null) {
                for (int i = 0; i < dirList.length; i++) {
                    File f = new File(folder, dirList[i]);
                    if (f.isDirectory()) {
                        String filePath = f.getPath();
                        size += getFolderSizeInB(filePath);
                        continue;
                    }
                    size += f.length();
                    if ((size / 1000000000) > Sycro.limiteDeTamanhoDaPasta) {
                        return size;
                    }
                }
            }
        }
        return size;
    }

    /**
     * Retorna o tamanho da pasta desejada em GigaBytes
     *
     * @param path - Path da pasta designada
     * @return Tamanho da pasta em GigaBytes
     */
    public static double getFolderSizeInG(String path) {
        return Double.parseDouble(String.valueOf(getFolderSizeInB(path) / 1000000000));
    }

    /**
     * Retorna a quantidade de arquivos/pastas do diretório desejado.
     *
     * @param path - Path da pasta designada
     * @return Quantidade de arquivos
     */
    public static int contPaths(String path) {
        File folder = new File(path);
        int size = 0;
        if (folder.isDirectory()) {

            String[] dirList = folder.list();
            if (dirList != null) {
                for (int i = 0; i < dirList.length; i++) {
                    File f = new File(folder, dirList[i]);
                    if (f.isDirectory()) {
                        String filePath = f.getPath();
                        size += contPaths(filePath) + 1;
                        continue;
                    }
                    size++;
                }
            }
        }
        return size;
    }

    /**
     * Pegar todos os Paths de um diretório
     *
     * @param dir - Diretório designado
     * @return - Array dos Paths
     */
    public static File[] getPathsInDir(File dir) {
        File[] files = dir.listFiles();
        File[] filesFim = new File[CopyArq.contPaths(dir.getPath())];
        for (int j = 0; j < filesFim.length; j++) {

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    File[] newfiles = getPathsInDir(new File(dir + "\\" + files[i].getName()));
                    filesFim[j] = new File(dir + "\\" + files[i].getName());
                    j++;
                    for (int k = 0; k < newfiles.length; k++) {
                        filesFim[j] = newfiles[k];
                        j++;
                    }

                } else {
                    filesFim[j] = (new File(dir + "\\" + files[i].getName()));
                    j++;
                }
            }
        }

        return filesFim;
    }

    /**
     * Verifica se o path termina com a extensão
     *
     * @param path - String do path do arquivo.
     * @param ext - extensão avaliada.
     * @return - True se for a terminação
     */
    public static boolean isEqualsExt(String path, String ext) {
        try {
            if (ext.isEmpty() || path.isEmpty()) {
                return false;
            }
            if (!ext.contains(".")) {
                return false;
            }

            int index = path.length() - ext.length();
            return (path.substring(index).equalsIgnoreCase(ext));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se path termina com o nome de pasta desejado
     *
     * @param path - String do path do arquivo.
     * @param nomePasta - Nome da pasta.
     * @return - True se for a terminação e False se não
     */
    public static boolean isEqualsPasta(String path, String nomePasta) {
        try {
            if (nomePasta.isEmpty() || path.isEmpty()) {
                return false;
            }
            if (nomePasta.contains(".")) {
                return false;
            }
            return path.endsWith(nomePasta);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se path contém com o nome de pasta desejado
     *
     * @param path - String do path do arquivo.
     * @param nomePasta - Nome da pasta.
     * @param pathRaiz
     * @return True ou False
     */
    public static boolean isEqualsPastaInPath(String path, String nomePasta, String pathRaiz) {
        try {
            String sp = path.replace(pathRaiz, "");
            if (nomePasta.isEmpty() || path.isEmpty()) {
                return false;
            }
            if (nomePasta.contains(".")) {
                return false;
            }
            return sp.contains("\\"+nomePasta);
        } catch (Exception e) {
            return false;
        }
    }
}
