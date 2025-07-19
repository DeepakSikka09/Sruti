package in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by 63091 on 09-07-2019.
 */
@Module
public abstract class TodoFragmentBind {
    @ContributesAndroidInjector
    abstract ToDoListFragment bindFragmentTodo();
}
