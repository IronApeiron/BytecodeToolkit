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
import org.objectweb.asm.tree.ClassNode;

/**
 * Used to add interfaces to some class.
 *
 * @author IronApeiron
 *         Date Created: 2019-01-16
 */
public class AddInterfaceAdapter extends ClassVisitor implements Opcodes{

    private String[] interfacesToAdd;
    private ClassVisitor next;


    public AddInterfaceAdapter(final ClassVisitor cv, final String... interfacesToAdd) {
        super(ASM4, new ClassNode());
        next = cv;

        this.interfacesToAdd = interfacesToAdd;
    }


    @Override
    public void visitEnd() {
        ClassNode cn = (ClassNode) cv;

        for(String i : interfacesToAdd) {
            cn.interfaces.add(i);
            System.out.println("[Interface+] " + cn.name + " implements " + i);
        }

        cn.accept(next);

    }
}
