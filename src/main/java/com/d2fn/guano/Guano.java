package com.d2fn.guano;

import org.apache.commons.cli.*;

/**
 * Guano
 * @author Dietrich Featherston
 */
public class Guano implements Job {

    private Options options;
    private CommandLine cmd;

    private Job job;

    public Guano(Options options, CommandLine cmd) {
//        buildJob(cmd)
        this.options = options;
        this.cmd = cmd;
        buildJob();
    }

    public void buildJob() {

        boolean verbose = false;

        if(cmd.hasOption("v")) {
            verbose = true;
        }

        if(!cmd.hasOption("s")) {
            usage(options);
        }

        String server = cmd.getOptionValue("s");

        // dump?
        if(cmd.hasOption("d") && cmd.hasOption("o")) {
            String znode = cmd.getOptionValue("d");
            String outputDir = cmd.getOptionValue("o");
            job = new DumpJob(server, outputDir, znode);
        }
        // restore
        else if(cmd.hasOption("r") && cmd.hasOption("i")) {
            String znode = cmd.getOptionValue("r");
            String inputDir = cmd.getOptionValue("i");
            job = new RestoreJob(server, znode, inputDir);
        }
        else {
            usage(options);
        }
    }

    @Override
    public void go() {
        job.go();
    }

    public static void main(String[] args) {
        Options options = initOptions();
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            usage(options);
        }

        Guano g = new Guano(options, cmd);
        g.go();
    }

    public static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("guano", options);
        System.exit(1);
    }

    public static Options initOptions() {
        Options options = new Options();
        options.addOption("s", "server",            true,  "the zookeeper remote server to connect to (ie \"localhost:2181\"");
        options.addOption("r", "restore-znode",     true,  "the znode into which read data should be restored");
        options.addOption("d", "dump-znode",        true,  "the znode to dump (recursively)");
        options.addOption("o", "output-dir",        true,  "the output directory to which znode information should be written (must be a normal, empty directory)");
        options.addOption("i", "input-dir",         true,  "the input directory from which znode information should be read");
        options.addOption("v", "verbose",           false, "enable debug output");
        return options;
    }
}