package amazon;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;

/*
 * This class shows how to make a simple authenticated ItemLookup call to the
 * Amazon Product Advertising API.
 *
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class ItemLookupSample1 {
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

    public ItemLookupSample1() {

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

    public ItemLookupSample1(DefaultTableModel model, JProgressBar progress) {

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
    }

    public void getResultsByISBN(List<String> nodeID, FileWriter fW, List<String> existingIsbn) {
//            System.out.println(existingIsbn.size());
        System.out.println(nodeID);
        String tradeIn;
        double steps = nodeID.size()*0.01;
        int val=0;
        for (String itemId : nodeID) {
//            System.out.println("Curr : " + progress.getValue() + " Steps " + steps + " Total " + nodeID.size() + " val " + val);
            if(val++%(int)steps==0)
                progress.setValue(progress.getValue()+1);
            try {
                fW.flush();
            } catch (IOException ex) {
                Logger.getLogger(ItemLookupSample1.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (AmazonFrame.exStop) {
//                System.out.println("Stopped");
                return;
            }
            if (existingIsbn.contains(itemId)) {
//                System.out.println("ItemId : " + itemId);
                continue;
            }


            String requestUrl = null;



            Map<String, String> params = new HashMap<String, String>();
            params.put("Service", "AWSECommerceService");
            params.put("Version", "2009-03-31");
            params.put("Operation", "ItemLookup");
            params.put("ItemId", itemId);
            params.put("AssociateTag", ACCESS_TAG);
            params.put("ResponseGroup", "Large");
            requestUrl = helper.sign(params);

            displayDetailsOfBook(requestUrl, fW);

        }

    }

    private void displayDetailsOfBook(String requestUrl, FileWriter fW) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            NodeList allBooks = doc.getElementsByTagName("Item");
            Element singleInstance = null;

            for (int i = 0; i < allBooks.getLength(); i++) {
                singleInstance = (Element) allBooks.item(i);


                String isbn = new String("NA");
                String currNew = new String("NA");
                String currUsed = new String("NA");
                String weight = new String("NA");
                String title = new String("NA");

                int rankObt = -1;
                if (singleInstance.getElementsByTagName("SalesRank").getLength() >= 1) {
                    rankObt = Integer.parseInt(singleInstance.getElementsByTagName("SalesRank").item(0).getTextContent());
//                                     System.out.println(rank + " obt" + rankObt);
                    fW.write(rankObt);

                } else {
                    fW.write("NA");
                }
                fW.write(",");
                if (singleInstance.getElementsByTagName("ASIN").getLength() >= 1) {
                    isbn = singleInstance.getElementsByTagName("ASIN").item(0).getTextContent();
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
                    currNew = singleInstance.getElementsByTagName("LowestNewPrice").item(0).getLastChild().getTextContent();
                    fW.write(currNew);
                } else {
                    fW.write("NA");
                }
                fW.write(",");
//                if (singleInstance.getElementsByTagName("LowestUsedPrice").getLength() >= 1) {
//                    currUsed = singleInstance.getElementsByTagName("LowestUsedPrice").item(0).getLastChild().getTextContent();
//                    fW.write(currUsed);
//                } else {
//                    fW.write("NA");
//                }

                String oldPrice = new ItemLookupSample().likeNew("http://www.amazon.com/gp/offer-listing/" + isbn + "/ref=dp_olp_all_mbc?ie=UTF8&condition=used",isbn);
                if(oldPrice != null )
                    fW.write(oldPrice.split("\\$")[1]);
                else
                    fW.write("NA");
                fW.write(",");
//				System.out.println(singleInstance.getElementsByTagName("LowestCollectiblePrice").getLength());
//                if (singleInstance.getElementsByTagName(
//                        "LowestCollectiblePrice").getLength() >= 1) {
//                    fW.write(singleInstance.getElementsByTagName("LowestCollectiblePrice").item(0).getLastChild().getTextContent());
//                } else {
//                    fW.write("NA");
//                }
                fW.write(AmazonFrame.round(Double.parseDouble(currNew.split("\\$")[1]) - Double.parseDouble(oldPrice.split("\\$")[1]),2,BigDecimal.ROUND_HALF_UP) + "");
                fW.write(",");

                fW.write("\n");

                model.addRow(new Object[]{isbn, currNew, oldPrice, weight, title, rankObt,AmazonFrame.round(Double.parseDouble(currNew.split("\\$")[1]) - Double.parseDouble(oldPrice.split("\\$")[1]),2,BigDecimal.ROUND_HALF_UP) + ""});
            }
        } catch (SAXException ex) {
            Logger.getLogger(ItemLookupSample1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ItemLookupSample1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ItemLookupSample1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
