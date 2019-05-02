package com.digitalglobe.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.Credentials;
import org.testng.annotations.Test;

import java.util.UUID;

public class ClientBuilderTests {

    @Test(groups={"functional"})
    public void defaultBuilderTest() {

        new ClientBuilder<FakeAwsClass>().build(FakeAwsBuilderClass.class);
    }

    @Test(groups=("functional"))
    public void credentialBuilderTest() {

        AWSCredentials credentials = new BasicSessionCredentials("x", "y", "z");
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);

        new ClientBuilder<FakeAwsClass>().build(FakeAwsBuilderClass.class, provider);
    }

    @Test(groups = {"integration"})
    public void s3ClientTest() {

        //Test a normal S3 client
        AmazonS3 client = new ClientBuilder<AmazonS3>().build(AmazonS3ClientBuilder.class);

        // Get Session Credentials for the BizOrdering Functional PowerUsers
        AWSCredentialsProvider sessionCredentials = null;

        Credentials credentials;

        try {

            credentials = new ClientBuilder<AWSSecurityTokenService>().build(AWSSecurityTokenServiceClientBuilder.class)
                    .assumeRole(new AssumeRoleRequest()
                            .withRoleArn("arn:aws:iam::724019132696:role/PowerUsers")
                            .withRoleSessionName(UUID.randomUUID().toString()))
                    .getCredentials();

        } catch(Exception ex) {

            throw new RuntimeException( "Unable to assume role.", ex);
        }

        sessionCredentials = new AWSStaticCredentialsProvider(new BasicSessionCredentials(
                credentials.getAccessKeyId(),
                credentials.getSecretAccessKey(),
                credentials.getSessionToken()));

        // Test a S3 client with credentials.
        client = new ClientBuilder<AmazonS3>().build(AmazonS3ClientBuilder.class, sessionCredentials);
    }
}
