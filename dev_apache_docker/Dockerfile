FROM registry.docker.hcom/hotels/apache:latest
MAINTAINER Checkout SDET Team <HcomTechCKOSDETs@expedia.com>

RUN echo 'RewriteRule ^/(booking/car_trawler\.html.*) /ba/$1                        	[L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(booking/confirmation/car_trawler.html.*) /ba/$1            	[L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(booking/confirmation/travelhookoffers.html.*) /ba/$1       	[L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(booking/confirmation/travelhook.html.*) /ba/$1             	[L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(booking/messaging/confirmation/calendar.html.*) /ba/$1     	[L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(booking/messaging/confirmation/sms.html.*) /ba/$1          	[L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(booking/messaging/resend/confirmation/email.html.*) /ba/$1 	[L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(booking/resend_cancel_mail.html.*) /ba/$1                  	[L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(booking/resend_conf_mail.html.*) /ba/$1                    	[L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(booking/resend_conf_sms.html.*) /ba/$1                     	[L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(booking/travelhook\.html.*) /ba/$1                         	[L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(partner_booking_confirmation.html.*) /ba/$1                	[L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(profile/instant_signin.html) /ba/$1                	        [PT]'   >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(profile/signout.html) /ba/$1                       	        [L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(auth/externalauth/.*/redirect.html) /ba/$1                     [L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(auth/externalauth/.*/redirect) /$1.html                        [R,L]'  >> /etc/httpd/conf/rewrite.rules
#
# devrom
#
RUN sed -i '/RewriteRule ^\/(assets\/.\*)/d' /etc/httpd/conf/rewrite.rules
RUN sed -i '/RewriteRule ^\/(da\/daassets\/.\*)/d' /etc/httpd/conf/rewrite.rules

RUN echo 'RewriteCond %{REQUEST_URI} !^/assets/.*\.[a-z]+$' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(assets/.*) /da/$1                                                      [L,PT]' >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(da/daassets/.*) /da/$1                                                 [L,PT]' >> /etc/httpd/conf/rewrite.rules

RUN echo 'RewriteRule ^/assets/(?!cat\/)(?:s/)?(?:[0-9]+(?:\.[0-9]+)?/)?(.*) /dup/assets/$1     [L,PT]' >> /etc/httpd/conf/rewrite.rules

# Switch the following to lines to load client side templates from local DIO
RUN echo 'RewriteRule ^/(assets/cat/js/.*) /ba/$1                                               [L,PT]'  >> /etc/httpd/conf/rewrite.rules
#RUN echo 'RewriteRule ^/(assets/cat/js/.*) http://localhost:8080/$1                             [R,L]'  >> /etc/httpd/conf/rewrite.rules

RUN echo 'JkUnMount /dup/* hwa'                                                                         >> /etc/httpd/conf/rewrite.rules
