package com.fiorano.openesb.rmiclient.tests;

import com.fiorano.openesb.rmiclient.RmiClient;
import com.fiorano.openesb.rmiconnector.api.IApplicationManager;
import com.fiorano.openesb.rmiconnector.api.IRmiManager;
import com.fiorano.openesb.rmiconnector.api.ServiceException;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class SaveApplicationTest implements TestCase{

    IRmiManager rmiManager;
    IApplicationManager eventProcessManager;

    public void test() throws RemoteException, ServiceException {
        RmiClient rmiClient = null;
        try {
            rmiClient = new RmiClient();
        } catch (NotBoundException e) {
            throw new ServiceException(e.getMessage());
        }

        rmiManager = rmiClient.getRmiManager();
        String handleid = null;
        handleid = rmiManager.login("karaf", "karaf");
        eventProcessManager = rmiManager.getApplicationManager(handleid);
        try {
            saveApplication();
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        eventProcessManager.deleteApplication("BP-JAN", "1.0");
        rmiManager.logout(handleid);
    }
    private void saveApplication() throws ServiceException, IOException {
        boolean complete = false;
        byte[] contents;
        InputStream in = getClass().getResourceAsStream("/bp-jan-1.0.zip");
        BufferedInputStream bis = new BufferedInputStream(in);
        try {
            while (!complete) {
                byte[] tempContents = new byte[1024 * 40];
                int readCount = bis.read(tempContents);
                if (readCount < 0) {
                    complete = true;
                    readCount = 0;
                }
                contents = new byte[readCount];
                System.arraycopy(tempContents, 0, contents, 0, readCount);
                eventProcessManager.saveApplication(contents, complete);
            }
        } catch (Exception e) {
            throw new ServiceException("Exception while importing the application" + "Reason :: " + e);
        } finally {
            bis.close();
        }
    }
}
