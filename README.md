# Scala API Boilerplate [![License: MIT][license-badge]][license-link] [![Build Status][build-badge]][build-link] [![Chat][gitter-badge]][gitter-link]

A TypeLevel API Boilerplate inspired by [scala-pet-store](https://github.com/pauljamescleary/scala-pet-store)

## Features

- [Http4s](http://http4s.org/) as the web server
- [Circe](https://circe.github.io/circe/) for json serialization
- [Doobie](https://github.com/tpolecat/doobie) for database access
- [Cats](https://typelevel.org/cats/) for FP awesomeness
- [ScalaCheck](https://www.scalacheck.org/) for property based testing
- [Circe Config](https://github.com/circe/circe-config) for app config
- Tagless Final for the core domain.

## Getting Started

Start up sbt:

```
> sbt
```

Once sbt has loaded, you can start up the application

```
> ~reStart
```

This uses revolver, which is a great way to develop and test the
application. Doing things this way the application will be
automatically rebuilt when you make code changes

To stop the app in sbt, hit the `Enter` key and then type:

```
> reStop
```

Or## Testing

### Unit Test

Start up sbt:

```
> sbt
```

Once sbt has loaded, you can test the application

```
> test
```

### Functional Test

In order to run the functional tests, your machine will need to have
Python 3 and pip, and virtualenv.

1. To install pip on a Mac, run `sudo easy_install pip`
2. Then install virutalenv `sudo pip install virtualenv`

To test out the app, first start it up following the directions above
and doing `reStart`

Then, in a separate terminal, run the test suite:

```bash
cd functional_test
./run.py live_tests -v
```

### Using PostgreSQL

1. Install PostgreSQL

2. Initialize a CardiaX database (use password: "Password01!")

```bash
cd scala-api-boilerplate
mkdir .db
initdb -D .db/api
pg_ctl -D .db/api start
createuser -d -s api -P
createdb api_local -O api
```

3. Edit 'src/main/resources/reference.conf' to use new database
   settings or create an .env file and source it.

```bash
touch .env
echo 'export DB_DRIVER=org.postgresql.Driver' >> .env
echo 'export DB_URL=jdbc:postgresql://localhost:5432/api_local' >> .env
echo 'export DB_USER=api' >> .env
echo 'export DB_PASSWORD=password01' >> .env
echo 'export DB_POOL_SIZE=16' >> .env
source .env
```

## Contributing

Contributions are always welcome, no matter how large or small. Before contributing,
please read the [code of conduct](./.github/CODE_OF_CONDUCT.md).

[license-badge]: https://img.shields.io/badge/License-MIT-yellow.svg
[license-link]: https://github.com/faeldon/scala-api-boilerplate/blob/master/LICENSE
[build-badge]: https://travis-ci.org/faeldon/scala-api-boilerplate.svg?branch=master
[build-link]: https://travis-ci.org/faeldon/scala-api-boilerplate
[gitter-badge]: https://badges.gitter.im/Join%20Chat.svg
[gitter-link]: https://gitter.im/faeldon/scala-api-boilerplate
