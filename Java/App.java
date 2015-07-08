package JolleeB.BachelorT;

import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class App 
{
    public static void main( String[] args )
    {
    	System.out.println("Initializing..");
    	FrameGetter fg = new FrameGetter();
    	VideoCreator vc = new VideoCreator();
    	String fileStringInput = "../../Videos/AttentionShort.mp4";
    	double fps = fg.getFPS(fileStringInput);
    	ArrayList<BufferedImage> bufImList = fg.getFramesAsBufferedImages(0,fileStringInput,0, 100);
    	bufImList = MovementDetetor.detectWColorsIntSave(bufImList);
//    	vc.createMP4( bufImList.get(0).getWidth(),bufImList.get(0).getHeight(), fps, bufImList, "../../Videos/test5.mp4");
    	vc.encode(bufImList,"../../Videos/testXuggler.mp4");
    	System.out.println("done");
    	
        return;
    }
}
