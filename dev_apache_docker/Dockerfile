FROM registry.docker.hcom/hotels/apache:latest
MAINTAINER Checkout SDET Team <HcomTechCKOSDETs@expedia.com>

COPY dev-rewrite.rules /tmp/overrides.rules
RUN cat /tmp/overrides.rules >> /etc/httpd/conf/rewrite.rules

#
# devrom
#
RUN sed -i '/RewriteRule ^\/(assets\/.\*)/d' /etc/httpd/conf/rewrite.rules
RUN sed -i '/RewriteRule ^\/(da\/daassets\/.\*)/d' /etc/httpd/conf/rewrite.rules
RUN sed -i '/RewriteRule ^\/(auth\/.\*)/d' /etc/httpd/conf/rewrite.rules

RUN echo 'RewriteCond %{REQUEST_URI} !^/assets/.*\.[a-z]+$' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(assets/.*) /da/$1                                                              [L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(da/daassets/.*) /da/$1                                                         [L,PT]' >> /etc/httpd/conf/rewrite.rules

RUN echo 'RewriteRule ^/assets/(?!(?:cat|cop)\/)(?:s/)?(?:[0-9]+(?:\.[0-9]+)?/)?(.*) /dup/assets/$1     [L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/assets/cop/(.*) /dup/mvt/cop/$1                                                 [L,PT]' >> /etc/httpd/conf/rewrite.rules
# Switch the following to lines to load client side templates from local DIO
RUN echo 'RewriteRule ^/(assets/cat/js/.*) /ba/$1                                                       [L,PT]' >> /etc/httpd/conf/rewrite.rules
#RUN echo 'RewriteRule ^/(assets/cat/js/.*) http://localhost:8080/$1                                    [R,L]'  >> /etc/httpd/conf/rewrite.rules

RUN echo 'JkUnMount /dup/* hwa'                                                                                 >> /etc/httpd/conf/rewrite.rules
