/**
 * ********************************************************************************************
 * Copyright 2009 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not
 * use this file except in compliance with the License. A copy of the License is
 * located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE.txt" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * ********************************************************************************************
 *
 * Amazon Product Advertising API Signed Requests Sample Code
 *
 * API Version: 2009-03-31
 *
 */

package amazon;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This class shows how to make a simple authenticated ItemLookup call to the
 * Amazon Product Advertising API.
 *
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class ItemLookupSample {
    /*
     * Your AWS Access Key ID, as taken from the AWS Your Account page.
     */
    
    private String AWS_ACCESS_KEY_ID;

    /*
     * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
     * Your Account page.
     */
    private String AWS_SECRET_KEY;
    private String ACCESS_TAG;
    /*
     * Use one of the following end-points, according to the region you are
     * interested in:
     *
     * US: ecs.amazonaws.com CA: ecs.amazonaws.ca UK: ecs.amazonaws.co.uk DE:
     * ecs.amazonaws.de FR: ecs.amazonaws.fr JP: ecs.amazonaws.jp
     */
    private final String ENDPOINT = "ecs.amazonaws.com";
    private SignedRequestsHelper helper;
    DefaultTableModel model;
    JProgressBar progress;
    
    public ItemLookupSample() {
        
        try {
            BufferedReader bR = new BufferedReader(new FileReader("Amazon.properties"));
            String line;
            while ((line = bR.readLine()) != null) {
                String info[] = line.split("=");
                if (info[0].trim().equals("AWS_ACCESS_KEY_ID")) {
                    AWS_ACCESS_KEY_ID = info[1].trim();
                }
                if (info[0].trim().equals("AWS_SECRET_KEY")) {
                    AWS_SECRET_KEY = info[1].trim();
                }
                if (info[0].trim().equals("ACCESS_TAG")) {
                    ACCESS_TAG = info[1].trim();
                }
            }
            helper = SignedRequestsHelper.getInstance(ENDPOINT,
                    AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    
    public ItemLookupSample(DefaultTableModel model, JProgressBar progress) {
        
        try {
            BufferedReader bR = new BufferedReader(new FileReader("Amazon.properties"));
            String line;
            while ((line = bR.readLine()) != null) {
                String info[] = line.split("=");
                if (info[0].trim().equals("AWS_ACCESS_KEY_ID")) {
                    AWS_ACCESS_KEY_ID = info[1].trim();
                }
                if (info[0].trim().equals("AWS_SECRET_KEY")) {
                    AWS_SECRET_KEY = info[1].trim();
                }
                if (info[0].trim().equals("ACCESS_TAG")) {
                    ACCESS_TAG = info[1].trim();
                }
            }
            this.model = model;
            this.progress = progress;
            helper = SignedRequestsHelper.getInstance(ENDPOINT,
                    AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    
    public static void main(String[] args) {
        
        ItemLookupSample obj = new ItemLookupSample();
        //obj.findDetailsOfItem("0545010225");
//		obj.searchByTitle("Hadoop");
    }

    /*
     * Utility function to fetch the response from the service and extract the
     * title from the XML.
     */
    private static String fetchTitle(String requestUrl) {
        String title = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            Node titleNode = doc.getElementsByTagName("Title").item(0);
            title = titleNode.getTextContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return title;
    }
    
    private static void fetchDetails(String requestUrl) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            Node titleNode = doc.getElementsByTagName("Weight").item(0);
            System.out.println("Weight = " + titleNode.getTextContent());
            System.out.println("Lowest New Price = "
                    + doc.getElementsByTagName("LowestNewPrice").item(0).getLastChild().getTextContent());
            System.out.println("Lowest Used Price = "
                    + doc.getElementsByTagName("LowestUsedPrice").item(0).getLastChild().getTextContent());
            System.out.println("Lowest Collectible Price = "
                    + doc.getElementsByTagName("LowestCollectiblePrice").item(0).getLastChild().getTextContent());
            System.out.println("Sales Rank : "
                    + doc.getElementsByTagName("SalesRank").item(0).getTextContent());
            // titleNode = doc.getElementsByTagName("").item(0);
            // title = titleNode.getTextContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void writeToFile(String requestUrl) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            Source src = new DOMSource(doc);
            
            File file = new File("writeToFileDomDocument");
            Result rs = new StreamResult(file);
            
            TransformerFactory tmf = TransformerFactory.newInstance();
            Transformer trnsfrmr = tmf.newTransformer();
            trnsfrmr.transform(src, rs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public int searchByTitle(final String title, final String keywords, final String difference, final int rank, String path, final FileWriter fW, final List<String> existingIsbn) {
//        String requestUrl = null;
//        
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("Service", "AWSECommerceService");
//        params.put("Version", "2009-03-31");
//        params.put("Operation", "ItemSearch");
//        params.put("AssociateTag", ACCESS_TAG);
//        params.put("ResponseGroup", "Large");
//        params.put("SearchIndex", "Books");
//        params.put("Condition", "All");
//        params.put("Sort", "salesrank");
//        params.put("BrowseNode", title);
//        params.put("Keywords", keywords);
//        params.put("Power", "binding:Paperback or binding:Hardcover");
//
//        requestUrl = helper.sign(params);
//        
//        System.out.println(requestUrl);
//        displayAllBooks(requestUrl, fW, rank, existingIsbn, difference);

        //writeToFile(requestUrl);
        int i;
        for (i = 1; i <= 10 && !AmazonFrame.processingStop; i++) {
            
            progress.setValue((i-1) * 10);
//        Loop.withIndex(1, 11, new Loop.Each() {

//            @Override
//            public void run(int i) {
            System.out.println("loop index : " + i);
            String requestUrl = null;
            Map<String, String> params = new HashMap<String, String>();
            
            params = new HashMap<String, String>();
            params.put("Service", "AWSECommerceService");
            params.put("Version", "2009-03-31");
            params.put("Operation", "ItemSearch");
            params.put("AssociateTag", ACCESS_TAG);
            params.put("ResponseGroup", "Large");
            params.put("SearchIndex", "Books");
            params.put("Condition", "All");
            params.put("BrowseNode", title);
            params.put("Keywords", keywords);
            params.put("Sort", "salesrank");
            if (i != 1) {
                params.put("ItemPage", i + "");
            }
            params.put("Power", "binding:Paperback or binding:Hardcover");
            requestUrl = helper.sign(params);
            
            System.out.println(requestUrl);
            displayAllBooks(requestUrl, fW, rank, existingIsbn, difference);
            try {
                fW.flush();
            } catch (IOException ex) {
                Logger.getLogger(ItemLookupSample.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        progress.setValue((i-1) * 10);
        
        return 1;
        
    }
    
    public String likeNew(String asinPage, String asin) {
        
        org.jsoup.nodes.Document document = null;
        
        int finished = 0;
        
        
        AmazonFrame.jLabel6.setText("Scraping Like New for " + asin);
        
        try {
            while (finished <= 3) {
                try {
                    document = org.jsoup.Jsoup.connect(asinPage).userAgent(
                            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.57 Safari/536.11").timeout(5000).get();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    
                    System.out.println("Failed ISBN " + asin);
                }
                finished++;
            }

//            BufferedWriter bW = new BufferedWriter(new FileWriter("asd"));
            // bW.write(document.toString());
            // bW.close();
            for (int i = 0; i < 100; i++) {
                if (i > 0) {
                    AmazonFrame.jLabel6.setText("Scraping Page " + i + " for " + asin);
                    
                    org.jsoup.nodes.Element page = document.getElementById("page_" + (i));
                    if (page == null) {
                        break;
                    }
                    
                    asinPage = "http://www.amazon.com" + page.attr("href");
//					System.out.println(asinPage);
//					asinPage = "http://www.amazon.com/gp/offer-listing/1561585300/ref=olp_page_"+(i+1)+"?ie=UTF8&condition=used";
                    finished = 0;
                    while (finished <= 3) {
                        try {
                            document = org.jsoup.Jsoup.connect(asinPage).userAgent("Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1451.14 Safari/537.36").timeout(5000).get();
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Failed ISBN " + asin);
                        }
                        finished++;
                    }
                }
                org.jsoup.select.Elements res = document.getElementsByClass("result");
                // System.out.println(res);
                for (org.jsoup.nodes.Element s : res) {
                    // System.out.println(s);
                    // bW.write(s.toString());
                    if (s.getElementsByClass("condition").first().ownText().contains("Like New")) {
//                        System.out.println(s.getElementsByClass("price").first().ownText());
//                        processing.setVisible(false);
                        return (s.getElementsByClass("price").first().ownText());
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
        
    }
    
    private int getNumPages(String requestUrl) {
        int numPages = -1;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            Node titleNode = doc.getElementsByTagName("TotalPages").item(0);
            numPages = Integer.parseInt(titleNode.getTextContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return numPages;
    }
    
    private void displayAllBooks(String requestUrl, final FileWriter fW, final int rank, final List<String> existingIsbn, String difference) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            final NodeList allBooks = doc.getElementsByTagName("Item");
            
            final double differenceAmount = Double.parseDouble(difference);
            //for (int i = 0; i < allBooks.getLength(); i++) 
            Loop.withIndex(0, allBooks.getLength() , new Loop.Each() {
                
                @Override
                public void run(int i) {
                    try {
                        Element singleInstance = null;
                        singleInstance = (Element) allBooks.item(i);
                        //
                        // System.out.println("Title = " +
                        // singleInstance.getElementsByTagName("Title").item(0).getTextContent());
                        // System.out.println("Author = " +
                        // singleInstance.getElementsByTagName("Author").item(0).getTextContent());
                        // System.out.println("Weight = " +
                        // singleInstance.getElementsByTagName("Weight").item(0).getTextContent());
                        // System.out.println("Lowest New Price = " +
                        // singleInstance.getElementsByTagName("LowestNewPrice").item(0).getLastChild().getTextContent());
                        // System.out.println("Lowest Used Price = " +
                        // singleInstance.getElementsByTagName("LowestUsedPrice").item(0).getLastChild().getTextContent());
                        // System.out.println("Lowest Collectible Price = " +
                        // singleInstance.getElementsByTagName("LowestCollectiblePrice").item(0).getLastChild().getTextContent());
                        // System.out.println("Sales Rank : " +
                        // //
                        // //

                        String isbn = new String("NA");
                        String currNew = new String("NA");
                        String currUsed = new String("NA");
                        String weight = new String("NA");
                        String title = new String("NA");
                        
                        int rankObt = -1;
                        
                        if (singleInstance.getElementsByTagName("ASIN").getLength() >= 1) {
                            isbn = singleInstance.getElementsByTagName("ASIN").item(0).getTextContent();
                            if (existingIsbn.contains(isbn)) {
                                System.out.println("ItemId : " + isbn);
                                return;
                            }
                        }
                        double oldPrice = 0;
                        String price = null;
                        if (singleInstance.getElementsByTagName("SalesRank").getLength() >= 1) {
                            rankObt = Integer.parseInt(singleInstance.getElementsByTagName("SalesRank").item(0).getTextContent());
                            
                            System.out.println(rank + " obt" + rankObt);
                            if (rankObt <= rank) {
                                if (singleInstance.getElementsByTagName("LowestNewPrice").getLength() >= 1) {
                                    currNew = singleInstance.getElementsByTagName("LowestNewPrice").item(0).getLastChild().getTextContent();
                                    price = likeNew("http://www.amazon.com/gp/offer-listing/" + isbn + "/ref=dp_olp_all_mbc?ie=UTF8&condition=used", isbn);
                                    if (price == null) {
                                        return;
                                    }
                                    
                                    oldPrice = Double.parseDouble(price.split("\\$")[1]);
                                    System.out.println("Like " + oldPrice + " New " + currNew + " isbn " + isbn);
                                    if (Double.parseDouble(currNew.split("\\$")[1]) - oldPrice < differenceAmount && !(Double.parseDouble(currNew.split("\\$")[1]) - oldPrice < 0) ) {
                                        return;
                                    }
                                }
                                fW.write(rankObt + "");
                            } else {
                                return;
                            }
                        } else {
                            return;
                            //					fW.write("0");
                        }
                        fW.write(",");
                        if (singleInstance.getElementsByTagName("ASIN").getLength() >= 1) {
                            
                            fW.write(isbn);
                        } else {
                            fW.write("NA");
                        }
                        
                        fW.write(",");
                        
                        if (singleInstance.getElementsByTagName("Title").getLength() >= 1) {
                            title = singleInstance.getElementsByTagName("Title").item(0).getTextContent();
                            fW.write("\"" + title + "\"");
                        } else {
                            fW.write("No Title");
                        }
                        
                        fW.write(",");
                        if (singleInstance.getElementsByTagName("Author").getLength() >= 1) {
                            fW.write("\""
                                    + singleInstance.getElementsByTagName("Author").item(0).getTextContent() + "\"");
                        } else {
                            fW.write("Anonymous");
                        }
                        fW.write(",");
                        
                        if (singleInstance.getElementsByTagName("Weight").getLength() >= 1) {
                            weight = (Double.parseDouble(singleInstance.getElementsByTagName("Weight").item(0).getTextContent()) / 100) + "";
                            if (weight.equals("0") && singleInstance.getElementsByTagName("Weight").getLength() > 1) {
                                weight = singleInstance.getElementsByTagName("Weight").item(1).getTextContent();
                            }
                            fW.write(weight);
                        } else {
                            fW.write("NA");
                        }
                        
                        fW.write(",");
                        
                        if (singleInstance.getElementsByTagName("LowestNewPrice").getLength() >= 1) {
                            //currNew =singleInstance.getElementsByTagName("LowestNewPrice").item(0).getLastChild().getTextContent();
                            fW.write(currNew);
                        } else {
                            fW.write("NA");
                        }
                        fW.write(",");
//                        if (singleInstance.getElementsByTagName("LowestUsedPrice").getLength() >= 1) {
//                            currUsed = singleInstance.getElementsByTagName("LowestUsedPrice").item(0).getLastChild().getTextContent();
//                            fW.write(currUsed);
//                        } else {
//                            fW.write("NA");
//                        }
                        fW.write(price);
                        fW.write(",");
                        //				System.out.println(singleInstance.getElementsByTagName("LowestCollectiblePrice").getLength());
//                        if (singleInstance.getElementsByTagName(
//                                "LowestCollectiblePrice").getLength() >= 1) {
//                            fW.write(singleInstance.getElementsByTagName("LowestCollectiblePrice").item(0).getLastChild().getTextContent());
//                        } else {
//                            fW.write("NA");
//                        }
                        fW.write(AmazonFrame.round(Double.parseDouble(currNew.split("\\$")[1]) - oldPrice,2,BigDecimal.ROUND_HALF_UP) + "");
                        fW.write(",");
                        
                        fW.write("\n");
                        
                        model.addRow(new Object[]{isbn, currNew, price, weight, title, rankObt,AmazonFrame.round(Double.parseDouble(currNew.split("\\$")[1]) - oldPrice,2,BigDecimal.ROUND_HALF_UP) + ""});
                    } catch (IOException ex) {
                        Logger.getLogger(ItemLookupSample.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
    
    private void findDetailsOfItem(String itemId) {
        String requestUrl = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Version", "2009-03-31");
        params.put("Operation", "ItemLookup");
        params.put("ItemId", itemId);
        params.put("AssociateTag", ACCESS_TAG);
        params.put("ResponseGroup", "Large");
        
        requestUrl = helper.sign(params);
        // System.out.println("Signed Request is \"" + requestUrl + "\"");
        // writeToFile(requestUrl);
        fetchDetails(requestUrl);
    }
}

class Loop {
    
    public interface Each {
        
        void run(int i);
    }
//    private static final int CPUs = Runtime.getRuntime().availableProcessors();
    private static final int CPUs = 2;
    
    public static void withIndex(int start, int stop, final Loop.Each body) {
        int chunksize = (stop - start + CPUs - 1) / CPUs;
        int loops = (stop - start + chunksize - 1) / chunksize;
        ExecutorService executor = Executors.newFixedThreadPool(CPUs);
        final CountDownLatch latch = new CountDownLatch(loops);
        for (int i = start; i < stop;) {
            final int lo = i;
            i += chunksize;
            final int hi = (i < stop) ? i : stop;
            executor.submit(new Runnable() {
                
                @Override
                public void run() {
                    for (int i = lo; i < hi; i++) {
                        body.run(i);
                    }
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
        }
        executor.shutdown();
    }
}
