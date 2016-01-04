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

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * Scan the whole class path for files or Java types.
 */
public class ClassPathScanner {

    private static final String CLASS_RESOURCE_PATTERN = "**/*.class";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MetadataReaderFactory metadataReaderFactory;
    private final ResourcePatternResolver resourcePatternResolver;

    public ClassPathScanner() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public ClassPathScanner(ClassLoader classLoader) {
        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader(classLoader);
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(defaultResourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(defaultResourceLoader);
    }

    private static String getClassSearchPattern(String packageName) {
        String resolvedPackageName = SystemPropertyUtils.resolvePlaceholders(packageName);
        String resourcePath = ClassUtils.convertClassNameToResourcePath(resolvedPackageName);

        if (StringUtils.isEmpty(resourcePath)) {
            return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "??/" + CLASS_RESOURCE_PATTERN;
        }

        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resourcePath + "/" + CLASS_RESOURCE_PATTERN;
    }

    private static String getFileSearchPattern(String path) {
        String resolvedPath = SystemPropertyUtils.resolvePlaceholders(path);
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolvedPath;
    }

    /**
     * Find classes in the current class path that match the given <code>typeFilter</code> and reside in the given
     * <code>packages</code>.
     * 
     * @param typeFilter The {@link TypeFilter} a class must satisfy to be considered.
     * @param packages The packages to look for classes in. At least one is required!
     * 
     * @return The classes matching the given parameters.
     * 
     * @throws IOException If reading resources or loading classes failed.
     */
    public Set<Class<?>> findClasses(TypeFilter typeFilter, String... packages) throws IOException {
        Set<Class<?>> result = new HashSet<Class<?>>();

        if (packages == null || packages.length == 0) {
            throw new IllegalArgumentException("At least one package is required");
        }

        for (String eachPackage : packages) {
            result.addAll(this.scanForClasses(eachPackage, typeFilter));
        }

        return result;
    }

    /**
     * Finds files in the current class path that match the given <code>filePattern</code>.<br/>
     * The patterns supports Ant-style wildcards. See {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver
     * PathMatchingResourcePatternResolver} for details.<br/>
     * <br/>
     * The result may contain files packaged inside JAR files!
     * 
     * @param filePattern The pattern that a file must satisfy to be considered.
     * 
     * @return The files matching the given patterns.
     * 
     * @throws IOException If reading resources failed.
     */
    public Set<URL> findFiles(String... filePattern) throws IOException {
        if (filePattern == null || filePattern.length == 0) {
            throw new IllegalArgumentException("At least one file pattern is required!");
        }

        Set<URL> result = new HashSet<URL>();

        for (String eachPath : filePattern) {
            result.addAll(this.scanForFiles(eachPath));
        }

        return result;
    }

    private Class<?> getClass(Resource resource) throws ClassNotFoundException, IOException {
        MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
        String className = metadataReader.getClassMetadata().getClassName();
        return Class.forName(className);
    }

    private boolean matches(Resource resource, TypeFilter typeFilter) {
        try {
            if (!resource.isReadable()) {
                return false;
            }

            MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
            return typeFilter.match(metadataReader, this.metadataReaderFactory);
        } catch (IOException e) {
            this.logger.warn("Could not match resource '" + resource.getFilename() + "'", e);
            return false;
        }
    }

    private Set<Class<?>> scanForClasses(String packageName, TypeFilter typeFilter) throws IOException {
        Set<Class<?>> result = new HashSet<Class<?>>();

        String classSearchPattern = getClassSearchPattern(packageName);
        Resource[] resources = this.resourcePatternResolver.getResources(classSearchPattern);

        for (Resource resource : resources) {
            if (!this.matches(resource, typeFilter)) {
                continue;
            }

            try {
                Class<?> foundClass = this.getClass(resource);
                result.add(foundClass);
            } catch (ClassNotFoundException e) {
                throw new IOException("Failed to read class '" + resource.getFilename() + "'.", e);
            }
        }

        return result;
    }

    private Set<URL> scanForFiles(String pattern) throws IOException {
        Set<URL> result = new HashSet<URL>();

        String fileSearchPattern = getFileSearchPattern(pattern);
        Resource[] resources = this.resourcePatternResolver.getResources(fileSearchPattern);

        for (Resource eachResource : resources) {
            result.add(eachResource.getURL());
        }

        return result;
    }
}
