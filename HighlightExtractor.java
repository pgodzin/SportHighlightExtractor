import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import java.util.ArrayList;
import java.util.Arrays;

public class HighlightExtractor {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    static String filename = "hockey_full_test.mp4";
    static InfoExtractor infoExtractor = new InfoExtractor();
    static String team1Name = "";
    static String team2Name = "";
    static int team1Score = 0;
    static int team2Score = 0;
    static int framesToSkip = 5000;
    static ArrayList<Integer> scoreChangeFrames = new ArrayList<Integer>();

    public static void main(String[] args) {

        //getTeamInfoAndScoreChangeFrames();

        extractHighlightVideos();

        // System.exit(0);
    }

    private static void extractHighlightVideos() {


        //final int[] s = {18470};
        final int[] s = {18239, 151663, 167535};
        //int[] s = {35727, 63416, 75017, 137841};

        final MatOfFloat ranges = new MatOfFloat(0f, 256f, 0f, 256f, 0f, 256f);
        final MatOfInt histSize = new MatOfInt(8, 8, 8);
        final MatOfInt channels = new MatOfInt(0, 1, 2);

        for (final Integer frameNum : s) {
            extractVideoWithScoreboard(frameNum);
            /*new Thread() {
                public void run() {
                    //extractVideo(frameNum, channels, histSize, ranges);
                }
            }.start();*/
        }
    }

    private static void extractVideoWithScoreboard(int frameNum) {
        Mat frame = new Mat();
        int frameDuration = 2400;
        System.out.println("Extracting " + frameNum);
        VideoCapture v = new VideoCapture(filename);
        v.set(Videoio.CAP_PROP_POS_FRAMES, frameNum);
        boolean boardSeen = false;
        boolean startIdentified = false;
        int firstCutFrame = 0;
        int secondCutFrame = 0;

        while (v.isOpened()) {
            if (v.read(frame) && (int) v.get(Videoio.CAP_PROP_POS_FRAMES) - frameNum < frameDuration) {
                //frame = frame.submat(0, frame.rows(), 0, frame.cols());
                ImageUtils.display(frame, "frame");
                String[] names = infoExtractor.extractHockeyTeamNames(frame);
                if (!boardSeen && names[0] != null && names[1] != null) {
                    boardSeen = true;
                    System.out.println("Board seen");
                } else if (boardSeen && !startIdentified && names[0] == null && names[1] == null) {
                    firstCutFrame = (int) v.get(Videoio.CAP_PROP_POS_FRAMES) - frameNum;
                    startIdentified = true;
                    System.out.println("Start identified");
                } else if (startIdentified && names[0] != null && names[1] != null) {
                    secondCutFrame = (int) v.get(Videoio.CAP_PROP_POS_FRAMES) - frameNum;
                    System.out.println("End identified");
                    break;
                }
                double frameCount = v.get(Videoio.CAP_PROP_FRAME_COUNT);
                double posFrames = v.get(Videoio.CAP_PROP_POS_FRAMES);
                if (frameCount - posFrames < 30) break;
                v.set(Videoio.CAP_PROP_POS_FRAMES, posFrames + 30);
            } else break;
        }

        writeHighlight(frameNum, frame.size(), firstCutFrame, secondCutFrame);

        System.out.println("Start: " + (frameNum + firstCutFrame) + " End: " + (frameNum + secondCutFrame));

        v.release();
    }

    private static void extractVideo(int frameNum, MatOfInt channels, MatOfInt histSize, MatOfFloat ranges) {
        Mat frame = new Mat();
        Mat prev = new Mat();
        Mat histPrev = new Mat();
        Mat histFrame = new Mat();
        System.out.println("Extracting " + frameNum);
        double diff = 0;
        VideoCapture v = new VideoCapture(filename);
        v.set(Videoio.CAP_PROP_POS_FRAMES, frameNum);

        v.read(prev);
        prev = prev.submat(0, prev.rows(), 0, prev.cols());
        //prev = prev.submat(125, prev.rows() - 125, 250, prev.cols() - 250);
        //Imgproc.cvtColor(prev, prev, Imgproc.COLOR_BGR2HSV);
        int frameDuration = 2400; // Check the 1 mins following a score for a replay
        double[] diffHistArray = new double[frameDuration];
        double[] diffMotionArray = new double[frameDuration];

        while (v.isOpened()) {
            if (v.read(frame) && (int) v.get(Videoio.CAP_PROP_POS_FRAMES) - frameNum < frameDuration) {
                frame = frame.submat(0, frame.rows(), 0, frame.cols());
                ImageUtils.display(frame, "frame");
                //frame = frame.submat(125, frame.rows() - 125, 250, frame.cols() - 250);
                //Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);


                Imgproc.calcHist(Arrays.asList(prev), channels, new Mat(), histPrev, histSize, ranges);
                Imgproc.calcHist(Arrays.asList(frame), channels, new Mat(), histFrame, histSize, ranges);
                diff = Imgproc.compareHist(histPrev, histFrame, Imgproc.CV_COMP_CHISQR);
                double diff2 = 0;
                for (int r = 0; r < frame.rows(); r++) {
                    for (int c = 0; c < frame.cols(); c++) {
                        //double f = frame.get(r, c)[0];
                        diff2 += Math.abs(frame.get(r, c)[0] - prev.get(r, c)[0]);
                    }
                }
                diff /= (frame.cols() * frame.rows());
                diff2 /= (frame.cols() * frame.rows());
                int index = (int) (v.get(Videoio.CAP_PROP_POS_FRAMES) - frameNum - 2);
                diffHistArray[index] = diff;
                diffMotionArray[index] = diff2;
                if (diff > 100) {
                    ImageUtils.display(frame, "frame");
                    System.out.println((int) (v.get(Videoio.CAP_PROP_POS_FRAMES) - frameNum - 2) + ": " + diff + " & " + diff2);
                        /*Imgproc.cvtColor(prev, prev, Imgproc.COLOR_HSV2BGR);
                        ImageUtils.display(prev, "frame");
                        Mat filteredMat = new Mat();
                        Core.inRange(prev, new Scalar(40, 40, 40), new Scalar(256, 256, 256), filteredMat);
                        double whitePixels = Core.countNonZero(filteredMat);
                        ImageUtils.display(filteredMat, "frame");
                        System.out.println(whitePixels / (frame.rows() * frame.cols()));*/
                }
                prev = frame.clone();
            } else break;
        }

        new CutDetectionLineChart(diffHistArray);

        int firstCutFrame = 0;
        double maxDiff = Double.MIN_VALUE;
        for (int i = 0; i < diffHistArray.length / 3; i++) {
            if (diffHistArray[i] > 100 && diffMotionArray[i] > 40) {
                //maxDiff = diffHistArray[i];
                firstCutFrame = i;
                break;
            }
        }

        int secondCutFrame = firstCutFrame;
        maxDiff = Double.MIN_VALUE;
        for (int i = firstCutFrame + 250; i < diffHistArray.length; i++) {
            if (diffHistArray[i] > 160 && diffHistArray[i] < 2000 && diffMotionArray[i] > 40) {
                //maxDiff = diffHistArray[i];
                secondCutFrame = i;
                break;
            }
        }

        writeHighlight(frameNum, frame.size(), firstCutFrame, secondCutFrame);

        System.out.println("Start: " + (frameNum + firstCutFrame) + " End: " + (frameNum + secondCutFrame));
        v.release();
    }

    private static void writeHighlight(final int frameNum, final Size frameSize, final int firstCutFrame,
                                       final int secondCutFrame) {
        new Thread() {
            public void run() {
                System.out.println("Writing highlight...");
                VideoCapture capture = new VideoCapture(filename);
                Mat frame = new Mat();
                VideoWriter writer = new VideoWriter(frameNum + "SB.avi", VideoWriter.fourcc('F', 'M', 'P', '4'), 30, frameSize);
                capture.set(Videoio.CAP_PROP_POS_FRAMES, frameNum + firstCutFrame);
                while (capture.get(Videoio.CAP_PROP_POS_FRAMES) < frameNum + secondCutFrame) {
                    capture.read(frame);
                    writer.write(frame);
                }
                System.out.println("Highlight is ready");
                writer.release();
            }
        }.start();
    }

    private static void getTeamInfoAndScoreChangeFrames() {
        VideoCapture video = new VideoCapture(filename);
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
                    System.out.println("Score Change: " + team1Score + "-" + team2Score);
                    frameNum = infoExtractor.extractScoreFrame("hockey", (int) video.get(Videoio.CAP_PROP_POS_FRAMES), filename, framesToSkip, team1Score, 0);
                } else if (scores[1] != null && scores[1].length() == 1 && Character.isDigit(scores[1].charAt(0)) && Integer.parseInt(scores[1]) - team2Score == 1) {
                    team2Score = Integer.parseInt(scores[1]);
                    System.out.println("Score Change: " + team1Score + "-" + team2Score);
                    frameNum = infoExtractor.extractScoreFrame("hockey", (int) video.get(Videoio.CAP_PROP_POS_FRAMES), filename, framesToSkip, team2Score, 1);
                }

                if (frameNum > 0) scoreChangeFrames.add(frameNum);

                if (frameCount - posFrames < framesToSkip) break;
                video.set(Videoio.CAP_PROP_POS_FRAMES, posFrames + framesToSkip);

            }
        }
        video.release();
    }


}