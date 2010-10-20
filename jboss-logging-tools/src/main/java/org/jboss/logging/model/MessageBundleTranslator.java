/*
 * JBoss, Home of Professional Open Source Copyright 2010, Red Hat, Inc., and
 * individual contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.jboss.logging.model;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JFieldVar;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JMod;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


/**
 * The java message bundle java
 * class model.
 *
 * @author Kevin Pollet
 */
public class MessageBundleTranslator extends ClassModel {

    /**
     * The instance field name.
     */
    private static final String INSTANCE_FIELD_NAME = "INSTANCE";

    /**
     * The get instance method name.
     */
    private static final String GET_INSTANCE_METHOD_NAME = "readResolve";

    /**
     * The translation map.
     */
    private final Map<String, String> translations;

    /**
     * Create a MessageBundle with super class and interface.
     *
     * @param className      the qualified class name
     * @param superClassName the super class name
     */
    public MessageBundleTranslator(final String className, final String superClassName, final Map<String, String> translations) {
        super(className, superClassName);

        if (translations != null) {
            this.translations = translations;
        } else {
            this.translations = Collections.EMPTY_MAP;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JCodeModel generateModel() throws Exception {
        JCodeModel model = super.generateModel();
        JDefinedClass definedClass = definedClass();

        JMethod constructor = definedClass.constructor(JMod.PROTECTED);
        constructor.body().invoke("super");

        JFieldVar field = definedClass.field(JMod.PUBLIC + JMod.STATIC + JMod.FINAL, definedClass, INSTANCE_FIELD_NAME);
        field.init(JExpr._new(definedClass));

        JMethod readResolve = definedClass.method(JMod.PROTECTED, definedClass, GET_INSTANCE_METHOD_NAME);
        readResolve.annotate(Override.class);
        readResolve.body()._return(JExpr.ref(INSTANCE_FIELD_NAME));

        Set<Map.Entry<String, String>> entries = this.translations.entrySet();
        for (Map.Entry<String, String> entry : entries) {

            String key = entry.getKey();
            String value = entry.getValue();

            JMethod method = addMessageMethod(key, value, -1);
            method.annotate(Override.class);
        }

        return model;
    }

}
