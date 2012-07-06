package com.d2fn.guano;

import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * FSWalker
 * @author Dietrich Featherston
 */
public class FSWalker {

    private String localRoot;
    private String znodeRoot;

    public FSWalker(String localRoot, String znodeRoot) {
        this.localRoot = localRoot;
        this.znodeRoot = znodeRoot;
    }

    public void go(FSVisitor visitor) {
        File f = new File(localRoot);
        walk(visitor, f, znodeRoot);
    }

    private void walk(FSVisitor visitor, File f, String znode) {

        byte[] data = new byte[0];

        if(znode.endsWith("_znode")) {
            znode = znode.substring(0, znode.length()-6);
        }
        if(znode.endsWith("/") && znode.length() > 1) {
            znode = znode.substring(0, znode.length()-1);
        }

        try {
            data = FileUtils.readFileToByteArray(f);
        } catch (IOException e) {}

        visitor.visit(f, data, znode);

        if(f.isDirectory()) {
            File[] children = f.listFiles();
            for(File child : children) {
                walk(visitor, child, (znode.equals("/") ? "" : znode) + "/" + child.getName());
            }
        }
    }
}
