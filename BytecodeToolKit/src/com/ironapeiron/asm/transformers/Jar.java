/*
 This file is a part of the BytecodeToolkit Project (https://github.com/IronApeiron/BytecodeToolkit).

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>

 ASM (OW2 Consortium)
 Copyright (c) 2000-2011 INRIA, France Telecom
    All rights reserved.

 */
package com.ironapeiron.asm.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A representation of a JAR.
 * @author IronApeiron
 */
public class Jar {

    /**
     * The JAR's path.
     */
    private String jarPath;

    /**
     * The JAR's URL path.
     */
    private URL jarUrlPath;

    /**
     * The URLClassLoader that loads the JAR from a given path.
     */
    private URLClassLoader url;

    /**
     * The JAR's URL
     */
    private String jarUrl;


    /**
     * Creates a representation of a JAR file locally.
     *
     * @param jarPath The path to the JAR.
     */
    public Jar(final String jarPath){
        try {
            this.jarUrlPath = new URL("file:" + jarPath);
            this.jarPath = jarPath;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a representation of a JAR file via HTTP.
     *
     * @param jarUrl The URL to the JAR.
     * @param flags Placeholder
     *
     */
    public Jar(final String jarUrl, int flags){
        try {
            this.url = new URLClassLoader(new URL[]{new URL((jarUrl + "!/").replace("http://", "jar:http://"))});
            this.jarUrl = jarUrl;
            this.jarUrlPath = new URL((jarUrl + "!/").replace("http://", "jar:http://"));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all classes from the given JAR file and stores them into a HashMap for future use.
     *
     * @return True if it was successful, False if it was not.
     */
    public HashMap<String, ClassNode> loadClasses(){
        HashMap<String, ClassNode> loadedClassNodes = new HashMap<String, ClassNode>();
        try {
            JarFile jf;

            if(jarUrl != null) {
                System.out.println(jarUrlPath.toString());
                JarURLConnection u = (JarURLConnection) jarUrlPath.openConnection();
                jf = u.getJarFile();

            } else {
                jf = new JarFile(jarPath);
            }

            Enumeration<? extends JarEntry> en = jf.entries();

            while(en.hasMoreElements()){
                JarEntry entry = en.nextElement();

                if (entry.getName().endsWith(".class")) {
                    ClassReader cr = new ClassReader(jf.getInputStream(entry));
                    ClassNode cn = new ClassNode();

                    cr.accept(cn, 0);

                    loadedClassNodes.put(cn.name, cn);
                }
            }

            return loadedClassNodes;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

