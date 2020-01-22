package com.digitalglobe.utils;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.Credentials;
import org.testng.annotations.Test;

import java.util.UUID;

public class ClientBuilderTests {

    @Test(groups={"functional"})
    public void defaultBuilderTest() {

        new ClientBuilder<FakeAwsClass>().build(FakeAwsClass.class);
    }

    @Test(groups=("functional"))
    public void credentialBuilderTest() {

        AwsCredentials credentials = AwsSessionCredentials.create("x", "y", "z");
        StaticCredentialsProvider provider = StaticCredentialsProvider.create(credentials);

        new ClientBuilder<FakeAwsClass>().build(FakeAwsClass.class, provider);
    }

    @Test(groups=("functional"))
    public void regionCredentialBuilderTest() {

        AwsCredentials credentials = AwsSessionCredentials.create("x", "y", "z");
        StaticCredentialsProvider provider = StaticCredentialsProvider.create(credentials);

        new ClientBuilder<FakeAwsClass>().withRegion("us-east-1").build(FakeAwsClass.class, provider);
    }

    @Test(groups=("functional"))
    public void regionBuilderTest() {

        new ClientBuilder<FakeAwsClass>().withRegion("us-east-1").build(FakeAwsClass.class);
    }

    @Test(groups = {"integration"})
    public void s3ClientTest() {

        //Test a normal S3 client
        S3Client client = new ClientBuilder<S3Client>().build(S3Client.class);

        // Get Session Credentials for the BizOrdering Functional PowerUsers
        AwsCredentialsProvider sessionCredentials = null;

        Credentials credentials;

        try {

            credentials = new ClientBuilder<StsClient>().build(StsClient.class)
                    .assumeRole(AssumeRoleRequest.builder()
                            .roleArn("arn:aws:iam::727281582563:role/test_role")
                            .roleSessionName(UUID.randomUUID().toString())
                            .build())
                    .credentials();

        } catch(Exception ex) {

            throw new RuntimeException( "Unable to assume role.", ex);
        }

        sessionCredentials = StaticCredentialsProvider.create(AwsSessionCredentials.create(
                credentials.accessKeyId(),
                credentials.secretAccessKey(),
                credentials.sessionToken()));

        // Test a S3 client with credentials.
        client = new ClientBuilder<S3Client>().withRegion("us-east-1").build(S3Client.class, sessionCredentials);
    }
}
