package br.com.rodrigo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebScrapingANS {

    public static void main(String[] args) {
        String url = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";
        String downloadDir = "anexos/";

        try {

            Document doc = Jsoup.connect(url).get();


            List<String> pdfUrls = new ArrayList<>();

            Elements links = doc.select("a");
            for (Element link : links) {
                String linkText = link.text();
                String fileUrl = link.attr("href");

                boolean b = linkText.contains("Anexo I") || linkText.contains("Anexo II");
                if (b)
                    if (fileUrl.toLowerCase().endsWith(".pdf")) {
                        pdfUrls.add(fileUrl);
                        System.out.println("Encontrado PDF: " + linkText + " - " + fileUrl);
                    } else {
                        System.out.println("Ignorado (não é PDF): " + linkText + " - " + fileUrl);
                    }
            }


            File dir = new File(downloadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            List<String> downloadedFiles = new ArrayList<>();
            for (String pdfUrl : pdfUrls) {
                try {
                    String fileName = pdfUrl.substring(pdfUrl.lastIndexOf('/') + 1);
                    File destination = new File(downloadDir + fileName);
                    System.out.println("Baixando: " + pdfUrl);

                    FileUtils.copyURLToFile(new URL(pdfUrl), destination);
                    downloadedFiles.add(destination.getAbsolutePath());
                    System.out.println("Download concluído do arquivo " + fileName + " concluído com sucesso!");
                } catch (IOException e) {
                    System.err.println("Erro ao realizar o download do arquivo: " + pdfUrl);
                    e.printStackTrace();
                }
            }


            if (!downloadedFiles.isEmpty()) {
                String zipFileName = "resolucoes_ans.zip";
                zipFiles(downloadedFiles, zipFileName);
                System.out.println("Compactação concluída: " + zipFileName);


                File zipFile = new File(zipFileName);
                System.out.println("Arquivo ZIP criado em: " + zipFile.getAbsolutePath());
            } else {
                System.out.println("Nenhum arquivo foi baixado para compactação.");
            }

        } catch (IOException e) {
            System.err.println("Erro ao acessar a página: " + url);
            e.printStackTrace();
        }
    }

    private static void zipFiles(List<String> filePaths, String zipFileName) {
        try {
            net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipFileName);

            for (String filePath : filePaths) {
                File file = new File(filePath);
                zipFile.addFile(file);
            }

        } catch (Exception e) {
            System.err.println("Erro ao compactar arquivos");
            e.printStackTrace();
        }
    }
}