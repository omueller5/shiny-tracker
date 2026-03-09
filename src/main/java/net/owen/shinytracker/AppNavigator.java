package net.owen.shinytracker;

public interface AppNavigator {

    void showDashboard();

    void showCreateHunt();

    void showActiveHunt(Hunt hunt);

    void showOptions();

    void showAbout();

    void showHelp();

    boolean isSelectionMode();

    boolean isHuntSelected(Hunt hunt);

    void toggleSelectionMode();

    void toggleHuntSelection(Hunt hunt);

    void deleteHunt(Hunt hunt);

    void deleteSelectedHunts();
}