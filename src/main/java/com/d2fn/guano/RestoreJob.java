package com.d2fn.guano;

/**
 * RestoreJob
 * @author Dietrich Featherston
 */
public class RestoreJob implements Job {

    private String zkServer;
    private String znode;
    private String inputDir;

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
    }
}
