# Introduction

By implementing `JarvisLifecycleListener` interface, it is very easy to extend Jarvis with additional
features and functionalities. The `jarvis-deployment-core`, `jarvis-deployment-docker` and `jarvis-deployment-kubernetes`
modules ate Jarvis extensions that adds an **annotation-driven** support for deploying services as Docker
containers or as Kubernetes resources.

# Getting Started

The first step is to add one of the following dependencies to your `gradle.build` files:

```
testImplementation group: 'com.github.markostijak.jarvis.deployment', name: 'jarvis-deployment-docker', version: '0.0.1.xxxxx-main'
```

or

```
testImplementation group: 'com.github.markostijak.jarvis.deployment', name: 'jarvis-deployment-kubernetes', version: '0.0.1.xxxxx-main'
```

Once the `jarvis-deployment-docker` or `jarvis-deployment-kubernetes` is on your classpath, you can use `@Deploy("name")`
annotation to specify which service should be deployed for the test class.

## API Reference

Within the `@Deploy` annotation, you can replace or add service properties during service deployment:

- `name` - references service `deployment description` properties file (keep reading for more details).
- `env` - replace or append service environment variables.
    - Format: `key=value`
- `scope` - define service scope. Available values are `CLASS`, `PACKAGE` and `JVM`.
    - If not specified, the global default scope will be `CLASS` scope. Global default scope can be changed using properties,
      for example: `jarvis.deployment.scope: JVM`.
- `order` - define service order. Useful when one service depends on another service.
    - Services with the same order will be started in parallel.
- `delayed` - useful when the service takes some time to become ready but does not have a health check.
    - Otherwise, the health check can be used to wait for the service until it is ready.

The `@Deploy` annotation is itself annotated with a `@Deployment` annotation, which is
a meta-annotation that is used as a base to create a various deployment annotations, for example
`@DeployPostgres` annotation:

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Deployment(name = "postgres", delayed = "5s")
public @interface DeployPostgres {

    int order() default 0;

    String[] env() default {};

    Scope scope() default Scope.DEFAULT;

}

```

You can also specify multiple `@Deploy` or `@DeployXXX` annotations on a single annotation (i.e. `@DeployAll`),
which can then be used to start a group o services.

The `@Deploy("name")` annotation is always used in combination with the `@JarvisTest` annotation. Otherwise, it won't have any effect,
as Jarvis and Spring won't start and annotation scanning won't happen.

Here is the example how to use it:

```
@JarvisTest
@DeployPostgres
@Deploy(name = "kafka", delayed = "5s")
public class ExampleTest {

    @Test
    void example() {
        // kafka and postgres are deployed
        // you can use it in test
        System.out.println("Hello from Jarvis!");
    }

}
```

As mentioned earlier, deployment name will reference the `deployment description` in properties file.

In the case of Docker deployments, the `DockerDeploymentDescriptor` is used, and it allows you to specify
Docker image and container name. It also allows you to specify environment variables, exposed ports and volume mounts:

```
jarvis:
  deployment:
    docker:
      services:
        postgres:
          descriptor:
            container-name: postgres
            image: 'postgres-image'
            env:
              POSTGRES_USER: test
              POSTGRES_PASSWORD: test
              POSTGRES_DB: test-db
            ports:
              - 5432:5432
```

Similarly, in the case of Kubernetes deployments, the `KubernetesDeploymentDescriptor` is used, and it allows you to specify
kubernetes .yaml file (can be on classpath), or alternatively a helm chart and helm repo. It also allows you to specify
or override environment variables defined in kubernetes .yaml file, exposed ports and volume mounts:

```
jarvis:
  deployment:
    kubernetes:
      services:
        postgres:
          descriptor:
            # using kubernetes yaml file
            yaml: 'classpath:k8s/postgres.yaml'
            # or by using helm
            helm:
                chart: 'postgres-chart'
                version: 'postgres-version'
                repository: https://postgres-chart-repo
            env:
              POSTGRES_USER: test
              POSTGRES_PASSWORD: test
              POSTGRES_DB: test-db
            ports:
              - 5432:5432
```

Note: The Helm must be installed on the host, as `ProcessBuilder` is used for helm commands.

## DeploymentBean

One more way to register deployments is by creating one of following beans in Spring context:

- `DeploymentBean`
    - You need to provide deployment descriptor in properties file
- `DockerDeploymentBean`
    - You can specify docker deployment descriptor in bean itself
- `KubernetesDeploymentBean`
    - You can specify kubernetes deployment descriptor in bean itself

By creating one of those beans, you will register deployments for each test class. Note that default scope of
`DeploymentBean` is `JVM scope`, which means deployments will be started before all tests, and stopped after
all tests, on JVM shutdown.

---

## Deployment scopes

Supported deployment scope are:

- `CLASS` - service will be deployed only during the test class
- `PACKAGE` - only one service will be deployed for all test classes in the same package.
  If package changes, service will be stopped.
- `JVM` - service will be deployed only once, first time it is requested,
  and it will be stopped once all test classes are executed, on JVM shutdown.

Note: in parallel execution, only `CLASS` scope is supported.

---

# See more:

- See here how to add custom [jarvis-services](services/README.md)
