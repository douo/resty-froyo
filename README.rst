======
Resty
======

Introduction
--------------

Resty is a small, convenient interface to talk to RESTful services from Java.

Its focus is on simplicity and ease-of-use, often requiring only two lines of code to access any web service.
It supports chaining several requests which is very useful in RESTful application employing HATEOS.
  
Basic usage is very simple: Create a Resty instance, use authenticate methode to add credentials, then call one of the content type specific methods.
The idea is that the method name will convey the expected content type you can then operate on.
 
  import static us.monoid.web.Resty.*;

GETting an URL (as JSON):

  new Resty().json(url);

POSTing to an URL (using multipart/form-data) and expecting JSON back:

	new Resty().json(url, form(data("name", "Don Draper"), data("occupation", "Ad Man")));
	
PUTting content and expecting JSON back:

	new Resty().json(url, put(content(someJSON)));
 
DELETE a resource via URL expecting JSON back:

 	new Resty().json(url, delete());


Here is an example on how to use the geonames web service. It retrieves the JSON object (see json.org for details) and gets the name of a place from the zip code::
  
 	Resty r = new Resty();
	Object name = r.json("http://ws.geonames.org/postalCodeLookupJSON?postalcode=66780&country=DE").get("postalcodes[0].placeName");
 
See more examples below.
 
Features
--------
- GET, POST, PUT, DELETE for text, XML, JSON, binary
- Fluid-style API to follow hyperlinks easily
- Complex path queries for JSON (simple tests on fields with operators >,=,< and full boolean expressions (&&,||,!))
- Support for XPath expressions
- Authentication with login/passwd
- Automatic Cookie management
- Full support for multipart/form-data
- GAE compatible (no cookie support though)

Changes
-------

Since 0.2.0: 

- Support for PUT, DELETE, Support for application/multipart-formdata

Since 0.3.0: 

- Option to ignore SSL certificate errors: Resty.ignoreAllCerts (global switch for now)
- New constructor to specify options: new Resty(Option.timeout(3000)); (sets the socket connect timeout)
- Create your own Options (see Resty.Option.Timeout or Resty.Option.Proxy for example)
- Fixed scala naming issue
- enhanced syntax for JSON queries
- bugfixes from my contributors. Thank you!
- Proxy support. Thank you, Gabriel. r.setProxy(...) for object r or new Resty(Option.proxy(...)) to carry proxy settings over when traversing paths
- convenient location header:  new Resty().bytes(url, put(someContent)).location(); // gets Location header as URI

Since 0.3.1.1:

- Can now parse JSON responses into arrays without a toplevel object.

Since 0.3.1.2:

- Better error handling when REST messages fail.

Since 0.3.1.3:

- Using the faster and more tolerant RobertFischer:JSON-Parser rather than the JSON.org implementation.

Status
-------

Growing

- Some HTTP verbs still missing (HEAD, OPTIONS among them)
- No explicit HTML support yet. Use text(...).toString() and feed it a parser like Jericho or JSoup
- No oauth support yet.

Installation
-------------
Either create the JAR yourself (see target directory or grab the rest-*.jar file and add it to your CLASSPATH.
Or grab it using Maven::
 repo: http://repo.smokejumperit.com
 groupId: RobertFischer
 artifactId: resty
 version: 0.3.1.3

Compile it yourself
-------------------
Use Gradle 1.0 to build.


Examples
-----------

See http://beders.github.com/Resty/Resty/Overview.html 

*Getting location information from the geonames web service, published as JSON*::

	Resty r = new Resty();
	Object name = r.json("http://ws.geonames.org/postalCodeLookupJSON?postalcode=66780&country=DE").
		get("postalcodes[0].placeName");

This gets a JSON object from the specified URL and extracts the first place name.

*Getting the Google Developer calendar feed as JSON and following the first entry, which is an XML resource,
extracting the title tag*::

	Resty r = new Resty();
	String title = r.json("http://www.google.com/calendar/feeds/developer-calendar@google.com/public/full?alt=json").
			xml(path("feed.entry[0].id.$t")).get("entry/title/text()", String.class);

The path(...) expression is used to extract a URL from the returned JSON object, which is then used to read an XML document.

*Getting ATOM feed from Slashdot and printing article URLs*::

	Resty r = new Resty();
	NodeList nl = r.xml("http://rss.slashdot.org/Slashdot/slashdotGamesatom").get("feed/entry/link");
	for (int i = 0, len = nl.getLength(); i < len; i++) {
		System.out.println(((Element)nl.item(i)).getAttribute("href"));
	}

*Some supported JSON path constructs*::

 store.book[price>7 && price<12.999].author
 store.book[!category='reference'].author
 
JSON Sample for paths above::

 { "store": {
    "book": [ 
      { "category": "reference",
        "author": "Nigel Rees",
        "title": "Sayings of the Century",
        "price": 8.95
      }, ... ]}}
 
*Chaining calls to navigate JSON objects. This is useful if the JSON contains URIs to go down the rabbit hole so to say*::

 import static us.monoid.web.Resty.*;
 import us.monoid.web.Resty;

 JSONObject json = r.
	json("http://localhost:9999/rest/sc").
	json(path("serviceclients[displayName='Sample'].href")).
	json(path("workflows")).json(path("current")).json(path("levels[displayName='Incoming'].href")).
	json(path("ruleSets[1].EngageRouting")).object();

Developers
===========

- Robert Fischer (http://twitter.com/RobertFischer)

Contributors
============

- Jochen Bedersdorfer (resty@bedersdorfer.de)
- Gabriel Falkenberg <gabriel.falkenberg@gmail.com>
- Remi Alvergnat <remi.alvergnat@gmail.com>


 
