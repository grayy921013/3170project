import java.sql.Connection;

/**
 * Created by Zhehui Zhou on 3/23/16.
 */
public abstract class User {
    protected Connection connection;

    public User(Connection connection) {
        this.connection = connection;
    }

    public abstract void runAction(int action);

    public abstract int subMenu();
}
