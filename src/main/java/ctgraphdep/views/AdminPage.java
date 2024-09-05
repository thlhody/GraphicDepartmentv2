package ctgraphdep.views;

import ctgraphdep.constants.AppPaths;

public class AdminPage extends BasePage {

    @Override
    protected String getFxmlPath() {
        return AppPaths.ADMIN_PAGE_LAYOUT;
    }

    @Override
    protected String getTitle() {
        return "Admin Page";
    }

    public static void main(String[] args) {
        launch(args);
    }
}