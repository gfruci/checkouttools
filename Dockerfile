FROM registry.docker.hcom/hotels/apache:latest
MAINTAINER Checkout SDET Team

RUN echo 'RewriteRule ^/(profile/instant_signin.html) /ba/fakehwa/$1                [PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(profile/signout.html) /ba/fakehwa/$1                       [L,PT]' >> /etc/httpd/conf/rewrite.rules