package com.fiorano.openesb.tools;

import com.fiorano.openesb.application.constants.ConfigurationRepoConstants;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;

public abstract class FioranoTask extends Task implements ConfigurationRepoConstants {
    protected RmiLoginInfo loginInfo;
    protected static ErrorMessage consoleErr=new ErrorMessage() ;
    static ErrorMessage fileErr=new ErrorMessage() ;
    protected static boolean OPERTAION_FAILED=false;
    protected RmiClient rmiClient;

    /**
     */
    public FioranoTask()
    {
        loginInfo = new RmiLoginInfo();
    }

    @Override
    public void execute() throws BuildException {
        loginInfo.askLogin(this);
        try {
           // CLIConstants.CLI_HOME=new File(loginInfo.FIORANO_HOME).getCanonicalPath() + File.separator + ESB + File.separator + TOOLS + File.separator + CLIConstants.TOOL_cli;
            rmiClient = new RmiClient(loginInfo);
        } catch (Exception e) {
            /*consoleErr.append(CLIConstants.FAILED_LOGIN_SERVICE_PROVIDER+"\tCause : "+ e.getMessage());
            fileErr.append(CLIConstants.FAILED_LOGIN_SERVICE_PROVIDER, e);
            if (loginInfo.verboseFlag)
                throw new FioranoTaskException(fileErr, fileErr);
            else
                throw new FioranoTaskException(consoleErr, fileErr);*/
            e.printStackTrace();
        }

        try {
            executeTask();
        } catch (BuildException e) {
            /*if (loginInfo.verboseFlag)
                throw new FioranoTaskException(fileErr, fileErr);
            else
                throw new FioranoTaskException(consoleErr, fileErr);*/
            e.printStackTrace();
        }finally {
            /*try {
                if (rmiClient != null)
                    rmiClient.SPConnectionClose();
            } catch (Exception e) {
                consoleErr.append(CLIConstants.FAILED_LOGOUT_SERVICE_PROVIDER+"\tCause : "+ e.getMessage());
                fileErr.append(CLIConstants.FAILED_LOGOUT_SERVICE_PROVIDER, e);
                if (loginInfo.verboseFlag)
                    throw new FioranoTaskException(fileErr, fileErr);
                else
                    throw new FioranoTaskException(consoleErr, fileErr);
            }*/
        }

    }

    protected abstract void executeTask() throws BuildException;

    /**
     * Sets login info for object
     *
     * @param s
     */
    public void setLoginInfo(String s)
    {
        loginInfo.setLoginInfo(s);
    }
}

