package ctgraphdep.views;

import ctgraphdep.constants.AppPaths;

public class UserPage extends BasePage {

    @Override
    protected String getFxmlPath() {
        return AppPaths.USER_PAGE_LAYOUT;
    }

    @Override
    protected String getTitle() {
        return "User Page";
    }

    public static void main(String[] args) {
        launch(args);
    }
}