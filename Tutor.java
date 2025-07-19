public class Tutor {
    public String name;
    public String subject;
    public int level;

    public Tutor(String name, String subject, int level) {
        this.name = name;
        this.subject = subject;
        this.level = level;
    }

    @Override
    public String toString() {
        return name + "," + subject + "," + level;
    }
}