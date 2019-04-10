package eu.kennytv.worldeditcui.user;

public final class User {
    private boolean selectionShown = true;
    private boolean clipboardShown;

    User() {
    }

    public boolean isSelectionShown() {
        return selectionShown;
    }

    public void setSelectionShown(final boolean selectionShown) {
        this.selectionShown = selectionShown;
    }

    public boolean isClipboardShown() {
        return clipboardShown;
    }

    public void setClipboardShown(final boolean clipboardShown) {
        this.clipboardShown = clipboardShown;
    }
}
