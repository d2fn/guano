[ ![Download](https://api.bintray.com/packages/feldoh/Guano/guano/images/download.svg) ](https://bintray.com/feldoh/Guano/guano/_latestVersion)

NAME

     guano -- dump or restore Zookeeper trees

SYNOPSIS

     guano [operands ...]

DESCRIPTION

     This tool dumps and restores zookeeper state to/from a matching folder structure on disk.
     The server argument is mandatory then either a node to dump and where to dump it,
     or a previous dump to restore and the root to restore it to.

     usage: guano [-v] -s <zk_connect> [-d <znode> -o <folder>] [-r <znode-root> -i <folder>]

      -s,--server <arg>          *Required, the zookeeper remote server to connect to
                                 i.e. "localhost:2181"
                                 
      -d,--dump-znode <arg>      the znode to dump (recursively)
      -o,--output-dir <arg>      the output directory to which znode
                                 information should be written (must be a
                                 normal, empty directory)
                                 
      -i,--input-dir <arg>       the input directory from which znode
                                 information should be read
      -r,--restore-znode <arg>   the znode into which read data should be
                                 restored

      -v,--verbose               enable debug output

      Note: When restoring you need to provide one level up as the node selected for the dump is included.
        e.g. java -jar target/guano-0.1a.jar -s "zookeeper-01.mydomain.com" -o "/tmp/zkdump" -d "/myroot"
             java -jar target/guano-0.1a.jar -s "zookeeper-01.mydomain.com" -i "/tmp/zkdump" -r "/"
             
PREBUILT BINARIES

     Pre-built binaries are available for most common platforms on Bintray
