package cn.dcs.test;

import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

public class QueryAuthTest {

    public static void main(String[] args) {
        try {
            long s = System.currentTimeMillis(); //
//            String endpoint = "http://61.139.5.229:7071/dcslogserver/services/QueryAuth?wsdl";
            String endpoint = "http://localhost:8080/dcslogserver/services/QueryAuth?wsdl";
            Service service = new Service();
            Call call = (Call) service.createCall();
            service.createCall();
            call.setTargetEndpointAddress(endpoint);
            call.setOperationName("receive");
            call.addParameter("Query_Number",     XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("Query_Begin_Time", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("Query_End_Time",   XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("Query_Flag",       XMLType.XSD_INT,    ParameterMode.IN);
            call.setReturnType(XMLType.XSD_STRING);//
            String result = (String) call.invoke(new Object[] { "[1]00:01:38:81:ec:48", "2012-04-03 00:00:00", "2012-04-03 23:59:59", 1 });
            System.out.println(result);
            System.out.println(System.currentTimeMillis() - s);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

}
