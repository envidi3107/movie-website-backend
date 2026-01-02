package com.example.MovieWebsiteProject.Service;

import org.springframework.stereotype.Service;

@Service
public class TimeSolverService {

    public double convertTimeStringToSeconds(String timeString) {
        String[] parts = timeString.split(":");
        double seconds = 0;

        if (parts.length == 3) { // HH:MM:SS
            seconds += Integer.parseInt(parts[0]) * 3600; // Hours to seconds
            seconds += Integer.parseInt(parts[1]) * 60;   // Minutes to seconds
            seconds += Double.parseDouble(parts[2]);       // Seconds
        } else if (parts.length == 2) { // MM:SS
            seconds += Integer.parseInt(parts[0]) * 60;   // Minutes to seconds
            seconds += Double.parseDouble(parts[1]);       // Seconds
        } else if (parts.length == 1) { // SS
            seconds += Double.parseDouble(parts[0]);       // Seconds
        }

        return seconds;
    }

    public String convertSecondsToTimeString(double totalSeconds) {
        int hours = (int) totalSeconds / 3600;
        int minutes = ((int) totalSeconds % 3600) / 60;
        double seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%05.2f", hours, minutes, seconds);
        } else {
            return String.format("%02d:%05.2f", minutes, seconds);
        }
    }
}
