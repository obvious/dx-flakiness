# Quarantine Tests
A tool that is meant to surface flaky tests in your test suite.

## Running the server
The server uses [dPlauy](https://github.com/vinaysshenoy/dPlauy) to automatically deploy the server.

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