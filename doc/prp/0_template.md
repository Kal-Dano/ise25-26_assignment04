# Project Requirement Proposal (PRP)
<!-- Adapted from https://github.com/Wirasm/PRPs-agentic-eng/tree/development/PRPs -->

You are a senior software engineer.
Use the information below to implement a new feature or improvement in this software project.

## Goal

**Feature Goal**: Import eines neuen Point-of-Sales (POS) basierend auf
einem existierenden OpenStreetMap-Eintrag

**Deliverable**: Completed Method the Api can call that extracts the node info, adds a new point of sales, and handles missing information with a default statement.

**Success Definition**: If Import from open street nap works for a random node, the feature is complete

## User Persona

**Target User**: Admin

**Use Case**: Scraping of openstreet map to populate the intial dataset

**User Journey**: i find an osml node, call the api to integrate it (curl --request POST http://localhost:8080/api/pos/import/osm/5589879349 # set a valid OSM node ID here), then find it in the database

**Pain Points Addressed**: Easier import of local shops, so that i dont have to generate every json file by hand.

## Why

- automatize building of initial dataset

## What

the api works as intended

### Success Criteria

- as specified above

## Documentation & References

MUST READ - Include the following information in your context window.

The `README.md` file at the root of the project contains setup instructions and example API calls.
The `osmxmlexample.txt` contains an example of an opnendtreetmap node xml file.

This Java Spring Boot application is structured as a multi-module Maven project following the ports-and-adapters architectural pattern.
There are the following submodules:

`api` - Maven submodule for controller adapter.

`application` - Maven submodule for Spring Boot application, test data import, and system tests.

`data` - Maven submodule for data adapter.

`domain` - Maven submodule for domain model, main business logic, and ports.
