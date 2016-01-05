package transport;

import com.thoughtworks.xstream.XStream;
import model.Employee;
import model.EmployeeList;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static model.ProtocolConfig.PROTOCOL_GROUP_TIMEOUT;
import static org.apache.commons.lang3.SerializationUtils.deserialize;
import static org.apache.commons.lang3.SerializationUtils.serialize;

/**
 * Created by Adrian on 11/26/2015.
 */
public class TransportListener {
    private final static ExecutorService executor = Executors.newFixedThreadPool(10);
    private InetSocketAddress mavenLocation;


    public TransportListener(int dataServerPort) throws IOException, JSONException {
        mavenLocation = new InetSocketAddress("127.0.0.1", 3333);
        switch (dataServerPort) {
            case 1111:
                sendDataUdp(mavenLocation, "{\"employee\":{\"firstName\":\"Valeriu\",\"lastName\":\"Buzatu\",\"department\":\"Tehnic\",\"salary\":504}}");
                System.out.println("[INFO] -----------------------------------------\n" +
                        "[INFO] Data was sent to Maven");
                break;
            case 2222:
                sendDataUdp(mavenLocation, "{\"employee\":{\"firstName\":\"Eugen\",\"lastName\":\"Moraru\",\"department\":\"Vinzari\",\"salary\":501}}");
                System.out.println("[INFO] -----------------------------------------\n" +
                        "[INFO] Data was sent to Maven");
                break;
            case 3333:
                String xmlEmployees = receiveDataUdp(mavenLocation);
                sendDataTcp(dataServerPort,xmlEmployees);
                System.out.println("[INFO] -----------------------------------------\n" +
                        "[INFO] Data was sent to client");
                break;
            case 4444:
                sendDataUdp(mavenLocation, "{\"employee\":{\"firstName\":\"Ion\",\"lastName\":\"Pascaru\",\"department\":\"Marketing\",\"salary\":502}}");
                System.out.println("[INFO] -----------------------------------------\n" +
                        "[INFO] Data was sent to Maven");
                break;
            case 5555:
                sendDataUdp(mavenLocation, "{\"employee\":{\"firstName\":\"Adriana\",\"lastName\":\"Munteanu\",\"department\":\"Financiar\",\"salary\":503}}");
                System.out.println("[INFO] -----------------------------------------\n" +
                        "[INFO] Data was sent to Maven");
                break;
            case 6666:
                System.out.println("[INFO] ----------------------------------------- \n" +
                        "[INFO] This node is isolated.");
                break;
            default:
                System.out.println("[WARNING] ----------------------------------------- \n" +
                        "[WARNING] Server port is not correct.");
                break;
        }
    }

    private void sendDataTcp(int dataServerPort,String xmlEmployees) throws IOException {
        ServerSocket serverSocket = new ServerSocket(dataServerPort);
        serverSocket.setSoTimeout((int) SECONDS.toMillis(100));
        boolean isTimeExpired = false;
        while (!isTimeExpired) {
            try {
                Socket socket = serverSocket.accept();  // Blocking call!
                serialize(xmlEmployees, socket.getOutputStream());
                socket.close();
            } catch (SocketTimeoutException e) {
                System.out.println("[WARNING] ----------------------------------------- \n" +
                        "[WARNING] Waiting time expired... Socket is closed.");
                isTimeExpired = true;
                continue;
            }
        }
        serverSocket.close();
    }


    private Future sendDataUdp(final InetSocketAddress clientLocation,final String employee) {
        return executor.submit(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(SECONDS.toMillis(1));
                    DatagramSocket clientSocket = new DatagramSocket();
                    byte[] sendDataServer = serialize((String) employee);
                    DatagramPacket pongPacket = new DatagramPacket(sendDataServer,
                            sendDataServer.length,
                            clientLocation);
                    clientSocket.send(pongPacket);
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String receiveDataUdp(InetSocketAddress clientAddress) throws IOException, JSONException {
        EmployeeList employees = new EmployeeList();
        DatagramSocket datagramServer = new DatagramSocket(clientAddress);
        byte dataFromServer[] = new byte[2048];
        boolean isTimeExpired = false;
        datagramServer.setSoTimeout((int) SECONDS.toMillis(PROTOCOL_GROUP_TIMEOUT-5));

        System.out.println("[INFO] -----------------------------------------\n" +
                "[INFO] Collecting information from nodes....");
        while (!isTimeExpired) {
            DatagramPacket datagramPacketacket = new DatagramPacket(dataFromServer, dataFromServer.length);
            try {
                datagramServer.receive(datagramPacketacket);
            } catch (SocketTimeoutException e) {
                System.out.println("[WARNING] -----------------------------------------\n" +
                        "[WARNING] Waiting time expired...");
                isTimeExpired = true;
                continue;
            }
            String receivedJson = (String)deserialize(datagramPacketacket.getData());
            System.out.println("[INFO] " +
                    "Received employee in JSON: " +
                    receivedJson);
            employees.add(jsonToEmployee(receivedJson));
        }
        datagramServer.close();
        return employeeToXml(employees);
    }

    private Employee jsonToEmployee(String jsonString) throws JSONException {
        // Creating a JSONObject from a String
        JSONObject nodeRoot  = new JSONObject(jsonString);
        // Creating a sub-JSONObject from another JSONObject
        JSONObject nodeStats = nodeRoot.getJSONObject("employee");
        // Getting the value of a attribute in a JSONObject
        String firstName = nodeStats.getString("firstName");
        String lastName = nodeStats.getString("lastName");
        String department = nodeStats.getString("department");
        Double salary = nodeStats.getDouble("salary");
        return new Employee(firstName,lastName,department,salary);
    }

    private String employeeToXml(EmployeeList list){
        XStream xstream = new XStream();
        xstream.alias("employee", Employee.class);
        xstream.alias("employees", EmployeeList.class);
        xstream.addImplicitCollection(EmployeeList.class, "list");
        return xstream.toXML(list);
    }
}

