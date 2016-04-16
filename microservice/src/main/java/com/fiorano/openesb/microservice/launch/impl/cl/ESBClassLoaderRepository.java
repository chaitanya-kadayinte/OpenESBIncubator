package com.fiorano.openesb.microservice.launch.impl.cl;


import com.fiorano.openesb.utils.FileUtil;
import com.fiorano.openesb.utils.I18NUtil;
import com.fiorano.openesb.utils.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class ESBClassLoaderRepository
{

    public final static String ATTRS_EXTN = ".attrs";
    public final static String RARFILE_PROP = "rarFile";

    // <File, ClassLoader>
    private final static HashMap classLoaders = new HashMap();

    // <File, Vector<ChangeListener>>
    private final static HashMap listeners = new HashMap();

    /**
     * Adds a feature to the ChangeListener attribute of the ESBClassLoaderRepository class
     *
     * @param rarFile The feature to be added to the ChangeListener attribute
     * @param listener The feature to be added to the ChangeListener attribute
     * @param priority The feature to be added to the ChangeListener attribute
     * @exception IOException
     */
    public static synchronized void addChangeListener(File rarFile, ESBClassLoaderListener listener, int priority)
        throws IOException
    {
        if (rarFile != null)
            rarFile = rarFile.getCanonicalFile();
        if (classLoaders.containsKey(rarFile))
        {
            Vector list = (Vector) listeners.get(rarFile);

            if (list == null)
                listeners.put(rarFile, list = new Vector());
            if (priority == 0 && list.size() != 0)
                list.insertElementAt(listener, 0);
            else
                list.add(listener);
        }
    }

    /**
     * @param rarFile
     * @param listener
     * @exception IOException
     */
    public static synchronized void removeChangeListener(File rarFile, ESBClassLoaderListener listener)
        throws IOException
    {
        rarFile = rarFile.getCanonicalFile();

        Vector list = (Vector) listeners.get(rarFile);

        if (list != null)
        {
            list.remove(listener);
            if (list.size() == 0)
                listeners.remove(rarFile);
        }
    }

    /**
     * @param rarFile
     * @param recreate
     * @return
     * @exception IOException
     */
    public static synchronized ClassLoader createClassLoader(File rarFile, boolean recreate)
        throws IOException
    {
        return createClassLoader(rarFile, Thread.currentThread().getContextClassLoader(), recreate);
    }

    /**
     * @param rarFile
     * @return
     * @exception IOException
     */
    public static synchronized ClassLoader createClassLoader(File rarFile)
        throws IOException
    {
        return createClassLoader(rarFile, Thread.currentThread().getContextClassLoader());
    }

    /**
     * @param rarFile
     * @param parent
     * @return
     * @exception IOException
     */
    public static synchronized ClassLoader createClassLoader(File rarFile, ClassLoader parent)
        throws IOException
    {
        return createClassLoader(rarFile, parent, false);
    }

    /**
     * @param rarFile
     * @param parent
     * @param recreate
     * @return
     * @exception IOException
     */
    public static synchronized ClassLoader createClassLoader(File rarFile, ClassLoader parent, boolean recreate)
        throws IOException
    {
        rarFile = rarFile.getCanonicalFile();

        if (!recreate)
        {
            ClassLoader loader = (ClassLoader) classLoaders.get(rarFile);

            if (loader != null)
                return loader;
        }

        File files[] = rarFile.listFiles();

        if (files == null || files.length == 0)
            return ESBClassLoaderRepository.class.getClassLoader();
        ArrayList urls = new ArrayList();

        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isDirectory())
                continue;
            String name = files[i].getName().toUpperCase();

            //Changes after code review
            if (name.endsWith(".JAR") || name.endsWith(".ZIP"))
                //NOI18N
                urls.add(FileUtil.file2URL(files[i]));
        }
        if (urls.size() == 0)
            return ESBClassLoaderRepository.class.getClassLoader();
        else
        {
            ClassLoader loader = new ESBURLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), parent);

            addClassLoader(rarFile, loader);
            return loader;
        }
    }

    /**
     * @param parent
     * @param recreate
     * @param rarFileSet
     * @return ClassLoader that is creaetd
     */
    public static synchronized ClassLoader createClassLoader(Set rarFileSet, ClassLoader parent, boolean recreate){
        if(!recreate){
            ClassLoader loader = (ClassLoader)classLoaders.get(rarFileSet);

            if(loader!=null)
                return loader;
        }

        ArrayList urls = new ArrayList();
        Iterator iter = rarFileSet.iterator();

        while(iter.hasNext()){
            File rarFile = (File)iter.next();

            addLibraries(rarFile, urls);
        }
        if(urls.size()==0)
            return ESBClassLoaderRepository.class.getClassLoader();
        else{
            ClassLoader loader = new ESBURLClassLoader((URL[])urls.toArray(new URL[urls.size()]), parent);

            addClassLoader(rarFileSet, loader);
            return loader;
        }
    }

    /**
     * @param parent
     * @param recreate
     * @param rarFileSet
     * @return ClassLoader that is creaetd
     */
        //TODO: to be merged with createClassLoader when addLibraries2 merged with addLibraries
    public static synchronized ClassLoader createClassLoader2(Set rarFileSet, ClassLoader parent, boolean recreate){
        if(!recreate){
            ClassLoader loader = (ClassLoader)classLoaders.get(rarFileSet);

            if(loader!=null)
                return loader;
        }

        ArrayList urls = new ArrayList();
        Iterator iter = rarFileSet.iterator();
        while(iter.hasNext()){
            File rarFile = (File)iter.next();

            addLibraries2(rarFile, urls);
        }
        if(urls.size()==0)
            return ESBClassLoaderRepository.class.getClassLoader();
        else{
            ClassLoader loader = new ESBURLClassLoader((URL[])urls.toArray(new URL[urls.size()]), parent);

            addClassLoader(rarFileSet, loader);
            return loader;
        }
    }

    /**
     * @param srcFile
     * @return
     * @throws Exception
     */
    public static ClassLoader fetchClassLoader(String srcFile)
            throws Exception {
        ClassLoader loader = null;
        //read the cf.attrs files
        //the rarFile attrib is used to load the classloader for the MCF
        File cfAttrsFile = new File(new URI(srcFile+ATTRS_EXTN));
        Properties cfAttrs = new Properties();

        FileInputStream csTFileInputStream = new FileInputStream(cfAttrsFile);
        try{
            cfAttrs.load(csTFileInputStream);
        } finally{
            csTFileInputStream.close();
        }

        String rarFilePath = cfAttrs.getProperty(RARFILE_PROP);

        if(!StringUtil.isEmpty(rarFilePath)){
            String componentDir = System.getProperty("COMPONENTS_DIR");

            if(componentDir==null){
                String fioranoHome = System.getProperty("FIORANO_HOME");

                if(fioranoHome==null)
                    fioranoHome = System.getProperty("FMQ_DIR")+"/..";
                componentDir = fioranoHome+"/" + System.getProperty("ESB_REPOSITORY_DIRECTORY") + "/components";
            }

            File components = new File(componentDir);
            Map favorites = Collections.singletonMap("COMPONENTS", components.getCanonicalFile());
            File rarFile = FileUtil.resolve(cfAttrsFile.getParentFile(), rarFilePath, favorites);

            loader = ESBClassLoaderRepository.createClassLoader(rarFile);
        }
        return loader;
    }

    private static void addLibraries(File rarFile, List urls)
    {
        if(rarFile.isDirectory())
        {
            File files[] = rarFile.listFiles();

            if (files == null || files.length == 0)
                return;

            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isDirectory())
                    continue;
                String name = files[i].getName().toUpperCase();

                if (name.endsWith(".JAR") || name.endsWith(".ZIP"))
                {
                    URL url= FileUtil.file2URL(files[i]);
                    URL u= null;
                    try {
                        u = new URL("jar","",-1,url.toString()+"!/");
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(I18NUtil.getMessage(ESBClassLoaderRepository.class, "error.in.creating.url.for.resource.0", url.toString()),e);
                    }
                    urls.add(u);
                }
            }
        }
        else
        {
            String name = rarFile.getName().toUpperCase();

            if (name.endsWith(".JAR") || name.endsWith(".ZIP"))
                {
                    URL url=FileUtil.file2URL(rarFile);
                    URL u= null;
                    try {
                        u = new URL("jar","",-1,url.toString()+"!/");
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(I18NUtil.getMessage(ESBClassLoaderRepository.class, "error.in.creating.url.for.resource.0", url.toString()),e);
                    }
                    urls.add(u);
                }
        }
    }

    // This method considers directories to be having resources other than class archives
    //TODO: to be merged with addLibraries after resolving differences with other usages
    private static void addLibraries2(File rarFile, List urls)
    {
        if(rarFile.isDirectory())
        {
           URL url = FileUtil.file2URL(rarFile);
            urls.add(url);
        }
        else
        {
            String name = rarFile.getName().toUpperCase();

            if (name.endsWith(".JAR") || name.endsWith(".ZIP") || name.endsWith(".EAR")|| name.endsWith(".WAR"))
            {
                URL url=FileUtil.file2URL(rarFile);
                URL u= null;
                try {
                    u = new URL("jar","",-1,url.toString()+"!/");
                } catch (MalformedURLException e) {
                    throw new RuntimeException(I18NUtil.getMessage(ESBClassLoaderRepository.class, "error.in.creating.url.for.resource.0", url.toString()),e);
                }
                urls.add(u);
            }
        }
    }

    /**
     * Adds a feature to the ClassLoader attribute of the ESBClassLoaderRepository class
     *
     * @param loader The feature to be added to the ClassLoader attribute
     * @param rarFile The feature to be added to the ClassLoader attribute
     */
    private static void addClassLoader(Object rarFile, ClassLoader loader)
    {
        ClassLoader oldClassLoader = (ClassLoader) classLoaders.get(rarFile);

        Vector list = (Vector) listeners.get(rarFile);

        if (list != null)
        {
            list = (Vector) list.clone();
            for (int i = 0; i < list.size(); i++)
                ((ESBClassLoaderListener) list.get(i)).beforeUpdatingClassLoder(oldClassLoader, loader);
        }

        list = (Vector) listeners.get(null);
        if (list != null)
        {
            list = (Vector) list.clone();
            for (int i = 0; i < list.size(); i++)
                ((ESBClassLoaderListener) list.get(i)).beforeUpdatingClassLoder(oldClassLoader, loader);
        }

        classLoaders.put(rarFile, loader);

        if (oldClassLoader == null)
            return;

        list = (Vector) listeners.get(rarFile);

        if (list != null)
        {
            list = (Vector) list.clone();
            for (int i = list.size() - 1; i >= 0; i--)
                ((ESBClassLoaderListener) list.get(i)).afterUpdatingClassLoder(oldClassLoader, loader);
        }

        list = (Vector) listeners.get(null);
        if (list != null)
        {
            list = (Vector) list.clone();
            for (int i = list.size() - 1; i >= 0; i--)
                ((ESBClassLoaderListener) list.get(i)).afterUpdatingClassLoder(oldClassLoader, loader);
        }
    }
}
