# JMoney

[![Build Status](https://travis-ci.org/jogy/jmoney.svg?branch=master)](https://travis-ci.org/jogy/jmoney) 
[![codecov](https://codecov.io/gh/jogy/jmoney/branch/master/graph/badge.svg)](https://codecov.io/gh/jogy/jmoney)
[![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](http://jogy.mit-license.org)

JMoney is a personal finance tracker written in Java/JavaScript using Spring Boot, JPA, and AngularJS.

## Running locally

```
git clone https://github.com/jogy/jmoney.git
cd jmoney
./gradlew bootRun
```
Then fire up a browser at <http://localhost:8080>.

## Running with Docker

You can also build and run a Docker image:
```
git clone https://github.com/jogy/jmoney.git
cd jmoney
./gradlew buildDocker
docker run -p 8020:8080 -t name.gyger/jmoney
```

## Running on Heroku

A demo instance is available at <https://jmoney-demo.herokuapp.com>.

You can also deploy your own instance: 

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

## Development

* Back-end dev: Run JMoneyApplication in your IDE. It will listen on http://localhost:8080 and include a recent version
of the front-end.
* Front-end dev: `cd front-end; npm start`. This will start a separate web server on http://localhost:4200 for the
front-end and it will proxy requests to http://localhost:8080. See [front-end/README.md](front-end/README.md) for 
details.