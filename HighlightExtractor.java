import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.util.ArrayList;

public class HighlightExtractor {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    static String filename = "hockey_full_test.mp4";
    static VideoCapture video = new VideoCapture(filename);
    static InfoExtractor infoExtractor = new InfoExtractor();
    static String team1Name = "";
    static String team2Name = "";
    static int team1Score = 0;
    static int team2Score = 0;
    static int framesToSkip = 20000;
    static ArrayList<Integer> scoreChangeFrames = new ArrayList<Integer>();

    public static void main(String[] args) {

        getTeamInfoAndScoreChangeFrames();

        // extractHighlightVideos();

        System.exit(0);
    }

    private static void extractHighlightVideos() {
        int[] s = {18370, 153209, 167735};
        //int[] s = {18377};
        //int[] s = {18492, 18875, 153393, 154866, 167829, 168401};
        Mat frame = new Mat();
        Mat prev = new Mat();
        for (Integer frameNum : s) {
            video = new VideoCapture(filename);
            video.set(Videoio.CAP_PROP_POS_FRAMES, frameNum);

            video.read(prev);
            prev = prev.submat(200, prev.rows() - 200, 400, prev.cols() - 400);
            //prev = prev.submat(0, prev.rows(), 0, prev.cols());
            Imgproc.cvtColor(prev, prev, Imgproc.COLOR_BGR2GRAY);
            int frameDuration = 1800; // Check the 1 mins following a score for a replay
            double[] diffArray = new double[frameDuration];
            while (video.isOpened()) {
                if (video.read(frame) && (int) video.get(Videoio.CAP_PROP_POS_FRAMES) - frameNum < frameDuration) {
                    frame = frame.submat(200, frame.rows() - 200, 400, frame.cols() - 400);
                    //frame = frame.submat(0, frame.rows(), 0, frame.cols());
                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
                    //ImageUtils.display(frame, "frame");

                    double diff = 0;
                    for (int r = 0; r < frame.rows(); r++) {
                        for (int c = 0; c < frame.cols(); c++) {
                            diff += Math.abs(frame.get(r, c)[0] - prev.get(r, c)[0]);
                        }
                    }
                    diffArray[(int) video.get(Videoio.CAP_PROP_POS_FRAMES) - frameNum - 2] = diff / (frame.cols() * frame.rows());
                    //System.out.println(diff / (frame.cols() * frame.rows()));
                    prev = frame.clone();

                } else break;
            }

            int firstCutFrame = 0;
            double maxDiff = Double.MIN_VALUE;
            for (int i = 0; i < diffArray.length / 5; i++) {
                if (diffArray[i] > maxDiff) {
                    maxDiff = diffArray[i];
                    firstCutFrame = i;
                }
            }

            int secondCutFrame = firstCutFrame;
            maxDiff = Double.MIN_VALUE;
            for (int i = firstCutFrame + 100; i < diffArray.length; i++) {
                if (diffArray[i] > maxDiff) {
                    maxDiff = diffArray[i];
                    secondCutFrame = i;
                }
            }
            System.out.println("Start: " + (frameNum + firstCutFrame) + " End: " + (frameNum + secondCutFrame));
        }
    }

    private static void getTeamInfoAndScoreChangeFrames() {
        Mat frame = new Mat();
        while (video.isOpened()) {
            if (video.read(frame)) {
                double frameCount = video.get(Videoio.CAP_PROP_FRAME_COUNT);
                double posFrames = video.get(Videoio.CAP_PROP_POS_FRAMES);

                if (team1Name.equals("")) {
                    String[] teamNames = infoExtractor.extractTeamNames("hockey", frame);
                    if (teamNames[0] != null && teamNames[1] != null) {
                        team1Name = teamNames[0];
                        team2Name = teamNames[1];
                    }
                }

                int frameNum = -1;
                String[] scores = infoExtractor.extractScore("hockey", frame);
                if (scores[0] != null && scores[0].length() == 1 && Character.isDigit(scores[0].charAt(0)) && Integer.parseInt(scores[0]) - team1Score == 1) {
                    team1Score = Integer.parseInt(scores[0]);
                    System.out.println("Score: " + team1Score + "-" + team2Score);
                    frameNum = infoExtractor.extractScoreFrame("hockey", (int) video.get(Videoio.CAP_PROP_POS_FRAMES), filename, framesToSkip, team1Score, 0);
                } else if (scores[1] != null && scores[1].length() == 1 && Character.isDigit(scores[1].charAt(0)) && Integer.parseInt(scores[1]) - team2Score == 1) {
                    team2Score = Integer.parseInt(scores[1]);
                    System.out.println("Score: " + team1Score + "-" + team2Score);
                    frameNum = infoExtractor.extractScoreFrame("hockey", (int) video.get(Videoio.CAP_PROP_POS_FRAMES), filename, framesToSkip, team2Score, 1);
                }

                if (frameNum > 0) scoreChangeFrames.add(frameNum);

                if (frameCount - posFrames < framesToSkip) break;
                video.set(Videoio.CAP_PROP_POS_FRAMES, posFrames + framesToSkip);

            }
        }
    }


}