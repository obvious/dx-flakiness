# (Pre-Alpha!!!) Test Flakiness Management
A tool that is meant to surface flaky tests in your test suite.

Currently, only supports the following platforms:
- [`JUnit4`](https://junit.org/junit4/). 
    - Does not support test suites which use parameterized test runners like [this](https://github.com/Pragmatists/JUnitParams) and [this](https://github.com/junit-team/junit4/wiki/Parameterized-tests).
    - Support for these in some form is planned in the future.
- [Android Instrumented Tests](https://developer.android.com/training/testing/unit-testing/instrumented-unit-tests)
    - Requires some workarounds to be integrated, as can be seen in [this project](https://github.com/simpledotorg/simple-android)
    - A better way to integrate into Android is planned for the future.

## Pre-requisites

The application currently requires JDK 13 to build. If you already have JDK 13 installed, skip this step.

**Check if the right JDK is already available**

Run the command `java -version`. If you have the right version of the JDK installed, you should see something like:
```sh
openjdk version "13.0.2" 2020-01-14
OpenJDK Runtime Environment AdoptOpenJDK (build 13.0.2+8)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 13.0.2+8, mixed mode, sharing)
```

If this command has an error, or shows a different version, you can follow the instructions below to install the JDK.

**Install the JDK**

We recommend using [jEnv](https://www.jenv.be/) to manage your JDK installations. Here are instructions to setup a working JDK 1.8 installation (macOS only):

1. Setup up [Homebrew](https://brew.sh/).

2. Install `jEnv` using Homebrew.
```sh
brew install jenv
```

3. Add the following lines to your shell configuration file (`~/.bash_profile` if you're using bash, or `~/.zshrc` if you're using zsh).
```sh
export PATH="$HOME/.jenv/bin:$PATH"
eval "$(jenv init -)"
```

4. Install the JDK using Homebrew.
```sh
brew tap AdoptOpenJDK/openjdk
brew cask install adoptopenjdk8
```

5. Add the installed JDK to `jEnv`
```sh
jenv add /Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home
```

6. Run the command `jenv versions`. You should see something like:
```sh
  system
* 13.0
  openjdk64-13.0.1
```

## How to build

**Clone the project using git.**

Run the following command in a terminal.

 ```
 $ git clone git@github.com:obvious/dx-flakiness.git
 ```

**Install IntelliJ IDEA**

Download and install IntelliJ IDEA from [their website](https://www.jetbrains.com/idea/). Either the Community or Ultimate edition will suffice.

**Import the project into IntelliJ.**

When the IDE starts up, it will prompt you to create a new project or import an existing project. Select the
option to import an existing project, navigate to the `dx-flakiness` directory you cloned earlier, and select it.

When building for the first time, gradle will download all dependencies so it'll take a few minutes to complete.
Subsequent builds will be faster.

## Modules
There are currently four modules in the project:

### `quarantine`
This contains the source code for the client that is integrated on the test suite side. 
 
### `quarantine-server`
This is the server app that collects the test runs and computes the flakiness report. This is built using [Dropwizard](http://dropwizard.io/).

### `buildSrc`
This currently houses an (exploratory) gradle plugin that makes it trivially easy to start collecting flakiness reports from test suites.

### Root module
This currently houses a sample project that showcases how to integrate the tool in a [`JUnit4`](https://junit.org/junit4/) test suite.

## Running the server locally
The project should be pre-packaged with run configurations for IntelliJ IDEA that are available when the project is imported. The important ones are:

#### DB Migrate
This sets up a SQLite database for running the server locally. The file is currently created with the name `quarantine.db` and is excluded from version control. This will need to be run **at least** once before running the server. Running it multiple times has no effect.

#### Run server app
This runs the server locally at port `8080`.

## Deploying the server
We use [dPlauy](https://github.com/vinaysshenoy/dPlauy) to automatically deploy the server.

### Usage
- Clone the [dPlauy](https://github.com/vinaysshenoy/dPlauy) project.
- Clone the source repository.
- Add the server configuration in `dPlauy.toml`.
- Deploy the server using the following command.

```shell script
./gradlew :quarantine:shadowJar
cd ../path/to/dPlauy/project
python3 deploy.py ../path/to/quarantine-tests env=<env>
```