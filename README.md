# amgmvc
A lightweight MVC framework with basic IOC

The purpose of this project is to provide a lightweight version of Spring with the most important features but  
none of the configuration overhead. It scans through your project when starting up, populating configuration files  
as necessary, and includes reasonable defaults. It's far from done, but all you'll need to do to configure this  
when it's done is copy and paste a couple of files into your project directory.

It also uses much less memory for small applications than Grails, and so therefore is *perfect* for your free Heroku  
app, where you're constrained to 500MB - assuming you have a small code and user base.

### Update

This was never completed, partially because some of the design choices I made when I was writing this don't seem to
fit well with modern web design practices. Routing was especially poorly handled.
