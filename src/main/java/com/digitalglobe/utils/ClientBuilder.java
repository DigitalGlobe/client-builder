package com.digitalglobe.utils;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Use this class to construct an Amazon Web Service Client using the defaultClient or standard with credentials.
 *
 * @param <InterfaceType> is the class type for the Amazon Web Service Client to be constructed.
 */
public class ClientBuilder<InterfaceType> {

    private String region = "";

    /**
     * Set the region to use.
     *
     * @param region is the region to use.
     * @return a copy of the instance for initialization chaining.
     */
    public ClientBuilder<InterfaceType> withRegion(String region) {

        this.region = region;

        return this;
    }

    /**
     * Use this method to build a default client for the specified AWS client.
     *
     * @param staticClass is the static class to use when building the client.
     * @return a newly constructed client.
     * @throws RuntimeException when the method can't find or invoke the default client.
     */
    public InterfaceType build(Class staticClass) throws RuntimeException {

        InterfaceType result = null;

        try {

            if(!region.isEmpty()) result = build(staticClass, null);
            else {

                Method method = staticClass.getDeclaredMethod("create");
                if (method != null && Modifier.isStatic(method.getModifiers())) {

                    result = (InterfaceType) method.invoke((Object) null);

                } else {

                    throw new RuntimeException("Couldn't find static create method.");
                }
            }

        } catch (RuntimeException rte) {

            throw rte;

        } catch (Exception ex) {

            System.out.println(ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Couldn't invoke create method.", ex);
        }

        return result;
    }

    /**
     * Use this method when you want to construct and Amazon Web Service Client using credentials.
     *
     * @param staticClass is the static builder class for the Amazon Web Service Client
     * @param credentials are the credentials to use when with the newly constructed client.
     * @return the newly constructed client.
     * @throws RuntimeException when the method can't find the withCredentials or build methods or can't execute them.
     */
    public InterfaceType build(Class staticClass, AwsCredentialsProvider credentials) throws RuntimeException {

        InterfaceType result = null;

        try {

            Method method = staticClass.getDeclaredMethod("builder");
            if (method != null && Modifier.isStatic(method.getModifiers())) {

                Object factory = method.invoke((Object)null);
                if (factory != null) {

                    if(credentials != null) {

                        method = factory.getClass().getMethod("credentialsProvider", AwsCredentialsProvider.class);
                        if (method == null) {

                            throw new RuntimeException("Couldn't find credentialsProvider method.");
                        }

                        method.invoke(factory, credentials);
                    }

                    if(!region.trim().isEmpty()) {

                        method = factory.getClass().getMethod("region", Region.class);

                        if(method == null) {

                            throw new RuntimeException("Couldn't find the region method.");
                        }

                        method.invoke(factory, Region.of(region));
                    }

                    method = factory.getClass().getMethod("build", (Class[])null);

                    if (method == null) {

                        throw new RuntimeException("Couldn't find the build method.");

                    } else {

                        result = (InterfaceType)method.invoke(factory, (Object[])null);
                    }

                } else {

                    throw new RuntimeException("Unexpected return type from standard method.");
                }

            } else {

                throw new RuntimeException("Couldn't find static standard method.");
            }

        } catch (RuntimeException rte) {

            throw rte;

        } catch (Exception ex) {

            throw new RuntimeException("Couldn't invoke static standard method.", ex);
        }

        return result;
    }
}