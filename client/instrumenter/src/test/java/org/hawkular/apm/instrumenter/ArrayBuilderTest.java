/*
 * Copyright 2015-2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.apm.instrumenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author gbrown
 */
public class ArrayBuilderTest {

    @Test
    public void testEmpty() {
        Object[] array = ArrayBuilder.create().get();

        assertNotNull(array);

        assertTrue("Expecting empty array", array.length == 0);
    }

    @Test
    public void testnonEmpty() {
        Object[] array = ArrayBuilder.create().add("One").add(2).get();

        assertNotNull(array);

        assertTrue("Expecting 2 array elements", array.length == 2);

        assertEquals("One", array[0]);
        assertEquals(2, array[1]);
    }

}
