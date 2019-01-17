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

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * Used to construct a series of adapters to perform class transformations.
 *
 * @author IronApeiron
 */
public class Injector implements Runnable {

    //The Jar that transformations are based off of.
    private Jar jc;

    //The transformation classes
    private ArrayList<AdapterChain> adapterChains = new ArrayList<>();

    //The transformed classes
    protected HashMap<String, ClassNode> injClasses;

    HashMap<String, ClassNode> loadedClasses = new HashMap<>();

    /**
     * Constructs an Updater instance.
     */
    public Injector(Jar jc, ArrayList<AdapterChain> adapterChains){
        this.jc = jc;
        this.loadedClasses = jc.loadClasses();
        this.adapterChains = adapterChains;
        //TODO: Add the transformation classes - self explanatory

    }


    /**
     * The Jar to work with.
     *
     * @return The Jar with all loaded files.
     */
    protected Jar getJar(){
        return this.jc;
    }


    public void run(){
        HashMap<String, ClassNode> moddedMap = new HashMap<>(loadedClasses);

        for(AdapterChain ac : adapterChains){

        }

    }

    /**
     * Dumps the injected files.
     * @throws IOException
     */
    private void dumpMods(HashMap<String, ClassNode> moddedClasses) throws IOException {
        JarOutputStream out = new JarOutputStream(new FileOutputStream(new File("modified.jar")));

        for(ClassNode c : injClasses.values()){
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            c.accept(cw);
            out.putNextEntry(new JarEntry(c.name + ".class"));
            out.write(cw.toByteArray());

            moddedClasses.remove(c.name);
        }

        for(ClassNode c : moddedClasses.values()){
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            c.accept(cw);

            out.putNextEntry(new JarEntry(c.name + ".class"));

            out.write(cw.toByteArray());
        }

        out.flush();
        out.close();

    }

}

