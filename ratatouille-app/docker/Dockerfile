FROM node:10.15.3-alpine

MAINTAINER Loïc DIVAD ldivad@xebia.fr

USER node
RUN mkdir /home/node/.npm-global
ENV PATH=/home/node/.npm-global/bin:$PATH
ENV NPM_CONFIG_PREFIX=/home/node/.npm-global

RUN  npm install http-server -g

USER root

RUN mkdir -p /root/www/site/

WORKDIR /root/www/site/

ADD 404.html /root/www/site/
ADD index.html /root/www/site/
ADD css/ /root/www/site/css/
ADD fonts/ /root/www/site/fonts/
ADD image/ /root/www/site/image/

CMD http-server .