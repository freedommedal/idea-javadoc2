package com.sgota.plugin.idea.javadoc2.service;


import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;

public class MyStringResourceRepository extends StringResourceRepositoryImpl {

    public void clear() {
        resources.clear();
    }
}
