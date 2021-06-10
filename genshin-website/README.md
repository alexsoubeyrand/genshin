# Context

We focus here on providing a website for non-developers to consume the web service.

# Objectives

We aim to provide:
- all the features of the web service
- an easy to use, human-friendly website

# Focus

Since the web service is stateless, no session is shared with it.
In other words, all the data displayed on the website is stored locally by the browser and available offline.
Direct updates of the data, like changing a value in a cell of a table, should be possible offline too.
Indirect updates however (i.e. computation of the data to infer other data) is done through the web service.
If you want to do it offline too, you need to have your own web service running on your local machine.
Otherwise, accessing a service online should work as well.

**Special mention for personal data:**

We don't have it.
We don't need it.
We don't want it.

All your data must be stored by your browser.
Only the required data for computing common features must be exchanged, and personal data are useless for that.
Personal data might be processed at some point but only for local processing.
For example by consuming screenshots of your game to fill your data automatically.
If you see personal data going to the web service, this is a problem, not a feature.
Contact us immediately to find the culprit if it happens.

# Technologies

We program in HTML/CSS/JavaScript.
We also use a bit of PHP, although we try to keep it minimal to focus on dynamic updates with JavaScript.
We use jQuery to interact with the DOM and the web service.
Testing is done manually so far, but if you have a good testing framework to recommend we may consider it.
