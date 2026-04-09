import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Complaint {
    public final String id;
    public final String citizen;
    public final String area;
    public final String type; 
    public final int severity; 
    public final String severityLabel;
    public final String details;
    public final long timestamp;

    public Complaint(String citizen, String area, String type, int severity, String details) {
        this.id = UUID.randomUUID().toString().substring(0,8);
        this.citizen = citizen.trim();
        this.area = area.trim().toLowerCase();
        this.type = type.trim().toLowerCase();
        this.severity = severity;
        this.severityLabel = severityToLabel(severity);
        this.details = details.trim();
        this.timestamp = System.currentTimeMillis();
    }

    // Loading constructor
    public Complaint(String id, String citizen, String area, String type, int severity, long timestamp, String details) {
        this.id = id;
        this.citizen = citizen;
        this.area = area;
        this.type = type;
        this.severity = severity;
        this.severityLabel = severityToLabel(severity);
        this.timestamp = timestamp;
        this.details = details;
    }

    private static String severityToLabel(int s) {
        switch(s) {
            case 1: return "Critical";
            case 2: return "High";
            case 3: return "Medium";
            default: return "Low";
        }
    }

    public String summaryKey() {
        String shortDetails = details.length() > 50 ? details.substring(0,50).toLowerCase() : details.toLowerCase();
        return (area + "|" + type + "|" + shortDetails).replaceAll("\\s+"," ");
    }

    public String timeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }

    public String serializeLine() {
        String safeDetails = details.replace("|", " ");
        String safeCitizen = citizen.replace("|"," ");
        return String.format("%s|%s|%s|%s|%d|%d|%s", id, safeCitizen, area, type, severity, timestamp, safeDetails);
    }

    public static Complaint deserializeLine(String line) {
        String[] p = line.split("\\|",7);
        if (p.length < 7) return null;
        try {
            String id = p[0];
            String citizen = p[1];
            String area = p[2];
            String type = p[3];
            int severity = Integer.parseInt(p[4]);
            long ts = Long.parseLong(p[5]);
            String details = p[6];
            return new Complaint(id, citizen, area, type, severity, ts, details);
        } catch(Exception e) { return null; }
    }

    public String toString() {
        return String.format("[%s] %s | %s | %s | sev=%s | %s", id, citizen, area, type, severityLabel, timeString());
    }
}
