import notebook.controller.UserController;
import notebook.model.repository.GBRepository;
import notebook.model.repository.impl.UserRepository;
import notebook.view.UserView;

import static notebook.util.DBConnector.DB_PATH;
import static notebook.util.DBConnector.createDB;

public class Main {
    public static void main(String[] args) {
        createDB();
        UserRepository fileOperation = new UserRepository(DB_PATH);
        GBRepository repository = new UserRepository(fileOperation.toString());
        UserController controller = new UserController(repository);
        UserView view = new UserView(controller);
        view.run();
    }
}