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

import com.ironapeiron.asm.adapters.AddGetterAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.function.Predicate;

/**
 * Used to construct a series of adapters to perform class transformations.
 *
 * @author IronApeiron
 *         Date Created: 2019-01-16
 */
public abstract class AdapterChain implements Opcodes {

    /**
     * Used to decide by the {@link Injector} to be used or not.
     * @param classToTransform The {@link ClassNode} to be transformed.
     * @return True if the {@link ClassNode} is intended to be transformed with this adapter. False otherwise.
     */
    public abstract boolean accept(ClassNode classToTransform);

    public abstract void process(ClassNode cNode);

    public abstract void runTransform();

    /**
     * This will be the final adapter in the chain.
     */
    private ClassWriter cw;

    /**
     * This keeps track of the top-level adapter.
     */
    private ClassVisitor topLevelAdapter;

    public AdapterChain(final ClassNode cn){
        try{
            this.cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            topLevelAdapter = cn;

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Adds a getter method adapter to return a wanted value.
     *
     * @param targetVar - The target field name.
     * @param descriptor - The descriptor of the target field.
     * @param getterName - The name of the getter method.
     * @param retInsn - The return instruction as an {@link Opcodes} const.
     *
     */
    public void addGetterMethod(final String targetVar, final String descriptor, final String getterName, final int retInsn) {
        Predicate<FieldNode> fieldNodePredicate = (FieldNode field) -> targetVar.equals(field.name) && descriptor.equals(field.desc);
        Predicate<MethodNode> methodNodePredicate = (MethodNode method) -> getterName.equals(method.name) && descriptor.equals(method.desc);

        AddGetterAdapter adapter = new AddGetterAdapter(topLevelAdapter, fieldNodePredicate, methodNodePredicate, getterName, retInsn);
        topLevelAdapter = adapter;
    }

}
