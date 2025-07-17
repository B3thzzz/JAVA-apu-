package src;

public class ClassInfo {
    private String id;
    private String subjectName;
    private double charges;
    private String schedule;
    private String tutorUsername;

    public ClassInfo(String id, String subjectName, double charges, String schedule, String tutorUsername) {
        this.id = id;
        this.subjectName = subjectName;
        this.charges = charges;
        this.schedule = schedule;
        this.tutorUsername = tutorUsername;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public double getCharges() { return charges; }
    public void setCharges(double charges) { this.charges = charges; }
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public String getTutorUsername() { return tutorUsername; }
    public void setTutorUsername(String tutorUsername) { this.tutorUsername = tutorUsername; }

    @Override
    public String toString() {
        return id + "," + subjectName + "," + charges + "," + schedule + "," + tutorUsername;
    }
} 