#!/usr/bin/env bash
cf create-service cloudamqp lemur IhearRabbitmq
cf create-service p-service-registry trial IhearEureka
cf cups SERVICES -p '{"MONGO_HOST":"mongodb+srv://revo:revo@cluster0-fqb04.mongodb.net/test?retryWrites=true&w=majority"}'