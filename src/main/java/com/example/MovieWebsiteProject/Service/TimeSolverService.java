package com.example.MovieWebsiteProject.Service;

import org.springframework.stereotype.Service;

@Service
public class TimeSolverService {

    public double convertTimeStringToSeconds(String timeString) {
        double seconds = 0;

        // Hours
        if (timeString.contains("h")) {
            int hIndex = timeString.indexOf("h");
            seconds += Double.parseDouble(timeString.substring(0, hIndex)) * 3600;
            timeString = timeString.substring(hIndex + 1);
        }

        // Minutes (p = phút)
        if (timeString.contains("p")) {
            int pIndex = timeString.indexOf("p");
            seconds += Double.parseDouble(timeString.substring(0, pIndex)) * 60;
            timeString = timeString.substring(pIndex + 1);
        }

        // Seconds
        if (timeString.contains("s")) {
            int sIndex = timeString.indexOf("s");
            seconds += Double.parseDouble(timeString.substring(0, sIndex));
        }

        return seconds;
    }

    public String convertSecondsToTimeString(double totalSeconds) {
        int hours = (int) (totalSeconds / 3600);
        int minutes = (int) ((totalSeconds % 3600) / 60);
        double seconds = totalSeconds % 60;

        StringBuilder result = new StringBuilder();

        if (hours > 0) {
            result.append(hours).append("h");
        }
        if (minutes > 0) {
            result.append(minutes).append("p");
        }
        if (seconds > 0 || result.isEmpty()) {
            result.append(String.format("%.2f", seconds)).append("s");
        }

        return result.toString();
    }
}
