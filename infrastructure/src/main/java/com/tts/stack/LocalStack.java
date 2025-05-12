package com.tts.stack;





import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalStack extends Stack {

    private final Vpc vpc;
    private final Cluster ecsCluster;

    public LocalStack(final App scope, final String id, final StackProps props){
         super(scope, id, props);

         this.vpc = createVpc();

         DatabaseInstance authServiceDb = createDatabase("AuthServiceDB", "auth-service-db");

         DatabaseInstance patientServiceDb = createDatabase("PatientServiceDB", "patient-service-db");

         CfnHealthCheck authDbHealthCheck = createDbHealthCheck(authServiceDb, "AuthServiceDBHealthCheck");

         CfnHealthCheck patientDbHealthCheck = createDbHealthCheck(patientServiceDb, "PatientServiceDBHealthCheck");

         CfnCluster mskCluster = createMskCluster();

         this.ecsCluster = createEcsCluster();

         FargateService authService = createFargateService("AuthService", "auth-service",
                 List.of(4005), authServiceDb, Map.of("JWT_SECRET", "9028435891a8d31cd6842600ac452881b9cf6eed29c2d624bcad74c3be6334fdde9da92d1b16628dd050f05b228c6fc1e117f3a9a17cfc10b312fff9660e2793d6180df1c73f13dda9cdda1ac78c786a1e4d9d56011288e3be2bc7ece45b59d8e7ac8c6d8f7f26dd1fdbf6be780f472464224b509ee53262f93feae0ee781989addea11f85649119b399e13602c79c6de035b1569d498be1e8fc1c0e4985b1a1fc759bb5c33a79ede30caeaca128d431dd94a6ad252230ac8fd94773bbb1158f6df8925239c084301d1d0bbbbaa9729aeaf51a83368887f03fcb6ad6afa13030cb309b869189ad2e8ebea6d0899c0c6ec8ec598d85b4eb14f858cdeb57b4c71a"));

         authService.getNode().addDependency(authDbHealthCheck);
         authService.getNode().addDependency(authServiceDb);

         FargateService biddenService = createFargateService("BillingService", "billing-service",
                 List.of(4001, 9001), null, null);

         FargateService analyticsService = createFargateService("AnalyticsService", "analytics-service",
                 List.of(4002), null, null);

         analyticsService.getNode().addDependency(mskCluster);

         FargateService patientService = createFargateService("PatientService", "patient-service",
                 List.of(4000), patientServiceDb, Map.of("BILLING_SERVICE_ADDRESS", "host.docker.internal",
                         "BILLING_SERVICE_GRPC_PORT", "9001"));

         patientService.getNode().addDependency(patientServiceDb);
         patientService.getNode().addDependency(patientDbHealthCheck);
         patientService.getNode().addDependency(biddenService);
         patientService.getNode().addDependency(mskCluster);

         createApiGateWayService();

    }

    private Vpc createVpc(){
        return Vpc.Builder.create(this, "PatientManagementVPC").vpcName("PatientManagementVPC")
                .maxAzs(2).build();
    }

    private DatabaseInstance createDatabase(String id, String dbName){
        return DatabaseInstance.Builder.create(this, id).engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps
                .builder().version(PostgresEngineVersion.VER_17_2).build()))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("dexton"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db, String id){
        return CfnHealthCheck.Builder.create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP").port(Token.asNumber(db.getDbInstanceEndpointPort()))
                        .ipAddress(db.getDbInstanceEndpointAddress())
                        .requestInterval(30)
                        .failureThreshold(3)
                        .build())
                .build();
    }

    private CfnCluster createMskCluster(){
        return CfnCluster.Builder.create(this, "MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("2.8.0")
                .numberOfBrokerNodes(1)
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        .instanceType("kafka.m5.large")
                        .clientSubnets(vpc.getPrivateSubnets().stream().map(
                                ISubnet::getSubnetId)
                                .collect(Collectors.toList()))
                        .brokerAzDistribution("DEFULT").build())
                .build();
    }

    private Cluster createEcsCluster(){
        return Cluster.Builder.create(this, "PatientManagementCluster")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("patient-management.local")
                        .build())
                .build();
    }

    private FargateService createFargateService(String id, String imageName, List<Integer> ports,
                                                DatabaseInstance db, Map<String, String> additionalEnvVars){

        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this, id + "Task")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();

        ContainerDefinitionOptions.Builder containerOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry(imageName))
                .portMappings(ports.stream().map(port -> PortMapping.builder()
                        .containerPort(port)
                        .hostPort(port)
                        .protocol(Protocol.TCP)
                        .build()).toList())
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, id+"LogGroup")
                                        .logGroupName("/ecs/" + imageName)
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .retention(RetentionDays.ONE_DAY)
                                        .build())
                                .streamPrefix(imageName )
                        .build()));

        Map<String, String> envVars = new HashMap<>();
        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost.localstack.cloud:4510, localhost.localstack.cloud:4511, " +
                "localhost.localstack.cloud:4512");

        if(additionalEnvVars != null){
            envVars.putAll(additionalEnvVars);
        }

        if(db != null){
            envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db".formatted(
                    db.getDbInstanceEndpointAddress(),
                    db.getDbInstanceEndpointPort(),
                    imageName
            ));
            envVars.put("SPRING_DATASOURCE_USERNAME", "dexton");
            envVars.put("SPRING_DATASOURCE_PASSWORD", db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "600000");
        }

        containerOptions.environment(envVars);
        taskDefinition.addContainer(imageName + "Container", containerOptions.build());
        return FargateService.Builder.create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false)
                .serviceName(imageName)
                .build();
    }

    private void createApiGateWayService(){
          FargateTaskDefinition taskDefinition =
                  FargateTaskDefinition.Builder.create(this, "APIGateWayTaskDefinition")
                          .cpu(256)
                          .memoryLimitMiB(512)
                          .build();

        ContainerDefinitionOptions containerOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry("api-gateway"))
                .environment(Map.of("SPRING_PROFILES_ACTIVE", "prod",
                        "AUTH_SERVICE_URL", "http://host.docker.internal:4005"))
                .portMappings(List.of(4004).stream().map(port -> PortMapping.builder()
                        .containerPort(port)
                        .hostPort(port)
                        .protocol(Protocol.TCP)
                        .build()).toList())
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                        .logGroup(LogGroup.Builder.create(this, "ApiGatewayLogGroup")
                                .logGroupName("/ecs/api-gateway")
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .retention(RetentionDays.ONE_DAY)
                                .build())
                        .streamPrefix("api-gateway")
                        .build()))
                .build();

        taskDefinition.addContainer("APIGateWayContainer", containerOptions);

        ApplicationLoadBalancedFargateService apiGateway
                = ApplicationLoadBalancedFargateService.Builder.create(this, "APIGateWayService")
                .cluster(ecsCluster)
                .serviceName("api-gateway")
                .taskDefinition(taskDefinition)
                .desiredCount(1)
                .healthCheckGracePeriod(Duration.seconds(60))
                .build();
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