watcher
=======

Sample watchService test with scala and akka

I know that the tests fail, that's the reason why I'm posting this :)

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

watcher.WatcherSpec "outside akka"
----------------------------------

### on touch file:

    take...
    ENTRY_CREATE
    take...

### on touch again (modify)

    ENTRY_MODIFY
    take...
    ENTRY_MODIFY
    take...

### on touch delete

    ENTRY_DELETE

watcher.WatcherSpec "inside akka"
---------------------------------

### on touch file:

    take...

and nothing else :(

