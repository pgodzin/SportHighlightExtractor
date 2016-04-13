import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;

public class InfoExtractor {

    static Tesseract ocr;
    static HashSet<String> abbreviationSet;

    public InfoExtractor() {
        ocr = new Tesseract();
        initTeamAbbreviationSet();
    }

    public int extractScoreFrame(String sport, int currentFrameNum, String filename, int framesToSkip, int score, int whichTeam) {
        if (sport.equals("hockey")) {
            return extractHockeyScoreFrame(currentFrameNum, filename, framesToSkip, score, whichTeam);
        }
        return -1;
    }

    public String[] extractTeamNames(String sport, Mat frame) {
        if (sport.equals("hockey")) {
            return extractHockeyTeamNames(frame);
        }
        return new String[2];
    }

    public String[] extractScore(String sport, Mat frame) {
        if (sport.equals("hockey")) {
            return extractHockeyScore(frame);
        }
        return new String[2];
    }

    public String[] extractHockeyTeamNames(Mat frame) {
        String[] teamNames = new String[2];
        try {
            Mat teams = frame.submat(43, 80, 50, 100);
            teams = adjustTeamMat(teams);

            String teamsText = ocr.doOCR(ImageUtils.Mat2BufferedImage(teams));
            //System.out.println(teamsText);

            if (teamsText.split("\\n").length == 2) {
                String team1 = correctOCR(teamsText.split("\\n")[0]);
                String team2 = correctOCR(teamsText.split("\\n")[1]);
                team1 = team1.replace("NV", "NY");
                team2 = team2.replace("NV", "NY");
                //System.out.println(team1 + " & " + team2);
                if (validTeamName(team1) && validTeamName(team2)) {
                    teamNames[0] = team1;
                    teamNames[1] = team2;
                    System.out.println("Teams: " + team1 + " and " + team2);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return teamNames;
    }

    private boolean validTeamName(String name) {
        if (name.length() != 2 && name.length() != 3) return false;
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isUpperCase(name.charAt(i))) return false;
        }
        if (!abbreviationSet.contains(name)) return false;
        return true;
    }

    private String[] extractHockeyScore(Mat frame) {
        String[] scores = new String[2];

        Mat scoreMat = frame.submat(40, 80, 110, 135);
        scoreMat = adjustScoreMat(scoreMat);
        //ImageUtils.display(scoreMat, "score");

        try {

            String scoresText = ocr.doOCR(ImageUtils.Mat2BufferedImage(scoreMat));
            //System.out.println(scoresText);

            if (scoresText.split("\\n").length == 2) {
                String score1 = scoresText.split("\\n")[0];
                String score2 = scoresText.split("\\n")[1];
                scores[0] = correctOCR(score1);
                scores[1] = correctOCR(score2);
                //System.out.println(score1 + " -- " + score2);
                //System.out.println(score1 + " -- " + score2);
            }
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return scores;
    }

    private int extractHockeyScoreFrame(int frameNum, String filename, int framesToSkip, int score, int whichTeam) {
        System.out.println("Searching for score change frame...");
        int scoreFrameNum = binarySearchForScoreFrame(filename, frameNum - framesToSkip, frameNum, score, whichTeam);
        System.out.println("Found: " + scoreFrameNum);
        return scoreFrameNum;
    }


    private int binarySearchForScoreFrame(String filename, int frameStart, int frameEnd, int score, int whichTeam) {

        VideoCapture video = new VideoCapture(filename);
        if (Math.abs(frameEnd - frameStart) < 100) {
            video.release();
            return frameStart;
        } else {
            int mid = (frameStart + frameEnd) / 2;
            video.set(Videoio.CAP_PROP_POS_FRAMES, mid);
            Mat frame = new Mat();
            video.read(frame);
            //ImageUtils.display(frame, "binary_score");
            try {
                if (frame.dims() == 0) {
                    video.read(frame);
                    //System.out.println("dim issues!");
                }
                Mat goal = frame.submat(55, 80, 70, 150);
                //ImageUtils.display(goal, "goal");
                //System.out.println(correctOCR(ocr.doOCR(ImageUtils.Mat2BufferedImage(goal))));
                if (correctOCR(ocr.doOCR(ImageUtils.Mat2BufferedImage(goal))).equals("GOAL")) {
                    System.out.println("goal identified");
                    video.release();
                    return mid;
                }

                Mat scores = frame.submat(40, 80, 110, 135);
                scores = adjustScoreMat(scores);
                //ImageUtils.display(scores, "binary_score");
                String scoresText = ocr.doOCR(ImageUtils.Mat2BufferedImage(scores));
                //System.out.println(scoresText);
                if (scoresText.split("\\n").length == 2) {
                    String newScore = scoresText.split("\\n")[whichTeam];
                    newScore = correctOCR(newScore);
                    if (newScore.length() == 1 && Character.isDigit(newScore.charAt(0)) && Integer.parseInt(newScore) == score) {
                        video.release();
                        return binarySearchForScoreFrame(filename, frameStart, mid, score, whichTeam);
                    } else if (newScore.length() == 1 && Character.isDigit(newScore.charAt(0)) && score - Integer.parseInt(newScore) == 1) {
                        video.release();
                        return binarySearchForScoreFrame(filename, mid, frameEnd, score, whichTeam);
                    } else {
                        video.release();
                        return binarySearchForScoreFrame(filename, frameStart, frameEnd - 50, score, whichTeam);
                    }
                } else {
                    video.release();
                    return binarySearchForScoreFrame(filename, frameStart, frameEnd - 50, score, whichTeam);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        video.release();
        return -1;
    }

    private Mat adjustScoreMat(Mat scores) {
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

    private Mat adjustTeamMat(Mat teams) {
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

    private void initTeamAbbreviationSet() {
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

    private String correctOCR(String s) {
        s = s.replace(".", "");
        s = s.replace("-", "");
        s = s.replace("—", "");
        s = s.replace("_", "");
        s = s.replace("'", "");
        s = s.replace("|", "");
        s = s.replace("`", "");
        s = s.replace("‘", "");
        s = s.replace("\"", "");
        s = s.replace("\n", "");
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

    public static void extractFootballScore() {
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
}
