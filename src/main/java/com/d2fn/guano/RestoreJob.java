package com.d2fn.guano;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * RestoreJob
 * @author Dietrich Featherston
 */
public class RestoreJob implements Job, Watcher, FSVisitor {

    private String zkServer;
    private String znode;
    private String inputDir;

    private ZooKeeper zk;

    public RestoreJob(String zkServer, String znode, String inputDir) {
        this.zkServer = zkServer;
        this.znode = znode;
        this.inputDir = inputDir;
    }

    public void go() {
        System.out.println("running restore job: ");
        System.out.println("zookeeper server: " + zkServer);
        System.out.println("reading from local directory: " + inputDir);
        System.out.println("restoring to zookeeper path: " + znode);

        try {
            zk = new ZooKeeper(zkServer + znode, 10000, this);
            while(!zk.getState().isConnected()) {
                System.out.println("connecting to " + zkServer + " with chroot " + znode);
                Thread.sleep(1000L);
            }
            FSWalker walker = new FSWalker(inputDir, "/");
            walker.go(this);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * on the left we have data read from a file on the local file system
     *
     * @param f
     * @param data
     * @param znode
     */
    @Override
    public void visit(File f, byte[] data, String znode) {
//        System.out.println(f.getPath());
//        System.out.println(" -> " + znode);
        createOrSetZnode(data, znode);
    }

    private void createOrSetZnode(byte[] data, String znode) {
        try {
            String s = zk.create(znode, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException.NoNodeException e) {
            e.printStackTrace(System.err);
        } catch (KeeperException.NodeExistsException e) {
            try {
                byte[] zdata = zk.getData(znode, false, null);
                if(!Arrays.equals(data, zdata)) {
                    zk.setData(znode, data, -1);
                }
            } catch (Exception e2) {
                e2.printStackTrace(System.err);
            }
        } catch (KeeperException e) {
            e.printStackTrace(System.err);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        ;;
    }
}
