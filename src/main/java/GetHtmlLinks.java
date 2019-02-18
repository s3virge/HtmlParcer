import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* wget -i s-manuals.txt -P foldername
*

*  */


public class GetHtmlLinks {
    private static String pageUrl = "";
    private static String fileName = "";
    private static List<String> links = new Vector<>();

    public static void main(String[] args) {

        GetHtmlLinks htmlLinks = new GetHtmlLinks();

        while (pageUrl.isEmpty()){
            pageUrl = JOptionPane.showInputDialog("Enter url", "https://download.itadmins.net/Schematics/");

            if (pageUrl == null)
                return;
        }

        htmlLinks.processFiles(pageUrl);
//        htmlLinks.getLinks();
//        htmlLinks.printLinks();
//        htmlLinks.writeFile();
    }

    private void getLinks() {

        try {
            Document doc = Jsoup.connect(pageUrl).get();
            Elements elements = doc.select("a[href]");

            addLinksToList(elements);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addLinksToList(Elements elements){

        for (Element link : elements) {
            String strAttr = link.attr("href");
            links.add(strAttr);
        }
    }

    private void printLinks() {

        if (!links.isEmpty()) {
            for (String str : links){
                System.out.println(str);
            }
        }
        else {
//            System.out.println("No links were found.");
            JOptionPane.showMessageDialog(null, "No links were found.", "Облом", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void writeFile() {

        while (fileName.isEmpty()){
            fileName = JOptionPane.showInputDialog("Enter file name.", "linkList.txt");

            //cancel button was pressed
            if (fileName == null) {
                return;
            }
        }

        try {
            FileWriter file = new FileWriter(fileName);

            for (String strLink : links) {
                file.write(pageUrl + strLink + "\n");
            }
            file.close();

            JOptionPane.showMessageDialog(null, fileName + " file was written successful", "Operation", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "An error occurred while writing the file " + fileName, "Облом", JOptionPane.ERROR_MESSAGE);
            System.out.println("An error occurred while writing the file " + fileName);
            System.out.println(ex);
        }
    }

    //https://ru.stackoverflow.com/questions/465935/Как-обойти-все-файлы-в-папке-и-подпапках-и-прочитать-текстовые-файлы-в-массив
    public void processFiles(String pageUrl)
    {
        Vector<String> listOfLinks = takeLinks(pageUrl);

        for (String entry : listOfLinks)
        {

            if (checkMatch(entry, "(\\?.=.;)")) {
                continue;
            }

            if (checkMatch(entry, "(//$)")) {
                continue;
            }

            if (checkMatch(entry, "(//\\w+/)")) {
                continue;
            }

            if (!isFile(entry))
            {
                processFiles(entry);
                continue;
            }
            // иначе вам попался файл, обрабатывайте его!
            System.out.println(entry);
        }
    }

    private boolean checkMatch(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);

        return m.find();
    }

    private Vector<String> takeLinks(String fileUrl) {
        Vector<String> linkList = new Vector<>();

        try {
            Document doc = Jsoup.connect(fileUrl).get();
            Elements elements = doc.select("a[href]");

            for (Element link : elements) {
                String strAttr = link.attr("href");
                linkList.add(fileUrl + strAttr);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return linkList;
    }

    private boolean isFile(String path) {
        Pattern p = Pattern.compile("\\w+\\.[a-z]{3}$");
        Matcher m = p.matcher(path);
        return m.find();
    }
}
