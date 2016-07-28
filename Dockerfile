FROM registry.docker.hcom/hotels/apache:latest
MAINTAINER Checkout SDET Team

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
RUN echo 'RewriteRule ^/(profile/instant_signin.html) /ba/fakehwa/$1                	[PT]'   >> /etc/httpd/conf/rewrite.rules
RUN echo 'RewriteRule ^/(profile/signout.html) /ba/fakehwa/$1                       	[L,PT]' >> /etc/httpd/conf/rewrite.rules
