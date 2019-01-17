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
package com.ironapeiron.asm.adapters;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.function.Predicate;

/**
 * Adds a getter function specified by the parameters passed to an {@link AddGetterAdapter} instance.
 *
 * @author IronApeiron
 *         Date Created: 2019-01-16
 */
public class AddGetterAdapter extends ClassVisitor implements Opcodes {

    private ClassVisitor next;

    private Predicate<FieldNode> fieldPredicate;
    private Predicate<MethodNode> methodPredicate;

    private String getterName;
    private int retInsn;

    public AddGetterAdapter(final ClassVisitor cv, final Predicate<FieldNode> fieldPredicate, final Predicate<MethodNode> methodPredicate, final String getterName, final int retInsn){
        super(Opcodes.ASM4, new ClassNode());

        next = cv;

        this.fieldPredicate = fieldPredicate;
        this.methodPredicate = methodPredicate;

        this.getterName = getterName;
        this.retInsn = retInsn;
    }

    @Override
    public void visitEnd(){
        ClassNode cNode = (ClassNode) cv;

        boolean isFieldPresent = false;
        boolean isStatic = false;
        String signature = null;

        FieldNode targetField = null;

        for(Object f : cNode.fields){
            FieldNode field = (FieldNode) f;

            if(fieldPredicate.test(field)){
                isFieldPresent = true;
                signature = field.signature;
                isStatic = (field.access & ACC_STATIC) != 0;
                targetField = field;
                break;
            }
        }

        boolean isMethodPresent = false;

        for(Object m : cNode.methods){
            MethodNode method = (MethodNode) m;

            if(methodPredicate.test(method)){
                isMethodPresent = true;
            }
        }

        if(isFieldPresent && !isMethodPresent){
            MethodNode mn = new MethodNode(ACC_PUBLIC, getterName, "()" + targetField.desc, signature, null);

            mn.instructions.add(new VarInsnNode(ALOAD, 0));
            mn.instructions.add(new FieldInsnNode(isStatic ? GETSTATIC : GETFIELD, cNode.name, targetField.name, targetField.desc));
            mn.instructions.add(new InsnNode(retInsn));

            mn.visitMaxs(3, 3);
            mn.visitEnd();
            cNode.methods.add(mn);

            System.out.println("[Method+]" + targetField.desc + " " + getterName + "() identified as " +  cNode.name + "." + targetField.name);
        }
    }

}
