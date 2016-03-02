package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.application.service.Service;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.microservice.launch.Launcher;
import com.fiorano.openesb.microservice.launch.MicroServiceRuntimeHandle;
import com.fiorano.openesb.microservice.launch.impl.cl.ClassLoaderManager;
import com.fiorano.openesb.microservice.launch.impl.cl.IClassLoaderManager;
import com.fiorano.openesb.microservice.repository.MicroServiceRepoManager;
import com.fiorano.openesb.utils.exception.FioranoException;

import java.lang.reflect.Method;
import java.util.List;

public class InMemoryLauncher implements Launcher {
    private IClassLoaderManager m_classLoaderManager;
    private Object service;
    private LaunchConfiguration launchConfiguration;
    private Class serviceClass;

    public InMemoryLauncher() throws FioranoException {
        m_classLoaderManager = new ClassLoaderManager();
    }

    public MicroServiceRuntimeHandle launch(LaunchConfiguration launchConfiguration, String configuration) throws Exception {
        this.launchConfiguration = launchConfiguration;
        ClassLoader classLoader = m_classLoaderManager.getClassLoader(getComponentPS());
        InMemoryLaunchThread inMemoryLaunchThread = new InMemoryLaunchThread(classLoader);
        inMemoryLaunchThread.start();
        return new InMemoryRuntimeHandle(service, serviceClass, launchConfiguration);
    }

    private Service getComponentPS() throws FioranoException {
        return MicroServiceRepoManager.getInstance().readMicroService(launchConfiguration.getMicroserviceId(),
                launchConfiguration.getMicroserviceVersion());
    }

    public class InMemoryLaunchThread extends Thread {

        private final Method startup;
        private ClassLoader serviceClassLoader;

        public InMemoryLaunchThread(ClassLoader classLoader) throws Exception {
            serviceClassLoader = classLoader;
            setName(launchConfiguration.getServiceName() + " Launch In-memory Thread");
            startup = initStartMethod();
        }

        public void run() {
            try {
                Thread.currentThread().setContextClassLoader(serviceClassLoader);
                startup.invoke(service, getArguments());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private Method initStartMethod() throws FioranoException, ClassNotFoundException, InstantiationException, IllegalAccessException {
            String m_implClass = getComponentPS().getExecution().getInMemoryExecutable();
            ClassLoader serverClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(serviceClassLoader);

                if (m_implClass == null || m_implClass.trim().length() == 0)
                    throw new FioranoException(Bundle.class, LaunchErrorCodes.COMPONENT_INMEMORY_IMPL_NOT_SPECIFIED,
                            Bundle.COMPONENT_IMPL_INVALID);
                serviceClass = Class.forName(m_implClass, true, serviceClassLoader);
                try {
                    service = serviceClass.newInstance();
                } catch (ClassCastException e) {
                    throw new FioranoException(Bundle.class, LaunchErrorCodes.COMPONENT_CANNOT_LAUNCH_IN_MEMORY,
                            Bundle.COMPONENT_IMPL_INVALID);
                }
                Method startup;
                try {
                    startup = serviceClass.getMethod("startup", String[].class);
                    if (startup == null)
                        throw new FioranoException("Could not find the main method.");
                } catch (NoSuchMethodException e) {
                    throw new FioranoException(Bundle.class, LaunchErrorCodes.COMPONENT_CANNOT_LAUNCH_IN_MEMORY, e,
                            Bundle.COMPONENT_IMPL_INVALID);
                }
                return startup;
            } finally {
                Thread.currentThread().setContextClassLoader(serverClassLoader);
            }
        }

        private Object[] getArguments() throws Exception {
            Object[] argListForInvokedMain = new Object[1];

            CommandProvider commandProvider = new JVMCommandProvider();
            List<String> list = commandProvider.getCommandLineParams(launchConfiguration);
            argListForInvokedMain[0] =  list.toArray(new String[list.size()]);
            return argListForInvokedMain;
        }

    }
}
