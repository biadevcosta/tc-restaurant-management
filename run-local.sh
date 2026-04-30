#!/bin/bash
docker compose up db -d
JWT_SECRET=minha-chave-secreta-super-longa-com-pelo-menos-256-bits-aqui ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
