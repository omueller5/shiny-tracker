package net.owen.shinytracker;

public interface AppNavigator {

    void showDashboard();

    void showStats();

    void showCatchCalc();

    void showTimeline();

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
