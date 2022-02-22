package org.gbg.qa;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SiteGrinder {
    String root = null;
    InetAddress siteIP = null;
    Set<String> visited = new HashSet<>();

    public SiteGrinder(String root) throws MalformedURLException, UnknownHostException {
        this.root = root;
        this.siteIP = InetAddress.getByName(new URL(root).getHost());
    }

    protected boolean isPartOfSite(String url) {
        boolean retval = false;
        try {
            InetAddress ip = InetAddress.getByName(new URL(url).getHost());
            retval = (ip.equals(siteIP));
        } catch (Throwable th) {
            retval = false;
        }
        return retval;
    }

    protected Set<String> getLinks(String url) {
        Set<String> links = new HashSet<>();
        if (visited.contains(url))
            return Collections.emptySet();

        links.add(url);
        visited.add(url);
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .timeout(10000)
                    .userAgent("DevOps watchtower kit.mccormick@22squared.com")
                    .get();
            Elements elements = doc.select("a[href]");
            for (Element element : elements) {
                String newLink = element.attr("href");
                if (!visited.contains(newLink))
                    if (isPartOfSite(newLink)) {
                        if (newLink.startsWith("https")) {
                            System.err.println("Parent: " + url + " child: " + newLink);
                            links.addAll(getLinks(newLink));
                        } else {
                            System.err.println("ODDling!  Parent: " + url + " child: " + newLink);
                        }
                    }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Socket timeout (10 seconds) fetching " + url);
            links.remove(url);
        } catch (Throwable th) {
            System.err.println("Cannot process " + url + ". " + th.getMessage());
            links.remove(url);
        }
        return links;
    }

    public void grind() {
        Set<String> links = getLinks(this.root);
        for (String url: links) {
            System.err.println(url);
        }
    }
}
