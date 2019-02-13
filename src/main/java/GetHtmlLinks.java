import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class GetHtmlLinks {
    private static String pageUrl = "https://download.itadmins.net/Schematics/";
    private static List<String> links = new Vector<>();

    public static void main(String[] args) {
        GetHtmlLinks htmlLinks = new GetHtmlLinks();

        pageUrl = JOptionPane.showInputDialog("Enter url", pageUrl);

        if (pageUrl == null)
            return;

        htmlLinks.getFileLinks(pageUrl);
        htmlLinks.printLinks();
        htmlLinks.writeFile("listOfFiles" + ".txt");
    }

    private void getFileLinks(String strUrl) {

        try {
            Document doc = Jsoup.connect(strUrl).get();
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

    private void writeFile(String fileName) {
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
}
