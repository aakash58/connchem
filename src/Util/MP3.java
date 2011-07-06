package Util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.Player;


public class MP3 {
    private static String filename = "src/resources/sounds/Glass.mp3";
    private static Player player; 

   
    public void close() { if (player != null) player.close(); }

    public static  void play(int id) {
            try {
            if (id==1) 
             	filename = "src/resources/sounds/Ping.mp3";
            else if (id==2) 
             	filename = "src/resources/sounds/Fire.mp3";
            else if (id==3) 
            	filename = "src/resources/sounds/Ping4.mp3";
            else
            	filename = "src/resources/sounds/Glass.mp3";
            
            FileInputStream fis     = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
        }
        catch (Exception e) {
            System.out.println("Problem playing file " + filename);
            System.out.println(e);
        }
    	    
        new Thread() {
            public void run() {
            	//System.out.println("playing file " + filename);  
                try { player.play(); }
                catch (Exception e) { System.out.println("Exeption while playing MIDI: "+e); }
            }
        }.start();
    }

}

