package com.testing;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import java.io.*;
import java.util.Arrays;

public class ISOParserv2 {
    private ISOParserv2() {
    }

    static private GenericPackager packager;

    static {
        try {
            InputStream stream = ISOParserv2.class.getClassLoader().getResourceAsStream("configuration/packer.xml");
            packager = new GenericPackager(stream);
        } catch (ISOException e) {
            LoggerImp.logError(e.getMessage());
        }
    }

    /**
     * This method retrieves an ISO message from a stream. If the unpacking process of the ISO message fails,
     * an ISOException is thrown. In most cases, a 0620 message should be sent and the scenario should be finished.
     *
     * @param stream InputStream with ISOMsg.
     * @return unpacked ISOMsg
     */
    public static synchronized ISOMsg getISOMsg(InputStream stream) throws ISOException, IOException {
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(packager);
        isoMsg.unpack(stream);
        return isoMsg;
    }

    /**
     * This method reads an ISO message from an input stream and returns an ISOMsg object
     * if it is successfully unpacked.
     * If the unpacking process fails,
     * the method sends a 0620 message to an output stream and throws a ScenarioException.
     *
     * @param in inputStream
     * @param out outputStream
     * @return unpacked ISO message object.
     * @throws ScenarioException
     */
    public static synchronized ISOMsg getISOMsg(InputStream in, OutputStream out) throws ScenarioException {
        ISOMsg isoMsg = new ISOMsg();
        byte[] buf = new byte[8192];
        DataInputStream dIn = new DataInputStream(in);

        try {
            dIn.read(buf);
        } catch (IOException e) {
            LoggerImp.logError("Can't read from client");
            throw new ScenarioException("Can't read from client");
        }

        isoMsg.setPackager(packager);

        try {
            isoMsg.unpack(buf);
        } catch (ISOException e) {
            try {
                LoggerImp.logInfo("ISOExeption: " + e.getMessage());
                sentMessageInvalid(out, buf);
                //Recieve "0630" message
            } catch (ISOException | IOException ex) {
                LoggerImp.logError(ex.getMessage());
            }
            throw new ScenarioException("Invalid message is received");
        }
        logISOMsg(isoMsg);
        return isoMsg;
    }

    /**
     * Logs ISO message.
     * @param isoMsg
     */
    public static synchronized void logISOMsg(ISOMsg isoMsg) {
        StringBuilder message = new StringBuilder();
        message.append("ISO MESSAGE IS:\n");
        try {
            message.append("MTID: ").append(isoMsg.getMTI()).append("\n");
        } catch (ISOException e) {
            LoggerImp.logError(e.getMessage());
            e.printStackTrace();
        }
        for (int i = 0; i < isoMsg.getMaxField(); i++) {
            message.append("Field ").append(i).append(" ").append(isoMsg.getString(i)).append("\n");
        }
        message.append("end of message");
        LoggerImp.logDebug(message.toString());
    }

    /**
     * This method sends 0620 message to outputStream.
     * @param out output stream.
     * @param buf Bytes of ISO message that cannot be unpacked.
     * @throws ISOException
     * @throws IOException
     */
    private static synchronized void sentMessageInvalid (OutputStream out, byte[] buf) throws ISOException, IOException {
        ISOMsg response = new ISOMsg();
        response.setPackager(packager);
        response.setMTI("0620");
        response.set(120, Arrays.copyOf(buf, 200));
        out.write(response.pack());
        out.flush();
    }

    /**
     * This is the main method for testing client's ISO messages.
     * It reads the type of message and field 2, and then starts the required scenario.
     * @param in input stream
     * @param out output stream
     * @throws ScenarioException
     */
    public static synchronized void startTesting (InputStream in, OutputStream out) throws ScenarioException {
        ISOMsg isoMsg = getISOMsg(in, out);
        try {
            if ("0100".equals(isoMsg.getMTI())) {
                if ("0000000000000001".equals(isoMsg.getString(2))) {
                    mid0100scenario0001(isoMsg, in, out);
                }
                else if ("0000000000000002".equals(isoMsg.getString(2))) {
                    mid0100scenario0002(isoMsg, in, out);
                }
            }
            else  if ("0800".equals(isoMsg.getMTI())) {
                if ("0000000000000001".equals(isoMsg.getString(2))) {
                    mid0800scenario0001(isoMsg, in, out);
                }
            }
            else if ("0400".equals(isoMsg.getMTI())) {
                if ("0000000000000001".equals(isoMsg.getString(2))) {
                    mid0400scenario0001(isoMsg, in, out);
                }
            }
        } catch (ISOException e) {
            LoggerImp.logError(e.getMessage());
            throw new ScenarioException(e.getMessage());
        }
    }

    /**
     *     Authorization request [0100] and Standard response to authorization request [0110].
     */
    public static synchronized void mid0100scenario0001(ISOMsg isoMsg, InputStream in, OutputStream out) throws ScenarioException {
        try {
            isoMsg.setMTI("0110");
            DataOutputStream dOut = new DataOutputStream(out);
            dOut.write(isoMsg.pack());
            dOut.flush();
        } catch (ISOException e) {
            LoggerImp.logError(e.getMessage());
            throw new ScenarioException(e.getMessage());
        } catch (IOException e) {
            LoggerImp.logError("Can't read from client");
            throw new ScenarioException("Can't read from client");
        }
        LoggerImp.logDebug("Responded");
        logISOMsg(isoMsg);
    }

    /**
     * No response to authorization request [0110] or late response
     */
    public static synchronized void mid0100scenario0002(ISOMsg isoMsg, InputStream in, OutputStream out) throws ScenarioException {
        try {
            isoMsg.setMTI("0110");
            isoMsg.set(39, "91");
            DataOutputStream dOut = new DataOutputStream(out);
            dOut.write(isoMsg.pack());
            dOut.flush();
        } catch (ISOException e) {
            LoggerImp.logError(e.getMessage());
            throw new ScenarioException(e.getMessage());
        } catch (IOException e) {
            LoggerImp.logError("Can't read from client");
            throw new ScenarioException("Can't read from client");
        }
        LoggerImp.logDebug("Responded");
        logISOMsg(isoMsg);
    }

    /**
     * Network Management Request [0800] (echo test for participant).
     */
    public static synchronized void mid0800scenario0001(ISOMsg isoMsg, InputStream in, OutputStream out) throws ScenarioException {
        try {
            if (!("270".equals(isoMsg.getString(70)))) {
                isoMsg.setMTI("0620");
                isoMsg.set(39, "30");
                DataOutputStream dOut = new DataOutputStream(out);
                dOut.write(isoMsg.pack());
                dOut.flush();
                LoggerImp.logDebug("Responded");
                logISOMsg(isoMsg);
                throw new ScenarioException("Field '070' should be equal 270");
            }
            isoMsg.setMTI("0810");
            isoMsg.set(39, "00");
            DataOutputStream dOut = new DataOutputStream(out);
            dOut.write(isoMsg.pack());
            dOut.flush();
        } catch (ISOException e) {
            LoggerImp.logError(e.getMessage());
            throw new ScenarioException(e.getMessage());
        } catch (IOException e) {
            LoggerImp.logError("Can't read from client");
            throw new ScenarioException("Can't read from client");
        }
        LoggerImp.logDebug("Responded");
        logISOMsg(isoMsg);
    }

    /**
     * Cancel [0400], standard messaging
     */
    public static synchronized void mid0400scenario0001(ISOMsg isoMsg, InputStream in, OutputStream out) throws ScenarioException {
        try {
            isoMsg.setMTI("0410");
            isoMsg.set(39, "00");
            DataOutputStream dOut = new DataOutputStream(out);
            dOut.write(isoMsg.pack());
            dOut.flush();
        } catch (ISOException e) {
            LoggerImp.logError(e.getMessage());
            throw new ScenarioException(e.getMessage());
        } catch (IOException e) {
            LoggerImp.logError("Can't read from client");
            throw new ScenarioException("Can't read from client");
        }
        LoggerImp.logDebug("Responded");
        logISOMsg(isoMsg);
    }
}
