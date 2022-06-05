import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.mq.jms.*;

public class MqStub {
    public static void main(String[] args) {
        try {
            MQQueueConnection mqConn;
            MQQueueConnectionFactory mqCF;
            final MQQueueSession mqQSession;
            MQQueue mqIn;
            MQQueue mqOut;
            MQQueueReceiver mqReceiver;
            MQQueueSender mqSender;

            mqCF = new MQQueueConnectionFactory();
            mqCF.setHostName("localhost");

            mqCF.setPort(1414);

            mqCF.setQueueManager("ADMIN");
            mqCF.setChannel("SYSTEM.DEF.SVRCONN");

            mqConn = (MQQueueConnection) mqCF.createQueueConnection();
            mqQSession = (MQQueueSession) mqConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

            mqIn = (MQQueue) mqQSession.createQueue("MQIN"); // входная
//            mqReceiver = (MQQueueReceiver) mqQSession.createReceiver(mqIn);

            mqOut = (MQQueue) mqQSession.createQueue("MQOUT"); //выходная
                mqSender = (MQQueueSender) mqQSession.createSender(mqIn);

//            javax.jms.MessageListener Listener = new javax.jms.MessageListener() {
//                @Override
//                public void onMessage(Message msg) {
//                    System.out.println("Got message!");
//                    if (msg instanceof TextMessage) {
//                        try {
//                            TextMessage tMsg = (TextMessage) msg;
//                            String msgText = tMsg.getText();
//                            System.out.println(msgText);
//                        } catch (JMSException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//            };
            TextMessage message = (TextMessage) mqQSession.createTextMessage("jdsdk");
            message.setJMSReplyTo(mqIn);

            //mqReceiver.setMessageListener(Listener);
            mqConn.start();
            mqSender.send(message);
            System.out.println("Stub Started.");
            mqQSession.commit();

            String selecter = "JMSCorrelationID = '"+message.getJMSMessageID()+"'";
            MQQueueReceiver receiver = (MQQueueReceiver)mqQSession.createReceiver(mqOut, selecter);


            Message messageText;
            messageText = receiver.receive(5000);
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}