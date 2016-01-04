/*
 * Licensed to the Indoqa Software Design und Beratung GmbH (Indoqa) under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Indoqa licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indoqa.spring;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.junit.Test;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.indoqa.spring.classpathscanner.ExampleInterface;

public class ClassPathScannerTest {

    @Test
    public void findClasses() throws IOException {
        ClassPathScanner scanner = new ClassPathScanner();

        Set<Class<?>> matchingClasses = scanner.findClasses(new AssignableTypeFilter(ExampleInterface.class), "com.indoqa.spring");
        assertNotNull(matchingClasses);
        assertEquals(3, matchingClasses.size());
    }

    @Test
    public void findCssFiles() throws IOException {
        ClassPathScanner scanner = new ClassPathScanner();

        Set<URL> matchingFiles = scanner.findFiles("test-resources/**/*.css");
        assertNotNull(matchingFiles);
        assertEquals(1, matchingFiles.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findFiles() throws IOException {
        ClassPathScanner scanner = new ClassPathScanner();

        scanner.findFiles();
    }

    @Test
    public void findXmlFiles() throws IOException {
        ClassPathScanner scanner = new ClassPathScanner();

        Set<URL> matchingFiles = scanner.findFiles("test-resources/**/*.xml");
        assertNotNull(matchingFiles);
        assertEquals(2, matchingFiles.size());
    }
}
