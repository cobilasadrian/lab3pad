package transport;
import model.Location;

import java.io.IOException;
import java.net.Socket;

import static org.apache.commons.lang3.SerializationUtils.deserialize;

public class TransportClient {

    public String getEmployeesFrom(Location location) throws IOException {
        Socket socket = new Socket();
        socket.connect(location.getLocation());
        String employees = (String) deserialize(socket.getInputStream());
        socket.close();
        return employees;
    }
}
