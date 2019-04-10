FROM openjdk:8u181

MAINTAINER Loïc DIVAD ldivad@xebia.fr

RUN mkdir -p /etc/ratatouille-datagen/ /opt/

COPY ratatouille-datagen*.tar /tmp/

RUN tar -xf /tmp/ratatouille-datagen*.tar -C /opt/

RUN mv /opt/ratatouille-datagen*/ /opt/ratatouille-datagen/

WORKDIR /opt/ratatouille-datagen/

ENTRYPOINT [ "/opt/ratatouille-datagen/bin/ratatouille-datagen" ]