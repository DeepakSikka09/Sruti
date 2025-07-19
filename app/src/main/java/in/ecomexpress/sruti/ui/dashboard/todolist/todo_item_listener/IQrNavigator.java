package in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener;

public interface IQrNavigator {
    void onErrorMessage(String message);

    void nextScreen(String manifest_no, String sFileBody);
}
