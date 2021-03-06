package SNMPHandler;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;


public class ChangePortStatus {
    private String address = "192.168.1.35/161"; // switch address and snmp port
    private String writeCommunity = "public"; // write community name

    private Snmp snmp;
    private CommunityTarget target;

    public ChangePortStatus() {
        try {
            TransportMapping transport = new DefaultTcpTransportMapping();
            snmp = new Snmp(transport);

            Address targetAddress = GenericAddress.parse("192.168.1.35");
            target = new CommunityTarget();
            target.setCommunity(new OctetString(writeCommunity));
            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(3000);
            target.setVersion(SnmpConstants.version2c);

            PDU command = new PDU();
            command.setType(PDU.SET);
            command.add(new VariableBinding(new OID("1.3.6.1.2.1.2.2.1.7.10101"), new Integer32(2))); // port 1 down
          //  command.add(new VariableBinding(new OID("1.3.6.1.2.1.2.2.1.7.6"), new Integer32(1))); // port 6 up
            ResponseEvent response = snmp.send(command, target);
            System.out.println("response: " + response);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChangePortStatus test = new ChangePortStatus();
    }
}