package ac.ahocorasick.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IntervalNode {

    private enum Direction { LEFT, RIGHT }

    private ac.ahocorasick.interval.IntervalNode left = null;
    private ac.ahocorasick.interval.IntervalNode right = null;
    private int point;//中位数
    private List<Intervalable> intervals = new ArrayList<Intervalable>();
    /**
     * 构造方法
     * @param intervals
     */
    public IntervalNode(List<Intervalable> intervals) {
        this.point = determineMedian(intervals);

        List<Intervalable> toLeft = new ArrayList<Intervalable>();
        List<Intervalable> toRight = new ArrayList<Intervalable>();
        //end小于中位数放左边，大于中位数的放在右边，中位数在start和end之间的放在intervals里
        for (Intervalable interval : intervals) {
            if (interval.getEnd() < this.point) {
                toLeft.add(interval);
            } else if (interval.getStart() > this.point) {
                toRight.add(interval);
            } else {
                this.intervals.add(interval);
            }
        }

        if (toLeft.size() > 0) {
            this.left = new ac.ahocorasick.interval.IntervalNode(toLeft);
        }
        if (toRight.size() > 0) {
            this.right = new ac.ahocorasick.interval.IntervalNode(toRight);
        }
    }
    /**
     * 求中位数
     * 找到最小的start和最大的 end，然后折半
     * 此方法不通用，因为在这里start和end都不为-1
     */
    public int determineMedian(List<Intervalable> intervals) {
        int start = -1;
        int end = -1;
        for (Intervalable interval : intervals) {
            int currentStart = interval.getStart();
            int currentEnd = interval.getEnd();
            if (start == -1 || currentStart < start) {
                start = currentStart;
            }
            if (end == -1 || currentEnd > end) {
                end = currentEnd;
            }
        }
        return (start + end) / 2;
    }
    /**
     * 跟interval有交集的
     * @param interval
     * @return 跟interval有交集的元素
     */
    public List<Intervalable> findOverlaps(Intervalable interval) {

        List<Intervalable> overlaps = new ArrayList<Intervalable>();
       
        if (this.point < interval.getStart()) { // Tends to the right
            addToOverlaps(interval, overlaps, findOverlappingRanges(this.right, interval));//右节点
            addToOverlaps(interval, overlaps, checkForOverlapsToTheRight(interval)); //intervals沾到interval
        } else if (this.point > interval.getEnd()) { // Tends to the left
            addToOverlaps(interval, overlaps, findOverlappingRanges(this.left, interval));
            addToOverlaps(interval, overlaps, checkForOverlapsToTheLeft(interval));////intervals沾到interval
        } else { // Somewhere in the middle
            addToOverlaps(interval, overlaps, this.intervals);
            addToOverlaps(interval, overlaps, findOverlappingRanges(this.left, interval));
            addToOverlaps(interval, overlaps, findOverlappingRanges(this.right, interval));
        }

        return overlaps;
    }

    protected void addToOverlaps(Intervalable interval, List<Intervalable> overlaps, List<Intervalable> newOverlaps) {
        for (Intervalable currentInterval : newOverlaps) {
            if (!currentInterval.equals(interval)) {
                overlaps.add(currentInterval);
            }
        }
    }

    protected List<Intervalable> checkForOverlapsToTheLeft(Intervalable interval) {
        return checkForOverlaps(interval, Direction.LEFT);
    }

    protected List<Intervalable> checkForOverlapsToTheRight(Intervalable interval) {
        return checkForOverlaps(interval, Direction.RIGHT);
    }

    protected List<Intervalable> checkForOverlaps(Intervalable interval, Direction direction) {

        List<Intervalable> overlaps = new ArrayList<Intervalable>();
        for (Intervalable currentInterval : this.intervals) {
            switch (direction) {
                case LEFT :
                    if (currentInterval.getStart() <= interval.getEnd()) {//左边沾到interval
                        overlaps.add(currentInterval);
                    }
                    break;
                case RIGHT :
                    if (currentInterval.getEnd() >= interval.getStart()) {//右边沾到interval
                        overlaps.add(currentInterval);
                    }
                    break;
            }
        }
        return overlaps;
    }


    protected List<Intervalable> findOverlappingRanges(ac.ahocorasick.interval.IntervalNode node, Intervalable interval) {
        if (node != null) {
            return node.findOverlaps(interval);
        }
        return Collections.emptyList();
    }

}
