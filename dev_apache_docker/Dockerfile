FROM kumo-docker-release-local.artylab.expedia.biz/library/apache:latest
MAINTAINER Checkout SDET Team <HcomTechCKOSDETs@expedia.com>

COPY dev-rewrite.rules /tmp/overrides.rules
RUN cat /tmp/overrides.rules >> /etc/httpd/conf/rewrite.rules
