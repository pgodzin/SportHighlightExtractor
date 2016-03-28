import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

public class Extractor {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    static String filename = "hockey1.mp4";
    static VideoCapture video = new VideoCapture(filename);
    static Tesseract ocr = new Tesseract();
    static HashSet<String> abbreviationSet;
    static String team1Name = "";
    static String team2Name = "";
    static int team1Score = 0;
    static int team2Score = 0;

    public static void main(String[] args) {

        Mat frame = new Mat();
        initTeamAbbreviationSet();

        ArrayList<Integer> scoreChangeFrames = new ArrayList<Integer>();

        while (video.isOpened()) {
            if (video.read(frame)) {
                double frameCount = video.get(Videoio.CAP_PROP_FRAME_COUNT);
                double posFrames = video.get(Videoio.CAP_PROP_POS_FRAMES);

                int frameNum = extractHockeyScores(frame);
                if(frameNum > 0) scoreChangeFrames.add(frameNum);

                if (frameCount - posFrames < 500) video.release();
                for (int i = 0; i < 500; i++) video.grab();

            }
        }

/*
        int[] s =  {2556, 8518, 9520, 17536};
        for (Integer frameNum : s){
            video = new VideoCapture(filename);
            for (int i = 0; i < frameNum; i++) video.grab();

            Mat prev = new Mat();
            video.read(prev);
            //prev = prev.submat(0, prev.rows(), 0, prev.cols());
            Imgproc.cvtColor(prev, prev, Imgproc.COLOR_BGR2GRAY);
            System.out.println(video.get(Videoio.CAP_PROP_POS_FRAMES));
            while (video.isOpened()) {
                if (video.read(frame)) {
                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
                    // Mat flow = prev.clone();
                    //frame = frame.submat(0, frame.rows(), 0, frame.cols());
                    BufferedImage img = Mat2BufferedImage(frame);
                    displayImage(img, "frame");
                    System.out.println(video.get(Videoio.CAP_PROP_POS_FRAMES));

                    */
/*Video.calcOpticalFlowFarneback(prev.submat(150, 350, 200, 600), frame.submat(150, 350, 200, 600),
                            flow, .5, 1, 20, 1, 5, 1.1, Video.OPTFLOW_FARNEBACK_GAUSSIAN);
                    *//*

                    double diff = 0;
                    for(int r = 0; r < frame.rows(); r++){
                        for(int c = 0; c < frame.cols(); c++){
                            diff += Math.abs(frame.get(r, c)[0] - prev.get(r, c)[0]);
                        }
                    }
                    System.out.println(diff);
                    prev = frame;
                    BufferedImage img1 = Mat2BufferedImage(prev);
                    displayImage(img1, "prev");


                }
            }

        }
*/

        System.exit(0);
    }

    private static void initTeamAbbreviationSet() {
        abbreviationSet = new HashSet<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("team_abbreviations.txt")));
            String abbreviations = br.readLine();
            String[] abbrevArr = abbreviations.split(" ");
            for (String a : abbrevArr) abbreviationSet.add(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String correctOCR(String s) {
        s = s.replace(".", "");
        s = s.replace("-", "");
        s = s.replace("—", "");
        s = s.replace("_", "");
        s = s.replace("'", "");
        s = s.replace("`", "");
        s = s.replace("‘", "");
        s = s.replace("\"", "");
        if (s.equals("T")) return "1";
        else if (s.equals("I")) return "1";
        else if (s.equals("i")) return "1";
        else if (s.equals("l")) return "1";
        else if (s.equals("L")) return "1";
        else if (s.equals("D")) return "0";
        else if (s.equals("O")) return "0";
        else if (s.equals("o")) return "0";
        else if (s.equals("A")) return "4";
        return s;
    }

    public static boolean validTeamName(String name) {
        if (name.length() != 2 && name.length() != 3) return false;
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isUpperCase(name.charAt(i))) return false;
        }
        if (!abbreviationSet.contains(name)) return false;
        return true;
    }

    public static int extractHockeyScores(Mat frame) {
        Mat scores = frame.submat(40, 80, 108, 135);
        scores = adjustScoreMat(scores);


        try {

            if (team1Name.equals("")) {
                Mat teams = frame.submat(43, 80, 50, 100);
                teams = adjustTeamMat(teams);

                String teamsText = ocr.doOCR(Mat2BufferedImage(teams));
                //System.out.println(teamsText);

                if (teamsText.split("\\n").length == 2) {
                    String team1 = teamsText.split("\\n")[0];
                    String team2 = teamsText.split("\\n")[1];
                    team1 = team1.replace("NV", "NY");
                    team2 = team2.replace("NV", "NY");
                    if (validTeamName(team1) && validTeamName(team2)) {
                        team1Name = team1;
                        team2Name = team2;
                        System.out.println("Teams: " + team1 + " and " + team2);
                    }
                }
            }

            String scoresText = ocr.doOCR(Mat2BufferedImage(scores));
            //System.out.println(scoresText);

            if (scoresText.split("\\n").length == 2) {
                String score1 = scoresText.split("\\n")[0];
                String score2 = scoresText.split("\\n")[1];
                score1 = correctOCR(score1);
                score2 = correctOCR(score2);
                //System.out.println(score1 + " -- " + score2);

                if (score1.length() == 1 && Character.isDigit(score1.charAt(0)) && Integer.parseInt(score1) - team1Score == 1) {
                    team1Score = Integer.parseInt(score1);
                    System.out.println("Score: " + team1Score + "-" + team2Score);
                    return (int) video.get(Videoio.CAP_PROP_POS_FRAMES);
                } else if (score2.length() == 1 && Character.isDigit(score2.charAt(0)) && Integer.parseInt(score2) - team2Score == 1) {
                    team2Score = Integer.parseInt(score2);
                    System.out.println("Score: " + team1Score + "-" + team2Score);
                    return (int) video.get(Videoio.CAP_PROP_POS_FRAMES);
                }
            }
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static Mat adjustScoreMat(Mat scores) {
        Imgproc.cvtColor(scores, scores, Imgproc.COLOR_BGR2GRAY);
        Imgproc.resize(scores, scores, new Size(scores.width() * 3, scores.height() * 3));
        double sumTop = 0;
        double sumBot = 0;
        for (int i = 0; i < 20; i++) {
            for (int c = 0; c < scores.cols(); c++) {
                sumTop += scores.get(i, c)[0];
                sumBot += scores.get(scores.rows() - i - 1, c)[0];
            }
        }
        sumTop /= (scores.cols() * 20);
        sumBot /= (scores.cols() * 20);
        //System.out.println(sum);
        if (sumTop < 120) scores = scores.submat(20, scores.rows(), 0, scores.cols());
        if (sumBot < 120) scores = scores.submat(0, scores.rows() - 20, 0, scores.cols());
        return scores;
    }

    private static Mat adjustTeamMat(Mat teams) {
        Imgproc.cvtColor(teams, teams, Imgproc.COLOR_BGR2GRAY);
        Imgproc.resize(teams, teams, new Size(teams.width() * 3, teams.height() * 3));
        double sumTop = 0;
        for (int i = 0; i < 10; i++) {
            for (int c = 0; c < teams.cols(); c++) {
                sumTop += teams.get(i, c)[0];
            }
        }
        sumTop /= (teams.cols() * 10);
        //System.out.println(sumTop);
        if (sumTop < 80) teams = teams.submat(10, teams.rows(), 0, teams.cols());
        return teams;
    }

    public static void extractFootballScores() {
                        /* Mat board = frame.submat(40, 120, 115, 210); // Whole scoreboard
                Mat fox = frame.submat(650, 680, 1100, 1170); // Whole scoreboard
                Mat team1Score = frame.submat(60, 90, 115, 160);  // First score
                Mat team1Name  = frame.submat(40, 60, 115, 160);  // First team
                Mat team2Score  = frame.submat(60, 90, 170, 215); // Second score
                Mat team2Name = frame.submat(40, 60, 170, 215);  // Second team

                Imgproc.cvtColor(board, board, Imgproc.COLOR_BGR2GRAY);
                Imgproc.cvtColor(team1Score, team1Score, Imgproc.COLOR_BGR2GRAY);
                Imgproc.cvtColor(team2Score, team2Score, Imgproc.COLOR_BGR2GRAY);
                Imgproc.cvtColor(team1Name, team1Name, Imgproc.COLOR_BGR2GRAY);
                Imgproc.cvtColor(team2Name, team2Name, Imgproc.COLOR_BGR2GRAY);*/
    }

    public static BufferedImage Mat2BufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;

    }

    public static void displayImage(Image img2, String title) {
        JFrame f = new JFrame();
        f.setTitle(title);
        ImageIcon icon = new ImageIcon(img2);
        f.setLayout(new FlowLayout());
        f.setSize(img2.getWidth(null) + 50, img2.getHeight(null) + 50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        f.add(lbl);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}