import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TestJsoupParser {

    public static void main(String[] args) {

        System.out.println("================== список производителей ====================");
        //Get a list of motherboard manufacturers
        ArrayList<String> listOfManufactures = GetManufacturesLinks("http://www.s-manuals.com/motherboard");
        //ArrayList<String> listOfManufactures = GetManufacturesLinks("http://www.s-manuals.com/printer");

        PrintLinks(listOfManufactures);

        System.out.println("\n================== список материнок ====================");

        ArrayList<String> listOfMotherboard = new ArrayList<>();
        ArrayList<String> listTmp;

        for (String strLink : listOfManufactures){
            listTmp = GetMotherboardsLinks(strLink);
            listOfMotherboard.addAll(listTmp);
        }

        PrintLinks(listOfMotherboard);

        System.out.println("\n================== список файлов ========================");

        ArrayList<String> listOfFiles = new ArrayList<>();

        for (String strLnk : listOfMotherboard) {
            listTmp = GetDownloadLinks(strLnk);
            listOfFiles.addAll(listTmp);
        }

        PrintLinks(listOfFiles);

        String strFileName = "s-manual notebook schematics.txt";
        System.out.println("\n============ сохраняем ссылки на файлы в \"" + strFileName + "\" ============");

        WriteFile(listOfFiles, strFileName);
    }

    private static void PrintLinks(ArrayList<String> arraylist) {
        for (String str : arraylist){
            System.out.println(str);
        }
    }

    private static ArrayList GetManufacturesLinks(String strUrl) {

        ArrayList<String> arrHref = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(strUrl).get();
            Elements links = doc.select("a[href]");

            PushLinkToArray(links, arrHref);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return arrHref;
    }

    private static ArrayList GetMotherboardsLinks(String strUrl) {

        ArrayList<String> arrHref = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(strUrl).get();
            Elements links = doc.select("a[href]");

            PushLinkToArray(links, arrHref);

            //пока есть следующая страница
            String strNextPageLnk;

            while (IsNextPage(links)) {
                //то получить все ссылки со следующей страницы
                strNextPageLnk = GetNextPageLink(links);

                doc = Jsoup.connect(strUrl + strNextPageLnk ).get();
                links = doc.select("a[href]");
                PushLinkToArray(links, arrHref);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return arrHref;
    }

    private static ArrayList GetDownloadLinks(String strUrl){

        ArrayList<String> arrHref = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(strUrl).get();
            Elements links = doc.select("a[href]");

            for (Element elem : links) {
                if (elem.attr("href").contains("pdf")){
                    arrHref.add(elem.attr("href"));
                }
            }

            //пока есть следующая страница
            String strNextPageLnk;

            while (IsNextPage(links)) {
                //то получить все ссылки со следующей страницы
                strNextPageLnk = GetNextPageLink(links);

                doc = Jsoup.connect(strUrl + strNextPageLnk).get();
                links = doc.select("a[href]");

                for (Element elem : links) {
                    if (elem.attr("href").contains("pdf")){
                        arrHref.add(elem.attr("href"));
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return arrHref;
    }

    private static boolean IsNextPage(Elements linksEl){
        boolean result = false;

        for (Element el: linksEl) {
            if (el.text().equals("»")) {
                result = true;
                //System.out.println("There is the next page");
            }
        }

        return result;
    }

    private static String GetNextPageLink(Elements elem){

        for (Element el: elem) {
            if (el.text().equals("»")) {
                //System.out.println(el.attr("href"));
                return el.attr("href");
            }
        }

        return "";
    }

    private static void PushLinkToArray(Elements links, ArrayList<String> aLinks){

        boolean bSaveLinkToArray = false;

        for (Element link : links) {

            if (link.text().equals("Webmaster")) {
                //прерываем цикл
                break;
            }

            String strAttr = link.attr("href");

            if (strAttr.contains("?page=") || strAttr.equals("/") || strAttr.equals("/motherboard")){
                continue;
            }

//            if (bSaveLinkToArray) {
                aLinks.add(link.attr("href"));
//            }

            if (link.text().equals("Forum")) {
                bSaveLinkToArray = true;
            }
        }
    }

    private static void WriteFile(ArrayList<String> fileLinks, String fileName) {
        try {
            FileWriter file = new FileWriter(fileName);

            for (String strLink : fileLinks) {
                file.write("http://www.s-manuals.com" + strLink + "\n");
            }
            file.close();
        }
        catch (IOException ex) {
            System.out.println("An error occurred while writing the file " + fileName);
            System.out.println(ex);
        }
    }
}