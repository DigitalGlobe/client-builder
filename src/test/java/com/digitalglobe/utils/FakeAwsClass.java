package com.digitalglobe.utils;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;

public class FakeAwsClass {
    public static FakeAwsClass create() {

        return new FakeAwsClass();
    }

    public static FakeAwsClass builder() {

        return new FakeAwsClass();
    }

    public FakeAwsClass credentialsProvider(AwsCredentialsProvider provider) {

        return this;
    }

    public FakeAwsClass region(Region region) {

        return this;
    }

    public FakeAwsClass build() {

        return new FakeAwsClass();
    }
}
