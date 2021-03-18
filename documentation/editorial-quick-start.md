# Editorial Quick Start

--------------------------------------------------------------------------------

\[[Up](README.md)\] \[[Top](#top)\]

--------------------------------------------------------------------------------

## Introducing

The Content-sync-example repository is offering the ability to synchronize content between environments. 
The extension is explicitly for cloud customers.
Please note that content-sync-example is, as the name is suggesting, only ought to be an example on
how a sync can be implemented, and is only covering the use-case of a partial sync (partial sync is describing 
the functionality to synchronize only a limited amount of contents, and not a whole environment).

## Purpose
As mentioned in the introduction, the content-sync-example project is only offering a partial sync, but with
extension points for customizations. Such a customization can be:
- Implementation of a complete environment synchronization
- Implementation for all sites of a given customer

## Contrib
Please feel free to contribute your functionality! Contribution can be achieved by Pull-Requests.

## Overview

## Necessary and preliminary data
To configure the content-sync correctly, it is necessary to have the following data in place:
- URL to the ingest-service
- JWT token for the ingest-service. In case you haven't received the JWT token, yet, please create a support ticket asking for that token (support@coremedia.com)

## Content-sync-example architecture
The content-sync architecture is quite simple and inside this git repository, the following application extensions can found:
- studio-client: Extension for CoreMedia Studio offering a simple and extendable UI.
- studio-server: CoreMedia Studio server extension. Providing the communication interfaces (retrieval of ingest-service data plus WFS communication).
- workflow-server: Additional CoreMedia Workflow to execute the workflow.

The picture below is depicting the high-level architecture: 

![CoreMedia Labs Logo](img/Architecture.jpg "CoreMedia Labs Logo")

## Studio-client configuration

## Studio-server configuration

## Interface

