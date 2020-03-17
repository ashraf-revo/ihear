#!/usr/bin/env bash
cf create-service mlab sandbox IhearMongodb
cf create-service cloudamqp lemur IhearRabbitmq
cf create-service p-service-registry trial IhearEureka