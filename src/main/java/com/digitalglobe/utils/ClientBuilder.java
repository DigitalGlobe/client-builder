package com.digitalglobe.utils;

import com.amazonaws.auth.AWSCredentialsProvider;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Use this class to construct an Amazon Web Service Client using the defaultClient or standard with credentials.
 *
 * @param <InterfaceType> is the class type for the Amazon Web Service Client to be constructed.
 */
public class ClientBuilder<InterfaceType> {

    /**
     * Use this method to build a default client for the specified AWS client.
     *
     * @param staticBuilderClass is the static builder class to use when building the client.
     * @return a newly constructed client.
     * @throws RuntimeException when the method can't find or invoke the default client.
     */
    public InterfaceType build(Class staticBuilderClass) throws RuntimeException {

        InterfaceType result = null;

        try {

            Method method = staticBuilderClass.getDeclaredMethod("defaultClient");
            if (method != null && Modifier.isStatic(method.getModifiers())) {

                method.setAccessible(true);
                result = (InterfaceType)method.invoke((Object)null);

            } else {

                throw new RuntimeException("Couldn't find static defaultClient method.");
            }

        } catch (RuntimeException rte) {

            throw rte;

        } catch (Exception ex) {

            System.out.println(ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Couldn't invoke static defaultClient method.", ex);
        }

        return result;
    }

    /**
     * Use this method when you want to construct and Amazon Web Service Client using credentials.
     *
     * @param staticBuilderClass is the static builder class for the Amazon Web Service Client
     * @param credentials are the credentials to use when with the newly constructed client.
     * @return the newly constructed client.
     * @throws RuntimeException when the method can't find the withCredentials or build methods or can't execute them.
     */
    public InterfaceType build(Class staticBuilderClass, AWSCredentialsProvider credentials) throws RuntimeException {

        InterfaceType result = null;

        try {

            Method method = staticBuilderClass.getDeclaredMethod("standard");
            if (method != null && Modifier.isStatic(method.getModifiers())) {

                method.setAccessible(true);
                Object factory = method.invoke((Object)null);
                if (factory != null && factory.getClass().isAssignableFrom(staticBuilderClass)) {

                    method = factory.getClass().getMethod("withCredentials", AWSCredentialsProvider.class);
                    if (method == null) {

                        throw new RuntimeException("Couldn't find withCredentials method.");

                    } else {

                        method.invoke(factory, credentials);
                        method = factory.getClass().getMethod("build", (Class[])null);

                        if (method == null) {

                            throw new RuntimeException("Couldn't find the build method.");

                        } else {

                            result = (InterfaceType)method.invoke(factory, (Object[])null);
                        }
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