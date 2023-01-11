FROM nimlang/nim:alpine as build

COPY . /app
WORKDIR /app
RUN nimble install -d -y
RUN nimble buildProd

FROM alpine:latest

COPY --from=build /app /app
ENV HOME /app
WORKDIR /app
RUN apk update && \
    apk add libpq-dev
RUN adduser monika -D -H
USER monika
CMD /app/bin/reminder