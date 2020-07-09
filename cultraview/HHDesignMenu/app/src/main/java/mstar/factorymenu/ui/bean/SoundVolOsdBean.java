package mstar.factorymenu.ui.bean;

public class SoundVolOsdBean {
    private int progress;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;
    private String progressText;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getProgressText() {
        return progressText;
    }

    public void setProgressText(String progressText) {
        this.progressText = progressText;
    }
}
