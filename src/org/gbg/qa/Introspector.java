package org.gbg.qa;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

public class Introspector {
    static public void main(String args[]) throws MalformedURLException, UnknownHostException {
        SiteGrinder pg = new SiteGrinder("https://www.22squared.com");
        pg.grind();
    }
}
