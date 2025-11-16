import java.util.List;

public class MazeData {
    private int width;
    private int height;
    private List<Wall> hwalls;
    private List<Wall> vwalls;
    private List<Jump> jumps;
    private List<Quest> quests;

    // Getters and Setters
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public List<Wall> getHwalls() { return hwalls; }
    public void setHwalls(List<Wall> hwalls) { this.hwalls = hwalls; }

    public List<Wall> getVwalls() { return vwalls; }
    public void setVwalls(List<Wall> vwalls) { this.vwalls = vwalls; }

    public List<Jump> getJumps() { return jumps; }
    public void setJumps(List<Jump> jumps) { this.jumps = jumps; }

    public List<Quest> getQuests() { return quests; }
    public void setQuests(List<Quest> quests) { this.quests = quests; }
}
