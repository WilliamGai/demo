package ac.ahocorasick.interval;

public interface Intervalable extends Comparable<Object> {

    public int getStart();
    public int getEnd();
    public int size();

}
