package util;

import java.io.*;
//import javazoom.jl.player.Player;

public class AudioPlayer {
    
//    private Player player;
//    private AudioThread thread;
//    
//    public boolean repeat = true;
//    private boolean shouldStart = false;
//    private String fpath;
//    
//    public void loadFile(String path) {
//        fpath = path;
//        start();
//    }
//    
//    public void queueFile(String path) {
//        fpath = path;
//    }
//    
//    public synchronized void start() {
//        stop();
//        
//        try {
//            player = new Player(new BufferedInputStream(new FileInputStream(FileInput.platformFilePath(fpath))));
//        } catch(Exception e) {
//            try {
//                player = new Player(new BufferedInputStream(new FileInputStream(FileInput.platformFilePath("..\\" + fpath))));
//            } catch(Exception e2) {
//                System.err.println("FAILED TO LOAD AUDIO FILE: " + fpath);
//                player = null;
//                return;
//            }
//        }
//        
//        thread = new AudioThread(this, player);
//        thread.start();
//        
//        shouldStart = false;
//    }
//    
//    public synchronized void Update()
//    {
//     if (shouldStart)
//     {
//      start();
//     }
//    }
//    
//    private synchronized void finished() {
//        if (repeat)
//        {
//         shouldStart = true;
//        }
//    }
//    
//    public void stop() {
//     if (thread != null) thread.stop();
//        if(player != null) {player.close();}
//    }
//    
//    //just a thread which may call upon the player to restart on completion
//    class AudioThread extends Thread {
//        private final AudioPlayer master;
//        private Player player;
//        
//        public AudioThread(AudioPlayer ap, Player p) {
//            master = ap;
//            player = p;
//        }
//        
//        public void run() {
//            try
//            {
//          player.play();
//          master.finished();
//            }
//            catch (Exception e) { e.printStackTrace(); }
//        }
//    }
}
