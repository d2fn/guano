package com.d2fn.guano;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * DumpJob
 * @author Dietrich Featherston
 */
public class DumpJob implements Job, Watcher {

    private String zkServer;
    private String outputDir;
    private String znode;

    public DumpJob(String zkServer, String outputDir, String znode) {
        this.zkServer = zkServer;
        this.outputDir = outputDir;
        this.znode = znode;
    }

    public void go() {
        System.out.println("dumping data from zookeeper");
        System.out.println("zookeeper server: " + zkServer);
        System.out.println("reading from zookeeper path: " + znode);
        System.out.println("dumping to local directory: " + outputDir);

        try {
            ZooKeeper zk = new ZooKeeper(zkServer + znode, 10000, this);
            go(zk);
        } catch (IOException e) {
            System.err.println("error connecting to " + zkServer);
            System.exit(1);
        }
    }

    private void go(ZooKeeper zk) {
        try {
            while(!zk.getState().isConnected()) {
                System.out.println("connecting to " + zkServer + " with chroot " + znode);
                Thread.sleep(1000L);
            }
            dumpChild(zk, outputDir + znode, "", "");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void dumpChild(ZooKeeper zk, String outputPath, String znodeParent, String znode) throws Exception {

        String znodePath = znodeParent + znode;

        System.out.println("znodePath: " + znodePath);
        System.out.println("outputPath: " + outputPath);
        String currznode = znodePath.length() == 0 ? "/" : znodePath;
        List<String> children = zk.getChildren(currznode, false);
        if(!children.isEmpty()) {

            // ensure parent dir is created
            File f = new File(outputPath);
            boolean s = f.mkdirs();

            // this znode is a dir, so ensure the directory is created and build a __znode value in its dir
            writeZnode(zk, outputPath + "/_znode", currznode);

            for(String c : children) {
                System.out.println("c: " + c);
                dumpChild(zk, outputPath + "/" + c, znodePath + "/", c);
            }
        }
        else {
            // this znode has no contents to write a plan file with the znode contents here
            writeZnode(zk, outputPath, currznode);
        }
    }

    private void writeZnode(ZooKeeper zk, String outFile, String znode) throws Exception {
        Stat stat = new Stat();
        byte[] data = zk.getData(znode, false, stat);

        FileOutputStream out = new FileOutputStream(outFile);

        if(data != null && data.length > 0 && stat.getEphemeralOwner() == 0) {
            String str = new String(data); //Also what about data that can't be string? are there such cases?
            if(!str.equals("null")) {
                out.write(data);
            }
        }

        out.flush();
        out.close();
    }


    @Override
    public void process(WatchedEvent watchedEvent) {}
}
