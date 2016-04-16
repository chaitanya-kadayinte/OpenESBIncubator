package com.fiorano.openesb.tools;

import com.fiorano.openesb.microservice.launch.LaunchConstants;
import com.fiorano.openesb.rmiconnector.api.ServiceException;
import com.fiorano.openesb.utils.ExceptionUtil;
import com.fiorano.openesb.utils.StringUtil;
import com.fiorano.openesb.utils.exception.FioranoException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.Properties;

public class ServiceManualLauncher extends FioranoTask {

    public static String JAVA_HOME;
    public static String FIORANO_HOME;
    public static String COMP_REPOSITORY_PATH;
    private boolean ntService = false;
    private boolean noninteractive = false;
    public String propfileNT;
    public static String componentFile;
    private String servInstanceName, eventProcessName;
    private float appVersion;
    private Properties prop = new Properties();
    private PropertyReader propertyReader;

    /**
     * Sets componentFile path for object
     *
     * @param componentFile path
     */
    public void setComponentFile(String componentFile) {
        ServiceManualLauncher.componentFile = componentFile;
    }

    /**
     * Sets NT service for object
     *
     * @param ntOption boolean specifying whether the scriptgen has to be launched as NTService
     */
    public void setNTService(boolean ntOption) {
        ntService = ntOption;
    }

    /**
     * Sets Manual Interaction for object
     *
     * @param manualInteration boolean specifying whether the scriptgen has to be launched as NTService
     */
    public void setNoninteractive(boolean manualInteration) {
        this.noninteractive = manualInteration;
    }

    /**
     * Sets fiorano home for object
     *
     * @param fioranoHome path of fiorano SOA installation
     */
    public void setFioranoHome(String fioranoHome) {
        FIORANO_HOME = fixPath(fioranoHome);
    }

    /**
     * Sets components path for object
     *
     * @param componentPath components repository path
     */
    public void setComponentsPath(String componentPath) {
        COMP_REPOSITORY_PATH = fixPath(componentPath);
        System.setProperty("COMP_REPOSITORY_PATH", COMP_REPOSITORY_PATH);
    }

    /**
     * Sets java home for object
     *
     * @param javaHome java installation directory
     */
    public void setJavaHome(String javaHome) {
        JAVA_HOME = javaHome;
    }

    /**
     * This method contains the execution logic.
     */
    public void executeTask() {
        propertyReader = new PropertyReader(this);
        if (StringUtil.isEmpty(COMP_REPOSITORY_PATH) ||
                COMP_REPOSITORY_PATH.equalsIgnoreCase("${COMPONENT_REP_PATH}")) {
            throw new IllegalArgumentException("Component Repository path not specified.");
        }
       // WizardDialog dialog = null;
        String[] exec = null;
        JFrame psFrame = null;

       /* if (!noninteractive) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable thr) {
                //ignore the error
            }
            psFrame = new JFrame("Launch Configuration");
            psFrame.setLocation(-1000, -1000);
            psFrame.setIconImage(ImageUtil.getImage(this.getClass(), "title"));
            psFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            if (System.getProperty("os.name").toLowerCase().indexOf("win") < 0) {
                psFrame.setVisible(false);
            } else {
                psFrame.setVisible(true);
                psFrame.toFront();
            }
            ScriptComponentLaunch scriptCompLaunch = new ScriptComponentLaunch();
            scriptCompLaunch.setPropertyReader(propertyReader);
            dialog = new WizardDialog(psFrame, "Launch Configuration", true, null, false);
            dialog.addWizardPanel(scriptCompLaunch);
            if (ntService) {
                NTPropertiesComposer ntPropertiesComposer = new NTPropertiesComposer();
                dialog.addWizardPanel(ntPropertiesComposer);
            }
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            dialog.setSize((int) (screenSize.width * 0.70), (int) (screenSize.height * 0.80));
            dialog.setLocationRelativeTo(null);
            dialog.show();
            Properties propsFromUI = (Properties) dialog.getResource("EXEC_COMMAND");
            if (propsFromUI == null) {
                System.out.println("Load configuration failed, exiting.");
                System.exit(-1);
            }
            prop.putAll(propsFromUI);
        }*/

        loadProperties();
        exec = generateExec();

        try {
            /*JVMCommandProvider jvmCommandProvider = new JVMCommandProvider();
            ProcessBuilder processBuilder = new ProcessBuilder();
            MicroServiceLaunchConfiguration mslc = new MicroServiceLaunchConfiguration(prop.getProperty(LaunchConstants.EVENT_PROC_NAME),prop.getProperty(LaunchConstants.EVENT_PROC_VERSION), "karaf", "karaf", instance);
            ProcessBuilder command = processBuilder.command(jvmCommandProvider.generateCommand(mslc));
            File directory = new File(COMP_REPOSITORY_PATH + servInstanceName + File.separator+prop.getProperty(LaunchConstants.COMPONENT_VERSION));
            command.directory(directory);
            command.inheritIO();
            command.start();*/
            if (exec != null) {
                /*if (!noninteractive && dialog != null) {
                    psFrame.setVisible(false);
                }*/
                Runtime runtime = Runtime.getRuntime();
                if (ntService) {
                    handleNTService(runtime, exec);
                } else {
                    try {
                        handleNonNTService(runtime, exec);
                    } catch (FioranoException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(ExceptionUtil.getMessage(e));
           // if (!noninteractive && dialog != null) dialog.dispose();
        } catch (InterruptedException ie) {
            System.out.println(ExceptionUtil.getMessage(ie));
          //  if (!noninteractive && dialog != null) dialog.dispose();
        }
    }

    public RmiClient getConnectionManager(){
        return rmiClient;
    }

    private String[] generateExec() {
        servInstanceName = prop.getProperty(LaunchConstants.COMP_INSTANCE_NAME);
        appVersion = Float.parseFloat(prop.getProperty(LaunchConstants.EVENT_PROC_VERSION));
        eventProcessName = prop.getProperty(LaunchConstants.EVENT_PROC_NAME);
        String userDefinedJavaHome = prop.getProperty("RUNTIME_ARG_JAVA_HOME");
        if (userDefinedJavaHome != null && userDefinedJavaHome.trim().length() != 0)
            setJavaHome(userDefinedJavaHome);

        try {
            CommandGenerator commandGenerator = new CommandGenerator(prop, loginInfo);
            return commandGenerator.generateExecCommand();
        }  catch (FileNotFoundException e) {
            e.printStackTrace();
            //ExceptionDisplayDialog.showException(null, "<html>Service Descriptor is not found: <P>Please Launch the Event Process before launching the component manually </html>");
            System.err.println("ERROR: Exiting");
        } catch (FioranoException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadProperties() {
        if (prop.isEmpty()) {
            try {
                prop = propertyReader.read(componentFile);
            } catch (FioranoException e) {
                System.out.println("ERROR : Failed to load launch script. The properties file given is not specified or Invalid");
                System.exit(-1);
            }
        }
    }

    private void handleNonNTService(Runtime runtime, String[] exec) throws IOException, InterruptedException, FioranoException {
        boolean applicationLaunched;
        boolean componentLaunched;
        try {
            applicationLaunched = checkApplicationLaunched();
            componentLaunched = checkComponentLaunched();
        } catch (FioranoException e) {
            System.err.println("ERROR: Exiting");
            return;
        }
        if (!applicationLaunched) {
           /* ExceptionDisplayDialog.showException(null, new StringBuffer().append("<html>COMPONENT LAUNCH ERROR: Unable to launch \"").
                    append(servInstanceName).append("\" since the Event Process <P>\"").append(eventProcessName).
                    append("\" is not running. ").append("<html>").toString());*/
            System.err.println("ERROR: Exiting. Application not running");
            return;
        }
        if (componentLaunched) {
            /*ExceptionDisplayDialog.showException(null, new StringBuffer().append("<html>COMPONENT LAUNCH ERROR: Unable to launch \"").
                    append(servInstanceName).append("\" since <P>\"").append(servInstanceName).append("\" in \"" + eventProcessName + "\"").
                    append(" is already running. ").append("<html>").toString());*/
            System.err.println("ERROR: Exiting. Component already launched");
            return;
        }
        System.out.println("Following is the command: ");
        for(String s:exec){
            System.out.println(s);
        }
        Process proc = runtime.exec(exec);
        runtime.addShutdownHook(new ShutDownThread(proc));
        createStreamRedirectors(proc);
        int proc_exit = proc.waitFor();
        System.out.println("Launching Component Status : " + proc_exit);
    }

    private boolean checkComponentLaunched() throws FioranoException {
        try {
            if(rmiClient == null) {
                System.out.println("Could not connect to enterprise server.\n Please make sure the event process is running.");
                return true;
            }
            return rmiClient.getApplicationManager().isServiceRunning(eventProcessName, appVersion, servInstanceName);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new FioranoException(e);
        } catch (ServiceException e) {
            e.printStackTrace();
            throw new FioranoException(e);
        }
    }

    private void createStreamRedirectors(Process process) {
        new StreamRedirector(process.getInputStream(), System.out);
        new StreamRedirector(process.getErrorStream(), System.err);
    }

    private boolean checkApplicationLaunched() throws FioranoException {
        try {
            if(rmiClient == null) {
                System.out.println("Could not connect to enterprise server.\n Please make sure the event process is running.");
                return true;
            }
            return rmiClient.getApplicationManager().isRunning(eventProcessName,appVersion);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new FioranoException(e);
        } catch (ServiceException e) {
            e.printStackTrace();
            throw new FioranoException(e);
        }
    }

    private void handleNTService(Runtime runtime, String[] exec) throws IOException, InterruptedException {
        String execStr = FIORANO_HOME + "esb/tools/scriptgen/ntservice/bin" + "/install_" + eventProcessName + "_" +appVersion + "_" +  servInstanceName + "-NT.bat";
        Process proc = runtime.exec(execStr);
        runtime.addShutdownHook(new ShutDownThread(proc));
        createStreamRedirectors(proc);
        int proc_exit = proc.waitFor();
        System.out.println("NT Service Installed with status " + proc_exit);

        // Serializing the exec command array which is used during launch as NTService.
        File propFileDir = new File(ServiceManualLauncher.FIORANO_HOME + "esb/tools/scriptgen/component_properties/");
        if (!propFileDir.exists()) {
            propFileDir.mkdirs();
        }
        propfileNT = FIORANO_HOME + "/esb/tools/scriptgen/component_properties/" + eventProcessName + "_" + appVersion + "_" + servInstanceName + ".ser";
        File propertiesFile = new File(propfileNT);
        if (propertiesFile.exists()) {
            propertiesFile.delete();
            propertiesFile.createNewFile();
        }
        ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(propertiesFile));
        fos.writeObject(exec);
        fos.close();
    }

    /**
     * returns the computed Fiorano Home
     *
     * @param filePath
     * @return path of fiorano home
     */
    private String fixPath(String filePath) {
        File f = new File(filePath);
        try {
            filePath = f.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath + File.separator;
    }
}

class ShutDownThread extends Thread {

    private Process proc = null;

    ShutDownThread(Process proc) {
        this.proc = proc;
    }

    /**
     * Main processing method for the ShutDownThread object
     */
    public void run() {
        if (proc != null) {
            proc.destroy();
            proc = null;
        }
    }

}