package com.tts.stack;

import software.amazon.awscdk.*;
import java.io.IOException;

public class LocalStack extends Stack {

    public LocalStack(final App scope, final String id, final StackProps props){
         super(scope, id, props);
    }

    public static void main(final String[] args){

        try {
            Process process = Runtime.getRuntime().exec("node --version");
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Node.js is not properly installed. Please install Node.js and try again.");
                System.exit(1);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Node.js is not installed. Please install Node.js and try again.");
            System.err.println("Error details: " + e.getMessage());
            System.exit(1);
        }

        App app = new App(AppProps.builder().outdir("./cdk.out").build());

        StackProps props = StackProps.builder().synthesizer(new BootstraplessSynthesizer()).build();

        new LocalStack(app, "localstack", props);
        app.synth();

        System.out.println("App synthesizing in progress...");
    }
}