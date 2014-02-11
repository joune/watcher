watcher
=======

Sample watchService test with scala and akka

The tests are very unstable, that's the reason why I'm posting this. See http://stackoverflow.com/questions/21675323/java7-watcheservice-events-not-receiving

Consider the following outputs:

watcher.WatcherMain
-------------------

### on touch file:

    take...
    ENTRY_CREATE
    ENTRY_MODIFY
    take...

### on touch again (modify)

    ENTRY_MODIFY
    take...

### on touch delete

    ENTRY_DELETE

"randomly" in the tests, I get an extra MODIFY event
----------------------------------------------------

    take...
    ENTRY_CREATE
    (ENTRY_CREATE,file2-5811774905980517765) => Actor[akka://WatcherSpec/system/testActor1#-1564059626]
    take...
    ENTRY_MODIFY
    (ENTRY_MODIFY,file2-5811774905980517765) => Actor[akka://WatcherSpec/system/testActor1#-1564059626]
    take...
    ENTRY_MODIFY
    (ENTRY_MODIFY,file2-5811774905980517765) => Actor[akka://WatcherSpec/system/testActor1#-1564059626]
    take...
    ENTRY_MODIFY
    (ENTRY_MODIFY,file2-5811774905980517765) => Actor[akka://WatcherSpec/system/testActor1#-1564059626]
    take...
    ENTRY_DELETE
    (ENTRY_DELETE,file2-5811774905980517765) => Actor[akka://WatcherSpec/system/testActor1#-1564059626]
    take...
    [info] WatcherSpec:
    [info] - outside akka
    [info] - in akka *** FAILED ***
    [info]   java.lang.AssertionError: assertion failed: expected (ENTRY_DELETE,file2-5811774905980517765), found (ENTRY_MODIFY,file2-5811774905980517765)

