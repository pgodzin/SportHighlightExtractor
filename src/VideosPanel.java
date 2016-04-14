import javax.swing.*;
import java.awt.*;

/**
 * Created by sneha on 3/31/16.
 */
public class VideosPanel extends JPanel{

    private JList<VideoEntry> allEntries;

    public void paintComponent(Graphics g){
        allEntries = null;
        Graphics2D g2= (Graphics2D) g;
        g2.drawOval(0, 0, getWidth(), getHeight());
    }
}
