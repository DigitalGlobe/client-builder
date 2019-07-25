package com.digitalglobe.utils;

import com.amazonaws.auth.AWSCredentialsProvider;

public class FakeAwsBuilderClass {

    public static FakeAwsClass defaultClient() {

        return new FakeAwsClass();
    }

    public static FakeAwsBuilderClass standard() {

        return new FakeAwsBuilderClass();
    }

    public FakeAwsBuilderClass withCredentials(AWSCredentialsProvider provider) {

        return this;
    }

    public FakeAwsBuilderClass withRegion(String region) {

        return this;
    }

    public FakeAwsClass build() {

        return new FakeAwsClass();
    }
}
