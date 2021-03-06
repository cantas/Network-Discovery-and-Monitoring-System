package SNMPHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.snmp4j.CommunityTarget;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.PDU;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.Snmp;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.TransportMapping;

public class walktest
{
  //
  // Command line format:
  //   java SNMPWalk targetAddress targetOID
  // EX:
  //   java SNMPWalk 192.168.76.15/161 1.3.6.1.4.1.517
  //
  public static void main(String[] args)
  {
	  String output ="";
    Address targetAddress = new UdpAddress("192.168.1.35"+"/161");
    
    OID targetOID         = new OID("1.3.6.1.2.1.2.2.1.7");
    
    //1.3.6.1.2.1.3.1.1.2-----HP getVlan-> 1.3.6.1.4.1.11.2.14.11.5.1.7.1.15.1.1.1==1.3.6.1.4.1.9.9.68.1.2.2.1
    //1.3.6.1.2.1.17.1.4.1.2==1.3.6.1.4.1.9.9.46.1.3.1.1.2
    //1.3.6.1.4.1.9.9.46.1.3.1.1.2 - Active Vlans
    // OID targetOID         = new OID("1.3.6.1.4.1.9.9.23"); --- CDP
   // OID targetOID         = new OID("1.0.8802.1.1.2.1.4.1.1"); --- LLDP
    
    //1.0.8802.1.1.2.1.5.4795.1.2.7.0  = Model of the Device
    //1.3.6.1.4.1.9.9.23.1.2.1.1.7  cdp interface
   // Logger.getAnonymousLogger().setLevel(Level.OFF);

    PDU requestPDU = new PDU();
    requestPDU.add(new VariableBinding(targetOID));
    requestPDU.setType(PDU.GETNEXT);

    CommunityTarget target = new CommunityTarget();
    target.setCommunity(new OctetString("public"));
    target.setAddress(targetAddress);
    target.setVersion(SnmpConstants.version2c);

    try
    {
      TransportMapping transport = new DefaultUdpTransportMapping();
      Snmp snmp = new Snmp(transport);
      transport.listen();

      boolean finished = false;

      while (!finished)
      {
        VariableBinding vb = null;

        PDU responsePDU = snmp.sendPDU(requestPDU, target);
        if (responsePDU != null)
        {
          vb = responsePDU.get(0);
        }

        if (responsePDU == null)
        {
          System.out.println("responsePDU == null");
          finished = true;
        }
        else if (responsePDU.getErrorStatus() != 0)
        {
          System.out.println("responsePDU.getErrorStatus() != 0");
          System.out.println(responsePDU.getErrorStatusText());
          finished = true;
        }
        else if (vb.getOid() == null)
        {
          System.out.println("vb.getOid() == null");
          finished = true;
        }
        else if (vb.getOid().size() < targetOID.size())
        {
          System.out.println("vb.getOid().size() < targetOID.size()");
          finished = true;
        }
        else if (targetOID.leftMostCompare(targetOID.size(),
                                           vb.getOid()) != 0)
        {
          System.out.println("targetOID.leftMostCompare() != 0)");
          finished = true;
          
          
          
          //return null
        }
        else if (Null.isExceptionSyntax(vb.getVariable().getSyntax()))
        {
          System.out.println(
          "Null.isExceptionSyntax(vb.getVariable().getSyntax())");
          finished = true;
        }
        else if (vb.getOid().compareTo(targetOID) <= 0)
        {
          System.out.println("Variable received is not "+
                       "lexicographic successor of requested "+
                       "one:");
          System.out.println(vb.toString() + " <= "+targetOID);
          finished = true;

        }
        else
        {
          // Dump response.
        		output += vb.toString()+"\n";
        		//System.out.println(output);
          // Set up the variable binding for the next entry.
          requestPDU.setRequestID(new Integer32(0));
          requestPDU.set(0, vb);
        }
      }

      snmp.close();

    }
    catch (IOException e)
    {
      System.out.println("IOException: "+e);
    }
    
    System.out.println(output);
 

  }

}
