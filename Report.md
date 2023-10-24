Assignment 2
------------

# Team Members
Emily Gallacher

# GitHub link to your (forked) repository

https://github.com/em-i-ly/DS-Assignment2.git

# Task 1

1. Indicate the time necessary for the SimpleCrawler to work.

Ans: ~49.52 seconds

# Task 2

1. Is the flipped index smaller or larger than the initial index? What does this depend on?

Ans: The flipped index is larger than the initial index. Take this representative example:
In the initial representation (index.csv):

Link1 is associated with keywords A, B, and C.
Link2 is associated with keywords C, D, and E.

In the flipped representation (flipped_index.csv):

Keyword A is associated with Link1.
Keyword B is associated with Link1.
Keyword C is associated with both Link1 and Link2.
Keyword D is associated with Link2.
Keyword E are associated with Link2.

Given these relationships, we can observe that some keywords (e.g., A) are associated only one link, leading to 
additional unique keywords in the flipped_index.csv compared to the original representation.
Therefore, the flipped version tends to be longer because it captures individual associations for some keywords. If our
first keyword (C) associated with link2 is being replaced with a keyword "G", then our flipped index will have one more 
keyword that needs a seperate line in the flipped file:

Keyword A is associated with Link1.
Keyword B is associated with Link1.
Keyword C is associated with Link1.
Keyword G is associated with Link2.
Keyword D is associated with Link2.
Keyword E are associated with Link2.

--> Conclusion: the more unique keywords we have, the longer the flipped index will be.

# Task 3

1. Explain your design choices for the API design.

Ans: The task specified that we implement four new features into the API:
    1. launch a new crawling operation
    2. regenerate the flipped index
    3. delete a URL from the index
    4. update the information concerning a given URL in the index

For the launching of the new crawling operation I used a post request because you use this type of request when the action 
creating a new resource. This aligns with the RESTful convention of using POST to submit data to be processed. This request 
has two response codes. The first one is a 200 OK, indicating that the crawling operation has been launched. The other 
response is a 409 Conflict, telling us that a new crawling operation cannot be launched while one is still ongoing.

For the regeneration of the flipped index I used a post request as well because is a process that triggers a re-creation 
of an existing resource. The response type for this operation is simply a 200 OK. For the regeneration we assume that a
index.csv file exists.

To delete a url from the index I used the delete request. I think this is an obvious choice because we are requesting the
deletion of a certain url. This delete request has a 200 OK response code. Just incase the URL that was requested to be 
deleted does not exist, I added a 404 Not Found response code.

To update the information concerning a given URL in the index I used a put request because we are updating a current resource 
with new data. This aligns with typical put requests. This put request haa a 200 OK response code. I return the 200 code when 
the information has been added to a given URL in the index. I assume that you would be on the url that you wish to update, so 
it must exist. Also, the query can have any information, even be empty, as this would not affect the outcome of the process.

I made the added methods return to an admin page that confirms that the action has been performed. This can be seen in the
added schema called "AdminPage" of type String. 

# Task 4

1.  Indicate the time necessary for the MultithreadCrawler to work.

Ans: ~5.05 seconds

3. Indicate the ratio of the time for the SimpleCrawler divided by the time for the MultithreadedCrawler to get the increase in speed.

Ans: The Speed Increase Ratio is about 9.79.



